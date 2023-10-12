package energycontrol.back.bussines;

import java.io.IOException;
import java.time.LocalDate;

import mbt.utilities.BussinesException;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;
import mbt.utilities.Helper;

import energycontrol.back.entities.Bill;
import energycontrol.back.entities.BillConsumption;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ETimeBand;

/**
 * Clase con utiliades para la importación de consumos de una factura de Naturgy
 * 
 */
public class CsvImportBillNaturgyHelper extends CsvImportHelper
{
	/**
	 * Constructor.
	 * 
	 * @param csvPathFileBill Nombre completo (con el path) del archivo csv del cual importar los consumos
	 * @param propertiesFilenName Nombre del archivo de propiedades donde están los metadatos de los consumos de Naturgy
	 * @throws IOException 
	 */
	public CsvImportBillNaturgyHelper(String csvPathFileBill, String propertiesFilenName) throws IOException 
	{
		super(csvPathFileBill, propertiesFilenName);
		
		this.CSV_ROW_SEPARATOR = this.getValueOfProperty("BILL_NATURGY_CSVSEPARATOR");
	}

	/**
	 * Devuelve true si la fila es la correspondiente a la del número de factura.
	 * 
	 * @param csvRow
	 * @return
	 */
	public boolean isBillNumberRow(String csvRow)
	{
		boolean bResult = false;
		
		if(!Helper.stringIsNullOrEmpty(csvRow))
		{
			String sPattern = this.getValueOfProperty("BILL_NATURGY_BillNumberPattern");
			
			if(csvRow.toUpperCase().contains(sPattern.toUpperCase()))
			{
				bResult = true;
			}
		}
		
		return bResult;
	}
	
	/**
	 * Devuelve true si la fila es la correspondiente a la del CUPs.
	 * 
	 * @param csvRow
	 * @return
	 */
	public boolean isCupsRow(String csvRow)
	{
		boolean bResult = false;
		
		if(!Helper.stringIsNullOrEmpty(csvRow))
		{
			String sPattern = this.getValueOfProperty("BILL_NATURGY_CupsPattern");
			
			if(csvRow.toUpperCase().contains(sPattern.toUpperCase()))
			{
				bResult = true;
			}
		}
		
		return bResult;
	}
	
	/**
	 * Devuelve true si la fila es la correspondiente a los nombres de las columnas de los consumos.
	 * La siguiene fila el primer consumo de la factura.
	 * 
	 * @param csvRow
	 * @return
	 */
	public boolean isConsumptionsHeaderRow(String csvRow)
	{
		boolean bResult = false;
		
		if(!Helper.stringIsNullOrEmpty(csvRow))
		{
			String sPattern = this.getValueOfProperty("BILL_NATURGY_ConsumptionsHeaderPattern");
			
			if(csvRow.toUpperCase().contains(sPattern.toUpperCase()))
			{
				bResult = true;
			}
		}
		
		return bResult;
	}
	
	/**
	 * Devuelve en número de factura informado en la fila del csv pasada por argumento
	 * 
	 * @param row
	 * @return
	 */
	public String getBillNumber(String row) throws BussinesException
	{
		String billNumber;
	
		String[] cols = row.split(this.CSV_ROW_SEPARATOR);
				
		Integer index  = this.getIntValueOfProperty("BILL_NATURGY_BillNumberRowIndex");
		
		String colName = "Nº Factura";
		if(cols.length < index + 1)
		{
			BussinesException be = new BussinesException(
				String.format("El índice, %d, de la columna %s es mayor que el número de columnas %d.", index, colName, cols.length)
				,EUserMessagesKeys.IdenxOfColumnOutOfRange.stringValue
				);
			be.addUserMessageArgument(index);
			be.addUserMessageArgument(colName);
			be.addUserMessageArgument(cols.length);
			
			throw be;
		}
		else
		{
			billNumber = cols[index];
			
			if(Helper.stringIsNullOrEmpty(billNumber))
			{
				BussinesException be = new BussinesException(
					String.format("Columna %s no informada", colName)
					,EUserMessagesKeys.EmptyColumn.stringValue
					);
				be.addUserMessageArgument(colName);
									
				throw be;
			}
		}
		
		return billNumber;
	}
	
