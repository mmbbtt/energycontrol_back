package energycontrol.back.daos;

import energycontrol.back.entities.MSource;

public interface MSourceDao extends GenericDao<MSource, String>
{
	public MSource findByCode(String code);
}
