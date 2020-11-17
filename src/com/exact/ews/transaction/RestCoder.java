package com.exact.ews.transaction;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.Response;
import com.exact.ews.transaction.enums.TransactionType;
import com.exact.ews.transaction.enums.ECommerceFlag;
import com.exact.ews.transaction.enums.Language;
import com.exact.ews.transaction.enums.CvdPresenceIndicator;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

/*
 * User: donch
 * Date: 14-Jul-2008
 * Time: 12:05:53
 */

/*
 * A class responsible for encoding/decoding Requests and Responses to/from REST/XML
 */
class RestCoder implements Coder {
  public String getContentType() {
    return "application/xml";
  }


  public String getSuffix() {
    return ".xml";
  }

  public String encode(final Request r) throws CoderException {
    if(r instanceof ReadOnlyRequest)
      throw new IllegalArgumentException("Coder.encode(Request): ReadOnlyRequests cannot be encoded. Please use Request class.");

    try {
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      final DocumentBuilder db = dbf.newDocumentBuilder();

      final Document document = db.newDocument();

      final Element root = document.createElement("Transaction");
      document.appendChild(root);

      appendRequestElements(document, root, r);

      final TransformerFactory tf = TransformerFactory.newInstance();
      final Transformer t = tf.newTransformer();

      final StringWriter sw = new StringWriter();
      t.transform(new DOMSource(document), new StreamResult(sw));

      return sw.getBuffer().toString();
    } catch (Exception e) {
      throw new CoderException(e);
    }
  }

  protected void appendRequestElements(final Document document, final Element transactionRoot, final Request r) {
    appendElement(document, transactionRoot, "ExactID", r.getExactId());
    appendElement(document, transactionRoot, "Password", r.getPassword());
    appendElement(document, transactionRoot, "Transaction_Type", r.getTransactionType().getCode());

    appendElement(document, transactionRoot, "DollarAmount", r.getAmount().toString());
    appendElement(document, transactionRoot, "SurchargeAmount", r.getSurchargeAmount().toString());

    appendElement(document, transactionRoot, "Card_Number", r.getCardNumber());
    appendElement(document, transactionRoot, "CardHoldersName", r.getCardholderName());
    appendElement(document, transactionRoot, "Expiry_Date", r.getCardExpiryDate());
    appendElement(document, transactionRoot, "Track2", r.getTrack2());
    appendElement(document, transactionRoot, "Track1", r.getTrack1());
    appendElement(document, transactionRoot, "VerificationStr1", r.getCardVerificationStr1());
    appendElement(document, transactionRoot, "VerificationStr2", r.getCardVerificationStr2());
    final CvdPresenceIndicator cvdIndicator = r.getCvdPresenceIndicator();
    if(cvdIndicator != null) {
      appendElement(document, transactionRoot, "CVD_Presence_Ind", ((Integer)cvdIndicator.getCode()).toString());
    }

    appendElement(document, transactionRoot, "Authorization_Num", r.getAuthorizationNum());
    appendElement(document, transactionRoot, "Transaction_Tag", ((Long)r.getTransactionTag()).toString());
    appendElement(document, transactionRoot, "Reference_No", r.getReferenceNo());
    appendElement(document, transactionRoot, "Customer_Ref", r.getCustomerRef());
    appendElement(document, transactionRoot, "Reference_3", r.getReference3());
    final ECommerceFlag ecFlag = r.getEcommerceFlag();
    if(ecFlag != null) {
      appendElement(document, transactionRoot, "Ecommerce_Flag", ((Integer)ecFlag.getCode()).toString());
    }
    appendElement(document, transactionRoot, "PAN", r.getPrimaryAccountNumber());
    Language lang = r.getLanguage();
    if(lang == null) {
      lang = Language.English;
    }
    appendElement(document, transactionRoot, "Language", ((Integer)lang.getCode()).toString());
    appendElement(document, transactionRoot, "XID", r.getXid());

    appendElement(document, transactionRoot, "User_Name", r.getUserName());
    appendElement(document, transactionRoot, "ZipCode", r.getZipCode());

    appendElement(document, transactionRoot, "Tax1Amount", r.getTax1Amount().toString());
    appendElement(document, transactionRoot, "Tax1Number", r.getTax1Number());
    appendElement(document, transactionRoot, "Tax2Amount", r.getTax2Amount().toString());
    appendElement(document, transactionRoot, "Tax2Number", r.getTax2Number());

    appendElement(document, transactionRoot, "Secure_AuthRequired", ((Integer)r.getSecureAuthRequired()).toString());
    appendElement(document, transactionRoot, "Secure_AuthResult", ((Integer)r.getSecureAuthResult()).toString());

    appendElement(document, transactionRoot, "CAVV", r.getCavv());
    appendElement(document, transactionRoot, "CAVV_Algorithm", ((Integer)r.getCavvAlgorithm()).toString());

    appendElement(document, transactionRoot, "Client_Email", r.getClientEmail());
    appendElement(document, transactionRoot, "Client_IP", r.getClientIp());
  }


