package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.MSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class MSourceDaoJpaImplTest 
{
	private EntityManager entityManager;
	private MSourceDaoJpaImpl dao;
	
	public MSourceDaoJpaImplTest()
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("HSQLDB_TEST");
		this.entityManager = emf.createEntityManager();
		
		this.dao = new MSourceDaoJpaImpl(this.entityManager);
	}
	
	@Test
	public void crudTest()
	{
		boolean ok = true;
		
		MSource msNew = new MSource(ESourceType.Other, "TMS", "Test MSource");
		
		try
		{
			this.dao.insert(msNew);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("MSourceDaoJpaImplTest.crudTest: insert() KO: %s\n", e.getMessage());
		}
		
		if(ok)
		{
			System.out.println("MSourceDaoJpaImplTest.crudTest: insert() OK.");
			
			MSource msFind = null;
			try
			{
				msFind = this.dao.findById(msNew.getCode());
			}
			catch(Exception e)
			{
				ok = false;
				System.out.println("MSourceDaoJpaImplTest.crudTest: findById() KO.");
			}
			
			if(ok)
			{		
				if(msFind == null)
				{
					ok = false;
					System.out.println("MSourceDaoJpaImplTest.crudTest: findById() KO.");
				}
				else
				{
					System.out.println("MSourceDaoJpaImplTest.crudTest: findById() OK.");
					
					msNew.setDescription("Test MSource modificada");
					try
					{
						this.dao.save(msNew);
					}
					catch(Exception e)
					{
						ok = false;
						System.out.println("MSourceDaoJpaImplTest.crudTest: save() KO.");
					}
					
					if(ok)
					{
						msFind = this.dao.findById(msNew.getCode());
						
						if("Test MSource modificada" == msFind.getDescription())
						{
							System.out.println("MSourceDaoJpaImplTest.crudTest: save() OK.");
						}
						else
						{
							ok = false;
							System.out.println("MSourceDaoJpaImplTest.crudTest: save() KO.");
						}
						
						if(ok)
						{
							try
							{
								this.dao.delete(msNew);
							}
							catch(Exception e)
							{
								ok = false;
								System.out.println("MSourceDaoJpaImplTest.crudTest: delete() KO.");
							}
							
							if(ok)
							{
								msFind = this.dao.findById(msNew.getCode());
								
								if(msFind == null)
								{
									System.out.println("MSourceDaoJpaImplTest.crudTest: delete() OK.");
								}
								else
								{
									System.out.println("MSourceDaoJpaImplTest.crudTest: delete() KO.");
								}								
							}
						}
					}
				}
			}
		}
		
		assertEquals(true, ok);
	}
	
}
