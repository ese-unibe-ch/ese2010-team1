package models.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLImporter {

	private XMLHandler handler;
	private long parseTime = 0;

	public XMLImporter(File file) throws SAXException, IOException,
			ParserConfigurationException {

		// Create a JAXP "parser factory" for creating SAX parsers
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// Create a SAX input source for the file argument
		InputSource input = new InputSource(new FileReader(file));

		handler = new XMLHandler();

		// Finally, tell the parser to parse the input and notify the handler
		long startTime = System.currentTimeMillis();
		saxParser.parse(input, handler);
		this.parseTime = (System.currentTimeMillis() - startTime) / 1000;
	}

	public XMLHandler getHandler() {

		return handler;
	}

	public long getParseTime() {
		return this.parseTime;
	}

}
