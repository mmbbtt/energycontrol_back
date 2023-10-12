package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class DaosFactoryJpaImplTest 
{
	private EntityManager entityManager;
	
	
	public DaosFactoryJpaImplTest()
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("HSQLDB_TEST");
		this.entityManager = emf.createEntityManager();
	}
	
	@Test
	public void test()
	{
		boolean ok = true;
		
		DaosFactoryJpaImpl daosFactory = null;
	    
		try
		{
			daosFactory = new DaosFactoryJpaImpl(this.entityManager);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("DaosFactoryJpaImplTest.test: DaosFactoryJpaImpl() KO: %s\n", e.getMessage());
		}
		
		if(daosFactory == null)
		{
			System.out.println("DaosFactoryJpaImplTest.test: DaosFactoryJpaImpl() KO");
		}
		else
		{
			try
			{
				if(daosFactory.getBillConsumptionDao() == null)
				{
					ok = false;
					System.out.println("DaosFactoryJpaImplTest.test: getBillConsumptionDao() KO");
				}
				else if(daosFactory.getBillDao() == null)
				{
					ok = false;
					System.out.println("DaosFactoryJpaImplTest.test: getBillDao() KO");
				}
				else if(daosFactory.getConsumptionDao() == null)
				{
					ok = false;
					System.out.println("DaosFactoryJpaImplTest.test: getConsumptionDao() KO");
				}
				else if(daosFactory.getDateHourDao() == null)
				{
					ok = false;
					System.out.println("DaosFactoryJpaImplTest.test: getDateHourDao() KO");
				}	
				else if(daosFactory.getMSourceDao() == null)
				{
					ok = false;
					System.out.println("DaosFactoryJpaImplTest.test: getMSourceDao() KO");
				}
				else if(daosFactory.getSourceDao() == null)
				{
					ok = false;
					System.out.println("DaosFactoryJpaImplTest.test: getSourceDao() KO");
				}
			}
			catch(Exception e)
			{
				ok = false;
				System.out.printf("DaosFactoryJpaImplTest.test: Se ha producido una excepción: %s\n", e.getMessage());
			}
			
		}
		
		assertEquals(true, ok);
	}
}
