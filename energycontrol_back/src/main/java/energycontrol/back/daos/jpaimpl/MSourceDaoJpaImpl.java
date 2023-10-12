package energycontrol.back.daos.jpaimpl;

import energycontrol.back.daos.MSourceDao;
import energycontrol.back.entities.MSource;
import jakarta.persistence.EntityManager;

public class MSourceDaoJpaImpl extends GenericDaoJpaImpl<MSource, String> implements MSourceDao
{
	public MSourceDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}

}
