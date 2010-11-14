package models;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLImporter extends DefaultHandler {

	private StringBuilder builder;

	private static int level = -1;

	private static User currentUser;
	private static Question currentQuestion;
	private static Answer currentAnswer;
	private long fakeTagId;

	public XMLImporter(File file) throws SAXException, IOException,
			ParserConfigurationException {

		// Create a JAXP "parser factory" for creating SAX parsers
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// Create a SAX input source for the file argument
		InputSource input = new InputSource(new FileReader(file));

		// Finally, tell the parser to parse the input and notify the handler
		saxParser.parse(input, this);
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
	 * This method is called when an element is closed.
	 */
	public void endElement(String uri, String localName, String qName) {
		checkEndElement(qName);
		level--;
	}

	/**
	 * This method is called when the parser encounters plain text (not XML
	 * elements). This method stores the text in the StringBuilder
	 */
	public void characters(char[] ch, int start, int length) {
		builder.append(ch, start, length);
	}

	// Diese Methode checkt um was für ein Element es sich handelt. Das level
	// beschreibt die Ebene im File. Also
	// auf Höhe <QA> ist das level noch 0 und je tiefer man geht um so höher
	// wird es. Es checkt also immer nur
	// das was auf dem level überhaupt möglich ist.
	// case 0 wird ein neuer User gemacht. und case 1 weisst die id zu.

	public void checkStartElement(String qName, Attributes atts) {
		switch (level) {
		case 0:
			if (qName == "users") {
				currentUser = new User();
				return;
			}

			if (qName == "questions") {
				currentQuestion = new Question();
				currentAnswer = new Answer();
				return;
			}

			if (qName == "answers") {
				currentAnswer = new Answer();
				return;
			}
		case 1:

			if (qName == "user") {
				currentUser.id = Long.valueOf(atts.getValue(0)).longValue();
				return;
			}

			if (qName == "question") {
				currentQuestion.id = Long.valueOf(atts.getValue(0)).longValue();
				return;
			}

			if (qName == "answer") {
				currentAnswer.id = Long.valueOf(atts.getValue(0)).longValue();
				return;
			}

		case 2:
			if (qName == "tag") {
				fakeTagId = Long.valueOf(atts.getValue(0)).longValue();
				// in fact the id of tag is not needed yet
			}
		}
	}

	// wenn das element geschlossen wird und mann auf level 3 ist, werden die
	// Attribute zugeteilt. auf level 2 werden die Einträge gesaved.

	public void checkEndElement(String qName) {

		switch (level) {
		case 2:
			if (qName == "user") {

				User user = new User(currentUser.name, currentUser.email,
						currentUser.password).save();
				user.fakeId = currentUser.id; // added a fakeId to escape for db
				// error
				user.isAdmin = currentUser.isAdmin;
				user.setNewPassword(currentUser.password);
				user.save();
				return;
			}

			if (qName == "question") {
				Question q = currentQuestion.owner.addQuestion(
						currentQuestion.title, currentQuestion.content);
				q.fakeId = currentQuestion.id;
				q.save();
				return;
			}

			if (qName == "answer") {
				Answer a = new Answer(currentAnswer.owner,
						currentAnswer.question, currentAnswer.content);
				a.save();
				return;
			}

		case 3:
			if (qName == "displayname") {
				currentUser.name = builder.toString();
				return;
			}

			if (qName == "ismoderator") {
				if (builder.toString() == "true")
					currentUser.isAdmin = true;
				else
					currentUser.isAdmin = false;
				return;
			}
			if (qName == "email") {
				currentUser.email = builder.toString();
				return;
			}

			if (qName == "password") {
				currentUser.password = builder.toString();
				return;
			}

			if (qName == "diplayname") {
				currentUser.name = builder.toString();
				return;
			}

			if (qName == "ownerid") {
				User owner = User.find("byFakeId",
						Long.valueOf(builder.toString()).longValue()).first();
				if (owner != null) {
					currentAnswer.owner = owner;
					currentQuestion.owner = owner;
				} else {
					currentAnswer.owner = User.find("byName", "Default")
							.first();
					;
					currentQuestion.owner = User.find("byName", "Default")
							.first();
				}
			}

			if (qName == "title") {
				currentQuestion.title = builder.toString();
			}
			if (qName == "questionid") {
				Question question = Question.find("byFakeId",
						Long.valueOf(builder.toString()).longValue()).first();

				currentAnswer.question = question;

			}

			if (qName == "body") {
				currentQuestion.content = "default";
				// currentQuestion.content = builder.toString(); //not working
				currentAnswer.content = "default";
				// currentAnswer.content = builder.toString(); //not working
			}

			if (qName == "tag") {

				Tag tag = Tag.findOrCreateByName(builder.toString());
				currentQuestion.tags.add(tag);
			}
		}
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
