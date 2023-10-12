package energycontrol.back.bussines;

public enum EUserMessagesKeys 
{
	InternalError("InternalError"),
	NullParameter("NullParameter"),
	IncorrectParameter("IncorrectParameter"),
	IncorrectNumberOfParameters("IncorrectNumberOfParameters"),
	PropertyNotFound("PropertyNotFound"),
	EmptyProperty("EmptyProperty"),
	IncorrectValueOfPropery("IncorrectValueOfPropery"),
	IndexOfcolumnNotFound("IndexOfcolumnNotFound"),
	IncorrectFormatOfColumn("IncorrectFormatOfColumn"),
	EmptyColumn("EmptyColumn"),
	ReadColumnInEmptyRow("ReadColumnInEmptyRow"),
	IdenxOfColumnOutOfRange("IdenxOfUniversalSupplyPointCodeOutOfRange"),
	ValueLessThanMin("ValueLessThanMin"),
	ValueGreaterThanMax("ValueGreaterThanMax"),
	ValueNotValidFor("ValueNotValidFor"),
	FileNotExists("FileNotExists"),
	MandatoryFieldEmpty("MandatoryFieldEmpty"),
	BillGenerateOk("BillGenerateOk"),
	BillGenerateKo("BillGenerateKo"),
	CsvImportKo("CsvImportKo"),
	RowsAffected("RowsAffected"),
	BillNotLoaded("BillNotLoaded"),
	BillConsumptionsNotLoaded("BillConsumptionsNotLoaded"),
	MSourceNotLoaded("MSourceNotLoaded"),
	GnuPlotConsumptionBeforeBillConsumption("GnuPlotConsumptionBeforeBillConsumption"),
	GnuPlotConsumptionOtherDay("GnuPlotConsumptionOtherDay"),
	GnuPlotNoBillConsumptions("GnuPlotNoBillConsumptions"),
	GnuPlotNoSourceConsumptions("GnuPlotNoSourceConsumptions"),
	GnuPlotNoBill("GnuPlotNoBill"),
	GnuPlotNoMSource("GnuPlotNoMSource"),
	GnuPlotMissingBillConsumptions("GnuPlotMissingBillConsumptions"),
	GnuPlotMissingSourceConsumptions("GnuPlotMissingSourceConsumptions"),
	GnuPlotDataNotReady("GnuPlotDataNotReady"),
	GnuPlotDataFileNotGenerated("GnuPlotDataFileNotGenerated"),
	GnuPlotDataFileGeneratedWithErrors("GnuPlotDataFileGeneratedWithErrors"),
	GnuPlotMissingDatFileName("GnuPlotMissingDatFileName"),
	ConsumptionNotBelongBill("ConsumptionNotBelongBill");
	
	public final String stringValue;
	
	private EUserMessagesKeys(String stringValue)
	{
		this.stringValue = stringValue;
	}
}
