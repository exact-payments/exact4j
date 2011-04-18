package com.exact.ews.transaction;

import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.transaction.enums.ECommerceFlag;

/**
 * User: donch
 * Date: 09-Jun-2009
 * Time: 11:33:56
 */
public class ReadOnlyRequest extends Request
{
  public ReadOnlyRequest(final TransactionType type) {
    super(type);
  }

  public void setCvdPresenceIndicator(final CvdPresenceIndicator cvdPresenceIndicator)
  {
    this.cvdPresenceIndicator = cvdPresenceIndicator;
  }

  public void setSecureAuthRequired(final int secureAuthRequired)
  {
    this.secureAuthRequired = secureAuthRequired;
  }

  public void setSecureAuthResult(final int secureAuthResult)
  {
    this.secureAuthResult = secureAuthResult;
  }

  public void setLanguage(final Language language)
  {
    this.language = language;
  }

  public void setEcommerceFlag(final ECommerceFlag ecommerceFlag)
  {
    this.ecommerceFlag = ecommerceFlag;
  }

  public void setCavvAlgorithm(final int cavvAlgorithm)
  {
    this.cavvAlgorithm = cavvAlgorithm;
  }

  public void setExactId(final String exactId)
  {
    this.exactId = exactId;
  }

  public void setPassword(final String password)
  {
    this.password = password;
  }

  public void setCardNumber(final String cardNumber)
  {
    this.cardNumber = cardNumber;
  }

  public void setTrack1(final String track1)
  {
    this.track1 = track1;
  }

  public void setTrack2(final String track2)
  {
    this.track2 = track2;
  }

  public void setPrimaryAccountNumber(final String primaryAccountNumber)
  {
    this.primaryAccountNumber = primaryAccountNumber;
  }

  public void setAuthorizationNum(final String authorizationNum)
  {
    this.authorizationNum = authorizationNum;
  }

  public void setCardExpiryDate(final String cardExpiryDate)
  {
    this.cardExpiryDate = cardExpiryDate;
  }

  public void setCardholderName(final String cardholderName)
  {
    this.cardholderName = cardholderName;
  }

  public void setCardVerificationStr1(final String cardVerificationStr1)
  {
    this.cardVerificationStr1 = cardVerificationStr1;
  }

  public void setCardVerificationStr2(final String cardVerificationStr2)
  {
    this.cardVerificationStr2 = cardVerificationStr2;
  }

  public void setTax1Number(final String tax1Number)
  {
    this.tax1Number = tax1Number;
  }

  public void setTax2Number(final String tax2Number)
  {
    this.tax2Number = tax2Number;
  }

  public void setXid(final String xid)
  {
    this.xid = xid;
  }

  public void setCavv(final String cavv)
  {
    this.cavv = cavv;
  }

  public void setReferenceNo(final String referenceNo)
  {
    this.referenceNo = referenceNo;
  }

  public void setCustomerRef(final String customerRef)
  {
    this.customerRef = customerRef;
  }

  public void setReference3(final String reference3)
  {
    this.reference3 = reference3;
  }

  public void setClientIp(final String clientIp)
  {
    this.clientIp = clientIp;
  }

  public void setClientEmail(final String clientEmail)
  {
    this.clientEmail = clientEmail;
  }

  public void setUserName(final String userName)
  {
    this.userName = userName;
  }

  public void setAmount(final Float amount)
  {
    this.amount = amount;
  }

  public void setSurchargeAmount(final Float amount)
  {
    this.surchargeAmount = amount;
  }

  public void setTax1Amount(final Float amount)
  {
    this.tax1Amount = amount;
  }

  public void setTax2Amount(final Float amount)
  {
    this.tax2Amount = amount;
  }

  public void setZipCode(final String zipCode)
  {
    this.zipCode = zipCode;
  }

  public void setTransactionTag(final int transactionTag)
  {
    this.transactionTag = transactionTag;
  }
}
