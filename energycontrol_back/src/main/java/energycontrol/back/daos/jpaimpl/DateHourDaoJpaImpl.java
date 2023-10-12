package energycontrol.back.daos.jpaimpl;

import energycontrol.back.daos.DateHourDao;
import energycontrol.back.entities.DateHour;
import jakarta.persistence.EntityManager;

public class DateHourDaoJpaImpl extends GenericDaoJpaImpl<DateHour, String> implements DateHourDao
{
	public DateHourDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}
}
