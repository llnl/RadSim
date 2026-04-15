/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.bnl.nndc.ensdf.decay;

import gov.bnl.nndc.ensdf.EnsdfAlpha;
import gov.bnl.nndc.ensdf.EnsdfBeta;
import gov.bnl.nndc.ensdf.EnsdfDataSet;
import gov.bnl.nndc.ensdf.EnsdfElectronCapture;
import gov.bnl.nndc.ensdf.EnsdfEmission;
import gov.bnl.nndc.ensdf.EnsdfGamma;
import gov.bnl.nndc.ensdf.EnsdfLevel;
import gov.bnl.nndc.ensdf.EnsdfNormalization;
import gov.bnl.nndc.ensdf.EnsdfParent;
import gov.bnl.nndc.ensdf.EnsdfQuantity;
import gov.bnl.nndc.ensdf.EnsdfTimeQuantity;
import gov.llnl.rtk.physics.DecayTransition;
import gov.llnl.rtk.physics.Emission;
import gov.llnl.rtk.physics.EmissionCorrelation;
import gov.llnl.rtk.physics.Nuclide;
import gov.llnl.rtk.physics.Nuclides;
import gov.llnl.rtk.physics.Xray;
import gov.llnl.rtk.physics.XrayData;
import gov.llnl.rtk.physics.XrayEdge;
import gov.llnl.rtk.physics.XrayLibrary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 *
 * Utility to chop up isomers from an ENSDF decay record.
 *
 * ENSDF always stores decay records to the ground state even if the decay is to
 * an isomer. Thus to make use of ENSDF data we need to be able to split a decay
 * to each of the isomers that will be populated and compute their branching
 * ratio and emissions.
 *
 * This algorithm traverses the structure tree defined by the ENSDF record and
 * computes the split to assign to each emission and level so that we can
 * produce an emission diagram specific to each transition.
 *
 * This can be used even if there are no isomers to convert ENSDF to a
 * DecayTransition.
 *
 * @author nelson85
 */
public class SplitIsomers
{

  /**
   * Optional behavior controls.
   */
  public enum Option
  {
    /**
     * Ignore isomers and produce a record to ground only.
     */
    GROUND_ONLY,
    /**
     * Drop unassigned emissions from the output.
     */
    EXCLUDE_UNASSIGNED,
    /**
     * Use the ENSDF level MS field to assign isomers.
     */
    USE_MS_ISOMERS
  }

  // Settings
  double isomerHalflifeThreshold = 1.0;
  XrayLibrary xrayLibrary = null;
  public boolean xrayComments = false; // For LNHB
  private EnumSet<Option> options = EnumSet.noneOf(Option.class);

  final EnsdfDataSet dataSet;
  final EnsdfParent parent;
  final EnsdfNormalization normalization;
  final ArrayList<EnsdfLevel> isomers = new ArrayList<>();
  final LinkedList<EnsdfLevel> queue = new LinkedList<>();
  final HashMap<EnsdfLevel, LevelWorkspace> levelWorkspaceMap = new HashMap<>();
  final HashMap<EnsdfEmission, EmissionWorkspace> emissionWorkspaceMap = new HashMap<>();
  double normalizationGamma = -1;
  double normalizationTransition = -1;
  double normalizationBeta = -1;
  final double branchingRatio;  // The original branching ratio assigned to this 
  
  final double LINKAGE_THRESHOLD = 2;

  public void setOptions(Option option, Option... options)
  {
    this.options = EnumSet.of(option, options);
  }

  public void setXrayLibrary(XrayLibrary library)
  {
    this.xrayLibrary = library;
  }

  public SplitIsomers(EnsdfDataSet ds)
  {
    this.dataSet = ds;
    if(!ds.parents.isEmpty())
    {
      this.parent = ds.parents.get(0);
      System.out.println(ds.identification.DSID+" : "+this.parent);
    }
    else
      this.parent = null;
    // We will need the normalization record.
    if (!ds.normalizations.isEmpty())
    {
      this.normalization = ds.normalizations.get(0);
      if (normalization.NR.isSpecified())
        this.normalizationGamma = normalization.NR.toDouble(); // used to scale RI in Gamma
      if (normalization.NT.isSpecified())
        this.normalizationTransition = normalization.NT.toDouble(); // used to scale TI in Gamma
      if (normalization.BR.isSpecified())

        this.branchingRatio = normalization.BR.toDouble(); // used to compute the final branching ratio
      else
        this.branchingRatio = 0.0;
      if (normalization.NB.isSpecified())
        this.normalizationBeta = normalization.NB.toDouble(); // used to scale IB, IE, and TI
    } else
    {
      this.normalization = null;
      this.branchingRatio = 0.0;
    }
  }

