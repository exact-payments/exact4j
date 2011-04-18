package com.exact.ews.transaction;

import junit.framework.TestCase;
import junit.framework.Assert;

import java.util.List;
import java.util.Calendar;

import com.exact.ews.transaction.enums.TransactionType;

/**
 * User: donch
 * Date: 15-Jul-2008
 * Time: 12:28:51
 */
public class RequestTest extends TestCase {

  public RequestTest(final String name)   {
    super(name);
  }

  public void testForMandatoryAttributes() {
    // set up basic request
    final Request r = new Request(TransactionType.Purchase);
    r.setCardNumber("4111111111111111");
    r.setCardExpiryDate("0913");
    r.setCardholderName("Test User");
    r.setAmount(10.0f);

    List<String> errors = r.validate();
    assertFalse(errors.isEmpty());
    assertTrue(errors.contains("ExactId has not been set."));
    assertTrue(errors.contains("Password has not been set."));

    r.setExactId("sample");
    errors = r.validate();
    assertFalse(errors.isEmpty());
    assertFalse(errors.contains("ExactId has not been set."));
    assertTrue(errors.contains("Password has not been set."));

    r.setPassword("sample");
    errors = r.validate();
    assertTrue(errors.isEmpty());
  }

  public void testExpiryDateValidation() {
    // set up basic request
    final Request r = new Request(TransactionType.Purchase);
    r.setExactId("A00049-01");
    r.setPassword("password");
    r.setCardNumber("4111111111111111");
    r.setCardholderName("Test User");
    r.setAmount(10.0f);

    r.setCardExpiryDate(null);
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date is required."));


    r.setCardExpiryDate("");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date is required."));

    // too short
    r.setCardExpiryDate("890");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date must be exactly 4 digits long."));

    // too long
    r.setCardExpiryDate("67899");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date must be exactly 4 digits long."));

    // text
    r.setCardExpiryDate("12a8");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date must be exactly 4 digits long."));

    // invalid month
    r.setCardExpiryDate("1308");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date must be in MMYY format."));


    // ensure we don't accept dates in the past
    final Calendar now = Calendar.getInstance();
    final int month = now.get(Calendar.MONTH) + 1;  // +1 cause it returns 0 == Jan etc.
    final int year = now.get(Calendar.YEAR) - 2000;

    // one year ago
    r.setCardExpiryDate(String.format("%02d%02d", month, year-1));
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date must not be in the past."));

    // one month ago
    r.setCardExpiryDate(String.format("%02d%02d", month-1, year));
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Expiry Date must not be in the past."));

    // none of these should fail
    r.setCardExpiryDate(String.format("%02d%02d", month, year));
    assertTrue(r.isValid());
    r.setCardExpiryDate(String.format("%02d%02d", month, year+1));
    assertTrue(r.isValid());
    r.setCardExpiryDate(String.format("%02d%02d", month+1, year));
    assertTrue(r.isValid());
    r.setCardExpiryDate("0612");
    assertTrue(r.isValid());
  }

  public void testCardNumberValidation() {
    final Request r = new Request(TransactionType.Purchase);
    r.setExactId("A00049-01");
    r.setPassword("password");
    r.setCardholderName("Test User");
    r.setCardExpiryDate("0913");
    r.setAmount(10.0f);

    r.setCardNumber(null);
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("One of the following must be supplied: Card Number, Track1, Track2 or TransactionTag."));

    r.setCardNumber("");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("One of the following must be supplied: Card Number, Track1, Track2 or TransactionTag."));

    // too short
    r.setCardNumber("123456789012");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Number must be between 13 and 16 digits long."));

    // too long
    r.setCardNumber("12345678901234567");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Card Number must be between 13 and 16 digits long."));

    // a fake number
    r.setCardNumber("4111111111111245");
    assertFalse(r.isValid());
    assertTrue(r.getErrors().contains("Invalid Card Number supplied."));

    // various test card numbers
    r.setCardNumber("4111111111111111");
    assertTrue(r.isValid());
    r.setCardNumber("5500000000000004");
    assertTrue(r.isValid());
    r.setCardNumber("340000000000009");
    assertTrue(r.isValid());
    r.setCardNumber("3088000000000009");
    assertTrue(r.isValid());
    r.setCardNumber("6011000000000004");
    assertTrue(r.isValid());
    r.setCardNumber("30000000000004");
    assertTrue(r.isValid());
  }

  public void testSetAvs() {
    // set up basic request
    final Request r = new Request(TransactionType.Purchase);
    r.setCardNumber("4111111111111111");
    r.setCardExpiryDate("0913");
    r.setCardholderName("Test User");
    r.setAmount(10.0f);
    AVS avs = new AVS("1234567LOUGHEEDHIGHW", null, null, "902101234", null);
    r.setAVS(avs);
    r.setExactId("sample");
    r.setPassword("sample");
    List<String> errors = r.validate();
    assertTrue(errors.isEmpty());
    assertEquals(r.getCardVerificationStr1(),  "1234567LOUGHEEDHIGHW|902101234");
  }
}
