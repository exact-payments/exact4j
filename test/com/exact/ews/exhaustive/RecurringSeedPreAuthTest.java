package com.exact.ews.exhaustive;

import com.exact.ews.exhaustive.BaseTestCase;
import com.exact.ews.Encoding;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;

/**
 * User: donch
 * Date: 05-Aug-2009
 * Time: 15:48:20
 */
public class RecurringSeedPreAuthTest extends BaseTestCase
{
  public RecurringSeedPreAuthTest(final String name) {
    super(name);
  }

  public void testByCreditCard() {
    final Request request = getRequestByCCNumber(TransactionType.RecurringSeedPreAuth);
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testByTrack1() {
    final Request request = getRequestByTrack1(TransactionType.RecurringSeedPreAuth);
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testByTrack2() {
    final Request request = getRequestByTrack2(TransactionType.RecurringSeedPreAuth);
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }
}
