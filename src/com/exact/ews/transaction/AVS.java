package com.exact.ews.transaction;
/**
 * The <code>AVS</code> class represents the Address Verification request in a Processor Independent format.
 */
public class AVS {

  private String testFlag;
  private String address;
  private String unit;
  private String poBox;
  private String postalCode;

  /** tcalculated AVS string */
  private String avsString;

  public AVS() {
    this(null, null, null, null, null);
  }

  /**
   * Construct new <code>AVBS</code> instance.
   */
  public AVS(String address, String unit, String poBox, String postalCode, String testFlag) {
    this.address = address;
    this.unit = unit;
    this.poBox = poBox;
    this.postalCode = postalCode;
    this.testFlag = testFlag;
    calculateAVS();
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String nAddress) {
    address = nAddress;
    calculateAVS();
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String nUnit) {
    unit = nUnit;
    calculateAVS();
  }

  public String getPoBox() {
    return poBox;
  }

  public void setPoBox(String nPoBox) {
    poBox = nPoBox;
    calculateAVS();
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String nPostalCode) {
    postalCode = nPostalCode;
    calculateAVS();
  }

  public String getTestFlag() {
    return testFlag;
  }

  public void setTestFlag(String nTestFlag) {
    testFlag = nTestFlag;
    calculateAVS();
  }

  /**
   * Produce the AVS string to be sent to the server.
   */
  public String toAvsString() {
    return avsString;
  }


  public String toString() {
    StringBuffer sb = new StringBuffer();
    return 
      sb.append("com.exact.ews.transaction.AVS").
      append("\n\tAddress=" + address).
      append("\n\tUnit=" + unit).
      append("\n\tpoBox=" + poBox).
      append("\n\tpostalCode=" + postalCode).
      append("\n\ttestFlag=" + testFlag).toString();
  }

  /**
   * Calculate AVS string, callback method invoked after the avs_*= methods
   */

  private void calculateAVS() {
    String poBoxOrAddress = "";
    if (address !=null) {
      poBoxOrAddress = address + (unit == null ? "" : unit);
    } else {
      poBoxOrAddress = (poBox == null ? "" : poBox);
    }
    String postalCodeWithPrefix = "";
    if (postalCode !=null) {
      postalCodeWithPrefix = "|" + postalCode;
    }
    StringBuffer sb = new StringBuffer();
    sb.append(testFlag == null ? "" : testFlag).
      append(poBoxOrAddress == null ? "" : poBoxOrAddress).
      append(postalCodeWithPrefix);

    avsString =  sb.toString();
  }
}
