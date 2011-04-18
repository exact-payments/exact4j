package com.exact.ews.exhaustive;

import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.TestUtils;
import com.exact.ews.Encoding;

import java.util.Random;

/**
 * User: donch
 * Date: 28-Apr-2010
 * Time: 17:30:40
 *
 * A ReferencedVoid behaves the same as a TaggedVoid, except, instead of using transaction_tag,
 * authorization_num and amount to lookup the transaction to be voided, you use reference_no,
 * customer_ref and amount.
 */
public class ReferencedVoidTest extends BaseTestCase
{
  private Response r = null;
  private Random rand = new Random();

  public ReferencedVoidTest(final String name) {
    super(name);
  }

  public void setUp() {
    final String randStr = Integer.toString(Math.abs(rand.nextInt()));
    r = doPurchase("bananas" + randStr, "apples" + randStr);
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.ReferencedVoid);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("ExactId has not been set."));
    request.setExactId(TestUtils.EmergisExactID);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Password has not been set."));
    request.setPassword(TestUtils.EmergisPassword);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("ReferenceNo is required."));
    request.setReferenceNo("something");
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("CustomerRef is required."));
    request.setCustomerRef("something");
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Amount is required."));
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    // ensure we don't set a transaction tag
    request.setTransactionTag(400);
    assertFalse(request.isValid());
    assertTrue(request.getErrors().contains("Do not set a transaction tag for referenced void requests."));
    request.setTransactionTag(0);

    // ensure we don't set any card information
    request.setCardNumber("4111111111111111");
    assertFalse(request.isValid());
    assertTrue(request.getErrors().contains("Do not set any card or track information for referenced void requests."));
  }

  public void testJsonWorks() {
    final Request request = getCredentialledRequest(TransactionType.ReferencedVoid);
    request.setReferenceNo(r.getRequest().getReferenceNo());
    request.setCustomerRef(r.getRequest().getCustomerRef());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testRestWorks() {
    final Request request = getCredentialledRequest(TransactionType.ReferencedVoid);
    request.setReferenceNo(r.getRequest().getReferenceNo());
    request.setCustomerRef(r.getRequest().getCustomerRef());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testSoapWorks() {
    final Request request = getCredentialledRequest(TransactionType.ReferencedVoid);
    request.setReferenceNo(r.getRequest().getReferenceNo());
    request.setCustomerRef(r.getRequest().getCustomerRef());
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkCreditCardDetails(response);
  }

  public void testAmountsMustBeSameAsOriginalTransaction() {
    final Request request = getCredentialledRequest(TransactionType.ReferencedVoid);
    request.setReferenceNo(r.getRequest().getReferenceNo());
    request.setCustomerRef(r.getRequest().getCustomerRef());
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

  public void testMultipleVoidsNotPermitted() {
    final Request request = getCredentialledRequest(TransactionType.ReferencedVoid);
    request.setReferenceNo(r.getRequest().getReferenceNo());
    request.setCustomerRef(r.getRequest().getCustomerRef());
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

  private Response doPurchase(final String referenceNo, final String customerRef) {
    final Request request = getRequestByCCNumber(TransactionType.Purchase);
    request.setAmount(10.0f);
    request.setReferenceNo(referenceNo);
    request.setCustomerRef(customerRef);

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());

    return response;
  }
}
