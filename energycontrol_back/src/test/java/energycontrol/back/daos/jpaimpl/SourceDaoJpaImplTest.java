package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.Source;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class SourceDaoJpaImplTest 
{
	private EntityManager entityManager;
	private SourceDaoJpaImpl dao;
	private MSource ms;
	private MSourceDaoJpaImpl msDao;
	
	public SourceDaoJpaImplTest()
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("HSQLDB_TEST");
		this.entityManager = emf.createEntityManager();
		
		this.dao = new SourceDaoJpaImpl(this.entityManager);
		
		ms = new MSource(ESourceType.Other, "TMS", "Test MSource");
		msDao = new MSourceDaoJpaImpl(this.entityManager);
		msDao.insert(ms);
	}
	
	@After
	public void tearDown()
	{
		msDao.delete(ms);
	}
	
	@Test
	public void crudTest()
	{
		boolean ok = true;
		
		LocalDate dateOfData = LocalDate.of(2023, Month.JANUARY, 1);		
		Source sNew = new Source(this.ms, dateOfData);
		
		try
		{
			this.dao.insert(sNew);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.println(String.format("SourceDaoJpaImplTest.crudTest: insert() KO: %s", e.getMessage()));
		}
		
		if(ok)
		{	
			List<Source> lSources = null;
			try
			{
				lSources = this.dao.findByCodeAndDate(sNew.getCode(), sNew.getDateOfData());
			}
			catch(Exception e)
			{
				ok = false;
				System.out.println(String.format("SourceDaoJpaImplTest.crudTest: findByCodeAndDate() KO: %s", e.getMessage()));
			}
			
			if(ok)
			{
				if((lSources == null) || (lSources.size() == 0))
				{
					ok = false;
					System.out.println("SourceDaoJpaImplTest.crudTest: insert() o findByCodeAndDate() KO.");
				}
				else
				{
					Source sFind = lSources.get(0);
					
					if(
						   (sFind.getCode() == sNew.getCode())
						&& (sFind.getDescription() == sNew.getDescription())
						&& (sFind.getDateOfData() == sNew.getDateOfData())
						)
					{
						System.out.println("SourceDaoJpaImplTest.crudTest: insert() OK.");
						System.out.println("SourceDaoJpaImplTest.crudTest: findByCodeAndDate() OK.");
					}
					else
					{
						ok = false;
						System.out.println("SourceDaoJpaImplTest.crudTest: insert() o findByCodeAndDate() KO.");
					}
					
					if(ok)
					{
						LocalDate dateOfData2 = LocalDate.of(2023, Month.JANUARY, 2);
						sNew.setDateOfData(dateOfData2);
						
						try
						{
							this.dao.save(sNew);
						}
						catch(Exception e)
						{
							ok = false;
							System.out.println(String.format("SourceDaoJpaImplTest.crudTest: save() KO: %s", e.getLocalizedMessage()));
						}
						
						if(ok)
						{
							String id = sFind.getId();
							
							try
							{
								sFind = this.dao.findById(id);
							}
							catch(Exception e)
							{
								ok = false;
								System.out.println(String.format("SourceDaoJpaImplTest.crudTest: findbyId() KO: %s", e.getMessage()));
							}
							
							if(ok)
							{
								if(sFind != null)
								{
									if(sFind.getDateOfData() == dateOfData2)
									{
										System.out.println("SourceDaoJpaImplTest.crudTest: save() OK.");
									}
									else
									{
										System.out.println("SourceDaoJpaImplTest.crudTest: save() KO.");
									}
								}
								else
								{
									ok = false;
									System.out.println("SourceDaoJpaImplTest.crudTest: findbyId() KO.");
								}
								
								if(ok)
								{
									try
									{
										this.dao.delete(sFind);
									}
									catch(Exception e)
									{
										ok = false;
										System.out.println(String.format("SourceDaoJpaImplTest.crudTest: delete() KO: %s", e.getMessage()));
									}
									
									if(ok)
									{
										sFind = this.dao.findById(id);
										
										if(sFind == null)
										{
											System.out.println("SourceDaoJpaImplTest.crudTest: delete() OK.");
										}
										else
										{
											System.out.println("SourceDaoJpaImplTest.crudTest: delete() KO.");
										}
									}
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
