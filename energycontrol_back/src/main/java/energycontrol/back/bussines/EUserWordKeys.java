package energycontrol.back.bussines;

public enum EUserWordKeys 
{
	Day("Day"),
	Month("Month"),
	Year("Year"),
	Hour("Hour"),
	ExpectedFormatHourIdValue("ExpectedFormatHourIdValue"),
	IntegerNumber("IntegerNumber"),
	CommandExecutedOk("CommandExecutedOk"),
	CommandExecutedKo("CommandExecutedKo"),
	ExpectedFormatDateValue("ExpectedFormatDateValue");
	
	public final String stringValue;
	
	private EUserWordKeys(String stringValue)
	{
		this.stringValue = stringValue;
	}
}
