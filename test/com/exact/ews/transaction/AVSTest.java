package com.exact.ews.transaction;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 */
public class AVSTest extends TestCase {

  public AVSTest(final String name) {
    super(name);
  }

  public void testPackAvsIntoVerificationStr1() {
    AVS avs = new AVS("1234567LOUGHEEDHIGHW", null, null, "902101234", null);
    assertEquals(avs.toAvsString(), "1234567LOUGHEEDHIGHW|902101234");
  }

  public void testprefersStreetAdrressOverPoBox() {
    AVS avs = new AVS("1234567LOUGHEEDHIGHW", null, "P.O.BOX24356", "902101234", null);
    assertEquals(avs.toAvsString(), "1234567LOUGHEEDHIGHW|902101234");
  }

}
