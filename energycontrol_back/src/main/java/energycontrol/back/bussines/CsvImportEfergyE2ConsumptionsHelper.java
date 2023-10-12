package energycontrol.back.bussines;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import mbt.utilities.BussinesException;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;
import mbt.utilities.Helper;

import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.Source;

/**
 * Clase con utiliades para la importación de consumos de un medidor Efergy E2
 */
public class CsvImportEfergyE2ConsumptionsHelper extends CsvImportHelper
{
	/**
	 * Constructor
	 * 
	 * @param csvPathFileEfergyE2Consumptions Nombre completo (con el path) del archivo csv del cual importar los consumos
	 * @param propertiesFilenName Nombre del archivo de propiedades donde están los metadatos de los consumos de un medidor Efergy E2
	 * @throws IOException 
	 */
	public CsvImportEfergyE2ConsumptionsHelper(String csvPathFileEfergyE2Consumptions, String propertiesFilenName) throws IOException
	{
		super(csvPathFileEfergyE2Consumptions, propertiesFilenName);
		
		this.CSV_ROW_SEPARATOR = this.getValueOfProperty("EFERGYE2CONSUMPTION_CSVSEPARATOR");
	}
	
	/**
	 * Devuelve true si la fila es la correspondiente a los nombres de las columnas de los consumos.
	 * La siguiene fila el primer consumo.
	 * 
	 * @param csvRow
	 * @return
	 */
	public boolean isConsumptionsHeaderRow(String csvRow)
	{
		boolean bResult = false;
		
		if(!Helper.stringIsNullOrEmpty(csvRow))
		{
			String sPattern = this.getValueOfProperty("EFERGYE2CONSUMPTION_ConsumptionsHeaderPattern");
			
			if(csvRow.toUpperCase().contains(sPattern.toUpperCase()))
			{
				bResult = true;
			}
		}
		
		return bResult;
	}
	
	/**
	 * Crea un Consumption a partir de una fila de un csv con consumos de un medidor Efergy E2
	 * 
	 * Formato de la fila:
	 *  Fecha;Hora;Consumo (kWh);Coste;Mǭxima potenci;Comentarios (notas)
	 *  01-07-2023;0:00;0,342;0,034;;
	 *  
	 * @param csvRow Fila csv con el consumo
	 * @param efergyE2Source bill Origen de datos, del tipo medidor Efergy E2, a la cual pertenece la fila de consumos.
	 * @return
	 */
	public GenericActionResult<Consumption> csvRow2Consumption(String csvRow, Source efergyE2Source)
	{
		GenericActionResult<Consumption> result = new GenericActionResult<Consumption>();
		result.setActionResult(EResult.NOT_EXECUTED);
		
		LocalDate ldConsumptionDate = null;
		LocalTime ltConsumptionHour = null;
		Double dKwh = null;
		
		//-> Obtener la fecha del consumo:
		try
		{
			ldConsumptionDate = this.getDateValueOfColumn(
				"Fecha", 
				"EFERGYE2CONSUMPTION_ROWINDEX_Date", 
				"EFERGYE2CONSUMPTION_DATE_PATTERN", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Obtener la hora hora del consumo
		try
		{
			ltConsumptionHour = this.getHourValueOfColumn(
				"Hora", 
				"EFERGYE2CONSUMPTION_ROWINDEX_Hour", 
				"EFERGYE2CONSUMPTION_HOUR_PATTERN", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Obtener el consumo
		try
		{
			dKwh = this.getDoubleValueOfColumn(
				"Consumo (kWh)", 
				"EFERGYE2CONSUMPTION_ROWINDEX_kWh", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Crear el consumo
		if(result.getResult() != EResult.KO)
		{
			DateHour dateHour = new DateHour(
				ldConsumptionDate.getYear(),
				ldConsumptionDate.getMonthValue(),
				ldConsumptionDate.getDayOfMonth(),
				ltConsumptionHour.getHour() + 1 //Los consumos del csv empiezan a las 0:00 y terminan a las 23:00
				);
			
			Consumption c = new Consumption(
				dateHour, 
				dKwh, 
				ECollectionMethod.REAL, 
				ETimeBand.Unknown, 
				efergyE2Source
				);
			
			result.setResultObject(c);
			result.setActionResult(EResult.OK);
		}
		
		return result;
	}
}
