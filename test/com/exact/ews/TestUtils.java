package com.exact.ews;

import com.exact.ews.transaction.Request;
import com.exact.ews.transaction.enums.TransactionType;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * User: donch
 * Date: 31-Jul-2008
 * Time: 10:46:11
 */
public class TestUtils
{
  /*
   * Almost all transaction types are supported by Emergis processors, so our default
   * testing terminal is an Emergis terminal. The Chase details are used only for
   * testing those transation types which are only supported by Chase, e.g: Online Debit.
   */

  // production web service test gateway details
  public static final String URL = "https://api.e-xact.com";
  public static final String EmergisExactID = "A08738-01";
  public static final String EmergisPassword = "4apitest";
  public static final String ChaseExactID = "A08732-02";
  public static final String ChasePassword = "4apitest";
  public static final String ChaseBatchExactID = "A08732-01";
  public static final String ChaseBatchPassword = "4apitest";
  public static final String MonerisExactID = "A08735-01";
  public static final String MonerisPassword = "4apitest";

  // local development
//   public static final String URL = "http://ws.local";
// //  public static final String URL = "http://localhost:3000";
//   public static final String EmergisExactID = "AD0155-01";
//   public static final String EmergisPassword = "4apitest";
//   public static final String ChaseExactID = "AD0157-01";
//   public static final String ChasePassword = "4apitest";
//   public static final String ChaseBatchExactID = "AD0158-01";
//   public static final String ChaseBatchPassword = "4apitest";
//   public static final String MonerisExactID = "AD0154-01";
//   public static final String MonerisPassword = "4apitest";

  // cc details specified by cc number
  public static final String CCNumber = "4111111111111111";
  public static final String CCExpiry = "0913";
  public static final String CardholderName = "Simon Jones";

  // cc details by track1
  public static final String Track1 = "%B4111111111111111^Jones/Simon ^1309101063510010000000306000000?";

  // cc details by track2
  public static final String Track2 = ";4111111111111111=1309101420320192611?";

  public static String readFile(final String fileName) throws IOException
  {
    // read in the file
    final BufferedReader reader = new BufferedReader(new FileReader(fileName));
    final StringBuffer response = new StringBuffer();

    int bread = 0;
    char[] stuff = new char[4096];
    while((bread = reader.read(stuff)) != -1)
      response.append(stuff,0,bread);
    reader.close();

    return response.toString();
  }
}
