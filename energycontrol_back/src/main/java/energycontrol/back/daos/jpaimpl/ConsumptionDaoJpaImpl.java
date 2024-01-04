package energycontrol.back.daos.jpaimpl;

import java.util.List;

import energycontrol.back.bussines.EntitiesHelper;
import energycontrol.back.daos.ConsumptionDao;
import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ConsumptionDaoJpaImpl extends GenericDaoJpaImpl<Consumption, String> implements ConsumptionDao
{
	public ConsumptionDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}

	
	public List<Consumption> findSourceConsumptions(String sourceId) 
	{
		List<Consumption> lObjects = null;

		TypedQuery<Consumption> consulta = manager.createQuery(
				"select c from Consumption c where c.source.id = :sourceId order by c.id",
				Consumption.class
				);
		consulta.setParameter("sourceId", sourceId);
		
		lObjects = consulta.getResultList();

		return lObjects;
	}
	
	public List<Consumption> findSourceConsumptionsOfDay(String codeMSource, DateHour dateHour) 
	{
		List<Consumption> lObjects = null;
		
		String dateHourMin = String.format("%s01", EntitiesHelper.dateHour2StringDate(dateHour));
		String dateHourMax = String.format("%s24", EntitiesHelper.dateHour2StringDate(dateHour));

		TypedQuery<Consumption> consulta = manager.createQuery(
				"select c from Consumption c " + 
		        "where c.source.mSource.code = :codeMSource " + 
				"and c.dateHour.id >= :dateHourMin " + 
				"and c.dateHour.id <= :dateHourMax " + 
				"order by c.id"
				,Consumption.class
				);
		consulta.setParameter("codeMSource", codeMSource);
		consulta.setParameter("dateHourMin", dateHourMin);
		consulta.setParameter("dateHourMax", dateHourMax);
		
		lObjects = consulta.getResultList();

		return lObjects;
	}
	
	public List<Consumption> findSourceConsumption(String codeMSource, DateHour dateHour) 
	{
		List<Consumption> lObjects = null;
		
		TypedQuery<Consumption> consulta = manager.createQuery(
				"select c from Consumption c " + 
		        "where c.source.mSource.code = :codeMSource " + 
				"and c.dateHour.id = :dateHour " + 
				"order by c.id"
				,Consumption.class
				);
		consulta.setParameter("codeMSource", codeMSource);
		consulta.setParameter("dateHour", dateHour.getId());
		
		lObjects = consulta.getResultList();

		return lObjects;
	}
}