  public List<DecayTransitionImpl> execute()
  {
    // FIXME this is depending on the title of the record
    // Check for an allowed decay type 
    String[] parts = this.dataSet.identification.DSID.split(" ");
    
    // Atoms that cannot be handled
    // Atoms that are known to decay to multiple daughters, but not noted in the record
    switch(parts[0])
    {
      case "MUONIC":
      case "11LI":
      case "22SI":
      case "27P":
      case "28S":
      case "29NE":
      case "36K": 
        return Collections.EMPTY_LIST;
    }
    
    switch (parts[1])
    {
      case "B-":
      case "B+":
      case "A":
      case "EC":
      case "IT":
        break;

      // We must merge multiple records to handle these types
      case "B+A":
      case "B+P":
      case "B+2P":
      case "B+3P":
      case "B-A":
      case "B-N":
      case "B-2N":
      case "B-P":
      case "ECP":
      case "ECA":
      case "SF":
      case "P":
      case "2P":
      case "EC2P":
      case "EC3P":
      case "N":
      case "2N":
      case "MIXED":
      case "2B-":
      case "2B+":
      case "2EC":
      case "14C":
        return Collections.EMPTY_LIST;
      default:
        throw new UnsupportedOperationException("Unknown decay type " + parts[1]);
    }

    // Allocate workspaces
    allocateEmissions();
    allocateLevels();

    crosslinkGammas();

    if (!findIsomers())
    {
      System.out.println("WARNING Decay to multiple daughters");
      return Collections.EMPTY_LIST;
    }
    collectGammas();
    
    // backward propogation
    visitAll(this::visitBackward);

    // forward propogation
    initializeForward();
    visitAll(this::visitForward);

    // Update the branching ratios
    updateBranchingRatios();

    // Split unassigned based on the branching ratio
    allocateUnassigned();

    // Convert the dataset after splitting into a transition record 
    ArrayList<DecayTransitionImpl> output = new ArrayList<>();
    for (EnsdfLevel isomer : this.isomers)
    {
      DecayTransitionImpl dt = convertIsomer(isomer);
      if (dt != null)
        output.add(dt);
    }
    return output;
  }

//<editor-fold desc="workspace" defaultstate="collapsed">
  /**
   * Get the working information for an emission.
   *
   * @param emission
   * @return
   */
  EmissionWorkspace getEmissionWorkspace(EnsdfEmission emission)
  {
    EmissionWorkspace out = emissionWorkspaceMap.get(emission);
    if (out == null)
      System.out.println("ERROR Missing workspace for " + emission);
    return out;
  }

  /**
   * Get the working information for a level.
   *
   * @param level
   * @return
   */
  LevelWorkspace getLevelWorkspace(EnsdfLevel level)
  {
    return levelWorkspaceMap.get(level);
  }

  /**
   * Allocate a workspace for all levels.
   */
  void allocateLevels()
  {
    dataSet.levels.forEach((p) -> levelWorkspaceMap.put(p, new LevelWorkspace(p, this)));
  }

  /**
   * Allocate a workspace for all of the emissions.
   */
  void allocateEmissions()
  {
    for (EnsdfLevel level : dataSet.levels)
    {
      level.gamma.forEach((p) -> emissionWorkspaceMap.put(p, new EmissionWorkspace(p, this.getGammaProbability(p))));
      level.beta.forEach((p) -> emissionWorkspaceMap.put(p, new EmissionWorkspace(p, this.getBetaTransition(p))));
      level.alpha.forEach((p) -> emissionWorkspaceMap.put(p,
              new EmissionWorkspace(p, this.getAlphaTransition(p))));
      level.ec.forEach((p) -> emissionWorkspaceMap.put(p,
              new EmissionWorkspace(p, this.getEcTransition(p))));
    }

    // Allocate space for unassigned emissions
    dataSet.unassigned.gamma.forEach((p) -> emissionWorkspaceMap.put(p, new EmissionWorkspace(p, this.getGammaProbability(p))));
    dataSet.unassigned.beta.forEach((p) -> emissionWorkspaceMap.put(p, new EmissionWorkspace(p, this.getBetaTransition(p))));
    dataSet.unassigned.alpha.forEach((p) -> emissionWorkspaceMap.put(p, new EmissionWorkspace(p, this.getAlphaTransition(p))));
    dataSet.unassigned.ec.forEach((p) -> emissionWorkspaceMap.put(p, new EmissionWorkspace(p, this.getEcTransition(p))));
  }

  //</editor-fold>
//<editor-fold desc="pre" defaultstate="collapsed">
  /**
   * Finds the level that each gamma transition goes to.
   */
  public void crosslinkGammas()
  {
    List<EnsdfGamma> gammas = dataSet.collectGammas();
    List<EnsdfLevel> levels = dataSet.levels;
    for (EnsdfGamma gamma : gammas)
    {
      if (!(gamma.level instanceof EnsdfLevel))
        continue;
      
      EnsdfLevel gammaLevel = (EnsdfLevel) gamma.level;
      double e = gamma.E.toDouble();
      double e2 = gammaLevel.E.toDouble();
      
      double e3 = e2 - e;
            
      // FIXME deal with questionable assignments that contain "?"
      if (gamma.continuation.containsKey("FL"))
        e3 = gamma.continuation.get("FL").toDouble();

      EnsdfLevel best = levels.get(0);
      double e5 = 0;
      for (EnsdfLevel level : levels)
      {
        double e4 = level.E.toDouble();
        if (Math.abs(e4 - e3) < Math.abs(e5 - e3))
        {
          e5 = e4;
          best = level;
        }
      }
      if (Math.abs(e5 - e3) > LINKAGE_THRESHOLD)
      {
        System.out.println("WARNING: "+ dataSet.identification.DSID+ " Bad linkage " + gamma.E);
      }
      getEmissionWorkspace(gamma).destination = best;
    }
  }

