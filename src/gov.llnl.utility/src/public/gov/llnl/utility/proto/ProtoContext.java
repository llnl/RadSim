/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Internal class used to hold state during parsing and serialization.
 *
 * @author nelson85
 */
public class ProtoContext
{
  ArrayList<HashMap<ProtoField, Object>> stack = new ArrayList<>();
  HashMap<ProtoField, Object> current = null;
  
  public void enterFields(ProtoField[] fields)
  {
    this.current = new HashMap<>();
    stack.add(current);
  }

  public void leaveFields(ProtoField[] fields)
  {
    stack.remove(stack.size() - 1);
    if (stack.isEmpty())
      current=null;
    else
      current = stack.get(stack.size() - 1);
  }

  public Object getState(ProtoField field, boolean search)
  {
    Object out = current.get(field);
    
    // search backwards
    if (out == null && search)
    {
      int i = stack.size()-1;
      while (i>0 && out == null)
      {
        i--;
        out = stack.get(i).get(field);      
      }
    }
    return out;
  }

  public void setState(ProtoField field, Object state)
  {
    current.put(field, state);
  }

  public ByteSource enterMessage(ByteSource bs, int size) throws ProtoException
  {
    if (size < 0)
      throw new ProtoException("negative field", bs.position());
    ByteSource out = bs.slice(size);
    if (size != out.remaining())
      throw new ProtoException("truncated field "+size+ " " + out.remaining(), bs.position());
    return out;
  }

  public void leaveMessage(ByteSource bs) throws ProtoException
  {
    if (bs.hasRemaining())
      throw new ProtoException("unparsed data", bs.position());
  }

}
