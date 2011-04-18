package com.exact.ews.transaction;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;

/*
 * User: donch
 * Date: 14-Jul-2008
 * Time: 11:58:49
 */

/**
 * An interface for classes which encode/decode REST, JSON and SOAP.
 */
public interface Coder
{
  /*
   * Encode a request object.
   *
   * @param r the Request object to encode
   * @return  a String representation of the encoded Request
   * @throws CoderException  if an error occurred while encoding the Request
   */
  String encode(final Request r) throws CoderException;

  /*
   * Decode a String into a Response object
   * @param s then encoded Response object
   * @return  a decoded Response object
   * @throws CoderException  if an error occurred while decoding the Response
   */
  Response decode(final String s) throws CoderException;

  /*
   * Get the Content-Type to be used with this encoding
   */
  String getContentType();

  /*
   * Get the request suffix to be used with this encoding.
   */
  String getSuffix();
}
