import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MainParse {

  public static void main(String[] args) throws Exception {
    BasicConfigurator.configure();
    File file = new File("Base.accdb");
    Database db = DatabaseBuilder.open(file);
    Table table = db.getTable("ASUXML");
    List<String> xmlFromDb = new ArrayList<>();
    List<Integer> idFromDb = new ArrayList<>();
    for (Row row : table) {
      xmlFromDb.add((String) row.get("BOXML"));
      idFromDb.add((Integer) row.get("ID"));
    }

    for (int i = 0; i < idFromDb.size(); i++) {

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      Document newXml = factory.newDocumentBuilder().newDocument();

      Element transmission = newXml.createElement("Transmission");
      newXml.appendChild(transmission);

      Element transmissionBody = newXml.createElement("TransmissionBody");
      transmission.appendChild(transmissionBody);

      Element gLogXMLElement = newXml.createElement("GLogXMLElement");
      transmissionBody.appendChild(gLogXMLElement);

      Element location = newXml.createElement("Location");
      gLogXMLElement.appendChild(location);

      Document document = loadXMLFromString(xmlFromDb.get(i));
      XPath xPath = XPathFactory.newInstance().newXPath();

      String expression1 = "BusinessPartnersReplicationRequest/BusinessPartners/KPP/KPP";
      NodeList kppList = (NodeList) xPath.evaluate(expression1, document, XPathConstants.NODESET);
      for (int j = 0; j < kppList.getLength(); j++) {

        Element locationRefnum = newXml.createElement("LocationRefnum");
        location.appendChild(locationRefnum);

        Element locationRefnumQualifierGid = newXml.createElement("LocationRefnumQualifierGid");
        locationRefnum.appendChild(locationRefnumQualifierGid);

        Element gid = newXml.createElement("Gid");
        locationRefnumQualifierGid.appendChild(gid);

        Element xid = newXml.createElement("Xid");
        xid.setTextContent("КПП_" + (j+1));
        gid.appendChild(xid);

        Element locationRefnumValue = newXml.createElement("LocationRefnumValue");
        locationRefnumValue.setTextContent(kppList.item(j).getTextContent());
        locationRefnum.appendChild(locationRefnumValue);
      }

      String expression2 = "BusinessPartnersReplicationRequest/BusinessPartners/BillAccounts/AccountNumber";
      NodeList accountNumbers = (NodeList) xPath.evaluate(expression2, document, XPathConstants.NODESET);
      for (int j = 0; j < accountNumbers.getLength(); j++) {

        Element locationRefnum = newXml.createElement("LocationRefnum");
        location.appendChild(locationRefnum);

        Element locationRefnumQualifierGid = newXml.createElement("LocationRefnumQualifierGid");
        locationRefnum.appendChild(locationRefnumQualifierGid);

        Element gid = newXml.createElement("Gid");
        locationRefnumQualifierGid.appendChild(gid);

        Element xid = newXml.createElement("Xid");
        xid.setTextContent("СЧЕТ_" + (j+1));
        gid.appendChild(xid);

        Element locationRefnumValue = newXml.createElement("LocationRefnumValue");
        locationRefnumValue.setTextContent(accountNumbers.item(j).getTextContent());
        locationRefnum.appendChild(locationRefnumValue);
      }

      String expression3 =
          "BusinessPartnersReplicationRequest/BusinessPartners/BillAccounts/Currency/LetterCode";
      NodeList letterCodes  = (NodeList) xPath.evaluate(expression3, document, XPathConstants.NODESET);
      for (int j = 0; j < accountNumbers.getLength(); j++) {

        Element locationRefnum = newXml.createElement("LocationRefnum");
        location.appendChild(locationRefnum);

        Element locationRefnumQualifierGid = newXml.createElement("LocationRefnumQualifierGid");
        locationRefnum.appendChild(locationRefnumQualifierGid);

        Element gid = newXml.createElement("Gid");
        locationRefnumQualifierGid.appendChild(gid);

        Element xid = newXml.createElement("Xid");
        xid.setTextContent("ВАЛЮТА_" + (j+1));
        gid.appendChild(xid);

        Element locationRefnumValue = newXml.createElement("LocationRefnumValue");
        locationRefnumValue.setTextContent(letterCodes.item(j).getTextContent());
        locationRefnum.appendChild(locationRefnumValue);
      }
      File file1 = new File("Partner_" + (i+1) + ".xml");
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "10");
      transformer.transform(new DOMSource(newXml), new StreamResult(file1));
    }
  }

  public static Document loadXMLFromString(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xml));
    return builder.parse(is);
  }
}

