package energycontrol.back.daos;

/**
 * Interfaz para la factoría de daos
 */
public interface DaosFactory 
{
	/**
	 * Devuelve una instacia de la clase MSourceDao
	 * @return
	 */
	public MSourceDao getMSourceDao();
	
	/**
	 * Devuelve una instacia de la clase SourceDao
	 * @return
	 */
	public SourceDao getSourceDao();
	
	/**
	 * Devuelve una instacia de la clase BillDao
	 * @return
	 */
	public BillDao getBillDao();
	
	/**
	 * Devuelve una instacia de la clase DateHourDao
	 * @return
	 */
	public DateHourDao getDateHourDao();
	
	/**
	 * Devuelve una instacia de la clase ConsumptionDao
	 * @return
	 */
	public ConsumptionDao getConsumptionDao();
	
	/**
	 * Devuelve una instacia de la clase BillConsumptionDao
	 * @return
	 */
	public BillConsumptionDao getBillConsumptionDao();
}