  /**
   * Search the level list to find the meta stable states that belong to this
   * decay.
   *
   * We will also include the ground state.
   */
  boolean findIsomers()
  {
    // Find isomers
    if (!options.contains(Option.GROUND_ONLY))
    {
      boolean useLabeledIsomers = options.contains(Option.USE_MS_ISOMERS);
      for (EnsdfLevel level : dataSet.levels)
      {
        // If the metastable flag is set then we will assume it is a metastable to extract
        if (useLabeledIsomers && !level.MS.isBlank())
        {
          isomers.add(level);
        } // Extract isomers above the time threshold
        else if (level.T.toDouble() > this.isomerHalflifeThreshold) // units seconds
        {
          // Check if the isomer is the parent of the decay.
          if (parent.NUCID.equals(dataSet.identification.NUCID) && parent.T.equals(level.T))
            continue;

          isomers.add(level);
        } // This is something in which the decay goes to multiple children
        // rather than one and is stored on multiple records 
        // ie.  B- DECAY parent to first child B-A DECAY parent to second child
        // To properly handle this we would need to combine the two records
        // together linking the levels based on energy.
        else if (level.continuation.containsKey("%P"))
        {
          return false;
        } else if (level.continuation.containsKey("%N"))
        {
          return false;
        } else if (level.continuation.containsKey("%A"))
        {
          return false;
        } else if (level.continuation.containsKey("%EC"))
        {
          return false;
        } else if (level.continuation.containsKey("%B-"))
        {
          return false;
        } else if (level.continuation.containsKey("%B+"))
        {
          return false;
        } else if (level.continuation.containsKey("%EC+B+"))
        {
          return false;
        } else if (level.continuation.containsKey("%SF"))
        {
          return false;
        } // Skip shortlived isomers.
        else if (level.T.isSpecified())
        {
//        System.out.println("Skip isomer " + level.E + " " + level.T);
        }
      }
    }
    
    if (parent.nuclide == null)
    {
      System.out.println("WARNING:" + dataSet.identification.DSID+ "parent nuclide is null");
      return false;
    }
    if (dataSet.levels.isEmpty())
    {
      EnsdfLevel assumedGround = new EnsdfLevel(dataSet, new EnsdfQuantity("0.0", "0.0"), "", new EnsdfTimeQuantity("", ""), "", null, '\u0000', "", '\u0000');
      isomers.add(assumedGround);
      dataSet.levels.add(assumedGround);
      levelWorkspaceMap.put(assumedGround, new LevelWorkspace(assumedGround, this));
//      System.out.println("WARNING parent levels are empty");
//      return false;
    }

    // Always add the ground state (even if we missed it somehow)
    EnsdfLevel ground = dataSet.levels.get(0);
    if (!isomers.contains(ground))
      isomers.add(ground);

    if (dataSet.identification.product == null)
      return false;

    // Assign the initial split values to all isomers and ground state
    int i = 0;
    List<Nuclide> nuclearIsomers = Nuclides.getIsomers(
            dataSet.identification.product.getAtomicNumber(),
            dataSet.identification.product.getMassNumber());
    for (EnsdfLevel level : isomers)
    {
      LevelWorkspace ws = levelWorkspaceMap.get(level);
      ws.splitIndex = i;
      // Make sure we can properly assign the child
      for (Nuclide isomer : nuclearIsomers)
      {
        if (Math.abs(isomer.getHalfLife() - level.T.toDouble()) < 0.03 * isomer.getHalfLife()
                || (Double.isInfinite(isomer.getHalfLife()) && Double.isInfinite(level.T.toDouble())))
          ws.nuclide = isomer;
      }
      if (ws.nuclide == null)
      {
        if (ws.level.E.isSpecified() && ws.level.E.toDouble() == 0.0)
          ws.nuclide = nuclearIsomers.get(0);
        else
        {
          System.out.println("WARNING:" + dataSet.identification.DSID+ " unable to find isomer for level " + level.E.field + " " + level.T.toDouble());
          return false;
        }
      }
      ws.split = new double[isomers.size()];
      ws.split[i++] = 1.0;
    }
    return true;
  }

  /**
   * Create reverse lookup list for gammas.
   *
   * ENSDF only stores the level that a gamma leaves from. W We need to add a
   * reverse lookup so we know everything that enters a level.
   */
  void collectGammas()
  {
    // Next we need to allocate bookkeeping information for each level and each emission type.
    // Collect all of the gammas that end on a level
    for (EnsdfGamma gamma : dataSet.collectGammas())
    {
      EmissionWorkspace ws = this.getEmissionWorkspace(gamma);
      if (ws.destination != null)
      {
        getLevelWorkspace(ws.destination).incomingGamma.add(ws);
      } else
      {
        System.out.println("WARNING:" + dataSet.identification.DSID+ " Gamma without destination " + gamma.E + " " + gamma.level);
      }
    }

    // Update the totals for every level
    for (LevelWorkspace entry : this.levelWorkspaceMap.values())
    {
      entry.computeTotals(this);
    }
  }

