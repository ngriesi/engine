package engine.general.save;

import java.util.Base64;

/**
 * This class consists of only static methods for Base64 decoding and encoding
 */
@SuppressWarnings("unused")
public class EncoderBase64 {



    /**
     * encodes the string passed to it in the parameter and returns the encoded version
     * of this String
     *
     * @param string to be encoded
     * @return the encoded string
     */
    public static String encode(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    /**
     * decodes the string passed to it in the parameter and returns the decoded version
     * of this string
     *
     * @param string to be decoded
     * @return the decoded string
     */
    public static String decode(String string) {
        return new String(Base64.getDecoder().decode(string));
    }
}
