package com.exact.ews.transaction;

/*
 * User: donch
 * Date: 21-Jul-2008
 * Time: 13:13:45
 */

/*
 * An Exception thrown when a problem occurred while encoding or decoding
 * Requests and/or Responses.
 */
public class CoderException extends Exception
{
  public CoderException(final String message)
  {
    super(message);
  }

  public CoderException(final Exception jsex)
  {
    this(jsex.getMessage());
    initCause(jsex);
  }
}
