package com.exact.ews.transaction.enums;

import java.util.Map;
import java.util.HashMap;

/*
 * User: donch
 * Date: 30-Jul-2008
 * Time: 12:24:28
 */
public enum CvdPresenceIndicator
{
  NotSupported(0),
  Present(1),
  Illegible(2),
  NotAvailable(9);

  private int code;
  private CvdPresenceIndicator(final int code)
  {
    this.code = code;
  }

  // set up a mapping so we can get them back when decoding
  private static Map<Integer,CvdPresenceIndicator> itemByCode = new HashMap<Integer,CvdPresenceIndicator>();
  static {
    for(CvdPresenceIndicator t: CvdPresenceIndicator.values())
      itemByCode.put(t.getCode(), t);
  }

  public static CvdPresenceIndicator getFromCode(final int code)
  {
    return itemByCode.get(code);
  }

  public int getCode()
  {
    return code;
  }
}
