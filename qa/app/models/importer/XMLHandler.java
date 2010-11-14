package models.importer;

import java.util.HashMap;
import java.util.Map;

import models.Question;
import models.User;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	private static StringBuilder builder;
	private StringBuilder report;

	private int level = -1;

	private int userCount = 0;
	private int questionCount = 0;
	private int answerCount = 0;

	private static Map<String, String> dataMap;

	// private List<String> tagList;

	public XMLHandler() {

		this.report = new StringBuilder();
	}

	/**
	 * This method is called when a new element begins.
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes atts) {

		builder = new StringBuilder();
		checkStartElement(qName, atts);
		level++;
	}

	/**
	 * This method is called when the parser encounters plain text (not XML
	 * elements). This method stores the text in the StringBuilder
	 */
	public void characters(char[] ch, int start, int length) {

		builder.append(ch, start, length);
	}

	/**
	 * This method is called when an element is closed.
	 */
	public void endElement(String uri, String localName, String qName) {
		level--;
		checkEndElement(qName);

	}

	private void checkStartElement(String qName, Attributes atts) {
		switch (level) {

		case 1:

			dataMap = new HashMap<String, String>();

			dataMap.put("id", atts.getValue(0));
			// System.out.println(atts.getValue(0));
			break;

		default:

			System.out.println("Start: " + level + " " + qName + " "
					+ atts.getValue(0));
			break;
		}
	}

	private void checkEndElement(String qName) {

		switch (level) {

		case 1:
			if (qName == "user") {
				System.out.println(level + " should create user");
				createUser();
				break;
			}

			if (qName == "question") {

				createQuestion();
				break;
			}
			if (qName == "answer") {

				// createAnswer();
				break;
			}

		case 2:
			dataMap.put(qName, builder.toString());
			break;

		default:
			System.out.println("End: " + level + " " + qName);
			break;
		}
	}

	private void createUser() {

		User userExists = User.find("byFakeId",
				Long.parseLong(dataMap.get("id"))).first();
		// check if userid is already in the database
		if (userExists == null) {

			// create user and set attributes
			User user = new User(dataMap.get("displayname"), dataMap
					.get("email"), dataMap.get("password"));
			user.fakeId = Long.parseLong(dataMap.get("id"));
			user.isAdmin = Boolean.parseBoolean(dataMap.get("ismoderator"));
			user.save();

			// TS to remove, just to see the progress for developing
			report.append("User " + dataMap.get("displayname") + " created\n");
			userCount++;
		} else {

			report.append("User {" + Long.parseLong(dataMap.get("id"))
					+ "} already exists \n");
		}

	}

	private void createQuestion() {

		User owner = User.find("byFakeId",
				Long.parseLong(dataMap.get("ownerid"))).first();

		if (owner != null) {

			Question question = owner.addQuestion(dataMap.get("title"), dataMap
					.get("body"));
			question.fakeId = Long.parseLong(dataMap.get("id"));
			question.save();
			questionCount++;

		} else {

			report.append("ERROR: Question {"
					+ Long.parseLong(dataMap.get("id")
							+ "} could not be imported \n"));
		}

		questionCount++;

	}

	private void createAnswer() {

		answerCount++;

	}

	public int getUserCount() {

		return this.userCount;
	}

	public int getQuestionCount() {

		return this.userCount;
	}

	/** This method is called when warnings occur */
	public void warning(SAXParseException exception) {
		System.err.println("WARNING: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/** This method is called when errors occur */
	public void error(SAXParseException exception) {
		System.err.println("ERROR: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/** This method is called when non-recoverable errors occur. */
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("FATAL: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		throw (exception);
	}

	public String getReport() {

		return this.report.toString();
	}
}
