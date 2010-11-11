import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Answer;
import models.Question;
import models.User;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLreader extends DefaultHandler {

	private StringBuilder builder;

	private static int level = 0;

	private static User currentUser;
	private static Question currentQuestion;
	private static Answer currentAnswer;
	private static Object currentObject;

	public XMLreader(String file) throws SAXException, IOException,
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
		checkStartElement(qName);
	}

	/**
	 * This method is called when an element is closed.
	 */
	public void endElement(String uri, String localName, String qName) {
		checkEndElement(qName);
	}

	/**
	 * This method is called when the parser encounters plain text (not XML
	 * elements). This method stores the text in the StringBuilder
	 */
	public void characters(char[] ch, int start, int length) {
		builder.append(ch, start, length);
	}

	// Diese Methode checkt um was für ein Elemnt es sich handelt. Das level
	// beschreibt die Ebene im File. Also
	// auf Höhe <QA> ist das level noch 0 und je tiefer man geht um so höher
	// wird es. Es checkt also immer nur
	// das was auf dem level überhaupt möglich ist. case 0 wird das current
	// Object zu User Question oder Answer.
	// case 1 wird ein neuer User gemacht. und case 2 erneuert den
	// Stringbuilder.
	public void checkStartElement(String qName) {

		switch (level) {
		case 0:
			if (qName == "users") {
				currentObject = currentUser;
				level = 1;
				return;
			}

			if (qName == "questions") {
				currentObject = currentQuestion;
				level = 1;
				return;
			}

			if (qName == "answers") {
				currentObject = currentAnswer;
				level = 1;
				return;
			}
		case 1:

			if (qName == "user") {
				currentUser = new User();
				level = 2;
				return;
			}

			if (qName == "question") {
				Question question = new Question();
				currentQuestion = question;
				level = 2;
				return;
			}

			if (qName == "answer") {
				Answer answer = new Answer();
				currentAnswer = answer;
				level = 2;
				return;
			}

		case 2:
			builder = new StringBuilder();
			level = 3;
			return;
		}
	}

	// wenn das element geschlossen wird und mann auf level 3 ist, werden die
	// Attribute zugeteilt. auf level 2 wird der
	// User gesaved.(Fehler) case 1 wird nichts gemacht.

	public void checkEndElement(String qName) {

		switch (level) {
		case 1:
			level = 0;
		case 2:
			if (qName == "user") {
				currentUser.save();
				level = 1;
				return;
			}
			level = 1;

		case 3:
			if (qName == "diplayname" && currentObject instanceof User) {
				currentUser.name(builder.toString());
				return;
			}

			if (qName == "ismoderator" && currentObject instanceof User) {
				if (builder.toString() == "true")
					currentUser.isAdmin(true);
				else
					currentUser.isAdmin(false);
				return;
			}
			if (qName == "email" && currentObject instanceof User) {
				currentUser.email(builder.toString());
				return;
			}

			if (qName == "password" && currentObject instanceof User) {
				currentUser.password(builder.toString());
				return;
			}

			if (qName == "diplayname" && currentObject instanceof User) {
				currentUser.name(builder.toString());
				return;
			}
			level = 2;
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
