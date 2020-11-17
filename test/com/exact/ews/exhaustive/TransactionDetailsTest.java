package com.exact.ews.exhaustive;

import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.MerchantDetails;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import com.exact.ews.transaction.enums.ECommerceFlag;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.Encoding;
import com.exact.ews.TestUtils;

/**
 * User: donch
 * Date: 07-Aug-2009
 * Time: 11:53:49
 */
public class TransactionDetailsTest extends BaseTestCase
{
  public TransactionDetailsTest(final String name) {
    super(name);
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.TransactionDetails);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("ExactId has not been set."));
    request.setExactId(TestUtils.EmergisExactID);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Password has not been set."));
    request.setPassword(TestUtils.EmergisPassword);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Transaction Tag must be greater than 0."));
    request.setTransactionTag(1234);
    assertTrue(request.isValid());
  }

  public void testWorks() {
    final Response r = doPurchase();

    final Request request = getCredentialledRequest(TransactionType.TransactionDetails);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertResponsesMatch(r, response);
    assertEquals("############1111", response.getRequest().getCardNumber());

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertResponsesMatch(r, response);
    assertEquals("############1111", response.getRequest().getCardNumber());

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertResponsesMatch(r, response);
    assertEquals("############1111", response.getRequest().getCardNumber());
  }

  public void testSupplyingAuthNumDoesNotDecryptCreditCardNumber() {
    final Request origRequest = getRequestByCCNumber(TransactionType.Purchase);
    origRequest.setAmount(10.0f);
    origRequest.setReferenceNo("barry/jones&^%$&^%");

    final Response r = submit(origRequest, Encoding.JSON);
    assertTrue(r.isApproved());

    final Request request = getCredentialledRequest(TransactionType.TransactionDetails);
    request.setTransactionTag(r.getRequest().getTransactionTag());
    request.setAuthorizationNum(r.getRequest().getAuthorizationNum());
    request.setReferenceNo("barry/jones&^%$&^%");
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertResponsesMatch(r, response);
    assertEquals("4111111111111111", response.getRequest().getCardNumber());

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertResponsesMatch(r, response);
    assertEquals("4111111111111111", response.getRequest().getCardNumber());

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertResponsesMatch(r, response);
    assertEquals("4111111111111111", response.getRequest().getCardNumber());
  }

  private void assertResponsesMatch(final Response r, final Response response) {
    assertEquals(r.getAvs(), response.getAvs());
    assertEquals(r.getBankMessage(), response.getBankMessage());
    assertEquals(r.getBankResponseCode(), response.getBankResponseCode());
    assertEquals(r.getBankResponseCode2(), response.getBankResponseCode2());
    assertEquals(r.getCavvResponse(), response.getCavvResponse());
    assertEquals(r.getCvv2(), response.getCvv2());
    assertEquals(r.getErrorDescription().trim(), response.getErrorDescription().trim());
    assertEquals(r.getErrorNumber(), response.getErrorNumber());
    assertEquals(r.getExactMessage(), response.getExactMessage());
    assertEquals(r.getExactResponseCode(), response.getExactResponseCode());
    assertEquals(r.getLogonMessage(), response.getLogonMessage());
//    assertEquals(r.getReceipt(), response.getReceipt());
    assertEquals(r.getRetrievalReferenceNo(), response.getRetrievalReferenceNo());
    assertEquals(r.getSequenceNo(), response.getSequenceNo());

    final MerchantDetails origMDetails = r.getMerchantDetails();
    final MerchantDetails actualMDetails = response.getMerchantDetails();
    assertEquals(origMDetails.getAddress(), actualMDetails.getAddress());
    assertEquals(origMDetails.getCity(), actualMDetails.getCity());
    assertEquals(origMDetails.getCountry(), actualMDetails.getCountry());
    assertEquals(origMDetails.getName(), actualMDetails.getName());
    assertEquals(origMDetails.getPostcode(), actualMDetails.getPostcode());
    assertEquals(origMDetails.getProvince(), actualMDetails.getProvince());
    assertEquals(origMDetails.getUrl(), actualMDetails.getUrl());

    final Request origRequest = r.getRequest();
    final Request actualRequest = response.getRequest();
    assertEquals(origRequest.getAuthorizationNum(), origRequest.getAuthorizationNum());
    assertEquals(origRequest.getCardholderName(),origRequest.getCardholderName());
    assertEquals(origRequest.getCavv(), actualRequest.getCavv());
    assertEquals(origRequest.getCavvAlgorithm(), actualRequest.getCavvAlgorithm());
    assertEquals(origRequest.getClientEmail(), actualRequest.getClientEmail());
//    assertEquals(origRequest.getClientIp(), actualRequest.getClientIp());
    assertEquals(origRequest.getCustomerRef(), actualRequest.getCustomerRef());
    assertEquals(origRequest.getCvdPresenceIndicator(), actualRequest.getCvdPresenceIndicator());
    assertEquals(origRequest.getEcommerceFlag(), actualRequest.getEcommerceFlag());
    assertEquals(origRequest.getCardExpiryDate(), actualRequest.getCardExpiryDate());
    assertEquals(origRequest.getLanguage(), actualRequest.getLanguage());
    assertEquals(origRequest.getPrimaryAccountNumber(), actualRequest.getPrimaryAccountNumber());
    assertEquals(origRequest.getExactId(), actualRequest.getExactId());
    assertEquals(origRequest.getPassword(), actualRequest.getPassword());
    assertEquals(origRequest.getReference3(), actualRequest.getReference3());
    assertEquals(origRequest.getReferenceNo(), actualRequest.getReferenceNo());
    assertEquals(origRequest.getSecureAuthRequired(), actualRequest.getSecureAuthRequired());
    assertEquals(origRequest.getSecureAuthResult(), actualRequest.getSecureAuthResult());
    assertEquals(origRequest.getSurchargeAmount(), actualRequest.getSurchargeAmount());
    assertEquals(origRequest.getTax1Amount(), actualRequest.getTax1Amount());
    assertEquals(origRequest.getTax1Number(), actualRequest.getTax1Number());
    assertEquals(origRequest.getTax2Amount(), actualRequest.getTax2Amount());
    assertEquals(origRequest.getTax2Number(), actualRequest.getTax2Number());
    assertEquals(origRequest.getTrack1(), actualRequest.getTrack1());
    assertEquals(origRequest.getTrack2(), actualRequest.getTrack2());
    assertEquals(origRequest.getTransactionTag(), actualRequest.getTransactionTag());
    assertEquals(origRequest.getTransactionType(), actualRequest.getTransactionType());
    assertEquals(origRequest.getUserName(), actualRequest.getUserName());
    assertEquals(origRequest.getCardVerificationStr1(), actualRequest.getCardVerificationStr1());
    assertEquals(origRequest.getCardVerificationStr2(), actualRequest.getCardVerificationStr2());
    assertEquals(origRequest.getXid(), actualRequest.getXid());
    assertEquals(origRequest.getZipCode(), actualRequest.getZipCode());
  }

}
