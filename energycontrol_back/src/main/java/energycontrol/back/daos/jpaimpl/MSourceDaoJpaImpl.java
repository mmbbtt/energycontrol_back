package energycontrol.back.daos.jpaimpl;

import java.util.List;

import energycontrol.back.daos.MSourceDao;
import energycontrol.back.entities.MSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class MSourceDaoJpaImpl extends GenericDaoJpaImpl<MSource, String> implements MSourceDao
{
	public MSourceDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}
	
	@Override
	public MSource findByCode(String code)
	{
		MSource ms = null;
		
		List<MSource> lMSources= null;
		
		@SuppressWarnings("unchecked")
		TypedQuery<MSource> consulta = (TypedQuery<MSource>) this.manager.createQuery(
				  "SELECT ms "
				+ "FROM MSource ms " 
				+ "WHERE ms.code = :code "
				);
		consulta.setParameter("code", code);
		lMSources = consulta.getResultList();
		
		if(
			(lMSources != null)
			&& lMSources.size() > 0
			)
		{
			ms = lMSources.get(0);
		}
		
		return ms;
	}

}