  /**
   * Working from the lowest level determine the fraction that is destine for
   * the ground state and each isomer state. *
   */
  void visitAll(Predicate<EnsdfLevel> consumer)
  {
    queue.addAll(dataSet.levels);
    EnsdfLevel last = null;
    while (!queue.isEmpty())
    {
      EnsdfLevel first = queue.remove();
      if (first==last)
          throw new RuntimeException("Stuck in visitAll cycle : "+first);
      if (!consumer.test(first))
        queue.add(first);
      last = first;
    }
  }
//</editor-fold>
//<editor-fold desc="backward" defaultstate="collapsed">

  /**
   * Process a level in the backward direction.
   *
   * If all of the outgoing information is available on the gammas, then we can
   * assign the destination fraction for each state.
   *
   * @param level
   * @return
   */
  private boolean visitBackward(EnsdfLevel level)
  {
     LevelWorkspace ws = getLevelWorkspace(level);
     // We have already visited this
    if (ws.done)
      return true;
    double[] split = ws.split;
    if (split == null)
    {
      // If any of the outgoing have not been labeled then throw back in queue
      for (EnsdfGamma gamma : level.gamma)
      {
        if (getEmissionWorkspace(gamma).split == null)
          return false;
      }
      // Okay everything had been assigned, so we next need to compute the weighted
      // average of all of the states entering this
      split = new double[isomers.size()];
      for (EnsdfGamma gamma : level.gamma)
      {
        EmissionWorkspace ws2 = getEmissionWorkspace(gamma);
        double[] gsplit = ws2.split;
        double factor = ws2.probablity / ws.totalOut;
        for (int i = 0; i < split.length; ++i)
          split[i] += gsplit[i] * factor;
      }
      ws.split = split;
    }

    // Verify nothing is NaN
    assertNotNaN(split);

    assignIncoming(level, split);
    ws.done = true;
    return true;
  }

  /**
   * Fill out all the splits for emissions that enter a level.
   *
   * @param level
   * @param split
   */
  private void assignIncoming(EnsdfLevel level, double[] split)
  { 
    if (split == null)
      throw new NullPointerException();
    // Assign the same split to all incoming emissions
    level.alpha.forEach((p) -> getEmissionWorkspace(p).split = split.clone());
    level.beta.forEach((p) -> getEmissionWorkspace(p).split = split.clone());
    level.ec.forEach((p) -> getEmissionWorkspace(p).split = split.clone());
    // Assign the same split to incoming gamma
    getLevelWorkspace(level).incomingGamma.stream().forEach((p) -> p.split = split.clone());
  }
  //</editor-fold>
//<editor-fold desc="forward" defaultstate="collapsed">

  void initializeForward()
  {
    // Clear done flags
    emissionWorkspaceMap.values().stream().forEach((p) -> p.done = false);

    // Mark downstream values as 0 to remove them from the branching ratios
    for (EnsdfLevel level : isomers)
      level.gamma.stream().forEach((p) ->
      {
        EmissionWorkspace ws = getEmissionWorkspace(p);
        ws.split = new double[isomers.size()];
        ws.done = true;
      });
  }

