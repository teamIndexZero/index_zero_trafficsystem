package kcl.teamIndexZero.traffic.simulator.osm;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * Parser of OpenStreetMap files format.
 * <p>
 * In order to work properly within our code, these files should be appropriately prepared - check out the trello card
 * OSM Loading. Generally, we need filter out only the ways which are roads interesting for us (where cars can move) and
 * also cut an interesting piece (bounding box) of the bigger map.s
 */
public class OsmParser {

    /**
     * Parse and Open Street Map XML file into our intermediate representation (to be fed to
     * {@link kcl.teamIndexZero.traffic.simulator.data.GraphConstructor})
     *
     * @param sourceName   name of the source we read from - useful for error messages.
     * @param streamSource actual stream of the source (file, string, memory)
     * @return parse result - container for collection of {@link kcl.teamIndexZero.traffic.simulator.data.descriptors.RoadDescription}
     * and collection of {@link kcl.teamIndexZero.traffic.simulator.data.descriptors.JunctionDescription}
     * @throws MapParseException in case any error happened.
     */
    public OsmParseResult parse(String sourceName, InputStream streamSource) throws MapParseException {
        try {
            OsmParseResult result = new OsmParseResult();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse(streamSource, new OsmSaxHandler(result));

            return result;
        } catch (Exception e) {
            throw new MapParseException("Error loading from source " + sourceName, e);
        }
    }
}
