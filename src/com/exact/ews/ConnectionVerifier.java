package com.exact.ews;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/*
 * User: donch
 * Date: 31-Jul-2008
 * Time: 10:26:41
 */

/**
 * Class used to verify an SSL connection.
 *
 * If during an SSL handshake, the server's hostname doesn't match the URL being connected
 * to, this class will be used to verify the connection.
 * <p>
 * We obtain the certificate chain of the server being connected to, and compare the SHA-1
 * fingerprints of the Server Cert and the Issuer Cert, with the relevant fingerprints of
 * the certificates configured with this verifier.
 */
public class ConnectionVerifier implements HostnameVerifier
{
  // the cert of the server we're connecting to
  private X509Certificate serverCert = null;
  // the cert of the issuer of the server's cert
  private X509Certificate issuerCert = null;
  private MessageDigest digester = null;
  private byte[] serverCertFP = null;
  private byte[] issuerCertFP = null;

  /**
   * Construct the default verifier with certificates for https://api.e-xact.com
   *
   * @throws NoSuchAlgorithmException if we were unable to instantiate a SHA-1 MessageDigest
   * @throws CertificateException if there was a problem decoding/encoding the certificates
   * @throws FileNotFoundException  if we could not load the default certificates
   */
  public ConnectionVerifier() throws NoSuchAlgorithmException, CertificateException, FileNotFoundException
  {
    // load our default certificates
    final CertificateFactory fact = CertificateFactory.getInstance("X.509");
    this.serverCert = (X509Certificate)fact.generateCertificate(new FileInputStream("certs/e-xact.com.crt"));

    this.issuerCert = (X509Certificate)fact.generateCertificate(new FileInputStream("certs/valicert_class2_root.crt"));

    setUpDigester();
  }

  /**
   * Construct a verifier.
   *
   * @param serverCert  the Server Cert for the URL being connected to.
   * @param issuerCert  the certificate for the issuer of the Server Cert.
   * @throws NoSuchAlgorithmException if we were unable to instantiate a SHA-1 MessageDigest
   * @throws CertificateException if there was a problem decoding/encoding the certificates
   */
  public ConnectionVerifier(final X509Certificate serverCert, final X509Certificate issuerCert)
    throws NoSuchAlgorithmException, CertificateException
  {
    if(serverCert == null)
      throw new IllegalArgumentException("ConnectionVerifier(X509Certificate,X509Certificate) : Server certificate is null.");
    if(issuerCert == null)
      throw new IllegalArgumentException("ConnectionVerifier(X509Certificate,X509Certificate) : Issuer certificate is null.");

    this.serverCert = serverCert;
    this.issuerCert = issuerCert;

    setUpDigester();
  }

  private void setUpDigester() throws NoSuchAlgorithmException, CertificateEncodingException
  {
    digester = MessageDigest.getInstance("SHA-1");
    serverCertFP = digester.digest(serverCert.getEncoded());
    digester.reset();
    issuerCertFP = digester.digest(issuerCert.getEncoded());
    digester.reset();
  }

  /**
   * The callback method which will be invoked automatically by the SSL handshake
   * procedure <b>only</b> if the hostnames do not match.
   * 
   * @param hostname  the hostname being connected to
   * @param sslSession  the current SSL session
   * @return  true if the Server and Issuer certs match, false if they dont, or if an exception occurred
   */
  public boolean verify(final String hostname, final SSLSession sslSession)
  {
    try
    {
      // certificate 0 = server cert
      // certificates 1..n = issuer certs
      final javax.security.cert.X509Certificate[] certs = sslSession.getPeerCertificateChain();

      final byte[] theirServerCertFP = digester.digest(certs[0].getEncoded());
      digester.reset();
      if(!Arrays.equals(theirServerCertFP, serverCertFP))
        return false;

      // if there are more than one certificates, check them out too.
      boolean valid = false;
      for (int i = 1; i < certs.length && !valid; i++)
      {
        final byte[] theirIssuerCertFP = digester.digest(certs[i].getEncoded());
        digester.reset();
        valid = Arrays.equals(theirIssuerCertFP, issuerCertFP);
      }

      return valid;
    }
    catch (SSLPeerUnverifiedException e)
    {
      e.printStackTrace();
    }
    catch (javax.security.cert.CertificateEncodingException e)
    {
      e.printStackTrace();
    }
    return false;
  }
}
