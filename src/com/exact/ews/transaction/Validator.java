package com.exact.ews.transaction;

import com.exact.ews.transaction.enums.TransactionType;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * User: donch
 * Date: 23-Jul-2008
 * Time: 16:35:12
 */

/*
 * A class to encapsulate all the validation rules for Requests.
 *
 * The rules were obtained from the E-xact WebServices Programming Guide.
 */
class Validator
{
  private static BigDecimal MIN_AMOUNT = BigDecimal.ZERO;
  private static BigDecimal MAX_AMOUNT = new BigDecimal("99999.99");
  
  static boolean isValidLength(final String item, final int maxLength)
  {
    return (item != null) && (item.length() <= maxLength);
  }
  
  static boolean isValidAmount(final Float amount)
  {
    BigDecimal bdAmount = new BigDecimal(amount);
    bdAmount = bdAmount.setScale(2, RoundingMode.HALF_UP);
    return (bdAmount.compareTo(MIN_AMOUNT) >= 0) && (bdAmount.compareTo(MAX_AMOUNT) <= 0);
  }

  static List<String> validate(final Request r)
  {
    // delete any previous error messages
    final List<String> errors = new ArrayList<String>();

    // check for presence of exact_id & password
    if(isBlank(r.getExactId()))
      errors.add("ExactId has not been set.");

    if(isBlank(r.getPassword()))
      errors.add("Password has not been set.");

    // check for other mandatory fields based on the way in which the card number is supplied
    if ((r.getTransactionType() == TransactionType.BatchQuery) ||
        (r.getTransactionType() == TransactionType.BatchClose)) {
      // do nothing as these transactions have nothing to validate 
    }
    else if (r.getTransactionType() == TransactionType.TransactionDetails) {
      // transaction_tag & auth_num mandatory
      if(r.getTransactionTag() < 1) {
        errors.add("Transaction Tag must be greater than 0.");
      }
    }
    else if (r.getTransactionType() == TransactionType.ReferencedVoid) {
      validateReferencedVoid(r, errors);
    }
    else if (isIDebit(r.getTransactionType())) {
      validateForPan(r, errors);
    }
    else if (r.getTransactionTag() != 0) {
      validateForTransactionTag(r, errors);
    }
    else if (! isBlank(r.getCardNumber())) {
      validateForCard(r, errors);
    }
    else if (! isBlank(r.getTrack1())) {
      validateForTrack1(r, errors);
    }
    else if (! isBlank(r.getTrack2())) {
      validateForTrack2(r, errors);
    }
    else {
      errors.add("One of the following must be supplied: Card Number, Track1, Track2 or TransactionTag.");
    }

    // validate formats
    final String cardError = validateCardNumber(r.getCardNumber());
    if (cardError != null) {
      errors.add(cardError);
    }
    final String expiryError = validateExpiryDate(r.getCardExpiryDate());
    if (expiryError != null) {
      errors.add(expiryError);
    }

    return errors;
  }

  //
  // Check for presence of mandatory fields when CardNumber is supplied
  private static void validateForCard(final Request r, final List<String> errors)
  {
    final TransactionType type = r.getTransactionType();

    // mandatory: transaction_type must != (30, 31, 32, 34, 35)
    if (isTaggedTransaction(type)) {
      errors.add("Invalid transaction type.");
    }

    // amount, cardholder_name always mandaory
    if (r.getAmount() <= 0) {
      errors.add("Amount is required.");
    }
    if (isBlank(r.getCardholderName())) {
      errors.add("Cardholder Name is required.");
    }

    // card_number & expiry_date mandatory for all except 50, 54
    // pan mandatory for only 50, 54
    if(isIDebit(type)) {
      if(isBlank(r.getPrimaryAccountNumber())) {
        errors.add("Primary Account Number is required.");
      }
    }
    else {
      if(isBlank(r.getCardNumber())) {
        errors.add("Card Number is required.");
      }
      if(isBlank(r.getCardExpiryDate())) {
        errors.add("Card Expiry Date is required.");
      }
    }

    // auth_number mandatory for (02, 03, 11, 12, 13)
    if((needsAuthorizationNum(type)) && isBlank(r.getAuthorizationNum())) {
      errors.add("Authorization Number is required.");
    }

    // reference_no mandatory for 60
    if((type == TransactionType.SecureStorage) && (isBlank(r.getReferenceNo()))) {
      errors.add("Reference Number is required.");
    }
  }

