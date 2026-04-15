// --- file: gov/llnl/rtk/response/sim/SpeedTest.java ---
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.llnl.rtk.response.sim;

/**
 *
 * @author nelson85
 */
public class SpeedTest
{
  public static  void main(String[] args)
  {
    CuboidSim cs = new CuboidSim();
    BatchManager bm = new BatchManager(cs.runDirect);
    bm.initial();
    System.out.println("fill");
    bm.fill();
    for (int i =0; i<10; i++)
    {
      System.out.println("fetch");
      bm.fetch(1000);
    }
  }
}
