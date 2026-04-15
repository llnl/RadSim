// --- file: gov/llnl/rtk/ml/Utility.java ---
package gov.llnl.rtk.ml;

/**
 *
 * @author nelson85
 */
class Utility
{
  // --- Utility methods ---
  /**
   * Adds two vectors elementwise.
   */
  public static void addAssign(double[] a, double[] b, int s, int e)
  {
    for (int i = 0; i < (e - s); ++i)
      a[i] += b[i + s];
  }

}