  //
  // Check for presence of mandatory fields when CardNumber is supplied
  // implicitly via TransactionTag
  private static void validateForTransactionTag(final Request r, final List<String> errors)
  {
    // mandatory: transaction_type must == (30, 31, 32, 34, 35)
    if (!isTaggedTransaction(r.getTransactionType())) {
      errors.add("Invalid transaction type.");
    }

    // transaction_tag, auth_num & amount mandatory
    if(isBlank(r.getAuthorizationNum())) {
      errors.add("Authorization Number is required.");
    }
    if((r.getAmount() <= 0) && (r.getTransactionType() != TransactionType.TaggedUpdate)) {
      errors.add("Amount is required.");
    }
    if(r.getTransactionTag() <= 0) {
      errors.add("Invalid Transaction Tag supplied.");
    }
    if(!(isBlank(r.getCardNumber()) &&
         isBlank(r.getTrack1()) &&
         isBlank(r.getTrack2()) &&
         isBlank(r.getPrimaryAccountNumber()))) {
      errors.add("Do not set any card or track information for tagged requests.");
    }
  }

  //
  // Check for presence of mandatory fields when CardNumber is supplied
  // via Track1
  private static void validateForTrack1(final Request r, final List<String> errors)
  {
    final TransactionType type = r.getTransactionType();

    // mandatory: transaction_type must != (30, 31, 32, 34, 35)
    if (isTaggedTransaction(type)) {
      errors.add("Invalid transaction type.");
    }

    // track1 mandatory, except 50,54
    // pan mandatory 50,54 only
    if(isIDebit(type)) {
      if(isBlank(r.getPrimaryAccountNumber())) {
        errors.add("Primary Account Number is required.");
      }
    }
    else {
      if(isBlank(r.getTrack1())) {
        errors.add("Track1 is required.");
      }
    }

    // amount mandatory for all
    if(r.getAmount() <= 0) {
      errors.add("Amount is required.");
    }

    // auth_number mandatory for (02, 03, 11, 12, 13)
    if((needsAuthorizationNum(type)) && isBlank(r.getAuthorizationNum())) {
      errors.add("Authorization Number is required.");
    }

    // reference_no mandatory for 60
    if((type == TransactionType.SecureStorage) && (isBlank(r.getReferenceNo()))) {
      errors.add("Reference Number is required.");
    }
  }

  //
  // Check for presence of mandatory fields when CardNumber is supplied
  // via Track2
  private static void validateForTrack2(final Request r, final List<String> errors)
  {
    final TransactionType type = r.getTransactionType();

    // mandatory: transaction_type must != (30, 31, 32, 34, 35, 50, 54)
    if (isTaggedTransaction(type) || isIDebit(type)) {
      errors.add("Invalid transaction type.");
    }

    // track2, expiry_date, cardholder_name, amount mandatory
    if(isBlank(r.getTrack2())) {
      errors.add("Track2 is required.");
    }
    if(isBlank(r.getCardholderName())) {
      errors.add("Cardholder Name is required.");
    }
    if(r.getAmount() <= 0) {
      errors.add("Amount is required");
    }

    // auth_number mandatory for (02, 03, 11, 12, 13)
    if((needsAuthorizationNum(type)) && isBlank(r.getAuthorizationNum())) {
      errors.add("Authorization Number is required.");
    }

    // reference_no mandatory for 60
    if((type == TransactionType.SecureStorage) && (isBlank(r.getReferenceNo()))) {
      errors.add("Reference Number is required.");
    }
  }

  //
  // Check for presence of mandatory fields when CardNumber is supplied
  // via Track2
  private static void validateForPan(final Request r, final List<String> errors)
  {
    final TransactionType type = r.getTransactionType();

    // mandatory: transaction_type must == (50, 54)
    if (!isIDebit(type)) {
      errors.add("Invalid transaction type.");
    }

    if(isBlank(r.getPrimaryAccountNumber())) {
      errors.add("PrimaryAccountNumber is required.");
    }
    if(isBlank(r.getCardholderName())) {
      errors.add("Cardholder Name is required.");
    }
    if(r.getAmount() <= 0) {
      errors.add("Amount is required.");
    }
  }

