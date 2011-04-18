package com.exact.ews.transaction;

import org.junit.Test;
import org.junit.Before; 
import junit.framework.Assert;
import java.math.BigDecimal;

import com.exact.ews.transaction.enums.TransactionType;

public class ValidatorTest {
  
  @Test
  public void lengthValidation() {
    Assert.assertTrue(Validator.isValidLength("", 1));
    Assert.assertTrue(Validator.isValidLength("1", 1));
    Assert.assertFalse(Validator.isValidLength("12", 1));

    Assert.assertTrue(Validator.isValidLength("123456789", 10));
    Assert.assertTrue(Validator.isValidLength("1234567890", 10));
    Assert.assertFalse(Validator.isValidLength("12345678901", 10));
  }
  
  @Test
  public void amountValidation() {
    Assert.assertFalse(Validator.isValidAmount(-0.01f));
    Assert.assertTrue(Validator.isValidAmount(-0f));
    Assert.assertTrue(Validator.isValidAmount(0f));
    Assert.assertTrue(Validator.isValidAmount(0.01f));
    Assert.assertTrue(Validator.isValidAmount(50000.05f));
    Assert.assertTrue(Validator.isValidAmount(99999.99f));
    Assert.assertTrue(Validator.isValidAmount(99999.991f));
    Assert.assertFalse(Validator.isValidAmount(99999.999f));
    Assert.assertFalse(Validator.isValidAmount(100000f));
    Assert.assertFalse(Validator.isValidAmount(100000.00f));
  }
}