/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gov.llnl.rtk.ml;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class SqrtPlusActivation extends ActivationFunction
{
  final double k;

  public SqrtPlusActivation()
  {
    this.k = 1;
  }

  public SqrtPlusActivation(double k)
  {
    this.k = k;
  }

  @Override
  public double applyAsDouble(double x)
  {
    return 0.5 * (x + Math.sqrt(x * x + k));
  }
  
  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this);
  }
}
