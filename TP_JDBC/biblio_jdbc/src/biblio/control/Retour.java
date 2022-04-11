package biblio.control;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import biblio.dao.ConnectionFactory;
import biblio.dao.EmpruntArchiveDAO;
import biblio.dao.EmpruntEnCoursDao;
import biblio.dao.ExemplaireDAO;
import biblio.domain.EmpruntArchive;
import biblio.domain.EmpruntEnCours;
import biblio.domain.Exemplaire;
import biblio.domain.Utilisateur;

public class Retour {


	public static void main(String[] args) throws SQLException {

		Properties props = new Properties();
		try (FileReader fis = new FileReader("biblio.properties")) {
			props.load(fis);

			Class.forName(props.getProperty("driver"));
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Driver inconnu - " + e.getMessage());
			System.exit(1);
		}

		String dbDriver = props.getProperty("driver");
		String dbUrl = props.getProperty("url");
		String dbLogin = props.getProperty("user");
		String dbPwd = props.getProperty("pwd");

		ConnectionFactory cf = new ConnectionFactory();

		Connection cnx1 = null;

		try {
			cnx1 = cf.getConnection(dbDriver, dbUrl, dbLogin, dbPwd);
			System.out.println("-------------------------------------");
			System.out.println("VOUS ETE CONNECT� !");
			System.out.println("-------------------------------------");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		EmpruntArchiveDAO archive = new EmpruntArchiveDAO(cnx1);
		EmpruntEnCoursDao emprunt = new EmpruntEnCoursDao(cnx1);

		EmpruntArchive ear = null;
		EmpruntEnCours retour = null;

		String idEmpruntArchive = JOptionPane.showInputDialog("Entrez l'id de l'archive : ");
		int idempruntarchive = Integer.parseInt(idEmpruntArchive);

		String idExemplaire = JOptionPane.showInputDialog("Entrez l'id de l'exemplaire a rendre : ");
		int idexemplaire = Integer.parseInt(idExemplaire);

		boolean isAvailable = emprunt.isAvailable(idexemplaire);
		

		try {
			System.out.println("-------------------------------------");
			System.out.println(" EMPRUNTARCHIVE BY KEY !");
			System.out.println("-------------------------------------");
			ear = archive.findByKey(idempruntarchive);
			System.out.println(ear);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		try {
			System.out.println("-------------------------------------");
			System.out.println(" CHOIX DE L'EMPRUNT � ARCHIVER  !");
			System.out.println("-------------------------------------");
			retour = emprunt.findByKey(idexemplaire);
			System.out.println(retour);

		} catch (SQLException e) 
		{ 
			e.printStackTrace();
		}

		if (isAvailable) {
			System.out.println("Exemplaire non pret� !");
		} else {

			try {

				retour = emprunt.findByKey(idexemplaire);

				archive.insertArchive(retour);

				System.out.println("-------------------------------------");

				System.out.println("Emprunt archiv� ");

				archive.DeleteEmprunt(idexemplaire);

				System.out.println("emprunt Effac� ");

				archive.UpdateExemplaire(idexemplaire);
				

				System.out.println("exemplaire disponible ");
				System.out.println("-------------------------------------");

			} catch (SQLException e) { // TODO Auto-generated
				e.printStackTrace();
			}
		}
	}
}

