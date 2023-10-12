package energycontrol.back.daos;

import java.util.List;

import energycontrol.back.entities.Bill;

public interface BillDao extends GenericDao<Bill, String>
{
	/**
	 * Busca facturas por el número de factura pasado por argumento
	 * 
	 * @param billNumber
	 * @return
	 */
	public List<Bill> findByBillNumber(String billNumber);
}
