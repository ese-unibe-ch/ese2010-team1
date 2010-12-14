package models.helper;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * The Class Utils implements methods used in different model classes.
 */
public class Utils {

	/**
	 * Encrypt string with the SHA-1 algorithm.
	 * 
	 * @param string
	 *            the string
	 * @return the encrypted string
	 */
	public static String encryptStringToSHA1(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(string.getBytes());
			return new BigInteger(1, md.digest(string.getBytes())).toString(16);
		} catch (Exception e) {
			return string;
		}
	}

}
