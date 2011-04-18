package com.exact.ews.transaction.enums;

import java.util.Map;
import java.util.HashMap;

/*
 * User: donch
 * Date: 31-Jul-2008
 * Time: 09:01:17
 */
public enum Cvv2Code
{
  Match('M'),
  NoMatch('N'),
  NotProcessed('P'),
  NotPresentOnCard('S'),
  IssuerNotCertified('U');

  private char code;
  private Cvv2Code(final char code)
  {
    this.code = code;
  }

  // set up a mapping so we can get them back when decoding
  private static Map<Character,Cvv2Code> itemByCode = new HashMap<Character,Cvv2Code>();
  static {
    for(Cvv2Code t: Cvv2Code.values())
      itemByCode.put(t.getCode(), t);
  }

  public static Cvv2Code getFromCode(final char code)
  {
    return itemByCode.get(code);
  }

  public char getCode()
  {
    return code;
  }
}