package com.exact.ews.transaction.enums;

import java.util.Map;
import java.util.HashMap;

/*
 * User: donch
 * Date: 30-Jul-2008
 * Time: 12:31:22
 */
public enum ECommerceFlag
{
  SingleMoto(1),
  RecurringMoto(2),
  InstallmentMoto(3),
  UnknownMoto(4),
  Authenticated3DSecure(5),
  NonParticipating3DSecure(6),
  NonAuthenticated3dSecure(7),
  NonSecureECommerce(8),
  NoSetCompliance(8);

  private static Map<Integer, ECommerceFlag> itemByCode = new HashMap<Integer, ECommerceFlag>();
  static {
    for(ECommerceFlag t: ECommerceFlag.values())
      itemByCode.put(t.getCode(), t);
  }

  private int code;
  private ECommerceFlag(final int code)
  {
    this.code = code;
  }

  public static ECommerceFlag getFromCode(final int code)
  {
    return itemByCode.get(code);
  }
  public int getCode()
  {
    return code;
  }
}
