package energycontrol.back.daos;

import java.util.List;

import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;

public interface ConsumptionDao extends GenericDao<Consumption, String>
{
	/**
	 * Devuelve los consumos asociados al origen de consumos cuyo Id coincide con el pasado por argumento.
	 * 
	 * @param sourceId
	 * @return
	 */
	public List<Consumption> findSourceConsumptions(String sourceId);
	
	/**
	 * Devuelve los consumos asociados al maestro de origen de consumos cuyo código coincide con el pasado por argumento,
	 * y que son del mismo día que el del dateHour pasado por argumento.
	 * 
	 * @param codeMSource
	 * @param dateHour
	 * @return
	 */
	public List<Consumption> findSourceConsumptionsOfDay(String codeMSource, DateHour dateHour);
	
	/**
	 * Devuelve los consumos asociados al maestro de origen de consumos cuyo código coincide con el pasado por argumento,
	 * y a la fecha pasada por argumento.
	 * 
	 * @param codeMSource
	 * @param dateHour
	 * @return
	 */
	public List<Consumption> findSourceConsumption(String codeMSource, DateHour dateHour);
}
