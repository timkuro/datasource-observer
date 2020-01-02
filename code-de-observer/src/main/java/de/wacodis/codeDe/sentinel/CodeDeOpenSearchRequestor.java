package de.wacodis.codeDe.sentinel;

import de.wacodis.codeDe.CodeDeJob;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for requesting CODE-DE Orthorectified images of the Sentinal satellites.
 *
 *@author <a href="mailto:tim.kurowski@hs-bochum.de">Tim Kurowski</a>
 *@author <a href="mailto:christian.koert@hs-bochum.de">Christian Koert</a>
 */

public class CodeDeOpenSearchRequestor {


    final static Logger LOG = LoggerFactory.getLogger(CodeDeOpenSearchRequestor.class);

    /**
     * Performs a query with the given paramerters.
     *
     * @param params all necessary parameters for the OpenSearch request
     * @return metadata for the found satellite images
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */

    public static List<CodeDeProductsMetadata> request(CodeDeRequestParams params) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        List<CodeDeProductsMetadata> resultMetadata = new ArrayList<CodeDeProductsMetadata>();

        LOG.debug("Start building connection parameters for GET-request");
        String getRequestUrl = null;
        LOG.debug("Start GET-request");
        InputStream getResponse = sendOpenSearchRequest(getRequestUrl);
        LOG.debug("Analyze InputStream");

        // create xml-Document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        dbf.setNamespaceAware(true);
        Document getResponseDoc = docBuilder.parse(getResponse);

        // analyze xml-Document
        CodeDeResponseResolver resolver = new CodeDeResponseResolver();
        List<String> downloadLinks = resolver.getDownloadLink(getResponseDoc);

        List<String> metadataLinks = resolver.getMetaDataLinks(getResponseDoc);
        // request links
        for (int i=0; i<metadataLinks.size(); i++) {
            CodeDeProductsMetadata metadata = new CodeDeProductsMetadata();
            getResponse = sendOpenSearchRequest(metadataLinks.get(i));
            Document getMetadataDoc = docBuilder.parse(getResponse);
            float cloudCoverage = resolver.getCloudCoverage(getMetadataDoc);
            metadata.setCloudCover(cloudCoverage);
            resultMetadata.add(metadata);
        }

        //metadata.setParentIdentifier();



        return resultMetadata;
    }

    /**
     *  Delivers the content of the GET response.
     * @param getRequestUrl string containing the URL of the GET request
     * @return content of the GET response as an Inputstream
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static InputStream sendOpenSearchRequest(String getRequestUrl) throws ClientProtocolException, IOException {

        // contact http-client
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getRequestUrl);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity(); // fill http-Object (status, parameters, content)
        InputStream httpcontent = entity.getContent(); // ask for content
        return httpcontent;
    }
}
