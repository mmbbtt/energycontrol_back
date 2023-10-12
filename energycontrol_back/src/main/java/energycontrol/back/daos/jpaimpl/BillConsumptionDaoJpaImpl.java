package energycontrol.back.daos.jpaimpl;

import java.util.List;

import energycontrol.back.daos.BillConsumptionDao;
import energycontrol.back.entities.BillConsumption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class BillConsumptionDaoJpaImpl extends GenericDaoJpaImpl<BillConsumption, String> implements BillConsumptionDao
{
	public BillConsumptionDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}

	public List<BillConsumption> findBillConsumptions(String billNumber)
	{
		List<BillConsumption> lObjects = null;

		TypedQuery<BillConsumption> consulta = manager.createQuery(
				"select bc from BillConsumption bc where bc.source.billNumber = :billNumber order by bc.id",
				BillConsumption.class
				);
		consulta.setParameter("billNumber", billNumber);
		
		lObjects = consulta.getResultList();

		return lObjects;
	}
}
