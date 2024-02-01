package users;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Usuario {

    private String nome;
    private String telefone;
    private String login;
    private List<String> grupos = new ArrayList<String>();

    private final String regexNome = "^[a-zA-Z ]{2,}$";
    private final String regexLogin = "^[a-zA-Z0-9]{4,}$";
    private final String regexTelefone = "^[0-9]{10,11}$";

    public Usuario(String nome, String telefone, String login, List<String> grupos) {
        this.nome = nome;
        this.telefone = telefone;
        this.login = login;
        this.grupos = grupos;
    }

    public Usuario() {
    }

    public void addUser(DirContext connection) {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");
        attributes.put(attribute);

        attributes.put("sn", this.nome);
        attributes.put("mobileTelephoneNumber", this.telefone);

        String aux = "cn=" + this.login + ",ou=Usuario,o=Company";

        try {
            try {
                connection.createSubcontext(aux, attributes);

                if (grupos != null && !grupos.isEmpty()) {
                    for (String grupo : grupos) {
                    	if(groupExists(grupo, connection)) {
                        addUserToGroup(this.login, grupo, connection);
                    	}else System.out.println("O grupo: " + grupo + " não existe");
                    }
                }
                System.out.println("Success: " + this.login + " Created!");
            } catch (Exception e) {
                System.out.println("cn de Usuário já existe erro: " + "\n" + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addUserToGroup(String login, String groupName, DirContext connection) {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("member", "cn=" + login + ",ou=Usuario,o=Company");
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
        try {
            connection.modifyAttributes("cn=" + groupName + ",ou=Grupo,o=Company", mods);
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public void addFromXML(String xmlFile, DirContext connection) {
        try {
        	xmlFile = "C:\\Users\\Daniel Castro\\Desktop\\desafio-open\\java-ldap\\src\\main\\java\\data\\" + xmlFile;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expr = xpath.compile("//add");

            NodeList addNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < addNodes.getLength(); i++) {
                String nome = xpath.evaluate("add-attr[@attr-name='Nome Completo']/value/text()", addNodes.item(i));
                String login = xpath.evaluate("add-attr[@attr-name='Login']/value/text()", addNodes.item(i));
                String telefone = xpath.evaluate("add-attr[@attr-name='Telefone']/value/text()", addNodes.item(i));
                
                NodeList gp = (NodeList) xpath.evaluate("add-attr[@attr-name='Grupo']/value/text()",
                        addNodes.item(i),
                        XPathConstants.NODESET);
                for (int j = 0; j < gp.getLength(); j++) {
                    grupos.add(gp.item(j).getTextContent());
                }
                
                setNome(nome);
                setTelefone(telefone);
                setLogin(login);

                addUser(connection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean groupExists(String groupName, DirContext connection) {
        try {
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            String filter = "(cn=" + groupName + ")";
            NamingEnumeration<SearchResult> results = connection.search("ou=Grupo,o=Company", filter, searchControls);
            return results.hasMore();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void modifyXML(String xmlFile, DirContext connection) {
        try {
        	xmlFile = "C:\\Users\\Daniel Castro\\Desktop\\desafio-open\\java-ldap\\src\\main\\java\\data\\" + xmlFile;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expr = xpath.compile("//modify");

            NodeList modifyNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < modifyNodes.getLength(); i++) {
                Node modifyNode = modifyNodes.item(i);

                String login = xpath.evaluate("association/text()", modifyNode);
                NodeList removeNodes = (NodeList) xpath.evaluate("modify-attr[@attr-name='Grupo']/remove-value/value", modifyNode, XPathConstants.NODESET);
                NodeList addNodes = (NodeList) xpath.evaluate("modify-attr[@attr-name='Grupo']/add-value/value", modifyNode, XPathConstants.NODESET);

                List<String> gruposToRemove = new ArrayList<>();
                List<String> gruposToAdd = new ArrayList<>();

                for (int j = 0; j < removeNodes.getLength(); j++) {
                	String group = removeNodes.item(j).getTextContent();
                    if (groupExists(group, connection)) {
                        gruposToRemove.add(group);
                    } else {
                        System.out.println("Grupo '" + group + "' não existe.");
                    }
                }

                for (int j = 0; j < addNodes.getLength(); j++) {
                	gruposToAdd.add(addNodes.item(j).getTextContent());
                }

                if (!gruposToRemove.isEmpty()) {
                    for (String grupo : gruposToRemove) {
                        removeUserFromGroup(login, grupo, connection);
                    }
                }

                if (!gruposToAdd.isEmpty()) {
                    for (String grupo : gruposToAdd) {
                        addUserToGroup(login, grupo, connection);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void removeUserFromGroup(String login, String groupName, DirContext connection) {
        try {
            ModificationItem[] mods = new ModificationItem[1];
            Attribute attribute = new BasicAttribute("member", "cn=" + login + ",ou=Usuario,o=Company");
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
            connection.modifyAttributes("cn=" + groupName + ",ou=Grupo,o=Company", mods);
            System.out.println("Usuário " + login + " removido do grupo " + groupName + " com sucesso.");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    
    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        if (nome.matches(regexNome)) {
            this.nome = nome;
        } else {
            throw new IllegalArgumentException(
                    "O nome deve conter apenas letras.");
        }
    }

    public String getTelefone() {
        return this.telefone;
    }

    public void setTelefone(String telefone) {
        telefone = telefone.replaceAll("[^0-9]", "");
        if (telefone.matches(regexTelefone)) {
            this.telefone = telefone;
        } else {
            throw new IllegalArgumentException(
                    "O número de telefone deve conter apenas números, contendo 10 ou 11 dígitos.");
        }
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        if (login.matches(regexLogin)) {
            this.login = login;
        } else {
            throw new IllegalArgumentException(
                    "O login não pode conter caracteres especiais.");
        }
    }

    public List<String> getGrupos() {
        return this.grupos;
    }

    public void setGrupos(List<String> grupos) {
        this.grupos = grupos;
    }

}