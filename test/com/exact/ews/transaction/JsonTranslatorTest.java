package com.exact.ews.transaction;

import junit.framework.TestCase;
import com.exact.ews.transaction.JsonCoder;
import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import com.exact.ews.transaction.enums.ECommerceFlag;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.transaction.Response;
import com.exact.ews.TestUtils;

/**
 * User: donch
 * Date: 14-Jul-2008
 * Time: 16:49:46
 */
public class JsonTranslatorTest extends TestCase {
  public JsonTranslatorTest(final String name) {
    super(name);
  }

  public void testEncodeRequest() {
    final Request r = new Request(TransactionType.Purchase);
    r.setAmount(10.12f);
    r.setCardholderName("James Brown");
    r.setCardNumber("4111111111111111");
    r.setCardExpiryDate("1012");
    r.setExactId("AD0008-01");
    r.setPassword("7nfcpc7n");
    r.setReferenceNo("987987");

    try {
      final String encoded = new JsonCoder().encode(r);
      assertNotNull(encoded);

      // check all the fields are present
      assertTrue(encoded.indexOf("\"password\":\"7nfcpc7n\"") != -1);
      assertTrue(encoded.indexOf("\"cardholder_name\":\"James Brown\"") != -1);
      assertTrue(encoded.indexOf("\"gateway_id\":\"AD0008-01\"") != -1);
      assertTrue(encoded.indexOf("\"amount\":10.12") != -1);
      assertTrue(encoded.indexOf("\"transaction_type\":\"00\"") != -1);
      assertTrue(encoded.indexOf("\"reference_no\":\"987987\"") != -1);
      assertTrue(encoded.indexOf("\"cc_number\":\"4111111111111111\"") != -1);

      // and that ones we didn't specify aren't
      assertTrue(encoded.indexOf("surchargeAmount") == -1);
    } catch (Exception e) {
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
      final String encoded = new JsonCoder().encode(r);
      assertNotNull(encoded);

      // check all the fields are present
      assertTrue(encoded.indexOf("\"amount\":10.12") != -1);
      assertTrue(encoded.indexOf("\"authorization_num\":\"12345\"") != -1);
      assertTrue(encoded.indexOf("\"cc_expiry\":\"0913\"") != -1);
      assertTrue(encoded.indexOf("\"cardholder_name\":\"Simon Jones\"") != -1);
      assertTrue(encoded.indexOf("\"cc_number\":\"4111111111111111\"") != -1);

      assertTrue(encoded.indexOf("\"cc_verification_str1\":\"Some String\"") != -1);
      assertTrue(encoded.indexOf("\"cc_verification_str2\":\"1234\"") != -1);
      assertTrue(encoded.indexOf("\"cavv\":\"123456789\"") != -1);
      assertTrue(encoded.indexOf("\"cavv_algorithm\":11") != -1);
      assertTrue(encoded.indexOf("\"client_email\":\"simon@example.com\"") != -1);
      assertTrue(encoded.indexOf("\"client_ip\":\"127.0.0.1\"") != -1);
      assertTrue(encoded.indexOf("\"customer_ref\":\"REF-321\"") != -1);
      assertTrue(encoded.indexOf("\"cvd_presence_ind\":9") != -1);
      assertTrue(encoded.indexOf("\"ecommerce_flag\":7") != -1);
      assertTrue(encoded.indexOf("\"gateway_id\":\""+TestUtils.EmergisExactID+"\"") != -1);
      assertTrue(encoded.indexOf("\"language\":4") != -1);
      assertTrue(encoded.indexOf("\"password\":\""+TestUtils.EmergisPassword+"\"") != -1);
      assertTrue(encoded.indexOf("\"pan\":\"123456789987654321\"") != -1);
      assertTrue(encoded.indexOf("\"reference_3\":\"REF-123\"") != -1);
      assertTrue(encoded.indexOf("\"reference_no\":\"REF-456\"") != -1);
      assertTrue(encoded.indexOf("\"secure_auth_required\":2") != -1);
      assertTrue(encoded.indexOf("\"secure_auth_result\":5") != -1);
      assertTrue(encoded.indexOf("\"surcharge_amount\":11.34") != -1);

      assertTrue(encoded.indexOf("\"tax1_amount\":9.87") != -1);
      assertTrue(encoded.indexOf("\"tax1_number\":\"789\"") != -1);
      assertTrue(encoded.indexOf("\"tax2_amount\":1.23") != -1);
      assertTrue(encoded.indexOf("\"tax2_number\":\"321\"") != -1);
      assertTrue(encoded.indexOf("\"track1\":\"%B4111111111111111^Jones/Simon ^1309101063510010000000306000000?\"") != -1);
      assertTrue(encoded.indexOf("\"track2\":\";4111111111111111=1309101420320192611?\"") != -1);
      assertTrue(encoded.indexOf("\"transaction_tag\":5678") != -1);
      assertTrue(encoded.indexOf("\"transaction_type\":\"00\"") != -1);
      assertTrue(encoded.indexOf("\"user_name\":\"user name\"") != -1);
      assertTrue(encoded.indexOf("\"xid\":\"x678\"") != -1);
      assertTrue(encoded.indexOf("\"zip_code\":\"10010\"") != -1);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception");
    }
    
  }

