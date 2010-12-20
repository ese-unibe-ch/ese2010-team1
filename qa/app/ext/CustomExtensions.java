package ext;

import play.templates.JavaExtensions;

/**
 * The Class CustomExtensions contains special methods used in the templates.
 */
public class CustomExtensions extends JavaExtensions {
	// TODO write javadoc comment
	/**
	 * Slice.
	 * 
	 * @param string
	 *            the string
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String slice(String string, int length) {
		StringBuffer output = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < string.length(); i++) {
			output.append(string.charAt(i));
			if (string.charAt(i) == ' ') {
				counter = 0;
			} else {
				counter++;
				if (counter >= length) {
					output.append(' ');
					counter = 0;
				}
			}

		}
		return output.toString();
	}

	/**
	 * Trims a given string to a given length and adds three dots.
	 * 
	 * @param string
	 *            the given string
	 * @param length
	 *            the new lenghth of the string
	 * @return the trimmed string
	 */
	public static String trim(String string, int length) {
		if (length >= string.length())
			return string;

		int pos = 0;
		for (int i = 0; i < length - 3; i++) {
			if (string.charAt(i) == ' ')
				pos = i;
		}
		return (pos == 0) ? string.substring(0, length - 3) + "..." : string
				.substring(0, pos)
				+ " ...";
	}

}