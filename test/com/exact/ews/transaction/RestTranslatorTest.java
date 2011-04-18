package com.exact.ews.transaction;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileReader;

import com.exact.ews.transaction.RestCoder;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import com.exact.ews.transaction.enums.ECommerceFlag;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.transaction.enums.AvsCode;
import com.exact.ews.transaction.enums.CavvResponse;
import com.exact.ews.transaction.enums.Cvv2Code;
import com.exact.ews.transaction.Response;
import com.exact.ews.TestUtils;

/**
 * User: donch
 * Date: 15-Jul-2008
 * Time: 15:58:18
 */
public class RestTranslatorTest extends TestCase
{
	public RestTranslatorTest(final String name)
	{
		super(name);
	}

	public void testEncodeRequest()
	{
		final Request r = new Request(TransactionType.Purchase);
		r.setAmount(10.12f);
		r.setCardholderName("James Brown");
		r.setCardNumber("4111111111111111");
		r.setCardExpiryDate("1012");
		r.setExactId("AD0008-01");
		r.setPassword("7nfcpc7n");
		r.setReferenceNo("987987");

		try
		{
			final String encoded = new RestCoder().encode(r);
			assertNotNull(encoded);

			// check all the fields are present
			assertTrue(encoded.indexOf("<Password>7nfcpc7n</Password>") != -1);
			assertTrue(encoded.indexOf("<CardHoldersName>James Brown</CardHoldersName>") != -1);
			assertTrue(encoded.indexOf("<ExactID>AD0008-01</ExactID>") != -1);
			assertTrue(encoded.indexOf("<DollarAmount>10.12</DollarAmount>") != -1);
			assertTrue(encoded.indexOf("<Transaction_Type>00</Transaction_Type>") != -1);
			assertTrue(encoded.indexOf("<Reference_No>987987</Reference_No>") != -1);
			assertTrue(encoded.indexOf("<Card_Number>4111111111111111</Card_Number>") != -1);

			// and that ones we didn't specify are still there
      final String expectedString = (System.getProperty("java.version").startsWith("1.6")) ?
        "<SurchargeAmount/>" :
        "<SurchargeAmount></SurchargeAmount>";
      assertTrue(encoded.indexOf(expectedString) != -1);
    }
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

  // ensure that if an attribute has a value, that value shows
  // up correctly in the encoded version of the request.
  // NOTE: the values set here are often nonsense, and most of these
  // values would never be set together, so do not use this test as
  // for an example of how various attributes work!
  public void testEncodingEverything() {
    final Request r = new Request(TransactionType.Purchase);
    r.setAmount(10.12f);
    r.setAuthorizationNum("12345");
    r.setCardExpiryDate("0913");
    r.setCardholderName("Simon Jones");
    r.setCardNumber(TestUtils.CCNumber);
    r.setCardVerificationStr1("Some String");
    r.setCardVerificationStr2("1234");
    r.setCavv("123456789");
    r.setCavvAlgorithm(11);
    r.setClientEmail("simon@example.com");
    r.setClientIp("127.0.0.1");
    r.setCustomerRef("REF-321");
    r.setCvdPresenceIndicator(CvdPresenceIndicator.NotAvailable);
    r.setEcommerceFlag(ECommerceFlag.NonAuthenticated3dSecure);
    r.setExactId(TestUtils.EmergisExactID);
    r.setLanguage(Language.French);
    r.setPassword(TestUtils.EmergisPassword);
    r.setPrimaryAccountNumber("123456789987654321");
    r.setReference3("REF-123");
    r.setReferenceNo("REF-456");
    r.setSecureAuthRequired(2);
    r.setSecureAuthResult(5);
    r.setSurchargeAmount(11.34f);
    r.setTax1Amount(9.87f);
    r.setTax1Number("789");
    r.setTax2Amount(1.23f);
    r.setTax2Number("321");
    r.setTrack1(TestUtils.Track1);
    r.setTrack2(TestUtils.Track2);
    r.setTransactionTag(5678);
    r.setUserName("user name");
    r.setXid("x678");
    r.setZipCode("10010");

    try {
      final String encoded = new RestCoder().encode(r);
      assertNotNull(encoded);

      // check all the fields are present
      assertTrue(encoded.indexOf("<DollarAmount>10.12</DollarAmount>") != -1);
      assertTrue(encoded.indexOf("<Authorization_Num>12345</Authorization_Num>") != -1);
      assertTrue(encoded.indexOf("<Expiry_Date>0913</Expiry_Date>") != -1);
      assertTrue(encoded.indexOf("<CardHoldersName>Simon Jones</CardHoldersName>") != -1);
      assertTrue(encoded.indexOf("<Card_Number>4111111111111111</Card_Number>") != -1);

      assertTrue(encoded.indexOf("<VerificationStr1>Some String</VerificationStr1>") != -1);
      assertTrue(encoded.indexOf("<VerificationStr2>1234</VerificationStr2>") != -1);
      assertTrue(encoded.indexOf("<CAVV>123456789</CAVV>") != -1);
      assertTrue(encoded.indexOf("<CAVV_Algorithm>11</CAVV_Algorithm>") != -1);
      assertTrue(encoded.indexOf("<Client_Email>simon@example.com</Client_Email>") != -1);
      assertTrue(encoded.indexOf("<Client_IP>127.0.0.1</Client_IP>") != -1);
      assertTrue(encoded.indexOf("<Customer_Ref>REF-321</Customer_Ref>") != -1);
      assertTrue(encoded.indexOf("<CVD_Presence_Ind>9</CVD_Presence_Ind>") != -1);
      assertTrue(encoded.indexOf("<Ecommerce_Flag>7</Ecommerce_Flag>") != -1);
      assertTrue(encoded.indexOf("<ExactID>"+TestUtils.EmergisExactID+"</ExactID>") != -1);
      assertTrue(encoded.indexOf("<Language>4</Language>") != -1);
      assertTrue(encoded.indexOf("<Password>"+TestUtils.EmergisPassword+"</Password>") != -1);
      assertTrue(encoded.indexOf("<PAN>123456789987654321</PAN>") != -1);
      assertTrue(encoded.indexOf("<Reference_3>REF-123</Reference_3>") != -1);
      assertTrue(encoded.indexOf("<Reference_No>REF-456</Reference_No>") != -1);
      assertTrue(encoded.indexOf("<Secure_AuthRequired>2</Secure_AuthRequired>") != -1);
      assertTrue(encoded.indexOf("<Secure_AuthResult>5</Secure_AuthResult>") != -1);
      assertTrue(encoded.indexOf("<SurchargeAmount>11.34</SurchargeAmount>") != -1);

      assertTrue(encoded.indexOf("<Tax1Amount>9.87</Tax1Amount>") != -1);
      assertTrue(encoded.indexOf("<Tax1Number>789</Tax1Number>") != -1);
      assertTrue(encoded.indexOf("<Tax2Amount>1.23</Tax2Amount>") != -1);
      assertTrue(encoded.indexOf("<Tax2Number>321</Tax2Number>") != -1);
      assertTrue(encoded.indexOf("<Track1>%B4111111111111111^Jones/Simon ^1309101063510010000000306000000?</Track1>") != -1);
      assertTrue(encoded.indexOf("<Track2>;4111111111111111=1309101420320192611?</Track2>") != -1);
      assertTrue(encoded.indexOf("<Transaction_Tag>5678</Transaction_Tag>") != -1);
      assertTrue(encoded.indexOf("<Transaction_Type>00</Transaction_Type>") != -1);
      assertTrue(encoded.indexOf("<User_Name>user name</User_Name>") != -1);
      assertTrue(encoded.indexOf("<XID>x678</XID>") != -1);
      assertTrue(encoded.indexOf("<ZipCode>10010</ZipCode>") != -1);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception");
    }

  }

  public void testCantEncodeReadOnlyResponse() {
    final ReadOnlyRequest r = new ReadOnlyRequest(TransactionType.Purchase);
    r.setAmount(10.12f);
    r.setCardholderName("James Brown");
    r.setCardNumber("4111111111111111");
    r.setCardExpiryDate("1012");
    r.setExactId("AD0008-01");
    r.setPassword("7nfcpc7n");
    r.setReferenceNo("987987");

    try {
      final String encoded = new RestCoder().encode(r);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException e)
    { /* do nothing */ }
    catch (Exception e) {
      fail("IllegalArgumentException expected");
    }
  }

  public void testDecodeResponse()
	{
		try
		{
      final String content = TestUtils.readFile("test/samples/rest.response.xml");

			final Response r = new RestCoder().decode(content);
			assertNotNull(r);

			assertFalse(r.isTransactionError());
			assertTrue(r.isTransactionApproved());

			assertEquals("APPROVED", r.getBankMessage());
			assertEquals("00", r.getExactResponseCode());
			assertEquals("Transaction Normal", r.getExactMessage());

      final MerchantDetails mDetails = r.getMerchantDetails();
      assertNotNull(mDetails);
      assertEquals("Paymentech Goods Buy and Sell(Calgary) 5300003", mDetails.getName());

			final Request req = r.getRequest();
			assertNotNull(req);

			assertEquals("Simon Brown", req.getCardholderName());
			assertEquals(2516, req.getTransactionTag());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}


  public void testDecodesDodgyResponse()
  {
    try
    {
      final String content = TestUtils.readFile("test/samples/rest.dodgy.response.xml");

			final Response r = new RestCoder().decode(content);
      assertNotNull(r);

      assertFalse(r.isTransactionError());
      assertTrue(r.isTransactionApproved());

      assertEquals("APPROVED", r.getBankMessage());
      assertEquals("00", r.getExactResponseCode());
      assertEquals("Transaction Normal", r.getExactMessage());

      final MerchantDetails mDetails = r.getMerchantDetails();
      assertNotNull(mDetails);
      assertEquals("Paymentech Goods Buy and Sell(Calgary) 5300003", mDetails.getName());

      final Request req = r.getRequest();
      assertNotNull(req);

      // the following are all invalid examples of the relevant fields, so ensure we
      // decode them without complaint.
      assertEquals("Simon Brown Is Not A Really Long Name But This Is", req.getCardholderName());
      assertEquals("nonsense", req.getCardNumber());
      assertEquals("This Is A Really LONG Customer Reference", req.getCustomerRef());
      assertEquals(2516, req.getTransactionTag());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Unexpected exception");
    }
  }

  public void testEverythingResponse()
	{
		try
		{
      final String content = TestUtils.readFile("test/samples/rest.everything.response.xml");

			final Response r = new RestCoder().decode(content);
			assertNotNull(r);

			assertFalse(r.isTransactionError());
			assertTrue(r.isTransactionApproved());

      assertEquals(AvsCode.ZipMatchOnly5Digit, r.getAvs());
      assertEquals("APPROVED", r.getBankMessage());
      assertEquals("000", r.getBankResponseCode());
      assertEquals("12", r.getBankResponseCode2());
      assertEquals(CavvResponse.AuthenticationCodeNotValid, r.getCavvResponse());
			assertEquals("This is\na CTR", r.getReceipt());
      assertEquals(Cvv2Code.IssuerNotCertified, r.getCvv2());
      assertEquals("Something", r.getErrorDescription());
      assertEquals(650, r.getErrorNumber());
      assertEquals("Transaction Normal", r.getExactMessage());
      assertEquals("00", r.getExactResponseCode());
      assertEquals("Processed by E-Xact", r.getLogonMessage());
      assertEquals("07203434", r.getRetrievalReferenceNo());
      assertEquals("258", r.getSequenceNo());

      final MerchantDetails mDetails = r.getMerchantDetails();
      assertNotNull(mDetails);
      assertEquals("Paymentech Goods Buy and Sell(Calgary) 5300003", mDetails.getName());
      assertEquals("4567 8th ave SE.", mDetails.getAddress());
      assertEquals("Calgary", mDetails.getCity());
      assertEquals("United States", mDetails.getCountry());
      assertEquals("V6B 2K4", mDetails.getPostcode());
      assertEquals("Alabama", mDetails.getProvince());
      assertEquals("http://somewhere.com/somehow/", mDetails.getUrl());

			final Request req = r.getRequest();
			assertNotNull(req);

      assertEquals("ET3434", req.getAuthorizationNum());
      assertEquals("Simon Brown", req.getCardholderName());
      assertEquals("4111111111111111", req.getCardNumber());
      assertEquals("123", req.getCavv());
      assertEquals(8, req.getCavvAlgorithm());
      assertEquals("simon@example.com", req.getClientEmail());
      assertEquals("127.0.0.1", req.getClientIp());
      assertEquals("REF-123", req.getCustomerRef());
      assertEquals(CvdPresenceIndicator.NotSupported, req.getCvdPresenceIndicator());
      assertEquals(ECommerceFlag.UnknownMoto, req.getEcommerceFlag());
      assertEquals("1210", req.getCardExpiryDate());
      assertEquals(Language.French, req.getLanguage());
      assertEquals("12345678900987654321", req.getPrimaryAccountNumber());
      assertEquals("AD0009-01", req.getExactId());
      assertEquals("passw0rd", req.getPassword());
      assertEquals("REF-456", req.getReference3());
      assertEquals("987987", req.getReferenceNo());
      assertEquals(1, req.getSecureAuthRequired());
      assertEquals(9, req.getSecureAuthResult());
      assertEquals(11.12f, req.getSurchargeAmount());
      assertEquals(23.23f, req.getTax1Amount());
      assertEquals("987654", req.getTax1Number());
      assertEquals(34.34f, req.getTax2Amount());
      assertEquals("123456", req.getTax2Number());
      assertEquals(TestUtils.Track1, req.getTrack1());
      assertEquals(TestUtils.Track2, req.getTrack2());
      assertEquals(2516, req.getTransactionTag());
      assertEquals(TransactionType.Purchase, req.getTransactionType());
      assertEquals("Superman", req.getUserName());
      assertEquals("verified", req.getCardVerificationStr1());
      assertEquals("6785", req.getCardVerificationStr2());
      assertEquals("x678", req.getXid());
      assertEquals("10100", req.getZipCode());
    }
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}
}
