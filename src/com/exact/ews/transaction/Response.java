package com.exact.ews.transaction;

import com.exact.ews.transaction.enums.AvsCode;
import com.exact.ews.transaction.enums.CavvResponse;
import com.exact.ews.transaction.enums.Cvv2Code;

/*
 * User: donch
 * Date: 09-Jul-2008
 * Time: 16:11:51
 */
public class Response
{
	private int errorNumber = 0;
	private boolean transactionError = false, transactionApproved = false;
  private AvsCode avs = null;
  private Cvv2Code cvv2 = null;
  private CavvResponse cavvResponse = null;
  private String logonMessage = "",
                errorDescription = "",
                exactResponseCode = "",
                exactMessage = "",
                bankResponseCode = "",
                bankMessage = "",
                bankResponseCode2 = "",
                sequenceNo = "",
                retrievalReferenceNo = "",
                receipt = "";

	private ReadOnlyRequest request = null;
  private MerchantDetails merchantDetails = null;

  /*
   * Indicates whether or not the transaction was approved.
   *
   * A transaction is approved if
   * @return
   */
  public boolean isApproved()
  {
    return !isTransactionError() && isTransactionApproved();
  }
  
  public int getErrorNumber()
	{
		return errorNumber;
	}
	public void setErrorNumber(final int errorNumber)
	{
		this.errorNumber = errorNumber;
    setTransactionError(true);
    setTransactionApproved(false);
  }


	public boolean isTransactionError()
	{
		return transactionError;
	}
	void setTransactionError(final boolean transactionError)
	{
		this.transactionError = transactionError;
	}


	public boolean isTransactionApproved()
	{
		return transactionApproved;
	}
	void setTransactionApproved(final boolean transactionApproved)
	{
		this.transactionApproved = transactionApproved;
	}


	public String getLogonMessage()
	{
		return logonMessage;
	}
	void setLogonMessage(final String logonMessage)
	{
		this.logonMessage = logonMessage;
	}


	public String getErrorDescription()
	{
		return errorDescription;
	}
	public void setErrorDescription(final String errorDescription)
	{
		this.errorDescription = errorDescription;
	}


	public String getExactResponseCode()
	{
		return exactResponseCode;
	}
	void setExactResponseCode(final String exactResponseCode)
	{
		this.exactResponseCode = exactResponseCode;
	}


	public String getExactMessage()
	{
		return exactMessage;
	}
	void setExactMessage(final String exactMessage)
	{
		this.exactMessage = exactMessage;
	}


	public String getBankResponseCode()
	{
		return bankResponseCode;
	}
	void setBankResponseCode(final String bankResponseCode)
	{
		this.bankResponseCode = bankResponseCode;
	}


	public String getBankMessage()
	{
		return bankMessage;
	}
	void setBankMessage(final String bankMessage)
	{
		this.bankMessage = bankMessage;
	}


	public String getBankResponseCode2()
	{
		return bankResponseCode2;
	}
	void setBankResponseCode2(final String bankResponseCode2)
	{
		this.bankResponseCode2 = bankResponseCode2;
	}


	public String getSequenceNo()
	{
		return sequenceNo;
	}
	void setSequenceNo(final String sequenceNo)
	{
		this.sequenceNo = sequenceNo;
	}


	public AvsCode getAvs()
	{
		return avs;
	}
	void setAvs(final String avs)
  {
    this.avs = AvsCode.getFromCode(avs);
	}


	public Cvv2Code getCvv2()
	{
		return cvv2;
	}
	void setCvv2(final String cvv2Code)
	{
    if((cvv2Code != null) && !cvv2Code.equals("")) {
      this.cvv2 = Cvv2Code.getFromCode(cvv2Code.charAt(0));
    }
	}


	public String getRetrievalReferenceNo()
	{
		return retrievalReferenceNo;
	}
	void setRetrievalReferenceNo(final String retrievalReferenceNo)
	{
		this.retrievalReferenceNo = retrievalReferenceNo;
	}


	public CavvResponse getCavvResponse()
	{
		return cavvResponse;
	}
	void setCavvResponse(final String cavvCode)
	{
    if((cavvCode != null) && !cavvCode.equals("")) {
      this.cavvResponse = CavvResponse.getFromCode(cavvCode.charAt(0));
    }
	}


	public String getReceipt()
	{
		return receipt;
	}
	void setReceipt(final String receipt)
	{
		this.receipt = receipt;
	}


	public Request getRequest()
	{
		return request;
	}
	void setRequest(final Request request)
	{
		this.request = (ReadOnlyRequest)request;
	}

  public MerchantDetails getMerchantDetails()
  {
    return merchantDetails;
  }

  void setMerchantDetails(MerchantDetails merchantDetails)
  {
    this.merchantDetails = merchantDetails;
  }
}

