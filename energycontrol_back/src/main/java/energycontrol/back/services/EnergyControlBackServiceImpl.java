package energycontrol.back.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import mbt.utilities.ActionResult;
import mbt.utilities.BussinesException;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;
import mbt.utilities.PropertiesFileReader;
import mbt.utilities.PropertiesFileReaderFactory;
import mbt.utilities.ResourceBundleReader;
import mbt.utilities.ResourceBundleReaderFactory;
import energycontrol.back.bussines.BillChecker;
import energycontrol.back.bussines.BillValidator;
import energycontrol.back.bussines.CsvImportBillNaturgyHelper;
import energycontrol.back.bussines.CsvImportEfergyE2ConsumptionsHelper;
import energycontrol.back.bussines.DateHourValidator;
import energycontrol.back.bussines.EUserMessagesKeys;
import energycontrol.back.bussines.EntitiesHelper;
import energycontrol.back.bussines.GnuplotHelper;
import energycontrol.back.bussines.MSourceValidator;
import energycontrol.back.bussines.SourceValidator;
import energycontrol.back.daos.BillConsumptionDao;
import energycontrol.back.daos.BillDao;
import energycontrol.back.daos.ConsumptionDao;
import energycontrol.back.daos.DaosFactory;
import energycontrol.back.daos.DaosFactoryProvider;
import energycontrol.back.daos.DateHourDao;
import energycontrol.back.daos.MSourceDao;
import energycontrol.back.daos.SourceDao;
import energycontrol.back.entities.Bill;
import energycontrol.back.entities.BillConsumption;
import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.Source;


/**
 * Implemetnación del servicio del back
 */
public class EnergyControlBackServiceImpl implements EnergyControlBackService
{
	private static final Logger logger = LogManager.getLogger(EnergyControlBackServiceImpl.class);
	
	private PropertiesFileReader pfr = null;
	private Locale defaultLocale;
	private ResourceBundleReader resourceBundleReader;
	
	private DateHourDao dateHourDao = null;
	private MSourceDao mSourceDao = null;
	private SourceDao sourceDao = null;
	private BillDao billDao = null;
	private BillConsumptionDao billConsumptionDao = null;
	private ConsumptionDao consumptionDao = null;
	
	private static DateHourValidator dateHourValidator = new DateHourValidator();
	private static MSourceValidator mSourceValidator = new MSourceValidator();
	private static SourceValidator sourceValidator = new SourceValidator();
	private static BillValidator billValidator = new BillValidator();
	
	/**
	 * Constructor por defecto.
	 * 
	 * Lee el archivo de propiedades energycontrol-back.properties y:
	 *  - Inicializa el locale a usar.
	 *  - Inicializa el ResourceBundleReader a usar.
	 *  - Inicializa el proveedor de la factoría de DAOs para que devuelva DAOs acordes al proveedor de 
	 *    la unidad de persistencia indicada en energycontrol-back.properties
	 */
	public EnergyControlBackServiceImpl()
	{
		try
		{
			pfr = PropertiesFileReaderFactory.getPropertiesFileReader("energycontrol-back.properties");
			
			//Inicializa el locale a usar:
			String sLocaleLanguage = pfr.getProperty("Locale.Language", "es");
			String sLocaleCountry = pfr.getProperty("Locale.Country", "ES");
			defaultLocale= new Locale(sLocaleLanguage, sLocaleCountry);
			
			//Inicializa el lector de archivo de idioma
			this.resourceBundleReader = ResourceBundleReaderFactory.getResourceBundleReader("Messages-back", defaultLocale);
			
			//Inicializa los DAOs:
			String sPersistenceUnitName = pfr.getProperty("PersistenceUnitName");
			DaosFactoryProvider daosFactoryProvider = new DaosFactoryProvider(sPersistenceUnitName);
			DaosFactory daosFactory = daosFactoryProvider.getDaosFactory();
			
			this.dateHourDao = daosFactory.getDateHourDao();
			this.mSourceDao = daosFactory.getMSourceDao();
			this.sourceDao = daosFactory.getSourceDao();
			this.billDao = daosFactory.getBillDao();
			this.billConsumptionDao = daosFactory.getBillConsumptionDao();
			this.consumptionDao = daosFactory.getConsumptionDao();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("EnergyControlBackServiceImpl()");
		}
	}
	
