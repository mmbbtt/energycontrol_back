package energycontrol.back.bussines;

import java.io.IOException;
import java.time.LocalDate;

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
 * Clase con utiliades para la importación de consumos de UFD
 */
public class CsvImportUfdConsumptionsHelper extends CsvImportHelper
{
	/**
	 * Constructor
	 * 
	 * @param csvPathFileUfdConsumptions Nombre completo (con el path) del archivo csv del cual importar los consumos
	 * @param propertiesFilenName Nombre del archivo de propiedades donde están los metadatos de los consumos de UFD
	 * @throws IOException 
	 */
	public CsvImportUfdConsumptionsHelper(String csvPathFileEfergyE2Consumptions, String propertiesFilenName) throws IOException
	{
		super(csvPathFileEfergyE2Consumptions, propertiesFilenName);
		
		this.CSV_ROW_SEPARATOR = this.getValueOfProperty("UDFCOMSUMTION_CSVSEPARATOR");
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
			String sPattern = this.getValueOfProperty("UDFCOMSUMTION_ConsumptionsHeaderPattern");
			
			if(csvRow.toUpperCase().contains(sPattern.toUpperCase()))
			{
				bResult = true;
			}
		}
		
		return bResult;
	}
	
	/**
	 * Crea un Consumption a partir de una fila de un csv con consumos UFD
	 * 
	 * Formato de la fila:
	 *  ﻿CUPS;Fecha;Hora;Consumo;Metodo_obtencion
	 *  ES0022000004124187BK1P;06/01/2022;24;0,258;R
	 *  
	 * @param csvRow Fila csv con el consumo
	 * @param ufdSource Origen de datos, del tipo medidor Efergy E2, a la cual pertenece la fila de consumos.
	 * @return
	 */
	public GenericActionResult<Consumption> csvRow2Consumption(String csvRow, Source ufdSource)
	{
		GenericActionResult<Consumption> result = new GenericActionResult<Consumption>();
		result.setActionResult(EResult.NOT_EXECUTED);
		
		LocalDate ldConsumptionDate = null;
		Integer iHour = null;
		Double dKwh = null;
		ECollectionMethod cm = ECollectionMethod.UNKNOWN;
		
		//-> Obtener la fecha del consumo:
		try
		{
			ldConsumptionDate = this.getDateValueOfColumn(
				"Fecha", 
				"UDFCOMSUMTION_ROWINDEX_Date", 
				"UDFCOMSUMTION_DATE_PATTERN", 
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
			iHour = this.getIntValueOfColumn(
				"Hora", 
				"UDFCOMSUMTION_ROWINDEX_Hour", 
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
				"Consumo", 
				"UDFCOMSUMTION_ROWINDEX_kWh", 
				csvRow
				);
		}
		catch(BussinesException be)
		{
			result.setActionResult(EResult.KO);
			result.addException(be);
		}
		
		//-> Obtener el método de obtención del consumo
		try
		{
			String sOm = this.getValueOfColumn(
				"Metodo_obtencion", 
				"UDFCOMSUMTION_ROWINDEX_ObtainigMethod", 
				csvRow
				);
			
			cm = ECollectionMethod.parseECollectionMethod(sOm);
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
				iHour 
				);
			
			Consumption c = new Consumption(
				dateHour, 
				dKwh, 
				cm, 
				ETimeBand.Unknown, 
				ufdSource
				);
			
			result.setResultObject(c);
			result.setActionResult(EResult.OK);
		}
		
		return result;
	}
}
