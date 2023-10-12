package energycontrol.back.bussines;

import mbt.utilities.BussinesException;
import mbt.utilities.BussinesObjectsValidator;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;
import mbt.utilities.Helper;

import energycontrol.back.entities.MSource;

/**
 * Validador de objetos MSource
 * 
 */
public class MSourceValidator implements BussinesObjectsValidator<MSource> 
{
	public GenericActionResult<BussinesException> validate(MSource objectToValidate) 
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
			if(Helper.stringIsNullOrEmpty(objectToValidate.getCode()))
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 "El campo code no puede estar vacío."
						,null
						,EUserMessagesKeys.MandatoryFieldEmpty.stringValue
						);
				be.addUserMessageArgument("code");
				
				result.addException(be);
			}
			
			if(Helper.stringIsNullOrEmpty(objectToValidate.getDescription()))
			{
				result.setActionResult(EResult.KO);
				BussinesException be = new BussinesException(
						 "El campo description no puede estar vacío."
						,null
						,EUserMessagesKeys.MandatoryFieldEmpty.stringValue
						);
				be.addUserMessageArgument("description");
				
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
