package com.exact.ews;

import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.CoderFactory;
import com.exact.ews.transaction.Coder;
import com.exact.ews.transaction.CoderException;

import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/*
 * User: donch
 * Date: 09-Jul-2008
 * Time: 16:21:59
 */
public class Transporter {

  private String url = "https://api.e-xact.com";
  private Encoding encoding = Encoding.JSON;
  private HostnameVerifier verifier = null;
  private SSLSocketFactory sslSocketFactory = null;

  private boolean isUsingSSL = true;

  /**
   * Construct a default Transporter.
   *
   * The default transporter connects to E-xact's Webserver at <code>https://api.e-xact.com</code>, and
   * uses JSON encoding. It uses a <code>ConnectionVerifier</code> to verify the SSL connection in the
   * event that the server's hostname doesn't match the given URL.
   *
   * @throws IOException  if we're unable to read our default certificates
   * @throws NoSuchAlgorithmException if we're unable to instanticate a SHA-1 MessageDigest
   * @throws CertificateException if we have a problem decoding certificates
   */
  public Transporter() throws CertificateException, IOException, NoSuchAlgorithmException {
    verifier = new ConnectionVerifier();
  }

  /**
   * Construct a Transporter for a specified URL.
   *
   * <b>Note:</b> this constructor does not automatically use the default <code>ConnectionVerifier</code>
   * which is only configured for use with E-xact's systems. Java's default <code>HostnameVerifier</code>
   * will be used. You may supply your own example by using the <code>setConnectionVerifier</code> method.
   * <p/
   * If <code>url</code> is <code>null</code>, we will use the default E-xact API URL.<br/>
   * If <code>encoding</code> is <code>null</code>, we default to JSON.
   *
   * @param url the URL to which transactions will be submitted
   * @param encoding  the Encoding to use when submitting transactions
   */
  public Transporter(final String url, final Encoding encoding) {
    if (url != null)
      this.url = url;

    if (encoding != null)
      this.encoding = encoding;

    this.isUsingSSL = this.url.toLowerCase().startsWith("https");
  }

  /**
   * Set the HostnameVerifier to use with an SSL connection.
   *
   * If you specify your own HTTPS-based URL to submit transactions to, you should also
   * add your own HostnameVerifier.
   * 
   * @param verifier
   */
  public void setConnectionVerifier(final HostnameVerifier verifier) {
    if(verifier == null)
      throw new IllegalArgumentException("setConnectionVerifier(HostnameVerifier): HostnameVerifier is null.");

    this.verifier = verifier;
  }

  /**
   * Set the SSLSocketFactory to use with an SSL connection.
   *
   * If you want to programmatically specify a client certificate to be sent to E-xact's servers, you
   * must first configure a custom SSLSocketFactory with the relevant KeyStore and specify it here.
   *
   * @param sslSocketFactory
   */
  public void setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
    this.sslSocketFactory = sslSocketFactory;    
  }

  /**
   * Submit a request to the E-xact Webservice using the encoding specified for this Transporter.
   *
   * @param request the request to submit
   * @return  a Response
   * @throws IOException  if a problem occurs while communicating with the E-xact Webservice.
   * @throws CoderException if a problem occurs while encoding the Request, or decoding the Response
   */
  public Response submit(final Request request) throws IOException, CoderException {
    return submit(request,null);
  }
  /**
   * Submit a request to the E-xact Webservice using a specific encoding.
   *
   * If <code>encoding</code> is <code>null</code>, we use the encoding specified for this Transporter.
   * @param request the request to submit
   * @param encoding the encoding to used when submitting transactions, or Transporter's encoding if <code>null</code>
   * @return  a Response
   * @throws IOException  if a problem occurs while communicating with the E-xact Webservice.
   * @throws CoderException if a problem occurs while encoding the Request, or decoding the Response
   */
  public Response submit(final Request request, final Encoding encoding) 
    throws IOException, CoderException {
    if(request == null)
      throw new IllegalArgumentException("Transporter.submit(Request, Encoding): no request supplied.");
    if(!request.isValid())
      throw new IllegalArgumentException("Transporter.submit(Request, Encoding): request is not valid.");

    final Encoding txnEncoding = (encoding == null) ? this.encoding : encoding;

    final Coder coder = CoderFactory.getTranslator(txnEncoding);

    final String content = coder.encode(request);

    // set up basic authentication
    final String authStr = request.getExactId() + ":" + request.getPassword();
    final String encodedAuth = Base64.encode(authStr.getBytes());
    final HttpURLConnection connection;
    if (!request.isFindTransaction() || txnEncoding == Encoding.SOAP) {
      final URL finalUrl = new URL(url + "/transaction/v8");
      connection = connectionForUrl(finalUrl, encodedAuth, coder);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);

      if (txnEncoding == Encoding.SOAP) {
        final String soapAction = (request.isFindTransaction()) ? "TransactionInfo" : "SendAndCommit";
        connection.setRequestProperty("soapaction", "http://secure2.e-xact.com/vplug-in/transaction/rpc-enc/"+soapAction);
      }

      final OutputStream os = connection.getOutputStream();
      os.write(content.getBytes());
      os.close();
    } else {
      final String encodedUrl = "authorization_num=" + URLEncoder.encode(request.getAuthorizationNum(), "UTF-8") + "&reference_no=" + URLEncoder.encode(request.getReferenceNo(), "UTF-8");
      final URL finalUrl = new URL(url + "/transaction/v8/" + request.getTransactionTag() + coder.getSuffix() + "?" + encodedUrl);
      connection = connectionForUrl(finalUrl, encodedAuth, coder);
      connection.setRequestMethod("GET");
    }

    Response r = new Response();
    // try to decode a response IF
    // a) everything is OK, or
    // b) we have a problem, but we're using SOAP (might have a SOAP Fault)
    if((connection.getResponseCode() < 400) || txnEncoding == Encoding.SOAP) {
      // if the status code is 400+, then read from the error stream, otherwise the normal input stream
      final InputStream is = (connection.getResponseCode() >= 400) ? connection.getErrorStream() : connection.getInputStream();
      final StringBuffer response = new StringBuffer();
      int bread = 0;
      char[] stuff = new char[4096];
      final Reader reader = new InputStreamReader(is);
      while((bread = reader.read(stuff)) != -1) {
          response.append(stuff,0,bread);
      }
      reader.close();
      r = coder.decode(response.toString());
    }

    // if we had an error, and we haven't parsed the error code from a response,
    // then set it based on the HTTP headers.
    if((connection.getResponseCode() >= 400) && (r.getErrorNumber() == 0)) {
      r.setErrorNumber(connection.getResponseCode());
      r.setErrorDescription(connection.getResponseMessage());
    }

    connection.disconnect();

    return r;
  }

  private HttpURLConnection connectionForUrl(final URL url, final String encodedAuth, final Coder coder) throws IOException
  {
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    // add our verifier if one was specified
    if(isUsingSSL) {
      if (verifier != null) {
        ((HttpsURLConnection)connection).setHostnameVerifier(verifier);
      }
      if (sslSocketFactory != null) {
        ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
      }
    }
    connection.setRequestProperty("User-Agent", "exact4j v2.0");
    connection.setRequestProperty("Authorization", "Basic "+encodedAuth);
    connection.setUseCaches(false);
    connection.setAllowUserInteraction(false);
    connection.setRequestProperty("Content-type", coder.getContentType() +"; charset=UTF-8");
    connection.setRequestProperty("Accept", coder.getContentType());

    return connection;
  }
}
