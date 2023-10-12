package energycontrol.back.bussines;

import java.util.Iterator;

import mbt.utilities.BussinesException;
import mbt.utilities.BussinesObjectsValidator;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;
import mbt.utilities.Helper;

import energycontrol.back.entities.Source;

/**
 * Validador de objetos de negocio tipo Source.
 * 
 */
public class SourceValidator implements BussinesObjectsValidator<Source> 
{
	public GenericActionResult<BussinesException> validate(Source objectToValidate) 
	{
		GenericActionResult<BussinesException> result = new GenericActionResult<BussinesException>();
		result.setActionResult(EResult.NOT_EXECUTED);
		
		if(objectToValidate == null)
		{
			result.setActionResult(EResult.KO);
			BussinesException be = new BussinesException(
					 "Parameter objectToValidate is null"
					,null
					,EUserMessagesKeys.NullParameter.stringValue
					);
			result.addException(be);
		}
		else
		{
			if(objectToValidate.getMSource() == null)
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 "El campo code no puede estar vacío."
						,null
						,EUserMessagesKeys.MandatoryFieldEmpty.stringValue
						);
				be.addUserMessageArgument("mSource");
				
				result.addException(be);
			}
			else
			{
				MSourceValidator msv = new MSourceValidator();
				GenericActionResult<BussinesException> resultMSourceValidation = msv.validate(objectToValidate.getMSource());
				
				if(resultMSourceValidation.getResult() != EResult.OK)
				{
					result.setActionResult(EResult.KO);
					BussinesException be = new BussinesException(
							 String.format("El parámetro mSource no es válido: %s.", resultMSourceValidation.getExceptionsMessages())
							,null
							,EUserMessagesKeys.IncorrectParameter.stringValue
							);
					be.addUserMessageArgument("mSource");
					be.addUserMessageArgument("");
					
					result.addException(be);
					
					Iterator<BussinesException> beIterator = resultMSourceValidation.getExceptionIterator();
					while(beIterator.hasNext())
					{
						BussinesException beMSource = beIterator.next();
						result.addException(beMSource);
					}
				}
			}
			
			if(Helper.stringIsNullOrEmpty(objectToValidate.getId()))
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 "El campo id no puede estar vacío."
						,null
						,EUserMessagesKeys.MandatoryFieldEmpty.stringValue
						);
				be.addUserMessageArgument("id");
				
				result.addException(be);
			}
			
			if(objectToValidate.getDateOfData() == null)
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 "El campo dateOfData no puede estar vacío."
						,null
						,EUserMessagesKeys.MandatoryFieldEmpty.stringValue
						);
				be.addUserMessageArgument("dateOfData");
				
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
