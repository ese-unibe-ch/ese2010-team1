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

// TS remove all system.out.println, if it's clear that the importer is working correct.
public class XMLHandler extends DefaultHandler {

	private static StringBuilder builder;
	private StringBuilder report;

	private int level = -1;

	private int userCount = 0;
	private int questionCount = 0;
	private int answerCount = 0;

	private static Map<String, String> dataMap;
	private static List<String> tagList;

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

			// TS instantiate in constructor and clear just the whole map, same
			// procedure @ tagList
			dataMap = new HashMap<String, String>();

			dataMap.put("id", atts.getValue(0));
			// System.out.println(atts.getValue(0));
			break;
		case 2:

			if (qName == "tags") {

				tagList = new ArrayList<String>();

			}
			break;

		default:

			// System.out.println("Start: " + level + " " + qName + " "
			// + atts.getValue(0));
			break;
		}
	}

	private void checkEndElement(String qName) {

		switch (level) {

		case 1:
			if (qName == "user") {
				// System.out.println(level + " should create user");
				createUser();
				break;
			}

			if (qName == "question") {

				createQuestion();
				break;
			}
			if (qName == "answer") {

				createAnswer();
				break;
			}

		case 2:

			dataMap.put(qName, builder.toString());

			break;
		case 3:
			if (qName == "tag") {
				tagList.add(builder.toString());
			}
			break;

		default:
			// System.out.println("End: " + level + " " + qName);
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
			// report.append("User " + dataMap.get("displayname") +
			// " created\n");
			userCount++;
		} else {

			report.append("ERROR: User {" + dataMap.get("id")
					+ "} already exists \n");
		}

	}

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

		questionCount++;

	}

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

		} else {

			report.append("ERROR: Answer {" + dataMap.get("id")
					+ "} could not be imported \n");
		}

		answerCount++;

	}

	private String removeCDATAContainer(String s) {

		s = s.replaceAll("\\<!\\[CDATA\\[", "");
		s = s.replaceAll("\\]\\]\\>", "");

		return s;
	}

	public int getUserCount() {

		return this.userCount;
	}

	public int getQuestionCount() {

		return this.userCount;
	}

	public int getAnswerCount() {

		return this.answerCount;
	}

	public String getReport() {

		return this.report.toString();
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

}
