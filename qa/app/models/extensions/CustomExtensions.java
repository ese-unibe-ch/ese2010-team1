package models.extensions;

import play.templates.JavaExtensions;

public class CustomExtensions extends JavaExtensions {

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