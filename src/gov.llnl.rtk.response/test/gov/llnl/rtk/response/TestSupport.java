/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.flux.FluxEncoding;
import gov.llnl.utility.HashUtilities;
import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoEncoding;
import gov.llnl.utility.proto.ProtoException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Base64;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class TestSupport
{

  public static <T> T loadResource(String resource, MessageEncoding<T> encoding)
  {
    try (final InputStream is = TestSupport.class.getClassLoader()
            .getResourceAsStream(resource))
    {
      if (is == null)
        throw new RuntimeException("Unable to locate resource " + resource);
      return encoding.parseStreamGZ(is);
    }
    catch (ProtoException | IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static String md5Checksum(byte[] b)
  {
    return HashUtilities.byteArrayToHexString(HashUtilities.hash(b));
  }

  @Test
  void testResources()
  {
//    loadFluxResource("flux.bin");
    loadResource("gov/llnl/rtk/resources/fluxBinned.bin", FluxEncoding.getInstance());
//    loadFluxResource("fluxSpectrum.bin");
//    loadFluxResource("fluxTrapezoid.bin");
//    loadFluxResource("results/toSpectrum.bin");
//    loadFluxResource("results/toTrapezoid.bin");
  }

  static double[] base64DecodeToDoubles(String str)
  {
    try
    {
      ByteBuffer b1 = ByteBuffer.wrap(str.getBytes(UTF_8));
      ByteBuffer b2 = Base64.getDecoder().decode(b1);
      return ProtoEncoding.Type.NetworkDoubles.parseBytes(b2.array());
    }
    catch (ProtoException ex)
    {
      throw new RuntimeException(ex);
    }
  }

}
