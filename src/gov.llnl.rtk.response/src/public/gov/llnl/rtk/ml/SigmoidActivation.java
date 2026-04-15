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
public class SigmoidActivation extends ActivationFunction
{
  @Override
  public double applyAsDouble(double x)
  {
    if (x >= 0)
      return 1.0 / (1.0 + Math.exp(-x));
    else
    {
      double ex = Math.exp(x);
      return ex / (1.0 + ex);
    }
  }

  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this);
  }

  @Override
  public boolean equals(Object o)
  {
    return (o instanceof SigmoidActivation);
  }

}
