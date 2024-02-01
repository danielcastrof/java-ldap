package groups;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Grupo {
	
    private String identificador;
    private String descricao;
    private List<String> files = new ArrayList<String>();

    public Grupo(String identificador, String descricao) {
        this.identificador = identificador;
        this.descricao = descricao;
    }

    public Grupo() {
    }

    public void addGroup(DirContext connection) {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("groupOfNames");
        attributes.put(attribute);

        attributes.put("description", this.descricao);
        attributes.put("member", "cn=admin,ou=Usuario,o=Company");
        

        String aux = "cn=" + this.identificador + ",ou=Grupo,o=Company";

        try {
                connection.createSubcontext(aux, attributes);
                System.out.println("Success: " + this.identificador + " Created!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addFromXML(List<String> files, DirContext connection) {
        try {
        	for(String xmlFile : files) {
        	xmlFile = "C:\\Users\\Daniel Castro\\Desktop\\desafio-open\\java-ldap\\src\\main\\java\\data\\" + xmlFile;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expr = xpath.compile("//add");

            NodeList addNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < addNodes.getLength(); i++) {
                String identificador = xpath.evaluate("add-attr[@attr-name='Identificador']/value/text()", addNodes.item(i));
                String descricao = xpath.evaluate("add-attr[@attr-name='Descricao']/value/text()", addNodes.item(i));
                
                setIdentificador(identificador);
                setDescricao(descricao);

                addGroup(connection);
            }
        	}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}

