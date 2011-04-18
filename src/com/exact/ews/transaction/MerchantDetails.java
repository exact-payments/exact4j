package com.exact.ews.transaction;

/*
 * User: donch
 * Date: 23-Jul-2008
 * Time: 15:53:58
 */

/*
 * A class to hold a Merchant's details.
 * Returned as part of a Gateway's response.
 */
public class MerchantDetails
{
  private String name, address, city, province, country, postcode, url;

  MerchantDetails(final String name,
                  final String address,
                  final String city,
                  final String province,
                  final String country,
                  final String postcode,
                  final String url)
  {
    this.name = name;
    this.address = address;
    this.city = city;
    this.province = province;
    this.country = country;
    this.postcode = postcode;
    this.url = url;
  }

  public String getName()
  {
    return name;
  }

  public String getAddress()
  {
    return address;
  }

  public String getCity()
  {
    return city;
  }

  public String getProvince()
  {
    return province;
  }

  public String getCountry()
  {
    return country;
  }

  public String getPostcode()
  {
    return postcode;
  }

  public String getUrl()
  {
    return url;
  }
}
