package com.exact.ews.transaction;

import org.json.JSONObject;
import org.json.JSONException;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import com.exact.ews.transaction.enums.ECommerceFlag;

/*
 * User: donch
 * Date: 14-Jul-2008
 * Time: 12:04:54
 */

/*
 * A class responsible for encoding/decoding Requests and Responses to/from JSON
 */
class JsonCoder implements Coder {

  public String getContentType() {
    return "application/json";
  }

  public String getSuffix() {
    return ".json";
  }

  public String encode(final Request r) throws CoderException {
    if(r instanceof ReadOnlyRequest)
      throw new IllegalArgumentException("Coder.encode(Request): ReadOnlyRequests cannot be encoded. Please use Request class.");

    final JSONObject json = new JSONObject();
    try
    {
      json.put("gateway_id", r.getExactId());
      json.put("password", r.getPassword());
      json.put("transaction_type", r.getTransactionType().getCode());
      json.put("cc_number", r.getCardNumber());
      if(r.getTransactionTag() != 0) {
        json.put("transaction_tag", r.getTransactionTag());
      }
      json.put("track1", r.getTrack1());
      json.put("track2", r.getTrack2());
      json.put("pan", r.getPrimaryAccountNumber());
      json.put("authorization_num", r.getAuthorizationNum());
      json.put("cc_expiry", r.getCardExpiryDate());
      json.put("cardholder_name", r.getCardholderName());
      json.put("cc_verification_str1", r.getCardVerificationStr1());
      json.put("cc_verification_str2", r.getCardVerificationStr2());
      final CvdPresenceIndicator cvdIndicator = r.getCvdPresenceIndicator();
      if(cvdIndicator != null) {
        json.put("cvd_presence_ind", cvdIndicator.getCode());
      }
      json.put("tax1_number", r.getTax1Number());
      json.put("tax2_number", r.getTax2Number());
      json.put("secure_auth_required", r.getSecureAuthRequired());
      json.put("secure_auth_result", r.getSecureAuthResult());
      final ECommerceFlag ecommerceFlag = r.getEcommerceFlag();
      if(ecommerceFlag != null) {
        json.put("ecommerce_flag", ecommerceFlag.getCode());
      }
      json.put("xid", r.getXid());
      json.put("cavv", r.getCavv());
      json.put("cavv_algorithm", r.getCavvAlgorithm());
      json.put("reference_no", r.getReferenceNo());
      json.put("customer_ref", r.getCustomerRef());
      json.put("reference_3", r.getReference3());
      json.put("language", r.getLanguage().getCode());
      json.put("client_ip", r.getClientIp());
      json.put("client_email", r.getClientEmail());
      json.put("user_name", r.getUserName());
      json.put("zip_code", r.getZipCode());

      json.put("amount", r.getAmount());
      json.put("surcharge_amount", r.getSurchargeAmount());
      json.put("tax1_amount", r.getTax1Amount());
      json.put("tax2_amount", r.getTax2Amount());

      return json.toString();
    } catch (JSONException e) {
    	throw new CoderException(e);
    }
  }

  public Response decode(final String s) throws CoderException {
    final Response r = new Response();

    try {
      final JSONObject json = new JSONObject(s);

      r.setLogonMessage(json.optString("logon_message"));
      r.setErrorNumber(json.optInt("error_number"));
      r.setErrorDescription(json.optString("error_description"));

      r.setTransactionError(json.optInt("transaction_error") == 1);
      r.setTransactionApproved(json.optInt("transaction_approved") == 1);

      r.setExactResponseCode(json.optString("exact_resp_code"));
      r.setExactMessage(json.optString("exact_message"));
      r.setBankResponseCode(json.optString("bank_resp_code"));
      r.setBankResponseCode2(json.optString("bank_resp_code2"));
      r.setBankMessage(json.optString("bank_message"));

      r.setSequenceNo(json.optString("sequence_no"));
      r.setAvs(json.optString("avs"));
      r.setCvv2(json.optString("cvv2"));
      r.setRetrievalReferenceNo(json.optString("retrieval_ref_no"));
      r.setCavvResponse(json.optString("cavv_response"));

      r.setReceipt(json.optString("ctr"));

      // extract the merchant details
      final MerchantDetails details = new MerchantDetails(
							  json.optString("merchant_name"),
							  json.optString("merchant_address"),
							  json.optString("merchant_city"),
							  json.optString("merchant_province"),
							  json.optString("merchant_country"),
							  json.optString("merchant_postal"),
							  json.optString("merchant_url")
							  );
      r.setMerchantDetails(details);

      if (r.isApproved()) {
      	r.setRequest(decodeRequest(json));
      }

      return r;

    } catch (JSONException e) {
	throw new CoderException(e);
    }
  }

  private ReadOnlyRequest decodeRequest(final JSONObject json) throws JSONException {
    final TransactionType type = TransactionType.getTypeFromCode(json.optString("transaction_type"));
    final ReadOnlyRequest r = new ReadOnlyRequest(type);

    r.setExactId(json.optString("gateway_id"));
    r.setPassword(json.optString("password"));

    // note: null gets decoded as "null", not "" or null
    final String cc_number = json.optString("cc_number");
    if(!cc_number.equals("null"))
      r.setCardNumber(cc_number);

    r.setTransactionTag(json.optLong("transaction_tag"));
    r.setTrack1(json.optString("track1"));
    r.setTrack2(json.optString("track2"));
    r.setPrimaryAccountNumber(json.optString("pan"));
    r.setAuthorizationNum(json.optString("authorization_num"));
    r.setCardExpiryDate(json.optString("cc_expiry"));
    r.setCardholderName(json.optString("cardholder_name"));
    r.setCardVerificationStr1(json.optString("cc_verification_str1"));
    r.setCardVerificationStr2(json.optString("cc_verification_str2"));
    final CvdPresenceIndicator cvdInd = CvdPresenceIndicator.getFromCode(json.optInt("cvd_presence_ind"));
    r.setCvdPresenceIndicator(cvdInd);
    r.setTax1Number(json.optString("tax1_number"));
    r.setTax2Number(json.optString("tax2_number"));
    r.setSecureAuthRequired(json.optInt("secure_auth_required"));
    r.setSecureAuthResult(json.optInt("secure_auth_result"));
    final ECommerceFlag ecFlag = ECommerceFlag.getFromCode(json.optInt("ecommerce_flag"));
    r.setEcommerceFlag(ecFlag);
    r.setXid(json.optString("xid"));
    r.setCavv(json.optString("cavv"));
    r.setCavvAlgorithm(json.optInt("cavv_algorithm"));
    r.setReferenceNo(json.optString("reference_no"));
    r.setCustomerRef(json.optString("customer_ref"));
    r.setReference3(json.optString("reference_3"));
    final Language lang = Language.getFromCode(json.optInt("language"));
    r.setLanguage(lang);
    r.setClientIp(json.optString("client_ip"));
    r.setClientEmail(json.optString("client_email"));
    r.setUserName(json.optString("user_name"));

    r.setAmount(new Float(json.optDouble("amount", 0.0)));
    r.setSurchargeAmount(new Float(json.optDouble("surcharge_amount", 0.0)));
    r.setTax1Amount(new Float(json.optDouble("tax1_amount", 0.0)));
    r.setTax2Amount(new Float(json.optDouble("tax2_amount", 0.0)));

    return r;
  }
}
