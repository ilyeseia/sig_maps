package dz.eadn.sig.util;

import dz.eadn.sig.constants.Constants;
import dz.eadn.sig.dto.FieldDto;
import dz.eadn.sig.dto.LayerDto;
import dz.eadn.sig.model.Field;
import dz.eadn.sig.model.Layer;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class SLDGeneratorImpl implements SLDGenerator {

    @Value("geoserver.rest.url")
    private  String geoServerRest;

    @Override
    public String createFeatureType(LayerDto layer) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><featureType>\n" +
                "  <name>" + Utils.toSlug(layer.getName()) + "</name>\n" +
                "  <nativeName>" + Utils.toSlug(layer.getName()) + "_view</nativeName>\n" +
                "  <namespace>\n" +
                "  <name>" + Constants.GEO_SERVER_WORKSPACE + "</name>\n" +
                "  <atom:link xmlns:atom=\"http://www.w3.org/2005/Atom\" rel=\"alternate\" href=\"" + this.geoServerRest  +"/namespaces/" + Constants.GEO_SERVER_WORKSPACE + ".xml\" type=\"application/xml\"/>\n" +
                "  </namespace>\n" +
                "  <title>" + Utils.toSlug(layer.getName()) + "</title>\n" +
                "  <keywords>\n" +
                "  <string>features</string>\n" +
                "  <string>" + Utils.toSlug(layer.getName()) + "</string>\n" +
                "  </keywords>\n" +
                "  <nativeCRS>\n" +
                "  GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Geodetic longitude\", EAST], AXIS[\"Geodetic latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]]\n" +
                "  </nativeCRS>\n" +
                "  <srs>EPSG:4326</srs>\n" +
                "  <nativeBoundingBox>\n" +
                "  <minx>-8.501223</minx>\n" +
                "  <maxx>11.997337</maxx>\n" +
                "  <miny>18.957525</miny>\n" +
                "  <maxy>37.09001</maxy>\n" +
                "  <crs>EPSG:4326</crs>\n" +
                "  </nativeBoundingBox>\n" +
                "  <latLonBoundingBox>\n" +
                "  <minx>-8.501223</minx>\n" +
                "  <maxx>11.997337</maxx>\n" +
                "  <miny>18.957525</miny>\n" +
                "  <maxy>37.09001</maxy>\n" +
                "  <crs>EPSG:4326</crs>\n" +
                "  </latLonBoundingBox>\n" +
                "  <projectionPolicy>FORCE_DECLARED</projectionPolicy>\n" +
                "  <enabled>true</enabled>\n" +
                "  <metadata>\n" +
                "  <entry key=\"JDBC_VIRTUAL_TABLE\">\n" +
                "  <virtualTable>\n" +
                "  <name>" + Utils.toSlug(layer.getName()) + "_view</name>\n" +
                "  <sql>select * from " + Utils.toSlug(layer.getName()) + "_view</sql>\n" +
                "  <escapeSql>false</escapeSql>\n" +
                "  <geometry>\n" +
                "  <name>geom</name>\n" +
                "  <type>" + layer.getTopo() + "</type>\n" +
                "  <srid>4326</srid>\n" +
                "  </geometry>\n" +
                "  </virtualTable>\n" +
                "  </entry>\n" +
                "  </metadata>\n" +
                "  <store class=\"dataStore\">\n" +
                "  <name>" + Constants.GEO_SERVER_WORKSPACE + ":database</name>\n" +
                "  <atom:link xmlns:atom=\"http://www.w3.org/2005/Atom\" rel=\"alternate\" href=\"" + this.geoServerRest  +"/geoserver/rest/workspaces/" + Constants.GEO_SERVER_WORKSPACE + "/datastores/database.xml\" type=\"application/xml\"/>\n" +
                "  </store>\n" +
                "  <serviceConfiguration>false</serviceConfiguration>\n" +
                "  <maxFeatures>0</maxFeatures>\n" +
                "  <numDecimals>0</numDecimals>\n" +
                "  <padWithZeros>false</padWithZeros>\n" +
                "  <forcedDecimal>false</forcedDecimal>\n" +
                "  <overridingServiceSRS>false</overridingServiceSRS>\n" +
                "  <skipNumberMatched>false</skipNumberMatched>\n" +
                "  <circularArcPresent>false</circularArcPresent>\n" +
                "  <attributes>\n" +
                "  <attribute>\n" +
                "  <name>id</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.util.UUID</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>create_date</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.sql.Timestamp</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>created_by</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.lang.String</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>deleted</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.lang.Boolean</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>last_modified_date</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.sql.Timestamp</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>modified_by</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.lang.String</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>geom</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>org.locationtech.jts.geom." + layer.getTopo() + "</binding>\n" +
                "  </attribute>\n" +
                "  <attribute>\n" +
                "  <name>layer_entity_element</name>\n" +
                "  <minOccurs>0</minOccurs>\n" +
                "  <maxOccurs>1</maxOccurs>\n" +
                "  <nillable>true</nillable>\n" +
                "  <binding>java.util.UUID</binding>\n" +
                "  </attribute> \n";
        for(FieldDto f: layer.getFieldDtos()){
            xml += " <attribute>\n" +
                    "    <name>" + Utils.toSlug(f.getName()) +  "</name>\n" +
                    "    <minOccurs>0</minOccurs>\n" +
                    "    <maxOccurs>1</maxOccurs>\n" +
                    "    <nillable>true</nillable>\n" +
                    "    <binding>java.lang.String</binding>\n" +
                    "    </attribute>";
        }
        xml +=  "</attributes></featureType>";
        return xml;
    }

    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }
}