  public Response decode(final String s) throws CoderException {
    final Response r = new Response();

    // don't waste time parsing garbage
    if((s == null) || s.length() < 10)
      return r;

    try {
      final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

      final InputStream is = new ByteArrayInputStream(s.getBytes());
      final Document doc = db.parse(is);
      doc.getDocumentElement().normalize();

      parseDocument(r, doc);
    } catch (Exception e) {
      throw new CoderException(e);
    }

    return r;
  }

  protected void parseDocument(final Response r, final Document document) throws CoderException {
    final NodeList listOfResults = document.getElementsByTagName("TransactionResult");

    if(listOfResults.getLength() == 0)
      throw new CoderException("No TransactionResult present in String");
    final Element root = (Element)listOfResults.item(0);

    extractResponseElements(r, root);
  }
  
  protected void extractResponseElements(final Response r, final Element transactionRoot) {
    r.setAvs(getElementValue(transactionRoot, "AVS"));
    r.setBankMessage(getElementValue(transactionRoot, "Bank_Message"));
    r.setBankResponseCode(getElementValue(transactionRoot, "Bank_Resp_Code"));
    r.setBankResponseCode2(getElementValue(transactionRoot, "Bank_Resp_Code_2"));
    r.setCavvResponse(getElementValue(transactionRoot, "CAVV_Response"));
    r.setCvv2(getElementValue(transactionRoot, "CVV2"));
    r.setReceipt(getElementValue(transactionRoot, "CTR"));
    r.setErrorDescription(getElementValue(transactionRoot, "Error_Description"));

    String value = getElementValue(transactionRoot, "Error_Number");
    if(!value.equals(""))
      r.setErrorNumber(Integer.parseInt(value));
    r.setExactResponseCode(getElementValue(transactionRoot, "EXact_Resp_Code"));
    r.setExactMessage(getElementValue(transactionRoot, "EXact_Message"));
    r.setLogonMessage(getElementValue(transactionRoot, "LogonMessage"));
    r.setRetrievalReferenceNo(getElementValue(transactionRoot, "Retrieval_Ref_No"));
    r.setSequenceNo(getElementValue(transactionRoot, "SequenceNo"));

    value = getElementValue(transactionRoot, "Transaction_Approved");
    if(!value.equals(""))
      r.setTransactionApproved(Boolean.parseBoolean(value));

    value = getElementValue(transactionRoot, "Transaction_Error");
    if(!value.equals(""))
      r.setTransactionError(Boolean.parseBoolean(value));

    // extract the merchant details
    final MerchantDetails details = new MerchantDetails(
              getElementValue(transactionRoot, "MerchantName"),
							getElementValue(transactionRoot, "MerchantAddress"),
							getElementValue(transactionRoot, "MerchantCity"),
              getElementValue(transactionRoot, "MerchantProvince"),
							getElementValue(transactionRoot, "MerchantCountry"),
							getElementValue(transactionRoot, "MerchantPostal"),
							getElementValue(transactionRoot, "MerchantURL")
							);
    r.setMerchantDetails(details);

    r.setRequest(decodeRequest(transactionRoot));
  }

