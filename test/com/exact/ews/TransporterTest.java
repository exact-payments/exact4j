package com.exact.ews;

import junit.framework.TestCase;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.CoderException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.KeyManagementException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: donch
 * Date: 14-Jul-2008
 * Time: 16:14:54
 */
public class TransporterTest extends TestCase {

  public TransporterTest(final String name) {
    super(name);
  }

  public void testSubmitTransaction() {
    try {
      final Transporter t = new Transporter(TestUtils.URL, null);
      t.setConnectionVerifier(new ConnectionVerifier());

      final Request request = getCreateRequest();

      final Response r = t.submit(request);
      assertTrue(r != null);

      assertTrue(r.getErrorDescription(), r.isApproved());

      assertEquals("00", r.getExactResponseCode());

      assertEquals(request.getAmount(), r.getRequest().getAmount());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception");
    }
  }

  public void testFindTransaction() throws Exception {
    final Transporter t = new Transporter(TestUtils.URL, null);
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


  public void testFindNonExistentTransaction()
  {
    try
    {
      final Transporter t = new Transporter(TestUtils.URL, null);
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

  public void testSuppliedConnectionVerifier() {
    try {
      // we need to use the IP Address because the connection verifier is only
      // invoked if the cert's domain name does not match the URL
      final Transporter t = new Transporter("https://66.199.168.91/", null);
      final Request request = getCreateRequest();

      final CertificateFactory fact = CertificateFactory.getInstance("X.509");
      final X509Certificate dotMacCert = (X509Certificate)fact.generateCertificate(new FileInputStream("test/samples/dot_mac_root.pem"));
      final X509Certificate issuerCert = (X509Certificate)fact.generateCertificate(new FileInputStream("certs/valicert_class2_root.crt"));
      final X509Certificate serverCert = (X509Certificate)fact.generateCertificate(new FileInputStream("certs/e-xact.com.crt"));

      // verifier with an incorrect server certificate
      final ConnectionVerifier incorrectServer = new ConnectionVerifier(dotMacCert, issuerCert);
      t.setConnectionVerifier(incorrectServer);

      try
      {
        t.submit(request);
        fail("HTTPS connection should fail");
      }
      catch (IOException e) { /* expected */ }

      // verifier with an incorrect issuer certificate
      final ConnectionVerifier incorrectIssuer = new ConnectionVerifier(issuerCert, dotMacCert);
      t.setConnectionVerifier(incorrectIssuer);

      try
      {
        t.submit(request);
        fail("HTTPS connection should fail");
      }
      catch (IOException e) { /* expected */ }

      // valid verifier
      final ConnectionVerifier correctVerifier = new ConnectionVerifier(serverCert, issuerCert);
      t.setConnectionVerifier(correctVerifier);

      final Response r = t.submit(request);
      assert(r != null);

      // NOTE: The web service will reject requests sent to the IP address,
      // so we expect a 'Bad Request'. However, it's enough to prove that
      // we've negotiated the SSL handshake correctly
      assertEquals(400, r.getErrorNumber());
      assertEquals("Bad Request", r.getErrorDescription());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception");
    }
  }

  public void testSupplyingClientCertificate() {
    if(!TestUtils.URL.toLowerCase().startsWith("https")) {
      return;
    }

    try
    {
      final char[] password = "4connection".toCharArray();

      final InputStream in = new FileInputStream("./certs/client.p12");
      final KeyStore clientKS = KeyStore.getInstance("PKCS12");
      clientKS.load(in, password);
      in.close();

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
      kmf.init(clientKS, password);

      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(kmf.getKeyManagers(), null, null);

      SSLSocketFactory sssf = ctx.getSocketFactory();

      final Transporter t = new Transporter(TestUtils.URL, Encoding.JSON);
      t.setSSLSocketFactory(sssf);

      final Request request = getCreateRequest();
      final Response r = t.submit(request);
      assert(r != null);
      assert r.isApproved();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Unexpected Exception");
    }
  }

  protected Request getCreateRequest() {
    final Request request = new Request(TransactionType.Purchase);
    request.setAmount(10.0f);
    request.setCardholderName("James Brown");
    request.setCardNumber("4111111111111111");
    request.setCardExpiryDate("0913");  // MMYY format
    
    request.setExactId(TestUtils.EmergisExactID);
    request.setPassword(TestUtils.EmergisPassword);

    return request;
  }

  protected Request getFindRequest(final int createdTag) {
    if(createdTag == 0)
      throw new IllegalArgumentException("getFindRequest(): createdTag cannot be 0");

    final Request request = new Request(TransactionType.TransactionDetails);
    request.setTransactionTag(createdTag);

    request.setExactId(TestUtils.EmergisExactID);
    request.setPassword(TestUtils.EmergisPassword);

    return request;
  }
}
