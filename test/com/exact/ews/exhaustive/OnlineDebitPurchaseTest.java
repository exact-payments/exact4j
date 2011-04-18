package com.exact.ews.exhaustive;

import com.exact.ews.exhaustive.BaseTestCase;
import com.exact.ews.Encoding;
import com.exact.ews.TestUtils;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;

/**
 * User: donch
 * Date: 05-Aug-2009
 * Time: 17:16:07
 */
public class OnlineDebitPurchaseTest extends BaseTestCase
{
  public OnlineDebitPurchaseTest(final String name) {
    super(name);
  }

  public void testMandatory() {
    final Request request = new Request(TransactionType.IDebitPurchase);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("ExactId has not been set."));
    request.setExactId(TestUtils.EmergisExactID);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Password has not been set."));
    request.setPassword(TestUtils.EmergisPassword);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("PrimaryAccountNumber is required."));
    request.setPrimaryAccountNumber(TestUtils.CCNumber);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Cardholder Name is required."));
    request.setCardholderName(TestUtils.CardholderName);
    assertFalse(request.isValid());

    assertTrue(request.getErrors().contains("Amount is required."));
    request.setAmount(10.0f);
    assertTrue(request.isValid());
  }

  public void testByPan() {
    final Request request = new Request(TransactionType.IDebitPurchase);
    request.setExactId(TestUtils.ChaseExactID);
    request.setPassword(TestUtils.ChasePassword);
    request.setPrimaryAccountNumber(TestUtils.CCNumber);
    request.setCardholderName(TestUtils.CardholderName);
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);
  }

  public void testByPanInTrack2Format() {
    final Request request = new Request(TransactionType.IDebitPurchase);
    request.setExactId(TestUtils.ChaseExactID);
    request.setPassword(TestUtils.ChasePassword);
    request.setPrimaryAccountNumber(TestUtils.Track2);
    request.setCardholderName(TestUtils.CardholderName);
    request.setAmount(10.0f);
    assertTrue(request.isValid());

    // JSON
    Response response = submit(request, Encoding.JSON);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    // REST
    response = submit(request, Encoding.REST);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);

    // SOAP
    response = submit(request, Encoding.SOAP);
    assertTrue(response.isApproved());
    assertEquals(request.getAmount(), response.getRequest().getAmount());
    checkDebitCardDetails(response);
  }
}
