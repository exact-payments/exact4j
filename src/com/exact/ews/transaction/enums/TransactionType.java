package com.exact.ews.transaction.enums;

import java.util.Map;
import java.util.HashMap;

/*
 * User: donch
 * Date: 14-Jul-2008
 * Time: 16:18:37
 */

/**
 * Transaction types supported by the E-xact API
 */
public enum TransactionType
{
  /**
   * For details on each of the transaction types below, including mandatory attributes
   * and usage examples, please see the test cases in the package com.exact.ews.exhaustive 
   */
  Purchase("00"),
	PreAuth("01"),
	PreAuthCompletion("02"),
	ForcedPost("03"),
	Refund("04"),
	PreAuthOnly("05"),
	PurchaseCorrection("11"),
	RefundCorrection("12"),
	Void("13"),
	TaggedPurchase("30"),
	TaggedPreAuth("31"),
	TaggedPreAuthCompletion("32"),
  	TaggedVoid("33"),
  	TaggedRefund("34"),
  	TaggedOnlineDebitRefund("35"),
  	ReferencedVoid("36"),
	RecurringSeedPreAuth("40"),
	RecurringSeedPurchase("41"),
	IDebitPurchase("50"),
	IDebitRefund("54"),
	SecureStorage("60"),
	SecureStorageEft("61"),
  	TransactionDetails("CR");

  // set up a mapping so we can get them back when decoding
  private static Map<String,TransactionType> itemByCode = new HashMap<String,TransactionType>();
	static {
    for(TransactionType t: TransactionType.values())
      itemByCode.put(t.getCode(), t);
	}

	private String code = "";

  private TransactionType(final String code)
  {
    this.code = code;
  }

	public static TransactionType getTypeFromCode(final String code)
	{
		return itemByCode.get(code);
	}

	public String getCode()
	{
		return code;
	}
}
