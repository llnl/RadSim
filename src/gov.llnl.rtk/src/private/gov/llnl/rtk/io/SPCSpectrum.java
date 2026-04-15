// --- file: gov/llnl/rtk/io/SPCSpectrum.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.utility.annotation.Internal;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author yao2
 */
@Internal
class SPCSpectrum
{
  short inftyp;     // 01
  short filtyp;     // 02
  short flags;      // 03 
  short resv1;      // 04
  short acqirp;     // 05
  short samdrp;     // 06
  short detrrp;     // 07
  short ebrdesc;    // 08
  short anarp1;     // 09
  short anarp2;     // 10
  short anarp3;     // 11
  short anarp4;     // 12
  short srpdes;     // 13
  short ieqdesc;    // 14
  short geodes;     // 15
  short mpcdesc;    // 16
  short caldes;     // 17
  short calrp1;     // 18
  short calrp2;     // 19
  short effprp;     // 20
  short roirp1;     // 21
  short ptr_energy; // 22
  short num_energy; // 23
  short resv2;      // 24
  short deconvolve; // 25
  short units;      // 26  true = microcuries, false = Bq
  short perptr;     // 27
  short maxrcs;     // 28
  short lstrec;     // 29
  short effpnm;     // 30
  short spctrp;     // 31
  short spcrcn;     // 32
  short spcchn;     // 33
  short abstch;     // 34
  double acqtim;     // 35, 36
  double acqti8;     // 37, 38, 39, 40
  short seqnum;     // 41
  short mcanu;      // 42
  short segnum;     // 43
  short mcadvt;     // 44
  short chnsrt;     // 45
  double rltmdt;     // 46, 47
  double lvtmdt;     // 48, 49
  short resv[] = new short[15];   // 50-64

