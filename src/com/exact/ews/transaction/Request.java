package com.exact.ews.transaction;

import com.exact.ews.transaction.Coder;
import com.exact.ews.transaction.CoderFactory;
import com.exact.ews.transaction.enums.ECommerceFlag;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import com.exact.ews.Encoding;

import java.util.List;
import java.util.ArrayList;

/*
 * User: donch
 * Date: 09-Jul-2008
 * Time: 16:11:44
 */

public class Request {

  private TransactionType transactionType;
  protected CvdPresenceIndicator cvdPresenceIndicator;
  protected Language language = Language.English;
  protected ECommerceFlag ecommerceFlag = null;
  protected long transactionTag = 0;
  protected int  secureAuthRequired = 0, secureAuthResult = 0, cavvAlgorithm = 0;
  protected String exactId = "", password = "", cardNumber = "", track1 = "", track2 = "",
    primaryAccountNumber = "", authorizationNum = "", cardExpiryDate = "", cardholderName = "",
    cardVerificationStr1 = "", cardVerificationStr2 = "", tax1Number = "", tax2Number = "", xid = "", cavv = "",
    referenceNo = "", customerRef = "", reference3 = "", clientIp = "", clientEmail = "", userName = "", zipCode = "";
  protected Float amount = 0f, surchargeAmount = 0f, tax1Amount = 0f, tax2Amount = 0f;

  protected AVS avs = null;

  private List<String> errors = new ArrayList<String>();
  
  protected Request() {}

  public Request(final TransactionType type) {
    if(type == null)
      throw new IllegalArgumentException("Request(TransactionType): No TransactionType specified.");

    this.transactionType = type;
  }

  /*
   * Identifies whether or not this is a 'TransactionDetails' transaction.
   *
   * @return  true if this is a "TransactionDetails" transaction
   */
  public boolean isFindTransaction() {
    return ((transactionTag != 0) && transactionType == TransactionType.TransactionDetails);
  }

  /*
   * Validates this request.
   *
   * @return true/false
   */
  public boolean isValid() {
    errors = validate();
    return errors.isEmpty();
  }

  /*
   * Get the list of errors for this request.
   *
   * @return a list of error strings
   */
  public List<String> getErrors() {
    return errors;
  }

  /*
   * Validates this request.
   *
   * @return a list of error strings
   * @deprecated  use isValid() instead
   */
  public List<String> validate() {
    return Validator.validate(this);
  }

  /*
   * Encodes this request as a JSON String.
   *
   * @return
   */
  public String toString() {
    final Coder t = CoderFactory.getTranslator(Encoding.JSON);
    try {
      return t.encode(this);
    } catch (Exception e) {
	return super.toString();
      }
  }


  public long getTransactionTag() {
    return transactionTag;
  }

  public void setTransactionTag(final long transactionTag) {
    if(transactionTag < 0)
      throw new IllegalArgumentException("setTransactionTag(long): TransactionTag can not be negative.");

    this.transactionTag = transactionTag;
  }


  public CvdPresenceIndicator getCvdPresenceIndicator() {
    return cvdPresenceIndicator;
  }

  public void setCvdPresenceIndicator(final CvdPresenceIndicator cvdPresenceIndicator) {
    this.cvdPresenceIndicator = cvdPresenceIndicator;
  }


  public Language getLanguage() {
    return language;
  }

  public void setLanguage(final Language language) {
    this.language = language;
  }


  public int getSecureAuthRequired() {
    return secureAuthRequired;
  }

  public void setSecureAuthRequired(final int secureAuthRequired) {
    this.secureAuthRequired = secureAuthRequired;
  }


  public int getSecureAuthResult() {
    return secureAuthResult;
  }

  public void setSecureAuthResult(final int secureAuthResult) {
    this.secureAuthResult = secureAuthResult;
  }


  public ECommerceFlag getEcommerceFlag() {
    return ecommerceFlag;
  }

  public void setEcommerceFlag(final ECommerceFlag ecommerceFlag) {
    this.ecommerceFlag = ecommerceFlag;
  }


