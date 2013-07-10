import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SaxParser {
	public static void example(String[] args) {
		try {
			// XMLReader erzeugen
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			// Pfad zur XML Datei
			FileReader reader = new FileReader("main.tei.xml");
			InputSource inputSource = new InputSource(reader);

			// DTD kann optional übergeben werden
			// inputSource.setSystemId("X:\\personen.dtd");

			// PersonenContentHandler wird übergeben
			xmlReader.setContentHandler(new PaliContentHandler());

			// Parsen wird gestartet
			xmlReader.parse(inputSource);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}