  void readspc(String filename, SPCSpectrum spec) throws FileNotFoundException, IOException
  {
    FileReader file = new FileReader(filename);
    BufferedReader reader = new BufferedReader(file);

    spec.inftyp = Short.parseShort(reader.readLine());
    spec.filtyp = Short.parseShort(reader.readLine());
    spec.flags = Short.parseShort(reader.readLine());
    spec.resv1 = Short.parseShort(reader.readLine());
    spec.acqirp = Short.parseShort(reader.readLine());
    spec.samdrp = Short.parseShort(reader.readLine());
    spec.detrrp = Short.parseShort(reader.readLine());
    spec.ebrdesc = Short.parseShort(reader.readLine());
    spec.anarp1 = Short.parseShort(reader.readLine());
    spec.anarp2 = Short.parseShort(reader.readLine());
    spec.anarp3 = Short.parseShort(reader.readLine());
    spec.anarp4 = Short.parseShort(reader.readLine());
    spec.srpdes = Short.parseShort(reader.readLine());
    spec.ieqdesc = Short.parseShort(reader.readLine());
    spec.geodes = Short.parseShort(reader.readLine());
    spec.mpcdesc = Short.parseShort(reader.readLine());
    spec.caldes = Short.parseShort(reader.readLine());
    spec.calrp1 = Short.parseShort(reader.readLine());
    spec.calrp2 = Short.parseShort(reader.readLine());
    spec.effprp = Short.parseShort(reader.readLine());
    spec.roirp1 = Short.parseShort(reader.readLine());
    spec.ptr_energy = Short.parseShort(reader.readLine());
    spec.num_energy = Short.parseShort(reader.readLine());
    spec.resv2 = Short.parseShort(reader.readLine());
    spec.deconvolve = Short.parseShort(reader.readLine());
    spec.units = Short.parseShort(reader.readLine());
    spec.perptr = Short.parseShort(reader.readLine());
    spec.maxrcs = Short.parseShort(reader.readLine());
    spec.lstrec = Short.parseShort(reader.readLine());
    spec.effpnm = Short.parseShort(reader.readLine());
    spec.spctrp = Short.parseShort(reader.readLine());
    spec.spcrcn = Short.parseShort(reader.readLine());
    spec.spcchn = Short.parseShort(reader.readLine());
    spec.abstch = Short.parseShort(reader.readLine());
    spec.acqtim = Float.parseFloat(reader.readLine());
    spec.acqti8 = Double.parseDouble(reader.readLine());
    spec.seqnum = Short.parseShort(reader.readLine());
    spec.mcanu = Short.parseShort(reader.readLine());
    spec.segnum = Short.parseShort(reader.readLine());
    spec.mcadvt = Short.parseShort(reader.readLine());
    spec.chnsrt = Short.parseShort(reader.readLine());
    spec.rltmdt = Float.parseFloat(reader.readLine());
    spec.lvtmdt = Float.parseFloat(reader.readLine());

    for (int i = 0; i < 15; i++)
    {
      spec.resv[i] = Short.parseShort(reader.readLine());
    }

//    System.out.printf("inftyp:    ", spec.inftyp);
//    System.out.printf("filtyp:    ", spec.filtyp);
//    System.out.printf("flags:     ", spec.flags);
//    System.out.printf("resv1:     ", spec.resv1);
//    System.out.printf("acqirp:    ", spec.acqirp);
//    System.out.printf("samdrp:    ", spec.samdrp);
//    System.out.printf("detrrp:    ", spec.detrrp);
//    System.out.printf("ebrdesc:   ", spec.ebrdesc);
//    System.out.printf("anarp1:    ", spec.anarp1);
//    System.out.printf("anarp2:    ", spec.anarp2);
//    System.out.printf("anarp3:    ", spec.anarp3);
//    System.out.printf("anarp4:    ", spec.anarp4);
//    System.out.printf("srpdes:    ", spec.srpdes);
//    System.out.printf("ieqdesc:   ", spec.ieqdesc);
//    System.out.printf("geodes:    ", spec.geodes);
//    System.out.printf("mpcdesc:   ", spec.mpcdesc);
//    System.out.printf("caldes:    ", spec.caldes);
//    System.out.printf("calrp1:    ", spec.calrp1);
//    System.out.printf("calrp2:    ", spec.calrp2);
//    System.out.printf("effprp:    ", spec.effprp);
//    System.out.printf("roirp1:    ", spec.roirp1);
//    System.out.printf("ptr_energy:", spec.ptr_energy);
//    System.out.printf("num_energy:", spec.num_energy);
//    System.out.printf("resv2:     ", spec.resv2);
//    System.out.printf("deconvolve:", spec.deconvolve);
//    System.out.printf("units:     ", spec.units);
//    System.out.printf("perptr:    ", spec.perptr);
//    System.out.printf("maxrcs:    ", spec.maxrcs);
//    System.out.printf("lstrec:    ", spec.lstrec);
//    System.out.printf("effpnm:    ", spec.effpnm);
//    System.out.printf("spctrp:    ", spec.spctrp);
//    System.out.printf("spcrcn:    ", spec.spcrcn);
//    System.out.printf("spcchn:    ", spec.spcchn);
//    System.out.printf("abstch:    ", spec.abstch);
//    System.out.printf("acqtim:    ", spec.acqtim);
//    System.out.printf("acqti8:    ", spec.acqti8);
//    System.out.printf("seqnum:    ", spec.seqnum);
//    System.out.printf("mcanu:     ", spec.mcanu);
//    System.out.printf("segnum:    ", spec.segnum);
//    System.out.printf("mcadvt:    ", spec.mcadvt);
//    System.out.printf("chnsrt:    ", spec.chnsrt);
//    System.out.printf("rltmdt:    ", spec.rltmdt);
//    System.out.printf("lvtmdt:    ", spec.lvtmdt);
//    for (int i = 0; i < 15; ++i)
//    {
//      System.out.printf("resv:      ", spec.resv[i]);
//    }
  }
}
