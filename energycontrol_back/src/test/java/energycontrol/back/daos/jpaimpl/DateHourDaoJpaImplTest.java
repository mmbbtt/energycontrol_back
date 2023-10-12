package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import energycontrol.back.daos.DateHourDao;
import energycontrol.back.entities.DateHour;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DateHourDaoJpaImplTest 
{
	private EntityManager entityManager;
	private DateHourDao dao;
	
	public DateHourDaoJpaImplTest()
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("HSQLDB_TEST");
		this.entityManager = emf.createEntityManager();
		
		this.dao = new DateHourDaoJpaImpl(this.entityManager);
	}
	
	@Test
	public void crudTest()
	{
		boolean ok = true;
		
		DateHour dhNew = new DateHour(2001, 1, 1, 1);
		
		try
		{
			this.dao.insert(dhNew);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("DateHourDaoJpaImplTest.crudTest: insert() KO: %s\n", e.getMessage());
		}
		
		if(ok)
		{
			DateHour dhFind = null;
			
			try
			{
				dhFind = this.dao.findById(dhNew.getId());
			}
			catch(Exception e)
			{
				ok = false;
				System.out.printf("DateHourDaoJpaImplTest.crudTest: insert() KO: %s\n", e.getMessage());
			}
			
			if(ok)
			{
				if(dhFind == null)
				{
					ok = false;
					System.out.println("DateHourDaoJpaImplTest.crudTest: insert() o findById() KO.");
				}
				else
				{
					System.out.println("DateHourDaoJpaImplTest.crudTest: insert() y findById() OK.");
				}
				
				if(ok)
				{
					try
					{
						this.dao.delete(dhNew);
					}
					catch(Exception e)
					{
						ok = false;
						System.out.printf("DateHourDaoJpaImplTest.crudTest: delete() KO: %s\n", e.getMessage());
					}
					
					if(ok)
					{
						dhFind = this.dao.findById(dhNew.getId());
						
						if(dhFind == null)
						{				
							System.out.println("DateHourDaoJpaImplTest.crudTest: delete() OK.");
						}
						else
						{
							ok = false;
							System.out.println("DateHourDaoJpaImplTest.crudTest: delete() KO.");
						}
					}				
				}
			}
		}
		
		assertEquals(true, ok);
	}
}
