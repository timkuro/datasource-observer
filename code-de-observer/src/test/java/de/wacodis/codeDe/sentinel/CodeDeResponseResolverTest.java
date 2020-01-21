package de.wacodis.codeDe.sentinel;

import de.wacodis.sentinel.apihub.decode.SimpleNamespaceContext;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CodeDeResponseResolverTest {

    private static CodeDeResponseResolver resolver;
    private static Document xmlDoc;
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder db;
    private static XPath xpath;
    private static Node node;

    @BeforeAll
    static void setup() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        resolver = new CodeDeResponseResolver();
        dbf.setNamespaceAware(true);
        //dbf.setCoalescing(true);
        db = dbf.newDocumentBuilder();
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        Map<String, String> prefMap = new HashMap<String, String>(){
            {
                put("a", "http://www.w3.org/2005/Atom");
            }
        };
        SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
        xpath.setNamespaceContext(namespaces);

        InputStream openSearchResponseStream = CodeDeResponseResolverTest.class.getClassLoader().getResourceAsStream("catalog.code-de.org.xml");
        xmlDoc = db.parse(openSearchResponseStream);
        String xPathString="/a:feed/a:entry[1]";
        XPathExpression expression = xpath.compile(xPathString);
        node = (Node) expression.evaluate(xmlDoc, XPathConstants.NODE);

    }
    @Test
    void testGetDownloadLink() throws IOException, SAXException, XPathExpressionException {

        // expected download links
        String expectedDownloadLink = "https://code-de.org/download/S2B_MSIL2A_20191012T103029_N0213_R108_T32ULB_20191012T135838.SAFE.zip";
        // actual download link
        String actualDownloadLink = resolver.getDownloadLink(node);

        Assertions.assertEquals(expectedDownloadLink, actualDownloadLink);
    }

    @Test
    void testGetMetadataLink() throws IOException, SAXException, XPathExpressionException {

        // expected metadata link
        String expectedMetadataLink = "https://catalog.code-de.org/opensearch/request/?httpAccept=application/gml%2Bxml&amp;parentIdentifier=EOP:CODE-DE:S2_MSI_L2A&amp;uid=EOP:CODE-DE:S2_MSI_L2A:/S2B_MSIL2A_20191012T103029_N0213_R108_T32ULB_20191012T135838&amp;recordSchema=om";

        // actual metadataLink
        String metadataLink = resolver.getMetaDataLink(node);
        String actualMetadataLink = metadataLink.replaceAll("&", "&amp;");
        Assertions.assertEquals(expectedMetadataLink, actualMetadataLink);

    }

    @Test
    void testGetCloudCoverage() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        // expected cloud coverage
        float expectedCloudCoverage = 29.141719f;
        // actual cloud coverage
        float actualCloudCoverage = resolver.getCloudCoverage(node);

        Assertions.assertEquals(expectedCloudCoverage, actualCloudCoverage);
    }

    @Test
    void testGetIdentifier() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        // expected identifier
        String expectedIdentifier = "EOP:CODE-DE:S2_MSI_L2A:/S2B_MSIL2A_20191012T103029_N0213_R108_T32ULB_20191012T135838";
        // actual identifier
        String actualIdentifier = resolver.getIdentifier(node);

        Assertions.assertEquals(expectedIdentifier, actualIdentifier);
    }

    @Test
    void testGetTimeFrame() throws IOException, SAXException, XPathExpressionException {

        // expected time frame
        List<DateTime> expectedTimeFrame = new ArrayList<DateTime>(){
            {
                add(DateTime.parse("2019-10-12T10:30:29.024Z", CodeDeResponseResolver.FORMATTER));
                add(DateTime.parse("2019-10-12T10:30:29.024Z", CodeDeResponseResolver.FORMATTER));
            }
        };

        // actual time frame
        List<DateTime> actualTimeFrame = resolver.getTimeFrame(node);

        Assertions.assertEquals(expectedTimeFrame, actualTimeFrame);
    }

    @Test
    void testGetBbox() throws IOException, SAXException, XPathExpressionException {

        // expected bbox
        List<Float> expectedbbox = new ArrayList<Float>(){
            {
                add(50.4368368428495f);
                add(6.589443020581445f);
                add(51.44399790803582f);
                add(7.729300891794365f);
            }
        };

        // actual bbox
        List<Float> actualBbox = resolver.getBbox(node);

        Assertions.assertEquals(expectedbbox, actualBbox);
    }

}