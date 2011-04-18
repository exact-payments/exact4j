package com.exact.ews.exhaustive;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.Encoding;
import com.exact.ews.TestUtils;
import com.exact.ews.exhaustive.BaseTestCase;

/**
 * User: donch
 * Date: 05-Aug-2009
 * Time: 15:53:22
 */
public class SecureStorageTest extends BaseTestCase
{
  public SecureStorageTest(final String name) {
    super(name);
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.SecureStorage);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("ExactId has not been set."));
    request.setExactId(TestUtils.EmergisExactID);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Password has not been set."));
    request.setPassword(TestUtils.EmergisPassword);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("One of the following must be supplied: Card Number, Track1, Track2 or TransactionTag."));
    request.setCardNumber(TestUtils.CCNumber);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Card Expiry Date is required."));
    request.setCardExpiryDate(TestUtils.CCExpiry);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Cardholder Name is required."));
    request.setCardholderName(TestUtils.CardholderName);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Amount is required."));
    request.setAmount(10.0f);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Reference Number is required."));
    request.setReferenceNo("7868");
    assertTrue(request.isValid());
  }

  public void testByCreditCard() {
    final Request request = getRequestByCCNumber(TransactionType.SecureStorage);
    request.setAmount(10.0f);
    request.setReferenceNo("REF-123");
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
  }

  public void testByTrack1() {
    final Request request = getRequestByTrack1(TransactionType.SecureStorage);
    request.setAmount(10.0f);
    request.setReferenceNo("REF-123");
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
  }

  public void testByTrack2() {
    final Request request = getRequestByTrack2(TransactionType.SecureStorage);
    request.setAmount(10.0f);
    request.setReferenceNo("REF-123");
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
  }
}
