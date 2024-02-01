import java.util.ArrayList;
import java.util.List;

import config.Config;
import groups.Grupo;
import users.Usuario;

public class Main {

	public static void main(String[] args) {

		Config ldap = new Config();
		
		String userXml = "AddUsuario1.xml";
		String modifyUser = "ModifyUsuario.xml";
		
		String grupo1 = "AddGrupo1.xml";
		String grupo2 = "AddGrupo2.xml";
		String grupo3 = "AddGrupo3.xml";
		
		List<String> files = new ArrayList<String>();
		files.add(grupo1);
		files.add(grupo2);
		files.add(grupo3);
		
		Usuario user = new Usuario();
		
		Grupo group = new Grupo();

		ldap.newConnection();
		
		group.addFromXML(files, ldap.getConnection());
		
		user.addFromXML(userXml, ldap.getConnection());
		
		user.modifyXML(modifyUser, ldap.getConnection());
		
		ldap.closeConnection();
	}

}
