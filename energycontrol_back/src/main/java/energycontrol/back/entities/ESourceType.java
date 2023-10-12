package energycontrol.back.entities;

import mbt.utilities.Helper;

/**
 * Enumeración para tipar el maestro de orígenes de consumos.
 * 
 */
public enum ESourceType 
{
	/**
	 * Obtenido de la factura.
	 */
	Bill,
	/**
	 * Obtenido de un medidor de consumo
	 */
	ConsumptionMeter,
	/**
	 * Obtenido de la compañía distribuidora
	 */
	DistributionCompany,
	/**
	 * Otros orígenes
	 */
	Other;
	
	public static ESourceType parseETimeBand(String stringValue)
	{
		ESourceType st = ESourceType.Other;
		
		if(!Helper.stringIsNullOrEmpty(stringValue))
		{
			if(stringValue.toUpperCase().equals("BILL"))
				st = ESourceType.Bill;
			else if(stringValue.toUpperCase().equals("B"))
				st = ESourceType.Bill;
			else if(stringValue.toUpperCase().equals("FACTURA"))
				st = ESourceType.Bill;
			else if(stringValue.toUpperCase().equals("CONSUMPTIONMETER"))
				st = ESourceType.ConsumptionMeter;
			else if(stringValue.toUpperCase().equals("CONSUMPTION METER"))
				st = ESourceType.ConsumptionMeter;
			else if(stringValue.toUpperCase().equals("METER"))
				st = ESourceType.ConsumptionMeter;
			else if(stringValue.toUpperCase().equals("MEDIDOR DE CONSUMO"))
				st = ESourceType.ConsumptionMeter;
			else if(stringValue.toUpperCase().equals("MEDIDOR"))
				st = ESourceType.ConsumptionMeter;
			else if(stringValue.toUpperCase().equals("DISTRIBUTIONCOMPANY"))
				st = ESourceType.DistributionCompany;
			else if(stringValue.toUpperCase().equals("DISTRIBUTION COMPANY"))
				st = ESourceType.DistributionCompany;
			else if(stringValue.toUpperCase().equals("COMPANY"))
				st = ESourceType.DistributionCompany;
			else if(stringValue.toUpperCase().equals("COMPAÑÍA DISTRIBUIDORA"))
				st = ESourceType.DistributionCompany;
			else if(stringValue.toUpperCase().equals("COMPAÑÍA"))
				st = ESourceType.DistributionCompany;
			else if(stringValue.toUpperCase().equals("DISTRIBUIDORA"))
				st = ESourceType.DistributionCompany;
			else
				st = ESourceType.Other;
		
		}
		
		return st;
	}
	
	public static String toShortString(ESourceType o)
	{
		if(o == ESourceType.Bill)
			return "B";
		else if (o == ESourceType.ConsumptionMeter)
			return "CM";
		else if (o == ESourceType.DistributionCompany)
			return "DC";
		else
			return "O";
	}
}
