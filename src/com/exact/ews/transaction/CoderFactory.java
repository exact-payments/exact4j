package com.exact.ews.transaction;

import com.exact.ews.Encoding;

/*
 * User: donch
 * Date: 14-Jul-2008
 * Time: 12:29:28
 */
public class CoderFactory
{
  /*
   * Given a particular encoding encoding, returns a Coder object capable
   * of encoding to and decoding from that encoding.
   *
   * @param encoding  the encoding type you want to use.
   * @return  the relevant Coder. Defaults to a JSON translator.
   */
  public static Coder getTranslator(final Encoding encoding)
	{
		final Coder t;
		switch(encoding)
		{
			case JSON:
				t = new JsonCoder();
				break;
			case REST:
				t = new RestCoder();
				break;
			case SOAP:
				t = new SoapCoder();
				break;
			default:
        t = new JsonCoder();
    }

		return t;
	}
}