  private static void validateReferencedVoid(final Request r, final List<String> errors) {
    if(isBlank(r.getReferenceNo())) {
      errors.add("ReferenceNo is required.");
    }
    if(isBlank(r.getCustomerRef())) {
      errors.add("CustomerRef is required.");
    }
    if(r.getAmount() <= 0) {
      errors.add("Amount is required.");
    }

    if(r.getTransactionTag() != 0) {
      errors.add("Do not set a transaction tag for referenced void requests.");
    }

    if(!(isBlank(r.getCardNumber()) &&
         isBlank(r.getTrack1()) &&
         isBlank(r.getTrack2()) &&
         isBlank(r.getPrimaryAccountNumber()))) {
      errors.add("Do not set any card or track information for referenced void requests.");
    }
  }

  private static boolean isTaggedTransaction(final TransactionType type)
  {
    return ((type == TransactionType.TaggedPurchase) ||
      (type == TransactionType.TaggedPreAuth) ||
      (type == TransactionType.TaggedPreAuthCompletion) ||
      (type == TransactionType.TaggedVoid) ||
      (type == TransactionType.TaggedRefund) ||
      (type == TransactionType.TaggedOnlineDebitRefund) ||
      (type == TransactionType.TaggedUpdate));
  }

  private static boolean isIDebit(final TransactionType type)
  {
    return ((type == TransactionType.IDebitPurchase) ||
      (type == TransactionType.IDebitRefund));
  }

  private static boolean needsAuthorizationNum(final TransactionType type)
  {
    return ((type == TransactionType.PreAuthCompletion) ||
      (type == TransactionType.ForcedPost) ||
      (type == TransactionType.PurchaseCorrection) ||
      (type == TransactionType.RefundCorrection) ||
      (type == TransactionType.Void));
  }

  private static boolean isBlank(final String value)
  {
    return ((value == null) || (value.length() == 0));
  }

  private static String validateCardNumber(final String cardNumber)
  {
    // empty is automatically valid
    if(isBlank(cardNumber))
      return null;

    // must have length between 13 and 16
    if(cardNumber.length() < 13 || cardNumber.length() > 16)
      return "Card Number must be between 13 and 16 digits long.";
      
    // now do Luhn's algorithm
    // Counting from rightmost digit (which is the check digit) and moving left, double the value of every alternate digit.
    //    For any digits that thus become 10 or more, take the two numbers and add them together.
    // Add all these digits together. For example, if 1111 becomes 2121, then 2+1+2+1 is 6
    // If the total ends in 0 then the number is valid according to the Luhn formula
  	int total = 0;
		boolean modify = false;
		final char[] chars = cardNumber.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--)
		{
      int n = chars[i] - 48;
			if (modify)
			{
				n *= 2;
				if (n > 9) n -= 9;
			}
			total += n;
			modify = !modify;
		}

		return (total % 10 == 0) ? null : "Invalid Card Number supplied.";
  }
  
  private static String validateExpiryDate(final String expiryDate)
  {
    if(isBlank(expiryDate))
      return null;

    if (!expiryDate.matches("\\d{4}"))
      return "Card Expiry Date must be exactly 4 digits long.";

    // first two digits <= 12
    // date not in past
    final int month = Integer.parseInt(expiryDate.substring(0,2));
    final int year = Integer.parseInt(expiryDate.substring(2,4)) + 2000;
    if (month > 12)
      return "Card Expiry Date must be in MMYY format.";

    // force current time to beginning of month
    final Calendar now = Calendar.getInstance();
    now.set(Calendar.DAY_OF_MONTH, 1);
    now.set(Calendar.HOUR_OF_DAY, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);

    final Calendar expD = Calendar.getInstance();
    expD.set(year, month, 1, 0, 0, 0);

    if (!expD.after(now))
      return "Card Expiry Date must not be in the past.";

    return null;
  }
}
