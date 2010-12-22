package ext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.templates.JavaExtensions;

/**
 * The Class CustomExtensions contains special methods used in the templates.
 */
public class CustomExtensions extends JavaExtensions {

	/**
	 * Slice long words in a String.
	 * 
	 * @param string
	 * @param length
	 *            maximum length of words
	 * @return string with sliced words
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

	public static String simpleHTML(String string) {
		string = string
				.replaceAll(
						"<(((/\\w*[^\\w\\s>])|([^/]\\w*[^\\w\\s>]))[^>]*[^/])(\\s?/)?>",
						"&lt;$1&gt;");
		string = string
				.replaceAll(
						"<([^\">]+)( [^>\"]*\"[^>\"]*\")*(( (href|src)=\"[^\"]+\")*)( [^>\"]*\"[^>\"]*\")*(\\s?/)?>",
						"<$1$3>");

		List<String> allowedTags = new ArrayList();
		allowedTags.add("b");
		allowedTags.add("code");
		allowedTags.add("blockquote");
		allowedTags.add("pre");
		allowedTags.add("strong");
		allowedTags.add("i");
		allowedTags.add("u");
		allowedTags.add("ul");
		allowedTags.add("li");
		allowedTags.add("em");
		allowedTags.add("br");
		allowedTags.add("a");
		allowedTags.add("img");
		allowedTags.add("p");
		allowedTags.add("hr");
		allowedTags.add("h2");

		StringBuffer cleanString = new StringBuffer();
		Pattern pattern = Pattern.compile("</?(\\w+)(\\s[^>]*)?(\\s?/)?>");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			if (!allowedTags.contains(matcher.group(1))) {
				matcher.appendReplacement(cleanString, "&lt;"
						+ matcher.group(1) + "&gt;");
			}
		}
		matcher.appendTail(cleanString);

		string = cleanString.toString().replaceAll("\n", "<br />");
		string = string.replaceAll("<br>", "<br />");

		return string;
	}
}