  public void testCantEncodeReadOnlyRequest() {
    final ReadOnlyRequest r = new ReadOnlyRequest(TransactionType.Purchase);
    r.setAmount(10.12f);
    r.setCardholderName("James Brown");
    r.setCardNumber("4111111111111111");
    r.setCardExpiryDate("1012");
    r.setExactId("AD0008-01");
    r.setPassword("7nfcpc7n");
    r.setReferenceNo("987987");

    try {
      final String encoded = new JsonCoder().encode(r);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException e)
    { /* do nothing */ }
    catch (Exception e) {
      fail("IllegalArgumentException expected");      
    }
  }

  public void testDecodeResponse() {
    final String sampleResponse = "{\"tax1_number\": null, \"cardholder_name\": \"Simon Brown\", \"filler\": null, \"trfid\": 854172, \"credit_card_type\": \"Visa\", \"cavv_algorithm\": null, \"terminal_id\": \"001\", \"ecommerce_flag\": 0, \"trace_no\": \"014001\", \"crc\": 0, \"pin_key\": null, \"terminal_ptr\": 16459656, \"avs\": null, \"local_merchant_ip\": null, \"transaction_ver\": null, \"pin_block\": null, \"bank_message\": \"APPROVED\", \"amount\": 10.13, \"transaction_approved\": 1, \"tax2_amount\": null, \"cc_expiry\": \"1210\", \"cavv_response\": 0, \"processing_centre\": 1, \"interac_trace_no\": null, \"xid\": null, \"gateway_id\": \"AD0008-01\", \"input_method\": 0, \"validation_code\": null, \"mac_value\": null, \"transaction_tag\": 2160, \"txflag2\": 0, \"sequence_no\": \"034\", \"client_email\": null, \"surcharge_amount\": null, \"sys\": 0, \"time_sent\": \"2008/07/14 17:31:21 +0000\", \"bank_resp_code\": \"000\", \"track2\": null, \"tax1_amount\": null, \"reversal_ind\": \"O\", \"cc_number\": \"4111111111111111\", \"abuff_length\": 6, \"force_key_request\": null, \"pan\": null, \"transaction_type\": \"00\", \"returned_aci\": 0, \"vital_trans_id\": null, \"pinpad_serial_no\": null, \"cmd\": 65418, \"len\": 493, \"transaction_id\": 0, \"txflag1\": 0, \"exact_message\": \"Transaction Normal\", \"client_ip\": null, \"zip_code\": null, \"time_received\": \"2008/07/14 17:31:21 +0000\", \"cc_verification_str2\": null, \"track1\": null, \"secure_auth_result\": null, \"sub\": 0, \"trans_desc\": null, \"authorization_num\": \"ET3121\", \"cvv2\": null, \"debit_key_index\": null, \"reference_3\": null, \"auth_source_code\": \"5\", \"reference_no\": \"987987\", \"requested_aci\": 0, \"account_type\": null, \"exact_resp_code\": \"00\", \"cc_number_ex\": null, \"currency_str\": \"USD\", \"tax2_number\": null, \"enc\": 2, \"tx_state\": 0, \"cc_verification_str1\": null, \"secure_auth_required\": null, \"trans_no\": \"01\", \"amount2\": null, \"cvd_presence_ind\": 0, \"mac_key\": null, \"user_name\": null, \"batch_no\": null, \"optional\": \"~uVisa\", \"retrieval_ref_no\": \"07143121\", \"transaction_source\": null, \"transaction_tag2\": 0, \"cashback_amount\": null, \"pid\": 64, \"bank_resp_code_2\": null, \"customer_ref\": null}";

    try {
      final Response r = new JsonCoder().decode(sampleResponse);
      assertNotNull(r);

      assertFalse(r.isTransactionError());
      assertTrue(r.isTransactionApproved());
			
      assertEquals("APPROVED", r.getBankMessage());
      assertEquals("00", r.getExactResponseCode());
      assertEquals("Transaction Normal", r.getExactMessage());

      final Request req = r.getRequest();
      assertNotNull(req);

      assertEquals("Simon Brown", req.getCardholderName());
      assertEquals(2160, req.getTransactionTag());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception");
    }
  }

