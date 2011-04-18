package com.exact.ews.transaction.enums;

import java.util.Map;
import java.util.HashMap;

/*
 * User: donch
 * Date: 31-Jul-2008
 * Time: 08:53:03
 */
public enum AvsCode
{
  ExactMatch9DigitZip("X"),
  ExactMatch5DigitZip("Y"),
  AddressMatchOnly("A"),
  ZipMatchOnly9Digit("W"),
  ZipMatchOnly5Digit("Z"),
  NoMatch("N"),
  AddressUnavailable("U"),
  NonNorthAmericanIssuer("G"),
  IssuerSystemUnavailable("R"),
  NotMailOrPhoneOrder("E"),
  ServiceNotSupported("S");

  private String code;
  private AvsCode(final String code)
  {
    this.code = code;
  }

  // set up a mapping so we can get them back when decoding
  private static Map<String, AvsCode> itemByCode = new HashMap<String, AvsCode>();
  static {
    for(AvsCode t: values())
      itemByCode.put(t.getCode(), t);
  }
  public static AvsCode getFromCode(final String code)
  {
    return itemByCode.get(code);
  }

  public String getCode()
  {
    return code;
  }
}