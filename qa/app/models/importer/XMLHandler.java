package models.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Answer;
import models.Question;
import models.User;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class XMLHandler defines the rules how to treat qa xml files.
 */
public class XMLHandler extends DefaultHandler {

	/** The builder. */
	private static StringBuilder builder;

	/** The report. */
	private StringBuilder report;

	/** The level in the xml tree. */
	private int level = -1;

	/** The number of inserted users. */
	private int userCount = 0;

	/** The question count. */
	private int questionCount = 0;

	/** The answer count. */
	private int answerCount = 0;

	/** The data map. */
	private static Map<String, String> dataMap;

	/** The tag list. */
	private static List<String> tagList;

	/**
	 * Instantiates a new xML handler.
	 */
	public XMLHandler() {
		this.dataMap = new HashMap<String, String>();
		this.tagList = new ArrayList<String>();
		this.report = new StringBuilder();
	}

	/**
	 * This method is called when a new element begins.
	 * 
	 * @param uri
	 *            the uri
	 * @param localName
	 *            the local name
	 * @param qName
	 *            the q name
	 * @param atts
	 *            the atts
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
	 * 
	 * @param ch
	 *            the ch
	 * @param start
	 *            the start
	 * @param length
	 *            the length
	 */
	public void characters(char[] ch, int start, int length) {

		builder.append(ch, start, length);
	}

	/**
	 * This method is called when an element is closed.
	 * 
	 * @param uri
	 *            the uri
	 * @param localName
	 *            the local name
	 * @param qName
	 *            the q name
	 */
	public void endElement(String uri, String localName, String qName) {
		level--;
		checkEndElement(qName);

	}

	/**
	 * Check start element and save the informations for the different cases.
	 * 
	 * @param qName
	 *            the q name
	 * @param atts
	 *            the atts
	 */
	private void checkStartElement(String qName, Attributes atts) {
		switch (level) {

		case 1:

			dataMap.clear();
			dataMap.put("id", atts.getValue(0));
			break;
		case 2:

			if (qName.equals("tags")) {

				tagList.clear();

			}
			break;

		}
	}

	/**
	 * Check end element and process gathered data for the different cases.
	 * 
	 * @param qName
	 *            the q name
	 */
	private void checkEndElement(String qName) {

		switch (level) {

		case 1:
			if (qName.equals("user")) {
				createUser();
				break;
			}

			if (qName.equals("question")) {
				createQuestion();
				break;
			}
			if (qName.equals("answer")) {

				createAnswer();
				break;
			}
			break;

		case 2:

			dataMap.put(qName, builder.toString());

			break;
		case 3:
			if (qName.equals("tag")) {
				tagList.add(builder.toString());
			}
			break;
		}

	}

	/**
	 * Creates a new user.
	 */
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
			userCount++;
		} else {

			report.append("ERROR: User {" + dataMap.get("id")
					+ "} already exists \n");
		}

	}

	/**
	 * Creates a new question.
	 */
	private void createQuestion() {

		User owner = User.find("byFakeId",
				Long.parseLong(dataMap.get("ownerid"))).first();

		Question questionExists = Question.find("byFakeId",
				Long.parseLong(dataMap.get("id"))).first();

		if (owner != null && questionExists == null) {

			String body = removeCDATAContainer(dataMap.get("body"));

			// add question fields
			Question question = owner.addQuestion(dataMap.get("title"), body);
			question.fakeId = Long.parseLong(dataMap.get("id"));
			question.timestamp = new Date(Long.parseLong(dataMap
					.get("creationdate")));

			if (tagList != null && tagList.size() > 0) {
				for (String tag : tagList) {
					question.tagItWith(tag);
				}
			}
			question.save();
			questionCount++;

		} else {

			report.append("ERROR: Question {" + dataMap.get("id")
					+ "} could not be imported \n");
		}

	}

	/**
	 * Creates a new answer.
	 */
	private void createAnswer() {

		User owner = User.find("byFakeId",
				Long.parseLong(dataMap.get("ownerid"))).first();
		Question question = Question.find("byFakeId",
				Long.parseLong(dataMap.get("questionid"))).first();
		Answer answerExists = Answer.find("byFakeId",
				Long.parseLong(dataMap.get("id"))).first();

		if (owner != null && question != null && answerExists == null) {

			String body = removeCDATAContainer(dataMap.get("body"));

			Answer answer = question.answer(owner, body);
			answer.timestamp = new Date(Long.parseLong(dataMap
					.get("creationdate")));

			Boolean bestAnswer = Boolean.parseBoolean(dataMap.get("accepted"));
			if (bestAnswer && !question.isBestAnswerSet) {
				answer.isBestAnswer();
			} else if (bestAnswer) {
				report.append("ERROR: Answer {" + dataMap.get("id")
						+ "} could not be accepted \n");
			}

			answer.save();
			answerCount++;

		} else {

			report.append("ERROR: Answer {" + dataMap.get("id")
					+ "} could not be imported \n");
		}

	}

	/**
	 * Removes the cdata container from a given string.
	 * 
	 * @param s
	 *            the input string
	 * @return the cleaned string
	 */
	private String removeCDATAContainer(String s) {

		s = s.replaceAll("\\<!\\[CDATA\\[", "");
		s = s.replaceAll("\\]\\]\\>", "");

		return s;
	}

	/**
	 * Getter methods for the import report.
	 * 
	 * @return the user count
	 */

	public int getUserCount() {

		return this.userCount;
	}

	/**
	 * Gets the question count.
	 * 
	 * @return the question count
	 */
	public int getQuestionCount() {

		return this.questionCount;
	}

	/**
	 * Gets the answer count.
	 * 
	 * @return the answer count
	 */
	public int getAnswerCount() {

		return this.answerCount;
	}

	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	public String getReport() {

		return this.report.toString();
	}

	/**
	 * This method is called when warnings occur.
	 * 
	 * @param exception
	 *            the exception
	 */
	public void warning(SAXParseException exception) {
		System.err.println("WARNING: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/**
	 * This method is called when errors occur.
	 * 
	 * @param exception
	 *            the exception
	 */
	public void error(SAXParseException exception) {
		System.err.println("ERROR: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/**
	 * This method is called when non-recoverable errors occur.
	 * 
	 * @param exception
	 *            the exception
	 * @throws SAXException
	 *             the sAX exception
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("FATAL: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		throw (exception);
	}

}
