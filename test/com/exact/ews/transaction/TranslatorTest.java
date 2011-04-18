package com.exact.ews.transaction;

import junit.framework.TestCase;
import com.exact.ews.Encoding;
import com.exact.ews.transaction.RestCoder;
import com.exact.ews.transaction.SoapCoder;
import com.exact.ews.transaction.CoderFactory;
import com.exact.ews.transaction.JsonCoder;
import com.exact.ews.transaction.Coder;

/**
 * User: donch
 * Date: 14-Jul-2008
 * Time: 16:33:57
 */
public class TranslatorTest extends TestCase
{
	public TranslatorTest(final String name)
	{
		super(name);
	}

	public void testFactory()
	{
		Coder t = CoderFactory.getTranslator(Encoding.JSON);
		assert (t instanceof JsonCoder);

		t = CoderFactory.getTranslator(Encoding.REST);
		assert (t instanceof RestCoder);


		t = CoderFactory.getTranslator(Encoding.SOAP);
		assert (t instanceof SoapCoder);
	}
}