package energycontrol.back.entities;

import mbt.utilities.Helper;

/**
 * Enumeración para tipar el método de obtención del consumo.
 * 
 */
public enum ECollectionMethod 
{
	/**
	 * Real
	 */
	REAL,
	/**
	 * Estimado
	 */
	SIMULATED,
	/**
	 * Desconocido
	 */
	UNKNOWN;
	
	public static ECollectionMethod parseECollectionMethod(String stringValue)
	{
		ECollectionMethod ct = ECollectionMethod.UNKNOWN;
		
		if(!Helper.stringIsNullOrEmpty(stringValue))
		{
			if(stringValue.toUpperCase().equals("REAL"))
				ct  = ECollectionMethod.REAL;
			else if(stringValue.toUpperCase().equals("R"))
				ct  = ECollectionMethod.REAL;
			else if(stringValue.toUpperCase().equals("SIMULATED"))
				ct  = ECollectionMethod.SIMULATED;
			else if(stringValue.toUpperCase().equals("SIMULADO"))
				ct  = ECollectionMethod.SIMULATED;
			else if(stringValue.toUpperCase().equals("S"))
				ct  = ECollectionMethod.SIMULATED;
			else if(stringValue.toUpperCase().equals("ESTIMADO"))
				ct  = ECollectionMethod.SIMULATED;
			else if(stringValue.toUpperCase().equals("E"))
				ct  = ECollectionMethod.SIMULATED;
		}
		
		return ct;		
	}
	
	public static String toShortString(ECollectionMethod o)
	{
		if(o == ECollectionMethod.REAL)
			return "R";
		else if (o == ECollectionMethod.SIMULATED)
			return "S";
		else
			return "U";
	}
}
