/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.llnl.rtk.response;

import gov.llnl.math.Cursor;
import gov.llnl.math.interp.SingleInterpolator;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.response.support.CubicSpline;
import gov.llnl.rtk.response.support.CubicSplineBoundary;
import gov.llnl.rtk.response.support.CubicSplineBuilder;
import java.util.Set;

/**
 *
 * @author nelson85
 */
public abstract class FunctionResponseEvaluator extends SpectralResponseEvaluatorBase
{
  final CubicSpline spline = new CubicSplineBuilder()
          .boundary(CubicSplineBoundary.NOTAKNOT)
          .create();

  // State variables
  double[] continuumX;
  double[] continuumY;

  Set<RenderItem> renderItems = RenderItem.ALL.clone();

  // Line shape functions
  final PeakFunction cdf;
  final PeakFunction ccdf;

  SpectralResponseIncomplete incomplete = new SpectralResponseIncomplete();
  Cursor edgesCursor;

  // Cache used to interpolate between SplineEntries
  final LineParameters lineParameters = new LineParameters();
  final LLDEvaluator lldEval;

  protected FunctionResponseEvaluator(SpectralResponseFunction responseFunction, EnergyScale energyScale,
          ShapeParameters params, LLDFunction lld)
  {
    super(responseFunction, energyScale);

    // Construct the peak function
    EmgCompensated emg = new EmgCompensated(params.theta, params.negativeTail, params.positiveTail);
    this.cdf = emg::cdf;
    this.ccdf = emg::ccdf;

    this.lldEval = new LLDEvaluator(lld);
    double[] edges = energyScale.getEdges();
    this.edgesCursor = new Cursor(edges, 0, edges.length);
    this.lldEval.update(this.energyScale, this.edgesCursor);
  }

  @Override
  public void setEnergyScale(EnergyScale scale)
  {
    super.setEnergyScale(scale);
    double[] edges = scale.getEdges();
    this.edgesCursor = new Cursor(edges, 0, edges.length);
    this.lldEval.update(this.energyScale, this.edgesCursor);
  }
  
  
  /**
   * @param renderItems the renderItems to set
   */
  @Override
  public void setRenderItems(Set<RenderItem> renderItems)
  {
    this.renderItems = renderItems;
  }

  @Override
  public Set<RenderItem> getRenderItems()
  {
    return this.renderItems;
  }
  
  //<editor-fold desc="continuum rendering" defaultstate="collapsed">
  /**
   * Called when adjusting the minimum number of spline points.
   *
   * @param n
   */
  void resizeContinuumSpline(int n)
  {
    if (this.continuumX == null || this.continuumX.length < n)
    {
      this.continuumX = new double[n];
      this.continuumY = new double[n];
    }
  }
 
//</editor-fold>
//<editor-fold desc="line rendering" defaultstate="collapsed"> 
  
  abstract Cursor seek(double energy);
  abstract SplineResponseEntry getEntry(int i);
  
  @Override
  public void renderLine(SpectralBuffer buffer, double energy, double intensity)
  {
    // Truncate if intensity is 0
    if (intensity <= 0)
      return;
    if (energy <= 10.0)
      return;

    buffer.set(this);

    // Apply distance correction
    intensity *= this.getGeometryFactor();

    // Find the nearest entries
    SplineResponseEntry entry1;
    SplineResponseEntry entry2;
    double fraction, fraction2;

    // Figure out what entries we are dealing with
    {
      Cursor cursor = this.seek(energy);
      int i = cursor.seek(energy);
      fraction = cursor.getFraction();

      entry1 = this.getEntry(i);
      entry2 = this.getEntry(i + 1);
    }
    
    fraction2 = fraction;
    if (fraction2 > 1)
      fraction2 = 1;

    // Make sure to tell the rendering code about the LLD if requested
    if (renderItems.contains(RenderItem.LLD))
    {
      if (this.lldEval.lldChannel==-1)
        this.lldEval.update(this.energyScale, this.edgesCursor);
    }
    else
    {
      this.lldEval.lldChannel = -1;
    }

    // Add continuumValues first
    if (renderItems.contains(RenderItem.CONTINUUM))
    {
      // Render from below
      entry1.continuum.render(buffer, this, energy, (1 - fraction2) * intensity);
      // Render from above
      entry2.continuum.render(buffer, this, energy, fraction2 * intensity);
    }

    // Add requested lines
    addLine(buffer, intensity, RenderItem.PHOTOELECTRIC, fraction, entry1.photoelectric, entry2.photoelectric);
    addLine(buffer, intensity, RenderItem.ESCAPE_SINGLE, fraction, entry1.singleEscape, entry2.singleEscape);
    addLine(buffer, intensity, RenderItem.ESCAPE_DOUBLE, fraction, entry1.doubleEscape, entry2.doubleEscape);

    SpectralResponseDeferred deferred = (SpectralResponseDeferred) buffer.deferred;
    if (this.renderItems.contains(RenderItem.ANNIHILATION))
    {
      lineParameters.computeLineParameters(fraction, entry1.annihilation, entry2.annihilation);
      deferred.add(lineParameters.center, lineParameters.amplitude * intensity, lineParameters.width);
    }

    // Add undefined peaks (xray, xray-escape)
    //   FIXME assumes all extra information is the same length and order in each
    //   entry.  Likely should be verified by builder.
    for (int j = 0; j < entry1.peaks.length; ++j)
    {
      RenderItem type = entry1.peaks[j].type;
      if (renderItems.contains(type))
      {
        if (entry1.peaks.length != entry2.peaks.length)
          entry2 = entry1;
        lineParameters.computeLineParameters(fraction, entry1.peaks[j], entry2.peaks[j]);
        if (type == RenderItem.XRAY_SCATTER)
          deferred.add(lineParameters.center, lineParameters.amplitude * intensity, lineParameters.width);
        else
          SplineResponseLine.render(buffer, this, intensity, lineParameters);
      }
    }

    buffer.checkNaN();
  }
  
  private void addLine(SpectralBuffer buffer, double intensity, RenderItem item,
          double fraction, SplineResponseLine entry1, SplineResponseLine entry2)
  {
    // Check to see if this feature should be rendered
    if (!renderItems.contains(item))
      return;

    // Compute the parameters associated with this line from the left and right entry
    lineParameters.computeLineParameters(fraction, entry1, entry2);

    // If the resulting amplitude is zero then we can skip rendering
    if (lineParameters.amplitude <= 0)
      return;

    // Render the line
    SplineResponseLine.render(buffer, this, intensity, lineParameters);

    // If there is also incomplete charge capture add it as well.
    //   (This is a bit of a complexity as the incomplete capture is more like
    //    a continuum feature rather than a line feature, but the parameters
    //    for the interpolation are based off of line.)
    if (incomplete.intensity > 0)
      incomplete.render(buffer, this, intensity, lineParameters.amplitude, lineParameters.center);
  }
//</editor-fold>
  
  
  @Override
  public void finish(SpectralBuffer buffer)
  {
    // Apply any lines that we have deferred
    SpectralResponseDeferred deferred = (SpectralResponseDeferred) buffer.deferred;
    deferred.apply(buffer, this);

    // ENHANCEMENT if we adjust the peak parameters then that should be reflected
    // on the continuum as well.  Currently the continuum is preresponsed
    // with the peak shape and tail parameters.
    // ENHANCEMENT we need to add the internal source if requested here.
    // ENHANCEMENT the internal source should be stored as a flux so that we
    // can apply peak function changes.
  }

}
