package com.exact.ews;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;

/**
 * User: donch
 * Date: 20-Jul-2008
 * Time: 19:37:24
 */
public class SoapTransportTest extends TransporterTest
{
  public SoapTransportTest(final String name)
  {
    super(name);
  }

  public void testSubmitTransaction()
  {
    try
    {
      final Transporter t = new Transporter(TestUtils.URL, Encoding.SOAP);
      t.setConnectionVerifier(new ConnectionVerifier());

      final Request request = getCreateRequest();

      final Response r = t.submit(request);

      assertTrue(r != null);
      assertTrue(r.getErrorDescription(), r.isApproved());

      assertEquals("00", r.getExactResponseCode());
      assertEquals(request.getAmount(), r.getRequest().getAmount());
      assertEquals(request.getCardExpiryDate(), r.getRequest().getCardExpiryDate());
      assertFalse(r.getReceipt().equals(""));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Unexpected Exception");
    }
  }

  public void testFindTransaction()
  {
    try
    {
      final Transporter t = new Transporter(TestUtils.URL, Encoding.SOAP);
      t.setConnectionVerifier(new ConnectionVerifier());

      // send a transaction...
      final Request originalRequest = getCreateRequest();
      final Response cr = t.submit(originalRequest);

      assertTrue(cr != null);
      assertTrue(cr.getErrorDescription(), cr.isApproved());
      assertEquals("00", cr.getExactResponseCode());

      Thread.sleep(10000);

      // now try finding the same transaction
      final Request request = getFindRequest(cr.getRequest().getTransactionTag());
      final Response fr = t.submit(request);

      assertTrue(fr != null);
      assertTrue(fr.getErrorDescription(), fr.isApproved());

      assertEquals(cr.getExactResponseCode(), fr.getExactResponseCode());
      assertEquals(cr.getRequest().getTransactionTag(), fr.getRequest().getTransactionTag());
      assertEquals(cr.getRequest().getAmount(), fr.getRequest().getAmount());
      assertEquals(cr.getRequest().getAuthorizationNum(), fr.getRequest().getAuthorizationNum());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Unexpected exception");
    }
  }

  public void testFindNonExistentTransaction()
  {
    try
    {
      final Transporter t = new Transporter(TestUtils.URL, Encoding.SOAP);
      t.setConnectionVerifier(new ConnectionVerifier());

      final Request request = getFindRequest(9000);
      final Response r = t.submit(request);

      assertTrue(r.isTransactionError());
      assertEquals(404, r.getErrorNumber());
      assertFalse(r.isApproved());
      assertFalse(r.isTransactionApproved());

      assertTrue(r.getReceipt().equals(""));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  public void testWithCvdPresenceIndicator() {
    try {
      final Request request = getCreateRequest();
      request.setCardVerificationStr1("Barry");
      request.setCardVerificationStr2("1234");
      request.setCvdPresenceIndicator(CvdPresenceIndicator.Present);

      final Transporter t = new Transporter(TestUtils.URL, Encoding.SOAP);
      t.setConnectionVerifier(new ConnectionVerifier());

      final Response r = t.submit(request);

      assertTrue(r != null);
      assertTrue(r.getErrorDescription(), r.isApproved());

      assertEquals("00", r.getExactResponseCode());
      assertEquals(request.getAmount(), r.getRequest().getAmount());
      assertEquals(request.getCardExpiryDate(), r.getRequest().getCardExpiryDate());
      assertFalse(r.getReceipt().equals(""));
    } catch (Exception e){
      e.printStackTrace();
      fail("Unexpected Exception");
    }
  }
}
