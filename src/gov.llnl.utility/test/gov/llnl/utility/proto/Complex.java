/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Test for the proto encoder.
 *
 * @author nelson85
 */
public class Complex
{
  int a;
  int b;
  int c;
  double d;
  String name;
  ArrayList<String> strs = new ArrayList<>();
  Map<String, Integer> map = new java.util.HashMap<>();
  float[] f;
}
