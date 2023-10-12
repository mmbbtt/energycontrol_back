package energycontrol.back.bussines;

import mbt.utilities.BussinesException;
import mbt.utilities.BussinesObjectsValidator;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;
import mbt.utilities.PropertiesFileReader;
import mbt.utilities.PropertiesFileReaderFactory;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import energycontrol.back.entities.DateHour;

/**
 * Validador de objetos de negocio del tipo DateHour
 * 
 */
public class DateHourValidator implements BussinesObjectsValidator<DateHour> 
{
	private static final Logger logger = LogManager.getLogger(DateHourValidator.class);
	static boolean initialized = false;
	
	static PropertiesFileReader pfr;
	static int yearMin;
	static int yearMax;
	
	private void readDefaultValues() throws IOException
	{
		pfr = PropertiesFileReaderFactory.getPropertiesFileReader("energycontrol-back.properties");
		String sYearMin = pfr.getProperty("YEAR_MIN", "2000");
		String sYearMax = pfr.getProperty("YEAR_MAX", "3000");
		
		yearMin = Integer.parseInt(sYearMin);
		yearMax = Integer.parseInt(sYearMax);
		
		initialized = true;
	}
	
	
	public GenericActionResult<BussinesException> validate(DateHour objectToValidate) 
	{
		GenericActionResult<BussinesException> result = new GenericActionResult<BussinesException>();
		result.setActionResult(EResult.NOT_EXECUTED);
		
		if(!initialized)
		{
			try 
			{
				readDefaultValues();
			} 
			catch (IOException e) 
			{
				logger.error("Se ha producido un error inesperado en validate()", e);
				
				BussinesException be = new BussinesException(
						 "Se ha producido un error inesperado en DateHourValidator.validate()"
						,null
						,EUserMessagesKeys.InternalError.stringValue
						);
				be.addUserMessageArgument("DateHourValidator.validate()");
				result.addException(be);
				
				return result;
			}
		}
		
		if(objectToValidate == null)
		{
			result.setActionResult(EResult.KO);
			BussinesException be = new BussinesException(
					 "Parameter is null"
					,null
					,EUserMessagesKeys.NullParameter.stringValue
					);
			result.addException(be);
		}
		else
		{
			if(objectToValidate.getYear() < yearMin)
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 String.format("El valor del año %d es inferior al mínimo permitido %d", objectToValidate.getYear(), yearMin)
						,null
						,EUserMessagesKeys.ValueLessThanMin.stringValue
						);
				be.addUserMessageArgument(objectToValidate.getYear());
				be.addUserMessageArgument(yearMin);
				
				result.addException(be);
			}
			
			if(objectToValidate.getYear() > yearMax)
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 String.format("El valor del año %d es mayor al máximo permitido %d", objectToValidate.getYear(), yearMax)
						,null
						,EUserMessagesKeys.ValueGreaterThanMax.stringValue
						);
				be.addUserMessageArgument(objectToValidate.getYear());
				be.addUserMessageArgument(yearMax);
				
				result.addException(be);
			}
			
			if((objectToValidate.getMonth() < 1) || (objectToValidate.getMonth() > 12))
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 String.format("Valor no válido para asignar a un mes: %d", objectToValidate.getMonth())
						,null
						,EUserMessagesKeys.ValueNotValidFor.stringValue
						);
				be.addUserMessageArgument(objectToValidate.getMonth());
				be.addUserMessageArgument(EUserWordKeys.Month);
				
				result.addException(be);
			}
			
			if((objectToValidate.getDay() < 1) || (objectToValidate.getDay() > 31))
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 String.format("Valor no válido para asignar a un dia: %d", objectToValidate.getDay())
						,null
						,EUserMessagesKeys.ValueNotValidFor.stringValue
						);
				be.addUserMessageArgument(objectToValidate.getDay());
				be.addUserMessageArgument(EUserWordKeys.Day);
				
				result.addException(be);
			}
			
			if((objectToValidate.getHour() < 1) || (objectToValidate.getHour() > 24))
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 String.format("Valor no válido para asignar a una hora: %d", objectToValidate.getHour())
						,null
						,EUserMessagesKeys.ValueNotValidFor.stringValue
						);
				be.addUserMessageArgument(objectToValidate.getHour());
				be.addUserMessageArgument(EUserWordKeys.Hour);
				
				result.addException(be);
			}
			
			if(result.getExceptionsCount() == 0)
			{
				result.setActionResult(EResult.OK);
			}
		}
		
		return result;
	}

}
