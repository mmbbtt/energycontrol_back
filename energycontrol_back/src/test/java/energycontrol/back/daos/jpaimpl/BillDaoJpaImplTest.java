package energycontrol.back.daos.jpaimpl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import energycontrol.back.daos.BillDao;
import energycontrol.back.daos.DateHourDao;
import energycontrol.back.daos.MSourceDao;
import energycontrol.back.entities.Bill;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.MSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class BillDaoJpaImplTest 
{
	private EntityManager entityManager;
	private BillDao dao;

	private DateHourDao dateHourDao;
	private MSourceDao mSourceDao;
	private MSource mSource;
	

	public BillDaoJpaImplTest() throws Exception 
	{
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("HSQLDB_TEST");
		this.entityManager = emf.createEntityManager();
		
		this.dao = new BillDaoJpaImpl(this.entityManager);
		
		this.dateHourDao = new DateHourDaoJpaImpl(this.entityManager);
		for(int i = 1; i <= 24; i++)
		{
			DateHour hourId = new DateHour(2022, 10, 7, i);
			this.dateHourDao.insert(hourId);
		}
		
		this.mSourceDao = new MSourceDaoJpaImpl(this.entityManager);
		mSource = new MSource(ESourceType.Bill, "FAC", "Factura");
		this.mSourceDao.insert(mSource);
	}
	
	@After
	public void tearDown()
	{
		List<DateHour> lDateHour = this.dateHourDao.findAll();
		for(int i = 0; i < lDateHour.size(); i ++)
		{
			this.dateHourDao.delete(lDateHour.get(i));
		}
		
		this.mSourceDao.delete(this.mSource);
		
		this.entityManager.close();
	}
	
	@Test
	public void crudTest() 
	{
		boolean ok = true;
		
		//-> Insert
		Bill b = new Bill(
			 this.mSource					//mSource
			,LocalDate.of(2022, 10, 8)		//dateOfData
			,"FE22137026650704" 			//billNumber
			,new DateHour(2022, 10, 7, 1)	//dateHourFrom
			,new DateHour(2022, 10, 7, 24)	//dateHourTo
			);
		
		try
		{
			this.dao.insert(b);
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("BillDaoJpaImplTest.crudTest: insert() KO: %s\n", e.getMessage());
		}	
		
		if(ok)
		{
			//-> FindById
			Bill bFind = null;
			
			try
			{
				bFind = this.dao.findById(b.getId());
			}
			catch(Exception e)
			{
				ok = false;
				System.out.printf("BillDaoJpaImplTest.crudTest: findById() KO: %s\n", e.getMessage());
			}
			
			
			if(ok)
			{
				if(bFind == null)
				{
					ok = false;
					System.out.println("BillDaoJpaImplTest.crudTest: insert() o findById() KO.");
				}
				else
				{
					System.out.println("BillDaoJpaImplTest.crudTest: insert() y findById() OK.");
					
					//-> findByBillNumber
					List<Bill> lBills = null;
					
					try
					{
						lBills = this.dao.findByBillNumber(b.getBillNumber());
					}
					catch(Exception e)
					{
						ok = false;
						System.out.printf("BillDaoJpaImplTest.crudTest: findByBillNumber() KO: %s\n", e.getMessage());
					}
					
					if(
						(lBills == null)
						|| lBills.size() != 1
						)
					{
						ok = false;
						System.out.println("BillDaoJpaImplTest.crudTest: findByBillNumber() KO.");
					}
					else
					{
						System.out.println("BillDaoJpaImplTest.crudTest: findByBillNumber() OK.");
						
						//-> Save
						b.setKwhTotal(14.2);
						
						try
						{
							this.dao.save(b);
						}
						catch(Exception e)
						{
							ok = false;
							System.out.printf("BillDaoJpaImplTest.crudTest: save() KO: %s\n", e.getMessage());
						}
						
						if(ok)
						{
							bFind = this.dao.findById(b.getId());
							
							if(bFind == null)
							{
								ok = false;
								System.out.println("BillDaoJpaImplTest.crudTest: findById() KO.");
							}
							else
							{
								if(
									bFind.getKwhTotal() != 14.2
									)
								{
									ok = false;
									System.out.println("BillDaoJpaImplTest.crudTest: save() KO.");
								}
								else
								{
									System.out.println("BillDaoJpaImplTest.crudTest: save() OK.");
									
									//-> Delete
									try
									{
										this.dao.delete(b);
									}
									catch(Exception e)
									{
										ok = false;
										System.out.printf("BillDaoJpaImplTest.crudTest: delete() KO: %s\n", e.getMessage());
									}
									
									if(ok)
									{
										bFind = this.dao.findById(b.getId());
										
										if(bFind == null)
										{
											System.out.println("BillDaoJpaImplTest.crudTest: delete() OK.");
										}
										else
										{
											ok = false;
											System.out.println("BillDaoJpaImplTest.crudTest: delete() KO.");
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