	// -> Implementación del interfaz EnergyControlBackService
	
	public GenericActionResult<Integer> generateDateHours(DateHour dateHourFrom, DateHour dateHourTo) 
	{
		GenericActionResult<Integer> gar = new GenericActionResult<Integer>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			//Validar parámetros:
			GenericActionResult<BussinesException> validationResult = dateHourValidator.validate(dateHourFrom);
			if(validationResult.getResult() != EResult.OK)
			{
				gar.setActionResult(EResult.KO);
				String sMessageError = ActionResult.getLocalUserMessage(validationResult, this.resourceBundleReader);
				BussinesException be = new BussinesException(
					 sMessageError
					,null
					,EUserMessagesKeys.IncorrectParameter.stringValue
					);
				be.addUserMessageArgument("dateHourFrom");
				be.addUserMessageArgument(sMessageError);
			}
			
			validationResult = dateHourValidator.validate(dateHourTo);
			if(validationResult.getResult() != EResult.OK)
			{
				gar.setActionResult(EResult.KO);
				String sMessageError = ActionResult.getLocalUserMessage(validationResult, this.resourceBundleReader);
				BussinesException be = new BussinesException(
					 sMessageError
					,null
					,EUserMessagesKeys.IncorrectParameter.stringValue
					);
				be.addUserMessageArgument("dateHourTo");
				be.addUserMessageArgument(sMessageError);
			}
			
			//Crear los DateHours y los persiste en la unidad de persistencia:
			if(gar.getResult() != EResult.KO)
			{
				DateHour dh = dateHourFrom;
				int count = 0;
				do
				{
					this.dateHourDao.save(dh);
					count++;
					
					dh = EntitiesHelper.addHours(dh, 1);
				}
				while(EntitiesHelper.dateHourCompare(dh, dateHourTo) <= 0);
				
				gar.setActionResult(EResult.OK);
				gar.setResultObject(count);
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("generateDateHours");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	
	public GenericActionResult<MSource> saveMSource(MSource mSource)
	{
		GenericActionResult<MSource> gar = new GenericActionResult<MSource>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			//Validar parámetro:
			GenericActionResult<BussinesException> validationResult = mSourceValidator.validate(mSource);
			
			if(validationResult.getResult() != EResult.OK)
			{
				gar.setActionResult(EResult.KO);
				String sMessageError = ActionResult.getLocalUserMessage(validationResult, this.resourceBundleReader);
				BussinesException be = new BussinesException(
					 sMessageError
					,null
					,EUserMessagesKeys.IncorrectParameter.stringValue
					);
				be.addUserMessageArgument("mSource");
				be.addUserMessageArgument(sMessageError);
			}
			else
			{
				MSource mSourceAlreadyExists = this.mSourceDao.findById(mSource.getCode());
				
				if((mSourceAlreadyExists == null))
				{
					this.mSourceDao.insert(mSource);
				}
				else
				{
					this.mSourceDao.save(mSource);
				}
			
				gar.setResultObject(mSource);
				gar.setActionResult(EResult.OK);
			}		
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("saveMSource");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		
		return gar;
	}
	
	public GenericActionResult<List<MSource>> getMSources() 
	{
		GenericActionResult<List<MSource>> gar = new GenericActionResult<List<MSource>>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			List<MSource> lMSources= this.mSourceDao.findAll();
			gar.setActionResult(EResult.OK);
			gar.setResultObject(lMSources);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("getMSources");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	
	public GenericActionResult<Source> saveSource(Source source)
	{
		GenericActionResult<Source> gar = new GenericActionResult<Source>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			//Validar parámetro:
			GenericActionResult<BussinesException> validationResult = sourceValidator.validate(source);
			
			if(validationResult.getResult() != EResult.OK)
			{
				gar.setActionResult(EResult.KO);
				String sMessageError = ActionResult.getLocalUserMessage(validationResult, this.resourceBundleReader);
				BussinesException be = new BussinesException(
					 sMessageError
					,null
					,EUserMessagesKeys.IncorrectParameter.stringValue
					);
				be.addUserMessageArgument("source");
				be.addUserMessageArgument(sMessageError);
			}
			else
			{
				Source sourceAlreadyExists = this.sourceDao.findById(source.getId());
				
				if(sourceAlreadyExists == null)
				{
					MSource mSourceAlreadyExists = this.mSourceDao.findById(source.getMSource().getCode());
					
					if((mSourceAlreadyExists == null))
					{
						this.mSourceDao.insert(source.getMSource());
					}
					
					this.sourceDao.insert(source);
				}
				else
				{
					this.sourceDao.save(source);
				}
						
				gar.setResultObject(source);
				gar.setActionResult(EResult.OK);
			}		
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("saveMSource");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	public ActionResult deleteSource(Source source)
	{
		ActionResult r = new ActionResult();
		r.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			List<Consumption> lc = this.consumptionDao.findSourceConsumptions(source.getId());
			for(int i = 0; i < lc.size(); i ++)
			{
				this.consumptionDao.delete(lc.get(i));
			}
			
			Source s = this.sourceDao.findById(source.getId());
			if(s != null)
			{
				this.sourceDao.delete(s);
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("deleteSource");
			
			r.setActionResult(EResult.KO, be);
		}
		
		return r;
	}
	
	public GenericActionResult<List<Source>> getSources() 
	{
		GenericActionResult<List<Source>> gar = new GenericActionResult<List<Source>>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			List<Source> lSources= this.sourceDao.findAll();
			gar.setActionResult(EResult.OK);
			gar.setResultObject(lSources);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("getSources");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	
	public GenericActionResult<Bill> saveBill(Bill bill)
	{
		GenericActionResult<Bill> gar = new GenericActionResult<Bill>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			//Validar parámetro:
			GenericActionResult<BussinesException> validationResult = billValidator.validate(bill);
			
			if(validationResult.getResult() != EResult.OK)
			{
				gar.setActionResult(EResult.KO);
				String sMessageError = ActionResult.getLocalUserMessage(validationResult, this.resourceBundleReader);
				BussinesException be = new BussinesException(
					 sMessageError
					,null
					,EUserMessagesKeys.IncorrectParameter.stringValue
					);
				be.addUserMessageArgument("bill");
				be.addUserMessageArgument(sMessageError);
			}
			else
			{
				List<Bill> lBills = this.billDao.findByBillNumber(bill.getBillNumber());
				
				if((lBills != null) && (lBills.size() > 0))
				{
					this.billDao.save(bill);
				}
				else
				{
					MSource msAlreadyExists = this.mSourceDao.findById(bill.getMSource().getCode());
					
					if(msAlreadyExists == null)
					{
						this.mSourceDao.insert(bill.getMSource());
					}
					
					if(bill.getDateHourFrom() != null)
					{
						this.dateHourDao.save(bill.getDateHourFrom());
					}
				
					if(bill.getDateHourTo() != null)
					{
						this.dateHourDao.save(bill.getDateHourTo());
					}
					
					this.billDao.insert(bill);
				}
						
				gar.setResultObject(bill);
				gar.setActionResult(EResult.OK);
			}		
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("saveBill");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	public ActionResult deleteBill(String billNumber)
	{
		ActionResult r = new ActionResult();
		r.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			List<BillConsumption> lBConsumptions = this.billConsumptionDao.findBillConsumptions(billNumber);
			for(int i = 0; i < lBConsumptions.size(); i ++)
			{
				this.billConsumptionDao.delete(lBConsumptions.get(i));
			}
			
			List<Bill> lBills = this.billDao.findByBillNumber(billNumber);			
			for(int i = 0; i < lBills.size(); i ++)
			{
				this.billDao.delete(lBills.get(i));
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("deleteBill");
			
			r.setActionResult(EResult.KO, be);
		}
		
		return r;
	}
	
	public GenericActionResult<Bill> getBill(String billNumber) 
	{
		GenericActionResult<Bill> gar = new GenericActionResult<Bill>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			List<Bill> lBills= this.billDao.findByBillNumber(billNumber);
			gar.setActionResult(EResult.OK);
			
			if((lBills != null) && (lBills.size() > 0))
			{
				gar.setResultObject(lBills.get(0));
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("getBill");
			
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	
	public GenericActionResult<Bill> loadBillNaturgyFromCsv(String csvPathFile, LocalDate dateOfBill, ECollectionMethod collectionMethod)
	{
		GenericActionResult<Bill> gar = new GenericActionResult<Bill>();
		gar.setActionResult(EResult.NOT_EXECUTED);
				
		try 
		{
			CsvImportBillNaturgyHelper ciibnHelper = new CsvImportBillNaturgyHelper(
					 csvPathFile
					,"energycontrol-back.properties"
					);
					
			String csvRow = null;
			String billNumber = null;
			String cups = null;
			Bill bill = null;
			DateHour dateHourFrom = null;
			DateHour dateHourTo = null;
			
			//-> Recuperar número de factura
			while((csvRow = ciibnHelper.readNextRowOfCsv()) != null)
			{
				if(ciibnHelper.isBillNumberRow(csvRow))
				{
					billNumber = ciibnHelper.getBillNumber(csvRow);
					break;
				}
			}
			
			//-> Recuperar el CUPS
			while((csvRow = ciibnHelper.readNextRowOfCsv()) != null)
			{
				if(ciibnHelper.isCupsRow(csvRow))
				{
					cups = ciibnHelper.getBillNumber(csvRow);
					break;
				}
			}
			
			//-> Crear el objeto factura
			MSource mSource = this.mSourceDao.findById("FAC");
			if(mSource == null)
			{
				mSource = new MSource(ESourceType.Bill, "FAC", "Factura");
				this.mSourceDao.insert(mSource);
			}
			
			bill = new Bill(mSource, dateOfBill, billNumber);
			bill.setCups(cups);
			
			//-> Persistir el objeto factura
			this.billDao.insert(bill);
			
			//-> Situarnos en la fila del primer consumo
			while ((csvRow = ciibnHelper.readNextRowOfCsv())  != null) 
			{ 
				if(ciibnHelper.isConsumptionsHeaderRow(csvRow))
				{
					break;
				}
			}
			
			//-> Recuperar consumos
			int rowsKo = 0;
			int rowsOk = 0;
			boolean isFirst = true;
			while ((csvRow = ciibnHelper.readNextRowOfCsv())  != null) 
			{
				if(ciibnHelper.isEmptyCsvRow(csvRow))
				{
					//Ya no hay más consumos a procesar
					break;
				}
				else
				{
					GenericActionResult<BillConsumption> garConsumption = ciibnHelper.csvRow2HourBill(csvRow, bill, collectionMethod);
					
					if(garConsumption.getResult() == EResult.OK)
					{
						BillConsumption bc = garConsumption.getResultObject();
						
						//-> Persistir BillConsumption.dateHour
						DateHour dh = this.dateHourDao.findById(bc.getDateHour().getId());
						if(dh == null)
						{
							dh = bc.getDateHour();
							this.dateHourDao.insert(dh);
						}
						
						//-> Recuperar las fechas de la factura
						if(isFirst)
						{
							dateHourFrom = dh;
							isFirst = false;
						}
						else
						{
							dateHourTo = dh;
						}
						
						//-> Validar el consumo
						//   [ToDo]
						
						//-> Persistir la línea de factura
						try
						{
							this.billConsumptionDao.insert(bc);
							
							logger.debug(String.format(
								"Consumo cargado: %s - %.3f kWh", 
								bc.getId(),
								bc.getKwh()
								));
							
							rowsOk++;
						}
						catch(Exception ee)
						{
							rowsKo ++;
							
							logger.warn(String.format("Error al guardar fila de consumo %d: %s", rowsKo, ee.getMessage()));
						}	
					}
					else
					{
						rowsKo ++;
						logger.warn(String.format("Error al leer fila de consumo %d: %s", rowsKo, garConsumption.getExceptionsMessages()));
					}
				}
			}
			
			//-> Establecer las fechas de la factura
			bill.setDateHourTo(dateHourTo);
			bill.setDateHourFrom(dateHourFrom);
			this.billDao.save(bill);
			
			//-> Establecer el objeto resuldao		
			gar.setResultObject(bill);

			//-> Establecer el resultado
			if(rowsKo > 0)
			{
				logger.warn(String.format("Fallo al generar %d líneas de consumo de la factura", rowsKo));
				
				BussinesException be = new BussinesException(
						 String.format("Fallo al generar %d líneas de consumo de la factura", rowsKo)
						,null
						,EUserMessagesKeys.BillGenerateKo.stringValue
						);
					be.addUserMessageArgument(rowsKo);
				gar.addException(be);
				
				gar.setActionResult(EResult.KO);	
			}
			else
			{
				logger.debug(String.format("Factura generada correctamente con %d líneas de consumo", rowsOk));
				gar.setActionResult(EResult.OK);
			}
		}
//		catch(BussinesException be)
//		{
//			logger.error(be.getMessage(), be);
//			gar.setActionResult(EResult.KO, be);
//		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("loadBillNaturgyFromCsv");
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	public GenericActionResult<Source> loadEfergyE2ConsumptionsFromCsv(String csvPathFile, LocalDate dateOfData)
	{
		GenericActionResult<Source> gar = new GenericActionResult<Source> ();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			CsvImportEfergyE2ConsumptionsHelper csvImportHelper = new CsvImportEfergyE2ConsumptionsHelper(
					 csvPathFile
					,"energycontrol-back.properties"
					);
			
			String csvRow = null;
			
			//-> Crear el objeto Source
			MSource ms = this.mSourceDao.findById("EE2");
			if(ms == null)
			{
				ms = new MSource(ESourceType.ConsumptionMeter, "EE2", "Efergy E2 Meter");
				this.mSourceDao.insert(ms);
			}
			
			Source source = new Source(ms, dateOfData);
			
			//-> Persistir el objeto Source
			this.sourceDao.insert(source);
			
			//-> Situarnos en la primera fila de consumo
			while ((csvRow = csvImportHelper.readNextRowOfCsv())  != null) 
			{ 
				if(csvImportHelper.isConsumptionsHeaderRow(csvRow))
				{
					break;
				}
			}
			
			//-> Recuperar consumos
			int rowsKo = 0;
			int rowsOk = 0;
			while ((csvRow = csvImportHelper.readNextRowOfCsv())  != null) 
			{
				GenericActionResult<Consumption> garConsumption = csvImportHelper.csvRow2Consumption(csvRow, source);
				
				if(garConsumption.getResult() == EResult.OK)
				{
					Consumption c = garConsumption.getResultObject();
					
					//-> Persistir Consumption.dateHour
					DateHour dh = this.dateHourDao.findById(c.getDateHour().getId());
					if(dh == null)
					{
						dh = c.getDateHour();
						this.dateHourDao.insert(dh);
					}
					
					//-> Validar el consumo
					//   [ToDo]
					
					//-> Persistir el consumo
					this.consumptionDao.insert(c);
					
					logger.debug(String.format(
						"Consumo cargado: %s - %.3f kWh", 
						c.getId(),
						c.getKwh()
						));
					
					rowsOk++;
				}
				else
				{
					rowsKo ++;
					logger.warn(String.format("Error al leer fila de consumo %d: %s", rowsKo, garConsumption.getExceptionsMessages()));
				}
			}
			
			//-> Establecer el objeto resuldao		
			gar.setResultObject(source);
			
			//-> Establecer el resultado
			if(rowsKo > 0)
			{
				logger.warn(String.format("Fallo al generar %d consumos.", rowsKo));
				
				BussinesException be = new BussinesException(
						 String.format("Fallo al generar %d consumos", rowsKo)
						,null
						,EUserMessagesKeys.CsvImportKo.stringValue
						);
					be.addUserMessageArgument(rowsKo);
					be.addUserMessageArgument(rowsKo + rowsOk);
				gar.addException(be);
				
				gar.setActionResult(EResult.KO);	
			}
			else
			{
				logger.debug(String.format("Csv importado correctamente con %d consumos", rowsOk));
				gar.setActionResult(EResult.OK);
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("loadEfergyE2ConsumptionsFromCsv");
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	public GenericActionResult<String> generateGnuPlotFileVerifiedBill(String billNumber, String mSourceCode)
	{
		GenericActionResult<String> gar = new GenericActionResult<String>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			//Recuperar la factura
			List<Bill> lBills = this.billDao.findByBillNumber(billNumber);
			
			if((lBills == null) || (lBills.size() < 1))
			{
				BussinesException be = new BussinesException(
					 "La factura no está cargada en el sistema."
					,null
					,EUserMessagesKeys.BillNotLoaded.stringValue
					);
				gar.setActionResult(EResult.KO, be);
			}
			else
			{
				//Cargar el Maestro del origen de consumos
				MSource ms = this.mSourceDao.findById(mSourceCode);
				
				if(ms == null)
				{
					BussinesException be = new BussinesException(
						 "El maestro de orígenes de consumos no está cargado en el sistema."
						,null
						,EUserMessagesKeys.MSourceNotLoaded.stringValue
						);
					gar.setActionResult(EResult.KO, be);
				}
				else
				{
					Bill bill = lBills.get(0);
					
					//Cargar los consumos de la factura
					List<BillConsumption> lbc = this.billConsumptionDao.findBillConsumptions(billNumber);
					
					if(lbc == null || lbc.size() < 1)
					{
						BussinesException be = new BussinesException(
							 "La factura no tienen consumos, o estos no están cargados en el sistema."
							,null
							,EUserMessagesKeys.BillConsumptionsNotLoaded.stringValue
							);
						gar.setActionResult(EResult.KO, be);
					}
					else
					{
						GnuplotHelper gpHelper = new GnuplotHelper(bill, ms);
						int koDays = 0;
						int okDays = 0;
						BillConsumption bcPrevio = lbc.get(0);
						
						for(BillConsumption bc : lbc)
						{
							boolean sameDay = gpHelper.addBillConsumption(bc);
							
							if(!sameDay)
							{
								//Es un consumo del siguiente día:
								
								//Cargar los consumos de verificación del nuevo día
								List<Consumption> lSourceConsumptions = this.consumptionDao.findSourceConsumptionsOfDay(
									mSourceCode, 
									bcPrevio.getDateHour()
									);
								gpHelper.addSourceConsumptions(lSourceConsumptions);
								
								//Verificamos que los dato del día previo están ok
								boolean dataOk = gpHelper.checkDataForGnuPlot();
								
								if(dataOk)
								{
									//Generaros el fichero .dat con los datos para GnuPlot
									gpHelper.generateGnuPlotDatFile();
									
									//Generamos el script para GnuPlot que crea el pdf con el gráfico
									gpHelper.generateGnuPlotScriptPdfFile();
									
									//Ejecutamos GnuPlot
									String[] cmd = {"sh", "-c", String.format("gnuplot -p %s", gpHelper.getScriptFileName())};
									Process process = Runtime.getRuntime().exec(cmd);
									Thread.sleep(100);
									process.destroy();
										
									okDays ++;
								}
								else
								{
									koDays ++;
								}
								
								//Tratamos los errores que se hayan producido al generar el .dat de GnuPlot
								if(gpHelper.getDataNotOkForGnuPlot().size() > 0)
								{	
									StringBuilder sbErrores = new StringBuilder();
									
									for(BussinesException be : gpHelper.getDataNotOkForGnuPlot())
									{
										sbErrores.append(String.format("\n  %s", BussinesException.getLocalUserMessage(be, resourceBundleReader)));
									}	 
									
									BussinesException beDay = null;
									if(dataOk)
									{
										beDay = new BussinesException(
											 String.format("Se ha generado el GnuPlot dat file del día %s con errores leves: %s", gpHelper.getDay(), sbErrores.toString())
											,null
											,EUserMessagesKeys.GnuPlotDataFileGeneratedWithErrors.stringValue
											);
										beDay.addUserMessageArgument(gpHelper.getDay());
										beDay.addUserMessageArgument(sbErrores.toString());
										
										gar.addException(beDay);
									}
									else
									{
										beDay = new BussinesException(
											 String.format("No se ha podido generar el GnuPlot dat file del día %s debido a: %s", gpHelper.getDay(), sbErrores.toString())
											,null
											,EUserMessagesKeys.GnuPlotDataFileNotGenerated.stringValue
											);
										beDay.addUserMessageArgument(gpHelper.getDay());
										beDay.addUserMessageArgument(sbErrores.toString());
										
										gar.addException(beDay);
									}				
								}
								
								//Creamos una nueva instancia de GnuplotHelper para el nuevo día
								gpHelper= new GnuplotHelper(bill, ms);
								
								//Agregamos el primer consumo del nuevo día
								gpHelper.addBillConsumption(bc);
							}
							
							bcPrevio = bc;
						}
						
						gar.setResultObject(String.format("%d Archivos generados para %d días de la factura", okDays, okDays + koDays));
						if(koDays == 0)
						{
							gar.setActionResult(EResult.OK);
						}
						else
						{
							gar.setActionResult(EResult.KO);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("generateGnuPlotFileVerifiedBill");
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	public GenericActionResult<String> checkBill(String billNumber, String mSourceCode)
	{
		GenericActionResult<String> gar = new GenericActionResult<String>();
		gar.setActionResult(EResult.NOT_EXECUTED);
		
		try
		{
			//Recuperar la factura
			List<Bill> lBills = this.billDao.findByBillNumber(billNumber);
			
			if((lBills == null) || (lBills.size() < 1))
			{
				BussinesException be = new BussinesException(
					 "La factura no está cargada en el sistema."
					,null
					,EUserMessagesKeys.BillNotLoaded.stringValue
					);
				gar.setActionResult(EResult.KO, be);
			}
			else
			{
				//Recuperar el Maestro del origen de consumos
				MSource ms = this.mSourceDao.findById(mSourceCode);
				
				if(ms == null)
				{
					BussinesException be = new BussinesException(
						 "El maestro de orígenes de consumos no está cargado en el sistema."
						,null
						,EUserMessagesKeys.MSourceNotLoaded.stringValue
						);
					gar.setActionResult(EResult.KO, be);
				}
				else
				{
					Bill bill = lBills.get(0);
					
					//Recuperar los consumos de la factura
					List<BillConsumption> lbc = this.billConsumptionDao.findBillConsumptions(billNumber);
					
					if(lbc == null || lbc.size() < 1)
					{
						BussinesException be = new BussinesException(
							 "La factura no tienen consumos, o estos no están cargados en el sistema."
							,null
							,EUserMessagesKeys.BillConsumptionsNotLoaded.stringValue
							);
						gar.setActionResult(EResult.KO, be);
					}
					else
					{
						//Crear una instancia de la clase BillChecker
						BillChecker billChecker = new BillChecker(bill, ms);
						
						int rowsKo = 0;
						int rowsOk = 0;
						
						//Cargar los consumos de la factura y los de verificación en billChecker
						for(BillConsumption bc : lbc)
						{
							try
							{
								billChecker.addBillConsumption(bc);
								
								List<Consumption> lsc = this.consumptionDao.findSourceConsumption(mSourceCode, bc.getDateHour());
								if(lsc != null && lsc.size() > 0)
								{
									billChecker.addVerifiedConsumption(lsc.get(0));
								}
								
								rowsOk++;
							}
							catch(BussinesException be)
							{
								rowsKo++;
								logger.warn(String.format("Error al cargar consumos de la fecha %s: %s", bc.getDateHour(), be.getMessage()));
							}
						}
						
						logger.debug(String.format("%d Consumos cargados correctamente de un total de %d", rowsOk, rowsOk + rowsKo));
						
						//Comprobar los consumos
						billChecker.checkLinesBill();
						
						logger.debug(String.format(
							"%d Consumos procesados correctamente de un total de %d", 
							billChecker.getRowsOk(), 
							billChecker.getRowsOk() + billChecker.getRowsKo()
							));
						
						//Generar el CSV con los resultados
						billChecker.generateCheckCsvFile();
						
						//Preparar el retorno del método
						gar.setResultObject(String.format(
							"Generado el csv %s con el resultado de la comprobación", 
							billChecker.getCsvFileName()
							));
						if(billChecker.getRowsKo() == 0)
						{
							gar.setActionResult(EResult.OK);
						}
						else
						{
							gar.setActionResult(EResult.KO);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("checkBill");
			gar.setActionResult(EResult.KO, be);
		}
		
		return gar;
	}
	
	//<-
}
