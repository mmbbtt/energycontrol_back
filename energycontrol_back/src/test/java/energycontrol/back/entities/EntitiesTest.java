package energycontrol.back.entities;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntitiesTest 
{
	private EntityManager entityManager = null;
	
	@Test
	public void GenerateDbTest()
	{
		try
		{
			String persistenceUnit = "HSQLDB_TEST";
			
			System.out.printf("Generando la base de datos de la unidad de persistencia %s ...\n", persistenceUnit);
			
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
			this.entityManager = emf.createEntityManager();
			
			if(this.entityManager != null)
			{
				System.out.println("Base de datos generada correctamente");
				assertTrue(true);
			}
			else
			{
				System.out.println("Error al genear base de datos");
				assertTrue(false);
			}
		}
		catch(Exception e)
		{
			System.out.printf("Se ha producido una excepción: %s", e.getMessage());
			e.printStackTrace();
			
			fail(String.format("Fails GenerateDbTest(): %s", e.getMessage()));
		}
	}
}
