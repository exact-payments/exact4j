package com.exact.ews.exhaustive;

import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.TestUtils;
import com.exact.ews.Encoding;

/**
 * User: donch
 * Date: 25-Sep-2009
 * Time: 17:34:11
 */
public class TaggedVoidTest extends BaseTestCase
{
  private Response r = null;

  public TaggedVoidTest(final String name) {
    super(name);
  }

  public void setUp() {
    r = doRecurringSeedPurchase();
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.TaggedVoid);
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
    final Request request = getCredentialledRequest(TransactionType.TaggedVoid);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testRestWorks() {
    final Request request = getCredentialledRequest(TransactionType.TaggedVoid);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testSoapWorks() {
    final Request request = getCredentialledRequest(TransactionType.TaggedRefund);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testAmountsMustBeSameAsOriginalTransaction() {
    final Request request = getCredentialledRequest(TransactionType.TaggedVoid);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(3.0f); // too low
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved());

    request.setAmount(12.0f); // too high
    response = submit(request, Encoding.REST);
    assertFalse(response.isApproved());

    request.setAmount(10.0f); // just right
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testMultipleVoidsAgainstOriginalSeed() {
    final Request request = getCredentialledRequest(TransactionType.TaggedVoid);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);

    response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved()); // we can't void an already-voided transaction
    assertNull(response.getRequest());
  }

  public void testNotPermittedAfterRecurringSeedPreAuth() {
    r = doRecurringSeedPreAuth();

    final Request request = getCredentialledRequest(TransactionType.TaggedVoid);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertFalse(response.isApproved());
    assertNull(response.getRequest());
  }

  public void testPermittedAfterPlainPurchase() {
    r = doPurchase();

    final Request request = getCredentialledRequest(TransactionType.TaggedVoid);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }
}
