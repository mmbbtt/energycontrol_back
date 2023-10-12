package energycontrol.back.daos.jpaimpl;

import energycontrol.back.daos.BillConsumptionDao;
import energycontrol.back.daos.BillDao;
import energycontrol.back.daos.ConsumptionDao;
import energycontrol.back.daos.DaosFactory;
import energycontrol.back.daos.DateHourDao;
import energycontrol.back.daos.MSourceDao;
import energycontrol.back.daos.SourceDao;
import jakarta.persistence.EntityManager;

public class DaosFactoryJpaImpl implements DaosFactory
{
	private EntityManager entityManager;
	
	public DaosFactoryJpaImpl(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	public MSourceDao getMSourceDao() 
	{
		return new MSourceDaoJpaImpl(this.entityManager);
	}

	public SourceDao getSourceDao() 
	{
		return new SourceDaoJpaImpl(this.entityManager);
	}

	public BillDao getBillDao() 
	{
		return new BillDaoJpaImpl(this.entityManager);
	}

	public DateHourDao getDateHourDao() 
	{
		return new DateHourDaoJpaImpl(this.entityManager);
	}

	public ConsumptionDao getConsumptionDao() 
	{
		return new ConsumptionDaoJpaImpl(this.entityManager);
	}

	public BillConsumptionDao getBillConsumptionDao() 
	{
		return new BillConsumptionDaoJpaImpl(this.entityManager);
	}

}
