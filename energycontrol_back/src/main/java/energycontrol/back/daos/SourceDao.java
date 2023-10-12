package energycontrol.back.daos;

import java.time.LocalDate;
import java.util.List;

import energycontrol.back.entities.Source;

public interface SourceDao extends GenericDao<Source, String>
{
	public List<Source> findByCodeAndDate(String code, LocalDate dateOfData);
}
