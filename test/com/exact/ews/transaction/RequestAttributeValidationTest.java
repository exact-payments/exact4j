package com.exact.ews.transaction;

import org.junit.Test;
import org.junit.Before; 
import junit.framework.Assert;
import java.math.BigDecimal;

import com.exact.ews.transaction.enums.TransactionType;

public class RequestAttributeValidationTest {
  
  private Request request = null;
  
  @Before
  public void setup() {
    request = new Request(TransactionType.Purchase);
  }
  
  @Test
  public void authorizationNum() {
    // check OK length
    try {
      request.setAuthorizationNum("12345678");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setAuthorizationNum("123456789");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void cardholderName() {
    // check OK length
    try {
      request.setCardholderName("123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setCardholderName("1234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void cavv() {
    // check OK length
    try {
      request.setCavv("1234567890123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setCavv("12345678901234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void clientEmail() {
    // check OK length
    try {
      request.setClientEmail("123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long (over 50)
    try {
      request.setClientEmail("12345678901234567890123456789011234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void clientIp() {
    // check OK length
    try {
      request.setClientIp("123456789012345");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setClientIp("1234567890123456");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void customerRef() {
    // check OK length
    try {
      request.setCustomerRef("12345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setCustomerRef("123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void exactId() {
    // check OK length
    try {
      request.setExactId("1234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setExactId("12345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
/*  @Test
  public void pan() {
    // check OK length
    try {
      request.setPan("123456789012345678901234567890123456789");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setPan("1234567890123456789012345678901234567890");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
*/  
  @Test
  public void password() {
    // check OK length
    try {
      request.setPassword("123456789012345678091234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setPassword("1234567890123456780912345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void reference3() {
    // check OK length
    try {
      request.setReference3("123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setReference3("1234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void referenceNo() {
    // check OK length
    try {
      request.setReferenceNo("12345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setReferenceNo("123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void tax1Number() {
    // check OK length
    try {
      request.setTax1Number("12345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setTax1Number("123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void tax2Number() {
    // check OK length
    try {
      request.setTax2Number("12345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setTax2Number("123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void track1() {
    // check OK length
    try {
      request.setTrack1("1234567890123456789012345678901234567890123456789012345678901234567890123456789");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setTrack1("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  @Test
  public void track2() {
    // check OK length
    try {
      request.setTrack2("1234567890123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setTrack2("12345678901234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void cardVerificationStr1() {
    // check OK length
    try {
      request.setCardVerificationStr1("1234567890123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setCardVerificationStr1("12345678901234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void cardVerificationStr2() {
    // check OK length
    try {
      request.setCardVerificationStr2("1234");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setCardVerificationStr2("12345");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void xid() {
    // check OK length
    try {
      request.setXid("1234567890123456789012345678901234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setXid("12345678901234567890123456789012345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void zipCode() {
    // check OK length
    try {
      request.setZipCode("1234567890");
    } catch (IllegalArgumentException e) {
      Assert.fail("Length should be OK.");
    }

    // check too long
    try {
      request.setZipCode("12345678901");
      Assert.fail("Length should have been too long.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void amount() {
    // check OK
    try {
      request.setAmount(0.0f);
    } catch (IllegalArgumentException e) {
      Assert.fail("Amount should be OK.");
    }

    // check too little
    try {
      request.setAmount(-0.01f);
      Assert.fail("Amount should have been too little.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    // check too much
    try {
      request.setAmount(100000f);
      Assert.fail("Amount should have been too much.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }


  @Test
  public void surchargeAmount() {
    // check OK
    try {
      request.setSurchargeAmount(0.0f);
    } catch (IllegalArgumentException e) {
      Assert.fail("Amount should be OK.");
    }

    // check too little
    try {
      request.setSurchargeAmount(-0.01f);
      Assert.fail("Amount should have been too little.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    // check too much
    try {
      request.setSurchargeAmount(100000f);
      Assert.fail("Amount should have been too much.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void tax1Amount() {
    // check OK
    try {
      request.setTax1Amount(0.0f);
    } catch (IllegalArgumentException e) {
      Assert.fail("Amount should be OK.");
    }

    // check too little
    try {
      request.setTax1Amount(-0.01f);
      Assert.fail("Amount should have been too little.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    // check too much
    try {
      request.setTax1Amount(100000f);
      Assert.fail("Amount should have been too much.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void tax2Amount() {
    // check OK
    try {
      request.setTax2Amount(0.0f);
    } catch (IllegalArgumentException e) {
      Assert.fail("Amount should be OK.");
    }

    // check too little
    try {
      request.setTax2Amount(-0.01f);
      Assert.fail("Amount should have been too little.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    // check too much
    try {
      request.setTax2Amount(100000f);
      Assert.fail("Amount should have been too much.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
}