  private ReadOnlyRequest decodeRequest(final Element root) {
    final TransactionType type = TransactionType.getTypeFromCode(getElementValue(root, "Transaction_Type"));
    final ReadOnlyRequest r = new ReadOnlyRequest(type);
    
    r.setAuthorizationNum(getElementValue(root, "Authorization_Num"));
    r.setCardholderName(getElementValue(root, "CardHoldersName"));
    r.setCardNumber(getElementValue(root, "Card_Number"));
    r.setCavv(getElementValue(root, "CAVV"));

    String value = getElementValue(root, "CAVV_Algorithm");
    if(!value.equals(""))
      r.setCavvAlgorithm(Integer.parseInt(value));

    r.setClientEmail(getElementValue(root, "Client_Email"));
    r.setClientIp(getElementValue(root, "Client_IP"));
    r.setCustomerRef(getElementValue(root, "Customer_Ref"));

    value = getElementValue(root, "CVD_Presence_Ind");
    if(!value.equals("")) {
      r.setCvdPresenceIndicator(CvdPresenceIndicator.getFromCode(Integer.parseInt(value)));
    }

    value = getElementValue(root, "DollarAmount");
    if(!value.equals(""))
      r.setAmount(Float.parseFloat(value));

    value = getElementValue(root, "Ecommerce_Flag");
    if(!value.equals("")) {
      r.setEcommerceFlag(ECommerceFlag.getFromCode(Integer.parseInt(value)));
    }
    r.setExactId(getElementValue(root, "ExactID"));
    r.setCardExpiryDate(getElementValue(root, "Expiry_Date"));

    value = getElementValue(root, "Language");
    if(!value.equals("")) {
      r.setLanguage(Language.getFromCode(Integer.parseInt(value)));      
    }
    r.setPrimaryAccountNumber(getElementValue(root, "PAN"));
    r.setPassword(getElementValue(root, "Password"));
    r.setReference3(getElementValue(root, "Reference_3"));
    r.setReferenceNo(getElementValue(root, "Reference_No"));

    value = getElementValue(root, "Secure_AuthRequired");
    if(!value.equals(""))
      r.setSecureAuthRequired(Integer.parseInt(value));

    value = getElementValue(root, "Secure_AuthResult");
    if(!value.equals(""))
      r.setSecureAuthResult(Integer.parseInt(value));

    value = getElementValue(root, "SurchargeAmount");
    if(!value.equals(""))
      r.setSurchargeAmount(Float.parseFloat(value));

    value = getElementValue(root, "Tax1Amount");
    if(!value.equals(""))
      r.setTax1Amount(Float.parseFloat(value));
    r.setTax1Number(getElementValue(root, "Tax1Number"));

    value = getElementValue(root, "Tax2Amount");
    if(!value.equals(""))
      r.setTax2Amount(Float.parseFloat(value));
    r.setTax2Number(getElementValue(root, "Tax2Number"));
    r.setTrack1(getElementValue(root, "Track1"));
    r.setTrack2(getElementValue(root, "Track2"));

    value = getElementValue(root, "Transaction_Tag");
    if(!value.equals(""))
      r.setTransactionTag(Long.parseLong(value));

    r.setUserName(getElementValue(root, "User_Name"));
    r.setCardVerificationStr1(getElementValue(root, "VerificationStr1"));
    r.setCardVerificationStr2(getElementValue(root, "VerificationStr2"));
    r.setXid(getElementValue(root, "XID"));
    r.setZipCode(getElementValue(root, "ZipCode"));

    return r;
  }

  // add an element to the request
  private void appendElement(final Document document,
                             final Element root,
                             final String elementName,
                             final String elementValue) {
    String value = (elementValue == null) ? "" : elementValue;
    if (value.equals("0") || value.equals("0.0"))
      value = "";

    final Element e = document.createElement(elementName);
    e.appendChild(document.createTextNode(value));
    root.appendChild(e);
  }

  // obtain an element's value from the response
  private String getElementValue(final Element root, final String tagName) {
    final NodeList list = root.getElementsByTagName(tagName);

    String value = "";
    if (list.getLength() > 0) {
      final Element e = (Element)list.item(0);
      final NodeList children = e.getChildNodes();
      value = (children.getLength() == 0) ? "" : children.item(0).getNodeValue().trim();
    }
    return value;
  }
}
