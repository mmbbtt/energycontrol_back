package energycontrol.back.daos;

import java.util.List;

import energycontrol.back.entities.BillConsumption;
public interface BillConsumptionDao extends GenericDao<BillConsumption, String>
{
	/**
	 * Devuelve los consumos asociados a la factura cuyo número coincide con el pasado por argumento.
	 * 
	 * @param billNumber
	 * @return
	 */
	public List<BillConsumption> findBillConsumptions(String billNumber);
}