	/**
	 * Devuelve el CUPs informado en la fila del csv pasada por argumento
	 * 
	 * @param row
	 * @return
	 */
	public String getCups(String row)
	{
		String cups = "";
		
		String[] cols = row.split(this.CSV_ROW_SEPARATOR);
		
		Integer index = this.getIntValueOfProperty("BILL_NATURGY_CupsRowIndex");
		
		String colName = "CUPS";
		if(cols.length < index + 1)
		{
			BussinesException be = new BussinesException(
				String.format("El índice, %d, de la columna %s es mayor que el número de columnas %d.", index, colName, cols.length)
				,EUserMessagesKeys.IdenxOfColumnOutOfRange.stringValue
				);
			be.addUserMessageArgument(index);
			be.addUserMessageArgument(colName);
			be.addUserMessageArgument(cols.length);
			
			throw be;
		}
		else
		{
			cups = cols[index];
			
			if(Helper.stringIsNullOrEmpty(cups))
			{
				BussinesException be = new BussinesException(
					String.format("Columna %s no informada", colName)
					,EUserMessagesKeys.EmptyColumn.stringValue
					);
				be.addUserMessageArgument(colName);
									
				throw be;
			}
		}
		
		return cups;
	}
	
	/**
	 * Crea un BillConsumption a partir de una fila de un csv con consumos de Naturgy.
	 * 
	 * Formato de la fila:
	 *   Fecha;Hora Desde;Hora Hasta;Tipo consumo;Consumo (kWh);Precio horario de energía (€/kWh);Importe horario de energía (€)
     *   07/01/2022;0;1;Energía Activa Valle;0,597;0,277119548640;0,165440370538
	 * 
	 * @param csvRow 
	 * @param bill Factura a la cual pertenece la fila de consumos.
	 * @param collectionMethod Tipo del método de obtención de los consumos de la factura (reales o estimados)
	 * @return
	 */
	public GenericActionResult<BillConsumption> csvRow2HourBill(String csvRow, Bill bill, ECollectionMethod collectionMethod)
	{
		GenericActionResult<BillConsumption> result = new GenericActionResult<BillConsumption>();
		result.setActionResult(EResult.NOT_EXECUTED);
		
		LocalDate ldConsumptionDate = null;
		Integer iConsumptionHour = null;
		ETimeBand eConsumptionTimeBand = ETimeBand.Unknown;
		Double dKwh = null;
		Double dKwhCost = null;
		Double dConsumptionCost = null;
		
		String sValue = null;
		
		//-> Obtener la fecha del consumo:
		try
		{
			ldConsumptionDate = this.getDateValueOfColumn(
				"Fecha", 
				"BILL_NATURGY_ConsumptionsDateColIndex", 
				"BILL_NATURGY_DatePattern", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
	
		//-> Obtener la hora del consumo:
		try
		{
			iConsumptionHour = this.getIntValueOfColumn(
				"Hora Hasta", 
				"BILL_NATURGY_ConsumptionsHourColIndex", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Obtener tipo de consumo
		
		try
		{
			sValue = this.getValueOfColumn(
				"Tipo consumo", 
				"BILL_NATURGY_ConsumptionsTypeColIndex", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		eConsumptionTimeBand = ETimeBand.parseETimeBand(sValue);
		
		
		//-> Obtener el consumo
		try
		{
			dKwh = this.getDoubleValueOfColumn(
				"Consumo (kWh)", 
				"BILL_NATURGY_ConsumptionskWhColIndex", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Obtener el coste del kwh
		try
		{
			dKwhCost = this.getDoubleValueOfColumn(
				"Precio horario de energía (€/kWh)", 
				"BILL_NATURGY_ConsumptionskWhCostColIndex", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Obtener el coste del consumo
		try
		{
			dConsumptionCost = this.getDoubleValueOfColumn(
				"Importe horario de energía (€)", 
				"BILL_NATURGY_ConsumptionsHourCostColIndex", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//Crear el consumo
		if(result.getResult() != EResult.KO)
		{
			BillConsumption consumption = new BillConsumption(
				 new DateHour(ldConsumptionDate.getYear(), ldConsumptionDate.getMonthValue(), ldConsumptionDate.getDayOfMonth(), iConsumptionHour)
				,dKwh
				,dKwhCost
				,dConsumptionCost
				,collectionMethod
				,eConsumptionTimeBand
				,bill
				);
				
			result.setResultObject(consumption);
			result.setActionResult(EResult.OK);
		}
			
		return result;
	}
}
