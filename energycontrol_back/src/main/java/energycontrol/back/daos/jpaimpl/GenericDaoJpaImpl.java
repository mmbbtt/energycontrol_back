package energycontrol.back.daos.jpaimpl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import energycontrol.back.daos.GenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

/**
 * Implementación JPA de la interfaz GenericDAO
 * 
 * @param <T>
 * @param <Id>
 */
public class GenericDaoJpaImpl<T, Id extends Serializable> implements GenericDao<T, Id>
{
	protected Class<T> claseDePersistencia;
	protected EntityManager manager;
	
	@SuppressWarnings("unchecked")
	public GenericDaoJpaImpl(EntityManager entityManger)
	{
		this.claseDePersistencia = 
				(Class<T>) ( (ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		this.manager = entityManger;
	}

	public T findById(Id id) 
	{
		T object = null;

		object = (T) manager.find(claseDePersistencia, id);

		return object;
	}


	public List<T> findAll() 
	{
		List<T> lObjects = null;

		TypedQuery<T> consulta = manager.createQuery(
				"select o from "
				+ claseDePersistencia.getSimpleName() 
				+ " o",
				claseDePersistencia);
		lObjects = consulta.getResultList();

		return lObjects;
	}

	
	public void save(T objeto) 
	{
		EntityTransaction tx = null;
		
		try 
		{
			tx = manager.getTransaction();
			tx.begin();
			manager.merge(objeto);
			tx.commit();
		} 
		catch (PersistenceException e) 
		{
			tx.rollback();
			throw e;
		}	
	}

	
	public void delete(T objeto) 
	{
		EntityTransaction tx = null;
		try 
		{
			tx = manager.getTransaction();
			tx.begin();
			manager.remove(manager.merge(objeto));
			tx.commit();
		} 
		catch (PersistenceException e) 
		{
			tx.rollback();
			throw e;
		} 
	}

	
	public void insert(T objeto) 
	{
		EntityTransaction tx = null;
		try 
		{
			tx = manager.getTransaction();
			tx.begin();
			manager.persist(objeto);
			tx.commit();
		} 
		catch (PersistenceException e) 
		{
			tx.rollback();
			throw e;
		} 
	}
}
