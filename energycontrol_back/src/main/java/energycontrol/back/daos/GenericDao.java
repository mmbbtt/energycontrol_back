package energycontrol.back.daos;

import java.io.Serializable;
import java.util.List;


/**
 * Interfaz base para los DAOs en la que se definen los métodos comúnes a todos los DAOs
 * 
 * @param <T> Tipo de entidad 
 * @param <Id> Tipo de identficador de la entididad
 */
public interface GenericDao <T,Id extends Serializable> 
{
	/**
	 * Busca la entidad por el id pasado por argumento.
	 * 
	 * @param id Identificador de la entidad a buscar
	 * @return La entida encontrada, null en caso contrario.
	 */
	T findById (Id id);
	
	/**
	 * Busca todas las entidades de tipo T
	 * 
	 * @return Una lista con todas la entidades de tipo T
	 */
	List<T> findAll();
	
	/**
	 * Actualiza en la unidad de persistencia el objeto pasado por argumento.
	 * 
	 * @param objeto
	 */
	void save(T objeto);
	
	/**
	 * Borra de la unidad de persistenca el objeto pasado por argumento.
	 * @param objeto
	 */
	void delete(T objeto);
	
	/**
	 * Inserta en la unidad de persistencia el objeto pasado por argumento.
	 * 
	 * @param objeto
	 */
	void insert(T objeto);
}
