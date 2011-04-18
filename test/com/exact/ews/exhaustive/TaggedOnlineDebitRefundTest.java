package com.exact.ews.exhaustive;

import com.exact.ews.exhaustive.BaseTestCase;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.Encoding;
import com.exact.ews.TestUtils;

/**
 * User: donch
 * Date: 05-Aug-2009
 * Time: 17:10:10
 */
public class TaggedOnlineDebitRefundTest extends BaseTestCase
{
  private Response r = null;

  public TaggedOnlineDebitRefundTest(final String name) {
    super(name);
  }

  public void setUp() {
    r = doOnlineDebitPurchase();
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.TaggedOnlineDebitRefund);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("ExactId has not been set."));
    request.setExactId(TestUtils.EmergisExactID);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Password has not been set."));
    request.setPassword(TestUtils.EmergisPassword);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("One of the following must be supplied: Card Number, Track1, Track2 or TransactionTag."));
    request.setTransactionTag(1234);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Amount is required."));
    request.setAmount(10.0f);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Authorization Number is required."));
    request.setAuthorizationNum("ET7868");
    assertTrue(request.isValid());

    // ensure we do not set a card number
    request.setCardNumber("4111111111111111");
    assertFalse(request.isValid());
    assertTrue(request.getErrors().contains("Do not set any card or track information for tagged requests."));
  }

  public void testJsonWorks() {
    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);
  }

  public void testRestWorks() {
    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);
  }

  public void testSoapWorks() {
    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);
  }

  public void testMultipleRefundsAgainstOriginalPurchase() {
    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(3.0f);
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved()); // we can't refund more than the original seed amount
    assertNull(response.getRequest());
  }

  public void testNotPermittedAfterRecurringSeedPurchase() {
    r = doRecurringSeedPurchase();

    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved());
    assertNull(response.getRequest());
  }

  public void testNotPermittedAfterRecurringSeedPreAuth() {
    r = doRecurringSeedPreAuth();

    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved());
    assertNull(response.getRequest());
  }

  public void testNotPermittedAfterPlainPurchase() {
    r = doPurchase();

    final Request request = getCredentialledChaseRequest(TransactionType.TaggedOnlineDebitRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved());
  }

  private Response doOnlineDebitPurchase() {
    final Request request = getCredentialledChaseRequest(TransactionType.IDebitPurchase);
    request.setPrimaryAccountNumber(TestUtils.CCNumber);
    request.setCardholderName(TestUtils.CardholderName);
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    return response;
  }

  private Request getCredentialledChaseRequest(final TransactionType type) {
    final Request req = new Request(type);
    req.setExactId(TestUtils.ChaseExactID);
    req.setPassword(TestUtils.ChasePassword);

    return req;
  }

}
