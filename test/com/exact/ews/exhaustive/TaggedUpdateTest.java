package com.exact.ews.exhaustive;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.TestUtils;
import com.exact.ews.Encoding;

/**
 * User: donch
 * Date: 28-Apr-2010
 * Time: 17:09:51
 *
 * A TaggedUpdate request allows a merchant to edit the reference_no and customer_ref values of a
 * previous transaction. Please note that this is NOT a financial transaction, it merely allows
 * you to change the values stored in our DB for a given transaction.
 *
 * In common with all other tagged transactions, you supply the transaction_tag and authorization_num
 * for the transaction whose values you wish to change, along with the new values for reference_no
 * and/or customer_ref.
 *
 * NOTE: Empty strings ("") or whitespace strings ("   ") are not permitted, and, although the
 * request will be approved, the relevant attribute WILL NOT CHANGE.
 */
public class TaggedUpdateTest extends BaseTestCase
{
  private Response r = null;

  public TaggedUpdateTest(String name)
  {
    super(name);
  }

  public void setUp() {
    r = doPurchase("bananas", "apples");
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.TaggedUpdate);
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

    assertTrue(request.getErrors().contains("Authorization Number is required."));
    request.setAuthorizationNum("ET7868");
    assertTrue(request.isValid());

    // ensure we do not set a card number
    request.setCardNumber("4111111111111111");
    assertFalse(request.isValid());
    assertTrue(request.getErrors().contains("Do not set any card or track information for tagged requests."));
  }

  public void testJsonWorks() {
    final Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("omelette");
    request.setCustomerRef("raisins");
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    assertEquals("omelette", response.getRequest().getReferenceNo());
    assertEquals("raisins", response.getRequest().getCustomerRef());
  }

  public void testRestWorks() {
    final Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("omelette");
    request.setCustomerRef("raisins");
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    assertEquals("omelette", response.getRequest().getReferenceNo());
    assertEquals("raisins", response.getRequest().getCustomerRef());
  }

  public void testSoapWorks() {
    final Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("omelette");
    request.setCustomerRef("raisins");
    assertTrue(request.isValid());

    final Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    assertEquals("omelette", response.getRequest().getReferenceNo());
    assertEquals("raisins", response.getRequest().getCustomerRef());
  }

  public void testMultipleUpdates() {
    final Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("omelette");
    request.setCustomerRef("raisins");
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    assertEquals("omelette", response.getRequest().getReferenceNo());
    assertEquals("raisins", response.getRequest().getCustomerRef());

    // submit a second update
    request.setReferenceNo("chocolate");
    request.setCustomerRef("hamburger");

    response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    assertEquals("chocolate", response.getRequest().getReferenceNo());
    assertEquals("hamburger", response.getRequest().getCustomerRef());
  }

  public void testSingleUpdate() {
    // only change reference_no
    Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("omelette");
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    // has changed
    assertEquals("omelette", response.getRequest().getReferenceNo());
    // has not changed
    assertEquals("apples", response.getRequest().getCustomerRef());

    // now change customer_ref only
    request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setCustomerRef("raisins");
    assertTrue(request.isValid());

    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    // has not changed
    assertEquals("omelette", response.getRequest().getReferenceNo());
    // has changed
    assertEquals("raisins", response.getRequest().getCustomerRef());

  }

  public void testEmptyStringsIgnored() {
    final Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("");
    request.setCustomerRef("");
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    // has not changed
    assertEquals("bananas", response.getRequest().getReferenceNo());
    assertEquals("apples", response.getRequest().getCustomerRef());
  }

  public void testWhitespaceStringsIgnored() {
    final Request request = getCredentialledRequest(TransactionType.TaggedUpdate);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("    ");
    request.setCustomerRef("              ");
    assertTrue(request.isValid());

    Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    checkCreditCardDetails(response);

    // has not changed
    assertEquals("bananas", response.getRequest().getReferenceNo());
    assertEquals("apples", response.getRequest().getCustomerRef());
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
