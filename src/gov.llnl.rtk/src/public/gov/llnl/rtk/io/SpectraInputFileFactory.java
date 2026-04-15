// --- file: gov/llnl/rtk/io/SpectraInputFileFactory.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

/**
 *
 * @author yao2
 */
public class SpectraInputFileFactory
{
// FIXME gut and replace
  /*
  public static SpectraInputFile openFromExtension(String extension)
  {
    String toLowerCase = extension.toLowerCase();
    //extension[i]=tolower(extension[i]);

    if ("asc".equals(toLowerCase))
    {
      return new SpectraAscInputFile();
    }
    if ("pcc".equals(toLowerCase))
    {
      throw new UnsupportedOperationException("pcc format is not supported");
    }
    if ("pcf".equals(toLowerCase))
    {
      return new SpectraPcfInputFile();
    }
    if ("spe".equals(toLowerCase))
    {
      return new SpectraSpeInputFile();
    }
    if ("chn".equals(toLowerCase))
    {
      return new SpectraChnInputFile();
    }

    return null;
  }

  static public SpectraInputFile probeFile(File file) throws FileNotFoundException, IOException
  {
    // open the file and examine the first 8 bytes for identification

    byte[] magic;
    try (DataInputStream is = new DataInputStream(new FileInputStream(file)))
    {
      magic = new byte[8];
      is.readFully(magic);
    }

    String tmpStr = new String(magic);

    // Two binary file types recognized
    if (tmpStr.substring(0, 7).equals("ZZZZ135"))
    {
      return new SpectraGr135InputFile();
    }

    if (magic[0] == 0xad && magic[1] == 0xbe && magic[2] == 0xad && magic[3] == 0x0e)
    {
      throw new UnsupportedOperationException("SORDS format is not supported.");
    }

    return null;

  } // local scope

  public static SpectraInputFile open(Path file)
          throws IOException, FileNotFoundException, InputFile.BadFormat
  {
    SpectraInputFile sif = null;
    // FIXME okay if we don't have an extension I guess we will need 
    // to autosense it.  (Not complete)
    if (file.length() < 8)
    {
      return null;
    }

    // Determine by extension
    String extension = getFileExtension(file);
    if (!extension.isEmpty())
    {
      sif = openFromExtension(extension);
    }

    // Determine by examining the file
    if (sif == null)
    {
      sif = probeFile(file);
    }

    if (sif != null)
      sif.open(file);

    return sif;
  }
   */
}
