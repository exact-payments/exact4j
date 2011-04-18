package com.exact.ews.exhaustive;

import junit.framework.TestCase;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.Encoding;
import com.exact.ews.Transporter;
import com.exact.ews.TestUtils;
import com.exact.ews.ConnectionVerifier;

/**
 * User: donch
 * Date: 05-Aug-2009
 * Time: 09:15:36
 */
abstract class BaseTestCase extends TestCase
{
  public BaseTestCase(final String name) {
    super(name);
  }

  protected Response submit(final Request request, final Encoding encoding) {
    if(request == null)
      throw new IllegalArgumentException("request should not be null.");
    if(encoding == null)
      throw new IllegalArgumentException("encoding should not be null.");

    Response r = null;
    try
    {
      final Transporter t = new Transporter(TestUtils.URL, encoding);
      t.setConnectionVerifier(new ConnectionVerifier());

      r = t.submit(request);

      assertTrue(r != null);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Unexpected Exception");
    }

    return r;
  }

  protected Request getCredentialledRequest(final TransactionType type) {
    final Request req = new Request(type);
    req.setExactId(TestUtils.EmergisExactID);
    req.setPassword(TestUtils.EmergisPassword);

    return req;
  }

  protected Request getRequestByCCNumber(final TransactionType type) {
    final Request req = getCredentialledRequest(type);
    req.setCardNumber(TestUtils.CCNumber);
    req.setCardExpiryDate(TestUtils.CCExpiry);
    req.setCardholderName(TestUtils.CardholderName);

    return req;
  }

  protected Request getRequestByTrack1(final TransactionType type) {
    final Request req = getCredentialledRequest(type);
    req.setTrack1(TestUtils.Track1);

    return req;
  }

  protected Request getRequestByTrack2(final TransactionType type) {
    final Request req = getCredentialledRequest(type);
    req.setTrack2(TestUtils.Track2);
    req.setCardholderName(TestUtils.CardholderName);

    return req;
  }

  // send a Purchase request, and return its authorizationNum
  protected Response doPurchase() {
    final Request request = getRequestByCCNumber(TransactionType.Purchase);
    request.setAmount(10.0f);

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());

    return response;
  }

  // send a RecurringSeedPurchase request, and return its response
  protected Response doRecurringSeedPurchase() {
    final Request request = getRequestByCCNumber(TransactionType.RecurringSeedPurchase);
    request.setAmount(10.0f);

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());

    return response;
  }

  // send a RecurringSeedPreAuth request, and return its response
  protected Response doRecurringSeedPreAuth() {
    final Request request = getRequestByCCNumber(TransactionType.RecurringSeedPreAuth);
    request.setAmount(10.0f);

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());

    return response;
  }

  protected void checkCreditCardDetails(final Response response) {
    assertTrue(response.getRequest().getCardNumber().matches("............1111"));  // card number may be masked
    assertEquals(TestUtils.CardholderName, response.getRequest().getCardholderName());
    assertEquals(TestUtils.CCExpiry, response.getRequest().getCardExpiryDate());
  }

  protected void checkDebitCardDetails(final Response response) {
//    assertTrue(response.getRequest().getCardNumber().matches("............1111"));
    assertEquals(TestUtils.CardholderName, response.getRequest().getCardholderName());
  }

}