  private boolean visitForward(EnsdfLevel level)
  {
    LevelWorkspace ws = getLevelWorkspace(level);

    // Make sure all incoming are assigned
    for (EmissionWorkspace gamma : ws.incomingGamma)
    {
      if (!gamma.done)
        return false;
    }

    assertNotNaN(ws.split);

    // Some of the gamma inputs may have been zeroed.
    //   This happens when a gamma was recorded leaving from an isomer state.
    double[] tally = new double[this.isomers.size()];
    if (ws.totalIn == 0)
      tally = ws.split;
    else
      for (int i = 0; i < tally.length; ++i)
        tally[i] += ws.split[i] * (ws.totalIn - ws.totalGammaIn) / ws.totalIn;
    
    // Check for NaN
    assertNotNaN(tally);
    
    if (ws.totalIn > 0) {
      for (EmissionWorkspace emissionWorkspace : ws.incomingGamma) {
        double t = emissionWorkspace.probablity;
        for (int i = 0; i < tally.length; ++i) {
          tally[i] += emissionWorkspace.split[i] * t / ws.totalIn;
        }
      }
    }
    for (int i = 0; i < ws.split.length; ++i)
    {
      if (tally[i] == ws.split[i])
        continue;

      if (ws.split[i] == 0)
      {
        System.out.println("WARNING:" + dataSet.identification.DSID+ " Serious balance issue");
        continue;
      }
      double f = tally[i] / ws.split[i];
      if (Double.isNaN(f))
        throw new RuntimeException("NaN in visitForward" + tally[i] + " " + ws.split[i]);
      for (EnsdfGamma gamma : level.gamma)
      {
        EmissionWorkspace emissionWorkspace = getEmissionWorkspace(gamma);
        emissionWorkspace.split[i] *= f;
      }
    }
    for (EnsdfGamma gamma : level.gamma)
    {
      getEmissionWorkspace(gamma).done = true;
    }
    return true;
  }

//</editor-fold>
//<editor-fold desc="ensdf" defaultstate="collapsed">
  /**
   * Get the probability for transitioning by gamma or internal conversion.
   *
   * ENSDF stores this information in a number of different methods with
   * different normalizations. Usually only one method is specified.
   *
   * Gamma transitions can have serious problems whenever the RI specified is
   * very low. In such a case the transition is most certainly going to be
   * internal, but the internal field is stored as a ratio of the gamma
   * transition. Thus you get a gamma transition of 1e-9 times a probability of
   * internal of 1e10.
   *
   * FIXME this code should likely be located in the EnsdfGamma class.
   *
   * @param gamma
   * @return
   */
  private double getGammaProbability(EnsdfGamma gamma)
  {
    double total = 0;
      if (gamma.TI.isSpecified()) {
          if (this.normalizationTransition == -1) {
              System.out.println("WARNING:" + dataSet.identification.DSID+ " record without transition normalization specified");
              this.normalizationTransition = 1;
          }
          total = gamma.TI.toDouble() * this.normalizationTransition;
      } else
    {
      if (gamma.RI.isSpecified())
      {
        // Add internal conversion probability
        double cc = 0;
        if (gamma.CC.isSpecified())
          cc = gamma.CC.toDouble();
        if (this.normalizationGamma == -1) {
            System.out.println("WARNING:" + dataSet.identification.DSID+ " record without gamma normalization specified");
            this.normalizationGamma = 1;
        }
        total += gamma.RI.toDouble() * this.normalizationGamma * this.branchingRatio * (1 + cc);
      } else
        System.out.println("WARNING:" + dataSet.identification.DSID+ " Energy " + gamma.E.toDouble() + ": RI not specified");
    }
    return total / 100; // ENSDF stores in per 100 of parent decays
  }

  /**
   * Get the probability of an alpha.
   *
   * ENSDF does not appear to have a defined normalization factor for alpha
   * emissions, but that may be a misinterpretation.
   *
   * @param alpha
   * @return
   */
  private double getAlphaTransition(EnsdfAlpha alpha)
  {
    if (alpha.IA.isSpecified())
      return alpha.IA.toDouble() / 100.0;
    System.out.println("WARNING:" + dataSet.identification.DSID+ " alpha without intensity " + alpha.E);
    return 0;
  }

  /**
   * Get the probability of a beta.
   *
   * @param beta
   * @return
   */
  private double getBetaTransition(EnsdfBeta beta)
  {
    if (this.normalizationBeta == -1)
    {
      System.out.println("WARNING:" + dataSet.identification.DSID+ " Beta without NB specified");
      this.normalizationBeta = 1;
    }
    if (beta.IB.isSpecified())
      return beta.IB.toDouble() * this.normalizationBeta * this.branchingRatio / 100.0;
    System.out.println("WARNING:" + dataSet.identification.DSID+ " beta without intensity " + beta.E);
    return 0;
  }

  /**
   * Get the probability of a electron capture.
   *
   * @param ec
   * @return
   */
  private double getEcTransition(EnsdfElectronCapture ec)
  {
    if (this.normalizationBeta == -1)
    {
      System.out.println("WARNING:" + dataSet.identification.DSID+ " record missing NB specification");
      this.normalizationBeta = 1;
    }
    double total = 0;
    if (ec.TI.isSpecified())
      total = ec.TI.toDouble() * this.normalizationBeta * this.branchingRatio / 100.0;

    if (total == 0)
    {
      if (ec.IB.isSpecified())
        total += ec.IB.toDouble() * this.normalizationBeta * this.branchingRatio / 100.0;
      if (ec.IE.isSpecified())
        total += ec.IE.toDouble() * this.normalizationBeta * this.branchingRatio / 100.0;
    }

    if (total == 0)
      System.out.println("WARNING:" + dataSet.identification.DSID+ " electron capture without intensity " + ec.E);
    return total;
  }

  //</editor-fold>
//<editor-fold desc="post" defaultstate="collapsed">
  private void updateBranchingRatios()
  {
    double br2 = 0;
    for (EnsdfLevel isomerLevel : isomers)
    {
      double d = 0;
      LevelWorkspace levelWorkspace = getLevelWorkspace(isomerLevel);
      int index = levelWorkspace.splitIndex;
      for (EmissionWorkspace emissionWorkspace : levelWorkspace.incomingOther)
      {
        d += emissionWorkspace.probablity * emissionWorkspace.split[index];
      }
      for (EmissionWorkspace emissionWorkspace : levelWorkspace.incomingGamma)
      {
        d += emissionWorkspace.probablity * emissionWorkspace.split[index];
      }
      if (Double.isNaN(d))
        throw new RuntimeException("NaN in update branching ratios");
      levelWorkspace.branchingRatio = d;
      br2 += d;
      index++;
    }

    for (EnsdfLevel level : isomers)
    {
      LevelWorkspace ws2 = getLevelWorkspace(level);
      if (br2 > 0)
        ws2.branchingRatio *= branchingRatio / br2;
      if (Double.isNaN(ws2.branchingRatio))
        throw new RuntimeException("Error in branching ratio " + br2);
    }
  }

