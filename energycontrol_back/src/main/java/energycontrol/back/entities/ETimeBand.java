package energycontrol.back.entities;

import mbt.utilities.Helper;

/**
 * Enumeración para tipar la hora de consumo, si es en perído punta, valle o llano.
 * 
 */
public enum ETimeBand 
{
	/**
	 * Valle
	 */
	OffPeak,
	/**
	 * Llano
	 */
	Standard,
	/**
	 * Punta
	 */
	Peak,
	/**
	 * Desconocido
	 */
	Unknown;
	
	public static ETimeBand parseETimeBand(String stringValue)
	{
		ETimeBand cbt = ETimeBand.Unknown;
		
		if(!Helper.stringIsNullOrEmpty(stringValue))
		{
			if(stringValue.toUpperCase().equals("OFFPEAK"))
				cbt = ETimeBand.OffPeak;
			else if(stringValue.toUpperCase().equals("OFF-PEAK"))
				cbt = ETimeBand.OffPeak;
			else if(stringValue.toUpperCase().equals("OFF PEAK"))
				cbt = ETimeBand.OffPeak;
			else if(stringValue.toUpperCase().equals("ENERGÍA ACTIVA VALLE"))
				cbt = ETimeBand.OffPeak;
			else if(stringValue.toUpperCase().equals("VALLE"))
				cbt = ETimeBand.OffPeak;
			else if(stringValue.toUpperCase().equals("STANDARD"))
				cbt = ETimeBand.Standard;
			else if(stringValue.toUpperCase().equals("ENERGÍA ACTIVA LLANO"))
				cbt = ETimeBand.Standard;
			else if(stringValue.toUpperCase().equals("LLANO"))
				cbt = ETimeBand.Standard;
			else if(stringValue.toUpperCase().equals("PEAK"))
				cbt = ETimeBand.Peak;
			else if(stringValue.toUpperCase().equals("ENERGÍA ACTIVA PUNTA"))
				cbt = ETimeBand.Peak;
			else if(stringValue.toUpperCase().equals("PUNTA"))
				cbt = ETimeBand.Peak;
		}
		
		return cbt;
	}
	

	public static String toShortString(ETimeBand o)
	{
		if(o == ETimeBand.OffPeak)
			return "O";
		else if (o == ETimeBand.Standard)
			return "S";
		else if (o == ETimeBand.Peak)
			return "P";
		else
			return "U";
	}
}
