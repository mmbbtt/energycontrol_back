package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import energycontrol.back.daos.BillConsumptionDao;
import energycontrol.back.daos.BillDao;
import energycontrol.back.daos.DateHourDao;
import energycontrol.back.daos.MSourceDao;
import energycontrol.back.entities.Bill;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.BillConsumption;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class BillConsumptionDaoJpaImplTest 
{
	private String persistenceUnitName = "HSQLDB_TEST";
	
	private EntityManager entityManager;
	private BillConsumptionDao dao;
	
	private BillDao billDao;
	private DateHourDao dateHourDao;
	private MSourceDao mSourceDao;
	private Bill bill;
	private MSource mSource;
	
	
	public BillConsumptionDaoJpaImplTest() throws Exception 
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		this.entityManager = emf.createEntityManager();
		this.dao = new BillConsumptionDaoJpaImpl(this.entityManager);
		
		this.dateHourDao = new DateHourDaoJpaImpl(this.entityManager);
		for(int i = 1; i <= 24; i++)
		{
			DateHour dh = new DateHour(2022, 10, 7, i);
			this.dateHourDao.insert(dh);
		}
		
		this.mSourceDao = new MSourceDaoJpaImpl(this.entityManager);
		this.mSource = new MSource(ESourceType.Bill, "FAC", "Factura");
		this.mSourceDao.insert(this.mSource);
		
		this.billDao = new BillDaoJpaImpl(this.entityManager);
		
		this.bill = new Bill(
			 this.mSource					//mSource
			,LocalDate.of(2022, 8, 1)		//dateOfData
			,"FE22137026650704" 			//billNumber
			,new DateHour(2022, 10, 7, 1) 	//hourFrom
			,new DateHour(2022, 10, 7, 24) 	//hourTo
			);
		this.billDao.insert(bill);
		
	}
	
	@After
	public void tearDown()
	{
		this.billDao.delete(this.bill);
		
		this.mSourceDao.delete(this.mSource);

		List<DateHour> lDateHours = this.dateHourDao.findAll();
		for(int i = 0; i < lDateHours.size(); i ++)
		{
			this.dateHourDao.delete(lDateHours.get(i));
		}
	
		this.entityManager.close();
	}
	
	@Test
	public void crudTest() 
	{
		boolean ok = true;
		
		//-> Insert
		BillConsumption bc = new BillConsumption(
			 new DateHour(2022, 10, 7, 1)		//dateHour
			,0.454								//kwh
			,0.295902972508						//kwhCost
			,0.134339949519						//hourCost
			,ECollectionMethod.SIMULATED		//collectionMethod
			,ETimeBand.OffPeak					//timeBand
			,this.bill							//source
			);
		
		try
		{
			this.dao.insert(bc);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("BillConsumptionDaoJpaImplTest.crudTest: insert() KO: %s\n", e.getMessage());
		}	
		
		if(ok)
		{
			BillConsumption bcFind = null;
			
			try
			{
				bcFind = this.dao.findById(bc.getId());
			}
			catch(Exception e)
			{
				ok = false;
				System.out.printf("BillConsumptionDaoJpaImplTest.crudTest: findById() KO: %s\n", e.getMessage());
			}	
			
			if(ok)
			{
				if(bcFind == null)
				{
					ok = false;
					System.out.println("BillConsumptionDaoJpaImplTest.crudTest: insert() o findById() KO.");
				}
				else
				{
					System.out.println("BillConsumptionDaoJpaImplTest.crudTest: insert() y findById() OK.");
					
					//-> Save
					bc.setTimeBand(ETimeBand.Peak);
					
					try
					{
						this.dao.save(bc);
					}
					catch(Exception e)
					{
						ok = false;
						System.out.printf("BillConsumptionDaoJpaImplTest.crudTest: save() KO: %s\n", e.getMessage());
					}	
					
					if(ok)
					{
						bcFind = this.dao.findById(bc.getId());
						
						if(bcFind == null)
						{
							ok = false;
							System.out.println("BillConsumptionDaoJpaImplTest.crudTest: findById() KO.");
						}
						else
						{
							if(bcFind.getTimeBand() != ETimeBand.Peak)
							{
								ok = false;
								System.out.println("BillConsumptionDaoJpaImplTest.crudTest: save() KO.");
							}
							else
							{
								System.out.println("BillConsumptionDaoJpaImplTest.crudTest: save() OK.");
								
								//-> findConsumptionsAtBill
								try
								{
									List<BillConsumption> lbc = this.dao.findBillConsumptions(this.bill.getBillNumber());
									
									if(lbc.size() == 0)
									{
										ok = false;
										System.out.println("BillConsumptionDaoJpaImplTest.crudTest: findConsumptionsAtBill() KO.");
									}
									else
									{
										System.out.println("BillConsumptionDaoJpaImplTest.crudTest: findConsumptionsAtBill() OK.");
									}
								}
								catch(Exception e)
								{
									ok = false;
									System.out.printf("BillConsumptionDaoJpaImplTest.crudTest: findConsumptionsAtBill() KO: %s\n", e.getMessage());
								}
								
								
								//-> Delete
								try
								{
									this.dao.delete(bc);
								}
								catch(Exception e)
								{
									ok = false;
									System.out.printf("BillConsumptionDaoJpaImplTest.crudTest: delete() KO: %s\n", e.getMessage());
								}	
								
								if(ok)
								{
									bcFind = this.dao.findById(bc.getId());
									
									if(bcFind == null)
									{
										System.out.println("BillConsumptionDaoJpaImplTest.crudTest: delete() OK.");
									}
									else
									{
										ok = false;
										System.out.println("BillConsumptionDaoJpaImplTest.crudTest: delete() KO.");
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