  /**
   * Unassigned levels will be allocated based on the branching ratio.
   */
  private void allocateUnassigned()
  {
    double[] split = new double[this.isomers.size()];
    if (!options.contains(Option.EXCLUDE_UNASSIGNED))
    {
      double total = 0;
 
      if (this.isomers.size() == 1) {
        split[0] = 1;
      } else {
        for (EnsdfLevel level : this.isomers) {
          LevelWorkspace ws = this.getLevelWorkspace(level);
          total += ws.branchingRatio;
          split[ws.splitIndex] = ws.branchingRatio;
        }

        for (int j = 0; j < split.length; ++j) {
          split[j] /= total;
        }
      }

    }
    // Use the split to assign all of the unassigned radiation
    dataSet.unassigned.gamma.stream().forEach(p -> this.getEmissionWorkspace(p).split = split);
    dataSet.unassigned.beta.stream().forEach(p -> this.getEmissionWorkspace(p).split = split);
    dataSet.unassigned.alpha.stream().forEach(p -> this.getEmissionWorkspace(p).split = split);
    dataSet.unassigned.ec.stream().forEach(p -> this.getEmissionWorkspace(p).split = split);
  }
//</editor-fold>
//<editor-fold desc="internal" defaultstate="collapsed">

  private DecayTransitionImpl convertIsomer(EnsdfLevel isomer)
  {
    HashMap<EnsdfEmission, Emission> included = new HashMap<>();
    LevelWorkspace ws = this.getLevelWorkspace(isomer);
    int splitIndex = ws.splitIndex;
    double br = ws.branchingRatio;
    // Need to account for possiblity that nothing reached this isomer
    if (br <= 0 && this.isomers.size() > 1)
      return null;
    if (br == 0 && this.isomers.size() == 1) {
      ws.branchingRatio = this.branchingRatio;  // Assign all emissions to the only possible isomer
      br = ws.branchingRatio;
    }
    
    System.out.println(this.parent.nuclide+" "+ws.nuclide);
    // Set up an API stub
    DecayTransitionImpl dt = new DecayTransitionImpl(dataSet,
            this.parent.nuclide,
            ws.nuclide,
            ws.branchingRatio);

    // with LNHB we can get the xray from the comment section.
    // for BNL we need to mine the kshell vacancy information and use the florescence yield
    // Collect all the gammas
    for (EnsdfGamma gamma : dataSet.collectGammas())
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(gamma);
      if (!gamma.RI.isSpecified())
      {
        System.out.println("No relative intensity on Gamma " + gamma.E.field);
        continue;
      }
      double intensity = this.normalizationGamma * this.branchingRatio * gamma.RI.toDouble() / 100.0;
      double probability = ws2.split[splitIndex] * intensity / br;
      if (probability <= 0)
        continue;

      GammaImpl gi = new GammaImpl(dt, gamma, probability);
      dt.emissions.add(gi);

      // Add to list of correlating gammas
      included.put(gamma, gi);
    }

    convertAlphas(dt, ws, dataSet.collectAlphas());
    convertBetas(dt, ws, dataSet.collectBetas());
    convertElectronCaptures(dt, ws, dataSet.collectCaptures());

    // For BNL data we need to compute the vacancies and allocate them correctly.
    if (xrayLibrary != null)
    {
      convertXrays(dt);
    } else if (xrayComments)
    {

    }

    solveGammaCorrelations(dt, included);