  public void testDecodesGarbageInResponse() {
    final String sampleResponse = "{\"tax1_number\": null, \"cardholder_name\": \"Simon Brown Is Not A Really Long Name But This Is\", \"filler\": null, \"trfid\": 854172, \"credit_card_type\": \"Visa\", \"cavv_algorithm\": null, \"terminal_id\": \"001\", \"ecommerce_flag\": 0, \"trace_no\": \"014001\", \"crc\": 0, \"pin_key\": null, \"terminal_ptr\": 16459656, \"avs\": null, \"local_merchant_ip\": null, \"transaction_ver\": null, \"pin_block\": null, \"bank_message\": \"APPROVED\", \"amount\": 10.13, \"transaction_approved\": 1, \"tax2_amount\": null, \"cc_expiry\": \"1309\", \"cavv_response\": 0, \"processing_centre\": 1, \"interac_trace_no\": null, \"xid\": null, \"gateway_id\": \"AD0008-01\", \"input_method\": 0, \"validation_code\": null, \"mac_value\": null, \"transaction_tag\": 2160, \"txflag2\": 0, \"sequence_no\": \"034\", \"client_email\": null, \"surcharge_amount\": null, \"sys\": 0, \"time_sent\": \"2008/07/14 17:31:21 +0000\", \"bank_resp_code\": \"000\", \"track2\": null, \"tax1_amount\": null, \"reversal_ind\": \"O\", \"cc_number\": \"411111111111111166666666666\", \"abuff_length\": 6, \"force_key_request\": null, \"pan\": null, \"transaction_type\": \"00\", \"returned_aci\": 0, \"vital_trans_id\": null, \"pinpad_serial_no\": null, \"cmd\": 65418, \"len\": 493, \"transaction_id\": 0, \"txflag1\": 0, \"exact_message\": \"Transaction Normal\", \"client_ip\": null, \"zip_code\": null, \"time_received\": \"2008/07/14 17:31:21 +0000\", \"cc_verification_str2\": null, \"track1\": null, \"secure_auth_result\": null, \"sub\": 0, \"trans_desc\": null, \"authorization_num\": \"ET3121\", \"cvv2\": null, \"debit_key_index\": null, \"reference_3\": null, \"auth_source_code\": \"5\", \"reference_no\": \"987987\", \"requested_aci\": 0, \"account_type\": null, \"exact_resp_code\": \"00\", \"cc_number_ex\": null, \"currency_str\": \"USD\", \"tax2_number\": null, \"enc\": 2, \"tx_state\": 0, \"cc_verification_str1\": null, \"secure_auth_required\": null, \"trans_no\": \"01\", \"amount2\": null, \"cvd_presence_ind\": 0, \"mac_key\": null, \"user_name\": null, \"batch_no\": null, \"optional\": \"~uVisa\", \"retrieval_ref_no\": \"07143121\", \"transaction_source\": null, \"transaction_tag2\": 0, \"cashback_amount\": null, \"pid\": 64, \"bank_resp_code_2\": null, \"customer_ref\": \"This Is A Really LONG Customer Reference\"}";

    try {
      final Response r = new JsonCoder().decode(sampleResponse);
      assertNotNull(r);

      assertFalse(r.isTransactionError());
      assertTrue(r.isTransactionApproved());

      assertEquals("APPROVED", r.getBankMessage());
      assertEquals("00", r.getExactResponseCode());
      assertEquals("Transaction Normal", r.getExactMessage());

      final Request req = r.getRequest();
      assertNotNull(req);

      // the following are all invalid examples of the relevant fields, so ensure we
      // decode them without complaint.
      assertEquals("Simon Brown Is Not A Really Long Name But This Is", req.getCardholderName());
      assertEquals("411111111111111166666666666", req.getCardNumber());
      assertEquals("This Is A Really LONG Customer Reference", req.getCustomerRef());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception");
    }
  }
}
