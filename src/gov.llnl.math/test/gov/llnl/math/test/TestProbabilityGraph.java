/*
 * Copyright 2021, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.test;

import gov.llnl.math.graph.GraphQuery;
import gov.llnl.math.graph.ProbabilityGraph;
import gov.llnl.math.graph.ProbabilityGraphReader;
import gov.llnl.math.graph.QueryLookupInterface.ListQueryLookup;
import gov.llnl.math.graph.ViterbiProbabilityGraph;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.DocumentReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 * @author nelson85
 */
public class TestProbabilityGraph
{
  public static void main(String[] args) throws ReaderException, IOException
  {
    ProbabilityGraphReader reader = new ProbabilityGraphReader();
    DocumentReader<ProbabilityGraph> pgr = DocumentReader.create(reader);
    pgr.setProperty(ProbabilityGraphReader.DEFAULT_IDENTIFIER+".queryIdMap", 
            new ListQueryLookup("fred", "george", "bob"));
    pgr.setProperty(ProbabilityGraphReader.DEFAULT_IDENTIFIER+".conditionIdMap", 
            new ListQueryLookup("sally"));
    
    ProbabilityGraph pg = pgr.loadFile(Paths.get("graph.xml"));
    pg.dump(System.out);

    double f = 0.2;
    double p1;
    GraphQuery query = pg.allocateQuery();
    query.setTrue(1);
    p1 = pg.computeProbability(query);
    query.setCondition(0, 0);
    p1 = pg.computeProbability(query);
    query.setCondition(0, 1);
    p1 = pg.computeProbability(query);
    query.clearConditions();
    query.setTrue(2);
    double p2 = pg.computeProbability(query);
    query.clear();
    query.setTrue(1);
    query.setPartial(2, f);
    double p3 = pg.computeProbability(query);
    ViterbiProbabilityGraph vpg = new ViterbiProbabilityGraph();
    ViterbiProbabilityGraph.Output vpgo = new ViterbiProbabilityGraph.Output();
    vpg.propogate(vpgo, pg, query);
  }
}
