package energycontrol.back.daos.jpaimpl;

import java.util.List;

import energycontrol.back.daos.BillDao;
import energycontrol.back.entities.Bill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class BillDaoJpaImpl extends GenericDaoJpaImpl<Bill, String> implements BillDao
{
	public BillDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}

	
	public List<Bill> findByBillNumber(String billNumber) 
	{
		List<Bill> lBills = null;
		
		@SuppressWarnings("unchecked")
		TypedQuery<Bill> consulta = (TypedQuery<Bill>) this.manager.createQuery(
				  "SELECT b "
				+ "FROM Bill b " 
				+ "WHERE b.billNumber = :billNumber "
				);
		
		consulta.setParameter("billNumber", billNumber);
		
		lBills = consulta.getResultList();
		
		return lBills;
	}
}
