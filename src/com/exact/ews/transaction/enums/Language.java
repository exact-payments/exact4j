package com.exact.ews.transaction.enums;

import java.util.Map;
import java.util.HashMap;

/*
 * User: donch
 * Date: 30-Jul-2008
 * Time: 12:22:38
 */
public enum Language
{
  English(0),
  French(4);

  private int code;
  private Language(int code)
  {
    this.code = code;
  }

  // set up a mapping so we can get them back when decoding
  private static Map<Integer,Language> itemByCode = new HashMap<Integer,Language>();
  static {
    for(Language t: Language.values())
      itemByCode.put(t.getCode(), t);
  }

  public static Language getFromCode(final int code)
  {
    return itemByCode.get(code);
  }

  public int getCode()
  {
    return code;
  }

}
