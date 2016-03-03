package kcl.teamIndexZero.traffic.simulator.osm;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 */
public class OsmParser {
    public OsmParseResult parse(String sourceName, InputStream streamSource) throws MapParseException {
        try {
            OsmParseResult result = new OsmParseResult();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse(streamSource, new SaxHandler(result));

            return result;
        } catch (Exception e) {
            throw new MapParseException("Error loading from source " + sourceName, e);
        }
    }
}
