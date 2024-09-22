package energycontrol.back.services;

import java.time.LocalDate;
import java.util.List;

import mbt.utilities.ActionResult;
import mbt.utilities.GenericActionResult;

import energycontrol.back.entities.Bill;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.Source;

/**
 * Interfaz de los servicios del back
 */
public interface EnergyControlBackService 
{
	/**
	 * Genera y periste las fecha-horas comprendidas entre dateHourFrom y dateHourTo, ambas incluidos.
	 * 
	 * @param dateHourFrom
	 * @param dateHourTo
	 * @return El número de fecha-horas generadas.
	 */
	public GenericActionResult<Integer> generateDateHours(DateHour dateHourFrom, DateHour dateHourTo);
	
	/**
	 * Persiste el maestro de origen de consumo pasado por argumnto.
	 * 
	 * @param mSource
	 * @return El MSource persistido.
	 */
	public GenericActionResult<MSource> saveMSource(MSource mSource);
	
	/**
	 * Devuelve una lista con todos los maestros de orígenes de consumo existentes en la unidad de persistencia.
	 * 
	 * @return
	 */
	public GenericActionResult<List<MSource>> getMSources();
	
	/**
	 * Persiste el origen de consumo pasado por argumnto.
	 * 
	 * @param source
	 * @return El origen de consumo persistido.
	 */
	public GenericActionResult<Source> saveSource(Source source);
	
	/**
	 * Borra de la unidad de persistencia el origen de datos, y todos sus consumos asociados.
	 * 
	 * @param source
	 * @return
	 */
	public ActionResult deleteSource(Source source);
	
	/**
	 * Devuelve una lista con todos los orígenes de consumos existentes en la unidad de persistencia.
	 * 
	 * @return
	 */
	public GenericActionResult<List<Source>> getSources();
	
	/**
	 * Persiste la factura pasada por argumnto.
	 * 
	 * @param bill
	 * @return La factura persistida.
	 */
	public GenericActionResult<Bill> saveBill(Bill bill);
	
	/**
	 * Borra de la unidad de persistencia la factura, y todos sus consumos, cuyo número de factura coincide con el pasado por argumento.
	 * 
	 * @param billNumber
	 * @return
	 */
	public ActionResult deleteBill(String billNumber);
	
	/**
	 * Devuevel todas las facturas existentes cuyo número de factura coincide con el pasado por argumento.
	 * NOTA: Sólo debería existir una.
	 * 
	 * @param billNumber
	 * @return
	 */
	public GenericActionResult<Bill> getBill(String billNumber);
	
	/**
	 * Crea y persiste una factura, de Naturgy, a partir del fichero csv pasado por argumento, así como sus líneas de consumos.
	 * 
	 * @param csvPathFile Fichero CSV con el detalle de consumos de una factura de Naturgy.
	 * @param dateOfBill Fecha de emisión de la factura.
	 * @param collectionMethod Método de obtención de los consumos de la factura.
	 * @return si todo fue bien, en GenericActionResult.ResultObject devueve la cadena "<num consumos genrados> filas OK / <total de consumos d ela factura> filas rows"
	 */
	public GenericActionResult<Bill> loadBillNaturgyFromCsv(String csvPathFile, LocalDate dateOfBill, ECollectionMethod collectionMethod);
	
	/**
	 * Crea y persiste los consumos del fichero csv pasado por argumento y, que debe ser generado por un medidor Efergy E2.
	 * 
	 * @param csvPathFile Fichero CSV con los consumos generados por un medidor Efergy E2.
	 * @param dateOfData Fecha de la obtención del fichero.
	 * @return
	 */
	public GenericActionResult<Source> loadEfergyE2ConsumptionsFromCsv(String csvPathFile, LocalDate dateOfData);
	
	/**
	 * Crea y persiste los consumos del fichero csv pasado por argumento, obtenido de la página de UFD, consumo horario 
	 * exportado entre fechas en formato CNMC
	 * 
	 * @param csvPathFile
	 * @param dateOfData
	 * @return
	 */
	public GenericActionResult<Source> loadUfdConsumptionsFromCsv(String csvPathFile, LocalDate dateOfData);
	
	/**
	 * Genera un fichero .dat, procesable por gnuplot, por cada día de consumo incluido en la factura.
	 * 
	 * - Nombre del fichero: <yyyyMMdd>_<Num. facutura>_<Origen consumos verificados>.dat
	 * - Esturctura del fichero:
	 *   - Linea 1: Comentario con el número de factura.
	 *   - Linea 2: Comentario con el origen de los consumos que se usan para verificar.
	 *   - Linea 3: Comentario con el día (dd/MM/yyyy) al cual se refieren los consumos.
	 *   - Linea 4: Comentario con los kWh totales del día según la factura.
	 *   - Linea 5: Comentario con los kWh totales del día según el origen de verificación.
	 *   - Linea 6: Comentario con los kWh totales del día en período valle según la factura.
	 *   - Linea 7: Comentario con los kWh totales del día en período valle según el origen de verificación.
	 *   - Linea 8: Comentario con los kWh totales del día en período llano según la factura.
	 *   - Linea 9: Comentario con los kWh totales del día en período llano según el origen de verificación.
	 *   - Linea 10: Comentario con los kWh totales del día en período punta según la factura.
	 *   - Linea 11: Comentario con los kWh totales del día en período punta según el origen de verificación.
	 *   - Linea 12: Encabezados de las columnas
	 *   - Resto de líneas (una por cada hora del día):
	 * 	 	- Columna 1: Hora del día
	 *   	- Columna 2: kWh consumidos según factura
	 *   	- Columna 3: kWh consumidos según el origen de los consumos que se usan para verificar la factura
	 *   
	 * @param billNumber Número de factura a partir de la cual generar el fichero
	 * @param mSourceCode Código del maestro de de orígenes de consumos que se usan para verificar la factura
	 * @return Nombre del fichero generado
	 */
	public GenericActionResult<String> generateGnuPlotFileVerifiedBill(String billNumber, String mSourceCode);
	
	/**
	 * Genera un fichero CSV, con la comprobación de los consumos de la factura frente a los verificdos.
	 * 
	 * @param billNumber Número de factura a comprobar
	 * @param mSourceCode Código del maestro de de orígenes de consumos que se usan para verificar la factura
	 * @return Nombre del fichero generado.
	 */
	public GenericActionResult<String> checkBill(String billNumber, String mSourceCode);
}
