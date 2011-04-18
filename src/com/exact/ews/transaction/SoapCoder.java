package com.exact.ews.transaction;

import com.exact.ews.transaction.Request;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringWriter;

/*
 * User: donch
 * Date: 14-Jul-2008
 * Time: 11:55:54
 */

/*
 * A class responsible for encoding/decoding Requests and Responses to/from SOAP/XML
 */
class SoapCoder extends RestCoder {

  public String getSuffix() {
    return ".xmlsoap";
  }
  
  public String encode(final Request r) throws CoderException {
    if(r instanceof ReadOnlyRequest)
      throw new IllegalArgumentException("Coder.encode(Request): ReadOnlyRequests cannot be encoded. Please use Request class.");

    try {
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      final DocumentBuilder db = dbf.newDocumentBuilder();

      final Document document = db.newDocument();

      final Element envelope = document.createElement("soap:Envelope");
      envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
      envelope.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
      envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
      document.appendChild(envelope);

      final Element body = document.createElement("soap:Body");
      envelope.appendChild(body);

      final Element sac = document.createElement("n1:SendAndCommit");
      sac.setAttribute("xmlns:n1", "http://secure2.e-xact.com/vplug-in/transaction/rpc-enc/Request");
      sac.setAttribute("soap:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
      body.appendChild(sac);

      final Element sacs = document.createElement("SendAndCommitSource");
      sacs.setAttribute("xmlns:n2", "http://secure2.e-xact.com/vplug-in/transaction/rpc-enc/encodedTypes");
      sacs.setAttribute("xsi:type", "n2:Transaction");
      sac.appendChild(sacs);

      // now add the actual request elements
      appendRequestElements(document, sacs, r);

      final TransformerFactory tf = TransformerFactory.newInstance();
      final Transformer t = tf.newTransformer();

      final StringWriter sw = new StringWriter();
      t.transform(new DOMSource(document), new StreamResult(sw));

      return sw.getBuffer().toString();
    } catch (Exception e) {
      throw new CoderException(e);
    }
  }

  protected void parseDocument(final Response r, final Document document) throws CoderException {
    NodeList listOfResults = document.getElementsByTagName("types:TransactionResult");
    if(listOfResults.getLength() != 0) {
      // we have a normal response, so parse it
      final Element root = (Element)listOfResults.item(0);
      extractResponseElements(r, root);
    } else {
      listOfResults = document.getElementsByTagName("soap:Fault");
      if(listOfResults.getLength() != 0) {
      	// we have a SOAP Fault response, so parse it
      	final Element root = (Element)listOfResults.item(0);

      	extractFaultElements(r, root);
      }
    }
  }

  /*
   * We have a SOAP Fault.
   * If there is a <detail> element, then parse it for an error_number and error_description. If
   * there is no <detail> element, then throw an exception using the faultString as the message.
   */
  private void extractFaultElements(final Response r, final Element e) throws CoderException {
    // get the faultString
    final NodeList faultStrings = e.getElementsByTagName("faultstring");
    if(faultStrings.getLength() != 0) {
      final Element faultString = (Element)faultStrings.item(0);

      // now check to see if we have a details element as well
      boolean retrievedErrorFromDetails = false;
      final NodeList detailElems = e.getElementsByTagName("detail");
      if(detailElems.getLength() != 0) {
      	final Element detailElem = (Element)detailElems.item(0);
      	final NodeList errorElems = detailElem.getElementsByTagName("error");
      	if(errorElems.getLength() != 0) {
      	  final Element errorElem = (Element)errorElems.item(0);

      	  final String errorNum = errorElem.getAttribute("number");
      	  r.setErrorNumber(Integer.parseInt(errorNum));
      	  r.setErrorDescription(errorElem.getAttribute("description"));
      	  retrievedErrorFromDetails = true;
      	}
      }

      if(!retrievedErrorFromDetails) {
      	// we do not have a detail error, so throw an exception
      	throw new CoderException(faultString.getTextContent());
      }
    }
  }
}
