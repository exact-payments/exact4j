package com.exact.ews.exhaustive;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.Encoding;
import com.exact.ews.TestUtils;

/*
 * The BatchQuery and BatchClose transaction types are ONLY supported by Chase Paymentech processors,
 * and even then, only on merchants which have been configured to use Host Capture and have Auto Close
 * turned off. In this case it is the Merchant's responsibility to close each batch before the total
 * number of transactions in the batch reaches 1000.
 *
 * If the Merchant goes beyond that prior to issuing a BatchClose, all subsequent transactions will fail
 * with a BankResponseCode of 408. Once a BatchClose is sent, normal processing will resume.
 *
 * A BatchQuery transaction queries the current state of the open batch. It has no effect on the batch.
 * Requires ExactID and Password only.
 * AuthorizationNum attribute in the response will be "BATINQ"
 * BankMessage attribute in the response will contain summary of transactions (see below)
 *
 * A BatchClose transaction closes the current batch. Any subsequent transaction will initiate a new batch.
 * Requires ExactID and Password only.
 * AuthorizationNum attribute in the response will be "BATCLS"
 * BankMessage attribute in the response will contain summary of transactions (see below)
 *
 * BankMessage Format:
 * The summary of the transactions in the batch is formatted as follows:
 *    <Type|Count|Amount><Type|Count|Amount>....
 *
 * where
 *  Type is
 *    "CR" - Totals for all credit card types except Amex.
 *    "DB" - Debit including IOP
 *    "AE" - Amex
 *    "EB" - Electronic Benefits
 *  Count == number of transactions in the batch for this type
 *  Amount == total net amount of transaction for this type, in cents
 *
 * Example:
 * "<CR|4|3412><DB|9|8900>" == 4 creditcard transactions for $34.12 and 9 Debit Transactions for $89.00
 *
 * 
 * User: donch
 * Date: 02-Oct-2009
 * Time: 14:09:21
 */
public class BatchTransactionTest extends BaseTestCase
{
  public BatchTransactionTest(final String name) {
    super(name);
  }

  public static final String TEST_VISA_CARD = "4111111111111111";
  public static final String TEST_MASTERCARD = "5500000000000004";
  public static final String TEST_AMEX_CARD = "340000000000009";

  public void testQuery() {
    // ensure we have an open batch
    doPurchase(10.0f, TEST_VISA_CARD);

    final Request request = new Request(TransactionType.BatchQuery);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);

    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());

    final String batchNo = response.getRequest().getAuthorizationNum();

    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals(batchNo, response.getRequest().getAuthorizationNum());

    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals(batchNo, response.getRequest().getAuthorizationNum());
  }

  public void testCloseJson() {
    // ensure we have an open batch
    doPurchase(10.0f, TEST_VISA_CARD);

    final Request request = new Request(TransactionType.BatchClose);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);

    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertNotNull(response.getRequest().getAuthorizationNum());
  }

  public void testCloseRest() {
    // ensure we have an open batch
    doPurchase(10.0f, TEST_VISA_CARD);

    final Request request = new Request(TransactionType.BatchClose);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);

    Response response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertNotNull(response.getRequest().getAuthorizationNum());
  }

  public void testCloseSoap() {
    // ensure we have an open batch
    doPurchase(10.0f, TEST_VISA_CARD);

    final Request request = new Request(TransactionType.BatchClose);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);

    Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertNotNull(response.getRequest().getAuthorizationNum());
  }

  public void testCantCloseClosedBatch() {
    // ensure we have an open batch
    doPurchase(10.0f, TEST_VISA_CARD);

    // close it
    final Request request = new Request(TransactionType.BatchClose);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);

    Response response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertNotNull(response.getRequest().getAuthorizationNum());

    // try to close it again
    response = submit(request, Encoding.SOAP);
    assertFalse(response.isApproved());
    assertEquals("", response.getRequest().getAuthorizationNum());
    assertEquals("BAT ALREADY RELS", response.getBankMessage());
  }

  public void testSummation() {
    doBatchClose(); // ensure we're starting a new batch

    // 3 transactions, total of 148.04 on VISA
    doPurchase(10.0f, TEST_VISA_CARD);
    doPurchase(15.29f, TEST_VISA_CARD);
    doPurchase(122.75f, TEST_VISA_CARD);

    // 2 transactions, total of 3299.47 on Mastercard
    doPurchase(1754.60f, TEST_MASTERCARD);
    doPurchase(1544.87f, TEST_MASTERCARD);

    // 4 transactions, total of 1113.99 on Amex
    doPurchase(43.26f, TEST_AMEX_CARD);
    doPurchase(185.75f, TEST_AMEX_CARD);
    doPurchase(877.53f, TEST_AMEX_CARD);
    doPurchase(7.45f, TEST_AMEX_CARD);

    // query the batch
    Response response = doBatchQuery();

    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals("<CR|5|344751><AE|4|111399>", response.getBankMessage());

    final String batchNo = response.getRequest().getAuthorizationNum();

     // query again
    response = doBatchQuery();

    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals("<CR|5|344751><AE|4|111399>", response.getBankMessage());
    assertEquals(batchNo, response.getRequest().getAuthorizationNum());

    // close the batch
    response = doBatchClose();

    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals(batchNo, response.getRequest().getAuthorizationNum());
    assertEquals("<CR|5|344751><AE|4|111399>", response.getBankMessage());

  }

  public void testLargeSummation() {
    doBatchClose(); // ensure we're starting a new batch

    // 9 transactions, total of 899,993.70 on VISA
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);

    // 4 transactions, total of 399,997.20 on Mastercard
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);
    doPurchase(99999.30f, TEST_VISA_CARD);

    // query the batch
    Response response = doBatchQuery();

    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals("<CR|13|129999090>", response.getBankMessage());

    final String batchNo = response.getRequest().getAuthorizationNum();

    doPurchase(1.35f, TEST_VISA_CARD);

    // query the batch
    response = doBatchQuery();

    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals("<CR|14|129999225>", response.getBankMessage());
    assertEquals(batchNo, response.getRequest().getAuthorizationNum());

    // close the batch
    response = doBatchClose();

    assertTrue(response.isApproved());
    assertEquals("000", response.getBankResponseCode());
    assertEquals(batchNo, response.getRequest().getAuthorizationNum());
    assertEquals("<CR|14|129999225>", response.getBankMessage());

  }

  private Response doBatchQuery() {
    final Request request = new Request(TransactionType.BatchQuery);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);
    return submit(request, Encoding.JSON);
  }

  private Response doBatchClose() {
    final Request request = new Request(TransactionType.BatchClose);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);
    return submit(request, Encoding.JSON);
  }

  private void doPurchase(final float amount, final String cardNumber) {
    final Request request = getRequestByCCNumber(TransactionType.Purchase);
    request.setAmount(amount);
    request.setCardNumber(cardNumber);
    request.setExactId(TestUtils.ChaseBatchExactID);
    request.setPassword(TestUtils.ChaseBatchPassword);

    final Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
  }

}
