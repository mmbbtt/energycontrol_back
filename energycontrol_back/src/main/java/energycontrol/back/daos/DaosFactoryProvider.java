package energycontrol.back.daos;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import mbt.utilities.BussinesException;
import mbt.utilities.Helper;

import energycontrol.back.daos.jpaimpl.DaosFactoryJpaImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Proveedor el la factoría de DAOs.
 * 
 * Requiere pasar como argumento al constructor de la clase en nombre de la unidad de persistencia a usar,
 * la cual debe existir en el Persistence.xml.
 * 
 */
public class DaosFactoryProvider
{
	private static String persistenceUntiProvider;
	private static EntityManager entityManager;
	
	/**
	 * Constructor oblitatorio.
	 * Requiere pasar como argumento el nombre de la unidad de persistencia a usar, 
	 * la cual debe exitir en el Persistence.xml y debe incluir el elemento provider.
	 * 
	 * @param persistenceUnitName Nombre de la unidad de persistencia a usar
	 */
	public DaosFactoryProvider(String persistenceUnitName)
	{
		//-> Creamos en manager para la unidad de persistencia:
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		entityManager = emf.createEntityManager();
		//<-
		
		//-> Leemos el provier del Persistence.xml:
		BussinesException be = null; 
		try 
		{
			InputStream is = Thread.currentThread().getContextClassLoader().getResource("META-INF/persistence.xml").openStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			doc.getDocumentElement().normalize();
						
			NodeList nList = doc.getElementsByTagName("persistence-unit");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					String nameElement = eElement.getAttribute("name");
					if(
						!Helper.stringIsNullOrEmpty(nameElement)
						&& nameElement.equals(persistenceUnitName)
						)
					{
						NodeList nlElement = eElement.getElementsByTagName("provider");
						
						if(nlElement != null)
						{
							persistenceUntiProvider = nlElement.item(0).getTextContent();
						}
						
						break;
					}
				}
			}
			
			//<-		
		} 
		catch (IOException e) 
		{
			be = new BussinesException("Error al leer el provider del persistence unit del Persistence.xml", e);
		} 
		catch (ParserConfigurationException e) 
		{
			be = new BussinesException("Error al leer el provider del persistence unit del Persistence.xml", e);
		} 
		catch (SAXException e) 
		{
			be = new BussinesException("Error al leer el provider del persistence unit del Persistence.xml", e);
		}
		catch(Exception e)
		{
			be = new BussinesException("Error al leer el provider del persistence unit del Persistence.xml", e);
		}
		
		if(be != null)
		{
			throw be; 
		}
		else if(Helper.stringIsNullOrEmpty(persistenceUnitName))
		{
			be = new BussinesException("Error al leer el provider del persistence unit del Persistence.xml: No se ecuentra el persitence unit.");
		}
	}
	
	/**
	 * Devuelve la implementación de la factoría de DAOs acorde al proveedor de la unidad de persistrencia usada.
	 * 
	 * @return
	 */
	public DaosFactory getDaosFactory()
	{
		if(persistenceUntiProvider.toLowerCase().contains("jpa"))
		{
			return new DaosFactoryJpaImpl(entityManager);
		}
		else
		{
			throw new BussinesException(String.format(
					"Proveedor de unidad de persistencia no soportado: %s.",
					persistenceUntiProvider
					));
		}
	}
}
