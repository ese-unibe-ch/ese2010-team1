package models.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class XMLImporter is responsible for the input streams of xml
 * informations.
 */
public class XMLImporter {

	/** The handler. */
	private XMLHandler handler;

	/** The parse time. */
	private long parseTime = 0;

	/**
	 * Instantiates a new xML importer.
	 * 
	 * @param file
	 *            the file
	 * @throws SAXException
	 *             the sAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	public XMLImporter(File file) throws SAXException, IOException,
			ParserConfigurationException {

		// Create a JAXP "parser factory" for creating SAX parsers
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// Create a SAX input source for the file argument
		InputSource input = new InputSource(new FileReader(file));

		handler = new XMLHandler();

		// take start time to get duration
		long startTime = System.currentTimeMillis();
		// Finally, tell the parser to parse the input and notify the handler
		saxParser.parse(input, handler);
		this.parseTime = (System.currentTimeMillis() - startTime) / 1000;
	}

	/**
	 * Gets the handler.
	 * 
	 * @return the handler
	 */
	public XMLHandler getHandler() {

		return handler;
	}

	/**
	 * Gets the parses the time.
	 * 
	 * @return the parses the time
	 */
	public long getParseTime() {
		return this.parseTime;
	}

}
