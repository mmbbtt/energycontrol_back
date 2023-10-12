package energycontrol.back.daos.jpaimpl;

import java.time.LocalDate;
import java.util.List;

import energycontrol.back.daos.SourceDao;
import energycontrol.back.entities.Source;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class SourceDaoJpaImpl extends GenericDaoJpaImpl<Source, String> implements SourceDao
{

	public SourceDaoJpaImpl(EntityManager entityManger) 
	{
		super(entityManger);
	}
	
	@Override
	public List<Source> findByCodeAndDate(String code, LocalDate dateOfData) 
	{
		List<Source> lSources = null;
		
		@SuppressWarnings("unchecked")
		TypedQuery<Source> consulta = (TypedQuery<Source>) this.manager.createQuery(
				  "SELECT s "
				+ "FROM Source s " 
				+ "WHERE s.mSource.code = :code "
				+ "AND s.dateOfData = :dateOfData "
				+ "ORDER BY s.dateOfData desc"
				);
		
		consulta.setParameter("code", code);
		consulta.setParameter("dateOfData", dateOfData);
		
		lSources = consulta.getResultList();
		
		return lSources;
	}
	
}