  public int getCavvAlgorithm() {
    return cavvAlgorithm;
  }

  public void setCavvAlgorithm(final int cavvAlgorithm) {

    this.cavvAlgorithm = cavvAlgorithm;
  }


  public String getExactId() {
    return exactId;
  }

  public void setExactId(final String exactId) {
    if (!Validator.isValidLength(exactId, 10))
      throw new IllegalArgumentException("Request.setExactId(): Maximum length is 10 characters.");

    this.exactId = exactId;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    if (!Validator.isValidLength(password, 30))
      throw new IllegalArgumentException("Request.setPassword(): Maximum length is 30 characters.");

    this.password = password;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(final String cardNumber) {
    // todo: add card number verification
    this.cardNumber = cardNumber;
  }

  public String getTrack1() {
    return track1;
  }

  public void setTrack1(final String track1) {
    if (!Validator.isValidLength(track1, 79))
      throw new IllegalArgumentException("Request.setTrack1(): Maximum length is 79 characters.");

    this.track1 = track1;
  }

  public String getTrack2() {
    return track2;
  }

  public void setTrack2(final String track2) {
    if (!Validator.isValidLength(track2, 40))
      throw new IllegalArgumentException("Request.setTrack2(): Maximum length is 40 characters.");

    this.track2 = track2;
  }

  public String getPrimaryAccountNumber() {
    return primaryAccountNumber;
  }

  public void setPrimaryAccountNumber(final String primaryAccountNumber) {
    this.primaryAccountNumber = primaryAccountNumber;
  }

  public String getAuthorizationNum() {
    return authorizationNum;
  }

  public void setAuthorizationNum(final String authorizationNum) {
    if (!Validator.isValidLength(authorizationNum, 8))
      throw new IllegalArgumentException("Request.setAuthorizationNum(): Maximum length is 8 characters.");
    
    this.authorizationNum = authorizationNum;
  }

  public String getCardExpiryDate() {
    return cardExpiryDate;
  }
  /**
   * Set the credit card's expiry date.
   *
   * @param cardExpiryDate  the expiry date in MMYY format.
   */
  public void setCardExpiryDate(final String cardExpiryDate) {
    this.cardExpiryDate = cardExpiryDate;
  }

  public String getCardholderName() {
    return cardholderName;
  }

  public void setCardholderName(final String cardholderName) {
    if (!Validator.isValidLength(cardholderName, 30))
      throw new IllegalArgumentException("Request.setCardholderName(): Maximum length is 30 characters.");

    this.cardholderName = cardholderName;
  }
  
  /**
   * Return the Address Verification String. If the <code>cardVerificationStr1</code> is directly specified
   * it is returned. If there is <code>AVS</code> set it return AVS. Assigning <code>cardVerificationStr1</code> overrides
   * <code>AVS</code> set.
   */
  public String getCardVerificationStr1() {
    if (!(cardVerificationStr1 == null || "".equals(cardVerificationStr1))) return cardVerificationStr1;

    if (avs != null) return avs.toAvsString();

    return cardVerificationStr1;
  }
  public void setCardVerificationStr1(final String cardVerificationStr1) {
    if (!Validator.isValidLength(cardVerificationStr1, 40))
      throw new IllegalArgumentException("Request.setCardVerificationStr1(): Maximum length is 40 characters.");

    this.cardVerificationStr1 = cardVerificationStr1;
  }

  public String getCardVerificationStr2() {
    return cardVerificationStr2;
  }
  public void setCardVerificationStr2(final String cardVerificationStr2) {
    if (!Validator.isValidLength(cardVerificationStr2, 4))
      throw new IllegalArgumentException("Request.setCardVerificationStr2(): Maximum length is 4 characters.");

    this.cardVerificationStr2 = cardVerificationStr2;
  }

  public String getTax1Number() {
    return tax1Number;
  }

  public void setTax1Number(final String tax1Number) {
    if (!Validator.isValidLength(tax1Number, 20))
      throw new IllegalArgumentException("Request.setTax1Number(): Maximum length is 20 characters.");

    this.tax1Number = tax1Number;
  }


  public String getTax2Number() {
    return tax2Number;
  }

  public void setTax2Number(final String tax2Number) {
    if (!Validator.isValidLength(tax2Number, 20))
      throw new IllegalArgumentException("Request.setTax2Number(): Maximum length is 20 characters.");

    this.tax2Number = tax2Number;
  }


  public String getXid() {
    return xid;
  }

  public void setXid(final String xid) {
    if (!Validator.isValidLength(xid, 40))
      throw new IllegalArgumentException("Request.setXid(): Maximum length is 40 characters.");

    this.xid = xid;
  }


  public String getCavv() {
    return cavv;
  }

  public void setCavv(final String cavv) {
    if (!Validator.isValidLength(cavv, 40))
      throw new IllegalArgumentException("Request.setCavv(): Maximum length is 40 characters.");

    this.cavv = cavv;
  }


  public String getReferenceNo()  {
    return referenceNo;
  }

  public void setReferenceNo(final String referenceNo) {
    if (!Validator.isValidLength(referenceNo, 20))
      throw new IllegalArgumentException("Request.setReferenceNo(): Maximum length is 20 characters.");

    this.referenceNo = referenceNo;
  }


  public String getCustomerRef() {
    return customerRef;
  }

  public void setCustomerRef(final String customerRef) {
    if (!Validator.isValidLength(customerRef, 20))
      throw new IllegalArgumentException("Request.setCustomerRef(): Maximum length is 20 characters.");

    this.customerRef = customerRef;
  }


  public String getReference3() {
    return reference3;
  }

  public void setReference3(final String reference3) {
    if (!Validator.isValidLength(reference3, 30))
      throw new IllegalArgumentException("Request.setReference3(): Maximum length is 30 characters.");

    this.reference3 = reference3;
  }


  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(final String clientIp) {
    if (!Validator.isValidLength(clientIp, 15))
      throw new IllegalArgumentException("Request.setClientIp(): Maximum length is 15 characters.");

    this.clientIp = clientIp;
  }


  public String getClientEmail() {
    return clientEmail;
  }

  public void setClientEmail(final String clientEmail) {
    if (!Validator.isValidLength(clientEmail, 50))
      throw new IllegalArgumentException("Request.setClientEmail(): Maximum length is 50 characters.");

    this.clientEmail = clientEmail;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }

  public Float getAmount() {
    return amount;
  }

  public void setAmount(final Float amount) {
    if(!Validator.isValidAmount(amount))
      throw new IllegalArgumentException("Amount must be between 0 and 99999.99");

    this.amount = amount;
  }


  public Float getSurchargeAmount() {
    return surchargeAmount;
  }

  public void setSurchargeAmount(final Float amount) {
    if(!Validator.isValidAmount(amount))
      throw new IllegalArgumentException("Amount must be between 0 and 99999.99");

    this.surchargeAmount = amount;
  }

  public Float getTax1Amount() {
    return tax1Amount;
  }

  public void setTax1Amount(final Float amount) {
    if(!Validator.isValidAmount(amount))
      throw new IllegalArgumentException("Amount must be between 0 and 99999.99");

    this.tax1Amount = amount;
  }

  public Float getTax2Amount() {
    return tax2Amount;
  }

  public void setTax2Amount(final Float amount) {
    if(!Validator.isValidAmount(amount))
      throw new IllegalArgumentException("Amount must be between 0 and 99999.99");

    this.tax2Amount = amount;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(final String zipCode) {
    if (!Validator.isValidLength(zipCode, 10))
      throw new IllegalArgumentException("Request.setZipCode(): Maximum length is 10 characters.");

    this.zipCode = zipCode;
  }

  public AVS getAVS() {
    return avs;
  }

  public void setAVS(final AVS avs) {
    if (avs == null)
      throw new IllegalArgumentException("Request.setAVS(): AVS cannot be null.");

    this.avs = avs;
  }

}
