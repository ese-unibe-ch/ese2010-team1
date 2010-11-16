package ext;

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

}