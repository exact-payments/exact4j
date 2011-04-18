package com.exact.ews;

/**
 * A utility class for Base64 encoding and decoding.
 */
final class Base64
{
  private static final byte[] base64Encode = {
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
    75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
    85, 86, 87, 88, 89, 90, 97, 98, 99, 100,
    101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
    111, 112, 113, 114, 115, 116, 117, 118, 119, 120,
    121, 122, 48, 49, 50, 51, 52, 53, 54, 55,
    56, 57, 43, 47
  };

  static String encode(final byte[] data)
  {
    if (data == null) {
      throw new IllegalArgumentException("Base64.encode(byte[]) - The supplied byte[] is null.");
    }

    final byte[] result = new byte[data.length * 2];
    int index = 0;

    for (int i = 0; i < data.length; i+=3)
    {
      final int remaining = data.length - i;
      final int inputSize = (remaining > 2) ? 3 : remaining;

      base64Encode(data, i, inputSize, result, index);
      index += 4;
    }

    return new String(result, 0, index);
  }

  /**
   * Encode 3 src bytes to 4 base64 dest bytes. The numberBytes [3->1] determines whether
   * or not we pad with '=''s.
   */
  private static byte[] base64Encode(byte[] src, int srcIndex, int numberBytes, byte[] dest, int destIndex)
  {
    // we have to shift left 24 in order to flush out the 1's that appear
    // when Java treats a value as negative that is cast from a byte to an int.
    int value = (numberBytes > 0 ? ((src[srcIndex] << 24) >>> 8) : 0)
              | (numberBytes > 1 ? ((src[srcIndex + 1] << 24) >>> 16) : 0)
              | (numberBytes > 2 ? ((src[srcIndex + 2] << 24) >>> 24) : 0);

    switch (numberBytes)
    {
      case 3:

        dest[destIndex] = base64Encode[(value >>> 18)];
        dest[destIndex + 1] = base64Encode[(value >>> 12) & 0x3f];
        dest[destIndex + 2] = base64Encode[(value >>> 6) & 0x3f];
        dest[destIndex + 3] = base64Encode[(value) & 0x3f];

        return dest;

      case 2:

        dest[destIndex] = base64Encode[(value >>> 18)];
        dest[destIndex + 1] = base64Encode[(value >>> 12) & 0x3f];
        dest[destIndex + 2] = base64Encode[(value >>> 6) & 0x3f];
        dest[destIndex + 3] = (byte) '=';

        return dest;

      case 1:

        dest[destIndex] = base64Encode[(value >>> 18)];
        dest[destIndex + 1] = base64Encode[(value >>> 12) & 0x3f];
        dest[destIndex + 2] = (byte) '=';
        dest[destIndex + 3] = (byte) '=';

        return dest;

      default:

        return dest;
    }
  }
}