    // FIXME add auger electrons if requested
    return dt;
  }

  private void solveGammaCorrelations(DecayTransitionImpl dt,
          Map<EnsdfEmission, Emission> included)
  {
    HashMap<Emission, List<EmissionCorrelation>> tmp = new HashMap<>();

    // This only solves for single level correlations 
    // We have to work from bottom to top
    for (EnsdfLevel level : this.dataSet.levels)
    {
      LevelWorkspace wsl = this.getLevelWorkspace(level);
      for (EmissionWorkspace incomingGamma : wsl.incomingGamma)
      {
        // Skip adding a correlating for the wrong decay
        if (!included.containsKey(incomingGamma.emission))
          continue;

        // Add to list
        List<EmissionCorrelation> out = new ArrayList<>();
        Emission e1 = included.get(incomingGamma.emission);
        tmp.put(e1, out);
        HashMap<Emission, Double> secondary = new HashMap<>();

        for (EnsdfGamma outgoingGamma : level.gamma)
        {
          LevelWorkspace w3 = this.getLevelWorkspace(level);
          if (!included.containsKey(outgoingGamma))
            continue;
          Emission e2 = included.get(outgoingGamma);
          if (!outgoingGamma.RI.isSpecified())
            continue;
          double correlation = (outgoingGamma.RI.toDouble() * this.normalizationGamma * this.branchingRatio)
                  / w3.totalOut / 100.0;
          EmissionCorrelationImpl ec = new EmissionCorrelationImpl(e1, e2, correlation);
          dt.correlations.add(ec);
          out.add(ec);

          // FIXME assumes that all correlations are independent
          if (tmp.containsKey(e2))
          {
            for (EmissionCorrelation u : tmp.get(e2))
            {
              Emission m = u.getSecondary();
              if (secondary.containsKey(m))
                secondary.put(m, secondary.get(m) + correlation * u.getProbability());
              else
                secondary.put(m, correlation * u.getProbability());
            }
          }
        }
        for (Map.Entry<Emission, Double> e : secondary.entrySet())
        {
          EmissionCorrelationImpl ec2 = new EmissionCorrelationImpl(e1,
                  e.getKey(), e.getValue());
          dt.correlations.add(ec2);
          out.add(ec2);
        }
      }
    }
  }

  private void convertBetas(DecayTransitionImpl dt, LevelWorkspace ws, List<EnsdfBeta> betas)
  {
    int splitIndex = ws.splitIndex;
    double br = ws.branchingRatio;

    // The total of all betas for a transtion should sum to 1.
    double betaTotal = 0;
    for (EnsdfBeta beta : betas)
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(beta);
      double probability;
      if (betas.size() == 1) {   // if this is the only beta, assume 100%
        probability = 1.;
        ws2.probablity = br;
      }
      else {
        probability = ws2.split[splitIndex] * ws2.probablity / br;
      }
      if (probability <= 0)
        continue;
      betaTotal += probability;
    }

    // FIXME add checks here if betaTotal is way off as it may indicate a problem with the data.
    for (EnsdfBeta beta : betas)
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(beta);
      double probability = ws2.split[splitIndex] * ws2.probablity / br;
      if (probability <= 0)
        continue;
      dt.emissions.add(new BetaImpl(dt, beta, probability / betaTotal));
    }
  }

  private void convertAlphas(DecayTransitionImpl dt, LevelWorkspace ws, List<EnsdfAlpha> alphas)
  {
    int splitIndex = ws.splitIndex;
    double br = ws.branchingRatio;

    double alphaTotal = 0;
    for (EnsdfAlpha alpha : alphas)
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(alpha);
      double probability;
      if (alphas.size() == 1) {   // if this is the only alpha, assume 100%
        probability = 1.;
        ws2.probablity = br;
      }
      else {
        probability = ws2.split[splitIndex] * ws2.probablity / br;
      }
      if (probability <= 0)
        continue;
      alphaTotal += probability;
    }
    
    // FIXME add checks here if alphaTotal is way off as it may indicate a problem with the data.
    for (EnsdfAlpha alpha : alphas)
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(alpha);
      double probability = ws2.split[splitIndex] * ws2.probablity / br;
      if (probability <= 0)
        continue;
      dt.emissions.add(new AlphaImpl(dt, alpha, probability / alphaTotal));
    }
  }

  private void convertElectronCaptures(DecayTransitionImpl dt, LevelWorkspace ws, List<EnsdfElectronCapture> collectCaptures)
  {
    int splitIndex = ws.splitIndex;
    double br = ws.branchingRatio;

    double captureTotal = 0;
    for (EnsdfElectronCapture ec : collectCaptures)
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(ec);
      double probability;
      if (collectCaptures.size() == 1) {   // if this is the only EC, assume 100%
        probability = 1.;
        ws2.probablity = br;
      }
      else {
      probability = ws2.split[splitIndex] * ws2.probablity / br;
      }
      if (probability <= 0)
        continue;
      captureTotal += probability;
    }

    // FIXME add checks here captureTotal is way off as it may indicate a problem 
    // with the data.
    for (EnsdfElectronCapture ec : collectCaptures)
    {
      EmissionWorkspace ws2 = this.getEmissionWorkspace(ec);
      double probability = ws2.split[splitIndex] * ws2.probablity / br / captureTotal;
      if (probability <= 0)
        continue;

      // Split to get the B+ fraction only 
      double f1 = 0;
      double f2 = 0;
      if (ec.IB.isSpecified())
        f1 = ec.IB.toDouble();
      if (ec.IE.isSpecified())
        f2 = ec.IE.toDouble();

      double intensity1 = probability * f2 / (f1 + f2);
      double intensity2 = probability * f1 / (f1 + f2);
      if (intensity1 > 0)
        dt.emissions.add(new ElectronCaptureImpl(dt, ec, probability * f2 / (f1 + f2)));
      if (intensity2 > 0)
        dt.emissions.add(new PositronImpl(dt, ec, probability * f1 / (f1 + f2)));
    }
  }

  private void convertXrays(DecayTransitionImpl dt)
  {
    double kv = 0;
    double lv = 0;

    for (Emission emission : dt.emissions)
    {
      if (emission instanceof GammaImpl)
      {
        GammaImpl gamma = (GammaImpl) emission;
        double totalTransition = this.getGammaProbability(gamma.record);
        Map<String, EnsdfQuantity> continuation = gamma.record.continuation;

        // direct conversion
        if (continuation.containsKey("KC"))
        {
          EnsdfQuantity KC = continuation.get("KC");
          kv += KC.toDouble() * gamma.getIntensity().getValue();
        } // Ratio to total
        else if (continuation.containsKey("K/T"))
        {
          EnsdfQuantity KT = continuation.get("K/T");
          kv += KT.toDouble() * totalTransition;
        }

        if (continuation.containsKey("LC"))
        {
          EnsdfQuantity LC = continuation.get("LC");
          lv += LC.toDouble() * gamma.intensity.getValue();
        }
        else if (continuation.containsKey("L/T"))
        {
          EnsdfQuantity LT = continuation.get("L/T");
          lv += LT.toDouble() * totalTransition;
        }   
      }

      if (emission instanceof ElectronCaptureImpl)
      {
        ElectronCaptureImpl ec = (ElectronCaptureImpl) emission;
        Map<String, EnsdfQuantity> continuation = ec.record.continuation;
        if (continuation.containsKey("CK"))
        {
          EnsdfQuantity CK = continuation.get("CK");
          kv += CK.toDouble() * ec.getIntensity().getValue();
        }
        if (continuation.containsKey("CL"))
        {
          EnsdfQuantity CL = continuation.get("CL");
          lv += CL.toDouble() * ec.getIntensity().getValue();
        }
      }
    }
    
    // Now that we have all the vacances use the Xray data to get it.
    XrayData xrayData = xrayLibrary.get(dt.getChild().getElement());
    if (xrayData!=null) 
    {
      XrayEdge Kedge = xrayData.findEdge("K");
      if (kv > 0 && Kedge != null)
      {
        double yield = Kedge.getFluorescenceYield();
        for (Xray xray : Kedge.getXrays())
        {
          dt.emissions.add(new XrayImpl(dt, xray, yield * kv));
          // FIXME this may create vacances in other levels.  
        }
      }

      for (XrayEdge edge : xrayData.getEdges()) {
        if (lv > 0 && edge.getName().startsWith("L")) {
          double yield = edge.getFluorescenceYield();
          for (Xray xray : edge.getXrays())
            dt.emissions.add(new XrayImpl(dt, xray, yield * lv));
        }
      }
    }
    // FIXME L is complicated because we need to know which oribital the vacance was created in.
    // VC: temporary implementation. Validation required.
  }

   class EmissionWorkspace
  {

    EnsdfEmission emission;
    double[] split = null;
    boolean done = false;
    double probablity;
    private EnsdfLevel destination;

    EmissionWorkspace(EnsdfEmission emission, double probability)
    {
      this.emission = emission;
      this.probablity = probability;
    }
  }

   class LevelWorkspace
  {

    final EnsdfLevel level;
    double[] split;
    boolean done = false;

    List<EmissionWorkspace> incomingGamma = new ArrayList<>();
    List<EmissionWorkspace> incomingOther = new ArrayList<>();

    double totalIn = 0;
    double totalGammaIn = 0;
    double totalOut = 0;
    double branchingRatio;
    private Nuclide nuclide;
    private int splitIndex = -1;

    LevelWorkspace(EnsdfLevel level, SplitIsomers ws)
    {
      this.level = level;
      this.level.alpha.stream().map(ws::getEmissionWorkspace).forEach(incomingOther::add);
      this.level.beta.stream().map(ws::getEmissionWorkspace).forEach(incomingOther::add);
      this.level.ec.stream().map(ws::getEmissionWorkspace).forEach(incomingOther::add);
    }

    /**
     * Compute the total incoming and outgoing for each level.
     *
     * We hope this is balanced.
     *
     * @param level
     * @param ws
     */
    void computeTotals(SplitIsomers ws)
    {
      // Add gammas to the outgoing
      for (EnsdfGamma gamma : level.gamma)
      {
        totalOut += ws.getEmissionWorkspace(gamma).probablity;
      }

      // FIXME we do not currently consider decays from the isomer
      // other than internal.   We need to find a good example in which the 
      // isomer does something other than an internal decay.
      // Sum up all possible incoming 
      for (EmissionWorkspace gamma : this.incomingGamma)
      {
        totalIn += gamma.probablity;
      }
      totalGammaIn = totalIn;

      for (EmissionWorkspace other : this.incomingOther)
      {
        totalIn += other.probablity;
      }

      if (totalOut == 0 && !ws.isomers.contains(level))
      {
        System.out.println("WARNING:" + dataSet.identification.DSID+ " level with no output " + level.E.field);
        split = new double[ws.isomers.size()];
      }

      // This would represent a different way to exit the state. 
      // FIXME Wrong this is entering the state not leaving.
      if (!level.particles.isEmpty())
        throw new RuntimeException("Not supported");

      // The total in and out should balance, unless the level is an isomer
      if (!ws.isomers.contains(level))
      {
        double balance = 2 * Math.abs(totalIn - totalOut) / (totalIn + totalOut);
        if (balance > 0.05)
        {
          System.out.println("WARNING:" + dataSet.identification.DSID+ " Unbalanced level " + level.E.field + ":" + level.T + " in=" + totalIn + " out=" + totalOut + " " + balance);
        }
      }
    }

  }

  private void assertNotNaN(double[] values)
  {
    for (int i = 0; i < values.length; ++i)
      if (Double.isNaN(values[i]))
        throw new ArithmeticException();
  }

//</editor-fold>
}
