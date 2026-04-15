// --- file: gov/llnl/rtk/response/PeakFunction.java ---
package gov.llnl.rtk.response;

/**
 *
 * @author nelson85
 */
public interface PeakFunction
{
  double apply(double energy, double center, double width);

}
