package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import energycontrol.back.daos.ConsumptionDao;
import energycontrol.back.daos.DateHourDao;
import energycontrol.back.daos.MSourceDao;
import energycontrol.back.daos.SourceDao;
import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.Source;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ConsumptionDaoJpaImplTest 
{
	private EntityManager entityManager;
	private ConsumptionDao dao;
	
	private DateHour dateHour;
	private MSource mSource;
	private Source source;
	private DateHourDao hourIdDao;
	private MSourceDao mSourceDao;
	private SourceDao sourceDao;

	public ConsumptionDaoJpaImplTest() throws Exception 
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("HSQLDB_TEST");
		this.entityManager = emf.createEntityManager();
		
		this.dao = new ConsumptionDaoJpaImpl(this.entityManager);
		
		this.dateHour = new DateHour(2001, 1, 1, 1);
		this.hourIdDao = new DateHourDaoJpaImpl(this.entityManager);
		this.hourIdDao.insert(this.dateHour);
		
		this.mSource = new MSource(ESourceType.Other, "TMS", "Test MSource");
		this.mSourceDao = new MSourceDaoJpaImpl(this.entityManager);
		this.mSourceDao.insert(this.mSource);
		
		LocalDate dateOfData = LocalDate.of(2001, Month.JANUARY, 1);
		this.source = new Source(this.mSource, dateOfData);
		this.sourceDao = new SourceDaoJpaImpl(this.entityManager);
		this.sourceDao.insert(this.source);
	}
	
	@After
	public void tearDown()
	{
		this.sourceDao.delete(this.source);
		this.mSourceDao.delete(this.mSource);
		this.hourIdDao.delete(this.dateHour);
		
		this.entityManager.close();
	}

	@Test
	public void crudTest() 
	{
		boolean ok = true;
		
		//-> Insert
		Consumption cNew = new Consumption(
			 this.dateHour
			,0.345
			,ECollectionMethod.REAL
			,ETimeBand.Unknown
			,this.source
			);
		
		try
		{
			this.dao.insert(cNew);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("ConsumptionDaoJpaImplTest.crudTest: insert() KO: %s\n", e.getMessage());
		}
		
		if(ok)
		{
			//->findById
			Consumption cFind = null;
			
			try
			{
				cFind = this.dao.findById(cNew.getId());
			}
			catch(Exception e)
			{
				ok = false;
				System.out.printf("ConsumptionDaoJpaImplTest.crudTest: findById() KO: %s\n", e.getMessage());
			}
			
			if(ok)
			{
				if(cFind == null)
				{
					ok = false;
					System.out.println("ConsumptionDaoJpaImplTest.crudTest: insert() o findById() KO.");
				}
				else
				{
					System.out.println("ConsumptionDaoJpaImplTest.crudTest: insert() y findById() OK.");
					
					//->save
					cNew.setKwh(0.111);
					
					try
					{
						this.dao.save(cNew);
					}
					catch(Exception e)
					{
						ok = false;
						System.out.printf("ConsumptionDaoJpaImplTest.crudTest: save() KO: %s\n", e.getMessage());
					}
					
					if(ok)
					{
						cFind = this.dao.findById(cNew.getId());
						
						if(cFind == null)
						{
							ok = false;
							System.out.println("ConsumptionDaoJpaImplTest.crudTest: findById() KO.");
						}
						else
						{
							if(cFind.getKwh() != 0.111)
							{
								ok = false;
								System.out.println("ConsumptionDaoJpaImplTest.crudTest: save() KO.");							
							}
							else
							{
								System.out.println("ConsumptionDaoJpaImplTest.crudTest: save() OK.");
								
								//->findSourceConsumptions
								List<Consumption> lc = null;
								
								try
								{
									lc = this.dao.findSourceConsumptions(this.source.getId());
								}
								catch(Exception e)
								{
									ok = false;
									System.out.printf("ConsumptionDaoJpaImplTest.crudTest: findSourceConsumptions() KO: %s\n", e.getMessage());
								}
								
								if((lc != null) && (lc.size() > 0))
								{
									System.out.println("ConsumptionDaoJpaImplTest.crudTest: findSourceConsumptions() OK.");
								}
								else
								{
									ok = false;
									System.out.println("ConsumptionDaoJpaImplTest.crudTest: findSourceConsumptions() KO.");
								}
								
								//->delete
								try
								{
									this.dao.delete(cNew);
								}
								catch(Exception e)
								{
									ok = false;
									System.out.printf("ConsumptionDaoJpaImplTest.crudTest: delete() KO: %s\n", e.getMessage());
								}
								
								if(ok)
								{
									cFind = this.dao.findById(cNew.getId());
									
									if(cFind == null)
									{
										System.out.println("ConsumptionDaoJpaImplTest.crudTest: delete() OK.");
									}
									else
									{
										ok = false;
										System.out.println("ConsumptionDaoJpaImplTest.crudTest: delete() KO.");
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
