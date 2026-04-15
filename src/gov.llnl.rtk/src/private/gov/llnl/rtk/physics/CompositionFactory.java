// --- file: gov/llnl/rtk/physics/CompositionFactory.java ---
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.annotation.Internal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author nelson85
 */
@Internal
public class CompositionFactory
{
  public static Composition createFromString(String desc)
  {
    CompositionImpl out = new CompositionImpl();
    for (String part : desc.split("\\+"))
    {
      // Remove any extra whitespace
      part = part.trim();
      Pattern pattern = Pattern.compile("([A-z0-9]+)\\s*,\\s*([0-9.]+)([A-z]+)\\s*(\\{[0-9.]+,[0-9.]+\\})?");
      Matcher matcher = pattern.matcher(part);
      if (!matcher.matches())
        throw new RuntimeException("Unable to parse component " + part);

      Nuclide nuclide = Nuclides.get(matcher.group(1));
      String unit = matcher.group(3);
      Quantity activity = Quantity.of(Double.parseDouble(matcher.group(2)), unit);
      out.add(nuclide, 1, activity);
    }
    return out;
  }

}
