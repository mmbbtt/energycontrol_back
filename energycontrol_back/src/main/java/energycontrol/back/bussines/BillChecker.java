package energycontrol.back.bussines;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import energycontrol.back.entities.Bill;
import energycontrol.back.entities.BillConsumption;
import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.MSource;
import mbt.utilities.BussinesException;

/**
 * Clase para comprobar los consumos de de una factura frente a consumos verificados.
 * 
 */
public class BillChecker 
{
	public class ConsumptionCheckLine
	{
		public String dateHour;
		public ETimeBand timeBand = ETimeBand.Unknown;
		public Double kwhCost = 0.0;
		public Double kwhBill = 0.0;
		public Double kwhVerified = 0.0;
		public Double eurBill = 0.0;
		public Double eurVerified = 0.0;
		
		public ConsumptionCheckLine() {}
		
		public ConsumptionCheckLine(String dateHour)
		{
			this.dateHour = dateHour;
		}
		
		public ConsumptionCheckLine(
			 String dateHour
			,ETimeBand timeBand
			,Double kwhCost
			,Double kwhBill
			,Double kwhVerified
			,Double eurBill 
			,Double eurVerified
			)
		{
			this.dateHour = dateHour;
			this.timeBand = timeBand;
			this.kwhCost = kwhCost;
			this.kwhBill = kwhBill;
			this.kwhVerified = kwhVerified;
			this.eurBill = eurBill;
			this.eurVerified = eurVerified;
		}
	}
	
	private static final Logger logger = LogManager.getLogger(BillChecker.class);
	
	protected Bill bill;
	protected MSource mSource;
	protected Map<String, BillConsumption> mBillConsumptions = new HashMap<String, BillConsumption>();
	protected Map<String, Consumption> mSourceConsumptions = new HashMap<String, Consumption>();
	protected Map<String, Consumption> mTestConsumptions = new HashMap<String, Consumption>();
	protected SortedMap<String, ConsumptionCheckLine> mCheckConsumptions = new TreeMap<String, ConsumptionCheckLine>();
	
	protected Double kwhBillTotal = 0.0;
	protected Double kwhSourceTotal = 0.0;
	protected Double kwhOffPeakBillTotal = 0.0;
	protected Double kwhOffPeakSourceTotal = 0.0;
	protected Double kwhStandardBillTotal = 0.0;
	protected Double kwhStandardSourceTotal = 0.0;
	protected Double kwhPeakBillTotal = 0.0;
	protected Double kwhPeakSourceTotal = 0.0;
	protected Double kwhUnknownBillTotal = 0.0;
	protected Double kwhUnknownSourceTotal = 0.0;
	
	protected Double eurBillTotal = 0.0;
	protected Double eurSourceTotal = 0.0;
	protected Double eurOffPeakBillTotal = 0.0;
	protected Double eurOffPeakSourceTotal = 0.0;
	protected Double eurStandardBillTotal = 0.0;
	protected Double eurStandardSourceTotal = 0.0;
	protected Double eurPeakBillTotal = 0.0;
	protected Double eurPeakSourceTotal = 0.0;
	protected Double eurUnknownBillTotal = 0.0;
	protected Double eurUnknownSourceTotal = 0.0;
	
	protected String csvFileName = null;
	
	protected int rowsOk = 0;
	protected int rowsKo = 0;
	
	public int getRowsOk() { return this.rowsOk; }
	public int getRowsKo() { return this.rowsKo; }
	public String getCsvFileName() { return this.csvFileName; }
	
	/**
	 * Constructor obligatorio.
	 * 
	 * @param bill Factura a verificar
	 * @param mSource Maestro del origen de consumos verificados
	 */
	public BillChecker(Bill bill, MSource mSource)
	{
		this.bill = bill;
		this.mSource = mSource;
	}
	
	/**
	 * Añade el consumo de la factura a la lista de consumos de la factura.
	 * 
	 * @param billConsumption
	 * @return 
	 * @throws BussinesException Cuando el consumo a añadir no pertenece a la factura.
	 */
	public boolean addBillConsumption(BillConsumption billConsumption) throws BussinesException
	{
		boolean added = false;
		
		if(this.bill.getId().equals(billConsumption.getSource().getId()))
		{
			this.mBillConsumptions.put(billConsumption.getDateHour().getId(), billConsumption);
			added = true;
		}
		else
		{
			BussinesException be = new BussinesException(
				 "El consumo a añadir no pertenece a la factura."
				,null
				,EUserMessagesKeys.ConsumptionNotBelongBill.stringValue
				);
			
			throw be;
		}

		
		return added;
	}
	
	public boolean addVerifiedConsumption(Consumption consumption) throws BussinesException
	{
		boolean added = false;
		
		String keyDateHour = consumption.getDateHour().getId();
		
		if(!this.mBillConsumptions.containsKey(keyDateHour))
		{
			BussinesException be = new BussinesException(
				 "La fecha del consumo no pertenece a la factura."
				,null
				,EUserMessagesKeys.DateOfConsumptionNotBelongBill.stringValue
				);
			
			throw be;
		}
		else if(this.mSourceConsumptions.containsKey(keyDateHour))
		{
			logger.warn(String.format("Ya existe un consumo de verificación para la fecha %s", keyDateHour));
		}
		else
		{
			this.mSourceConsumptions.put(consumption.getDateHour().getId(), consumption);
			added = true;
		}
		
		return added;
	}
	

	/**
	 * Comprueba las líneas de la factura.
	 * Por cada línea de factura crea una línea ConsumptionCheckLine y la almacena en mCheckConsumptions.
	 * Antes de invocar a este método se deben cargar los consumos de verificación.
	 * 
	 * @throws BussinesException
	 */
	public void checkLinesBill() throws BussinesException
	{
		try
		{
			//Comprobar líneas de factura
			for(Map.Entry<String, BillConsumption> lf : this.mBillConsumptions.entrySet())
			{
				Consumption c = this.mSourceConsumptions.get(lf.getKey());
				
				if(c != null)
				{
					double kwhVerified = ((c != null) && (c.getKwh() != null)) ? c.getKwh().doubleValue() : 0.0;
									
					ConsumptionCheckLine ccl = new ConsumptionCheckLine(
						 lf.getKey() 																		//dateHour
						,lf.getValue().getTimeBand()														//timeBand
						,lf.getValue().getKwhCost()															//kwhCost
						,lf.getValue().getKwh().doubleValue()												//kwhBill
						,kwhVerified																		//kwhVerified
						,lf.getValue().getKwhCost().doubleValue() * lf.getValue().getKwh().doubleValue()	//eurBill 
						,lf.getValue().getKwhCost().doubleValue() * kwhVerified 							//eurVerified
						);
					
					this.kwhBillTotal += ccl.kwhBill;
					this.eurBillTotal += ccl.eurBill;
					this.kwhSourceTotal += ccl.kwhVerified;
					this.eurSourceTotal += ccl.eurVerified;
					
					switch(lf.getValue().getTimeBand())
					{
						case OffPeak:
							this.kwhOffPeakBillTotal += ccl.kwhBill;
							this.kwhOffPeakSourceTotal += ccl.kwhVerified;
							this.eurOffPeakBillTotal += ccl.eurBill;
							this.eurOffPeakSourceTotal += ccl.eurVerified;
							break;
						case Standard:
							this.kwhStandardBillTotal += ccl.kwhBill;
							this.kwhStandardSourceTotal += ccl.kwhVerified;
							this.eurStandardBillTotal += ccl.eurBill;
							this.eurStandardSourceTotal += ccl.eurVerified;
							break;
						case Peak:
							this.kwhPeakBillTotal += ccl.kwhBill;
							this.kwhPeakSourceTotal += ccl.kwhVerified;
							this.eurPeakBillTotal += ccl.eurBill;
							this.eurPeakSourceTotal += ccl.eurVerified;
							break;
						default:
							this.kwhUnknownBillTotal += ccl.kwhBill;
							this.kwhUnknownSourceTotal += ccl.kwhVerified;
							this.eurUnknownBillTotal += ccl.eurBill;
							this.eurUnknownSourceTotal += ccl.eurVerified;
					}
					
					this.mCheckConsumptions.put(ccl.dateHour, ccl);
					rowsOk++;
				}
				else
				{
					rowsKo ++;
					logger.warn(String.format("No existe consumo de verificación para la línea %s", lf.getKey()));
				}
			}
		}
		catch(Exception e)
		{
			BussinesException be = new BussinesException(
					 e.getMessage()
					,e
					,EUserMessagesKeys.InternalError.stringValue
					);
				be.addUserMessageArgument("checkLinesBill");
				
				throw be;
		}
	}
	
	/**
	 * Genera un CSV con la comprobación de las líneas de la factura
	 * 
	 * @throws BussinesException
	 */
	public void generateCheckCsvFile() throws BussinesException
	{
		BufferedWriter bw = null;
		
		try
		{
			//Crear el nombre de ficehro
			this.csvFileName = String.format(
				"%s_vs_%s.csv", 
		
				this.bill.getBillNumber(),
				this.mSource.getCode()
				);
			
			//Abrir el fichero para escritura
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.csvFileName ), StandardCharsets.ISO_8859_1));
			
			//Escribir datos generales
			String sLine = String.format("Factura; %s", this.bill.getBillNumber());
			bw.write(sLine);
			
			//Escribir resúmenes de consumos e importes
			sLine = "\n\nkWh Totales";
			bw.write(sLine);
			sLine = "\nkWh-Factura;kWh-Verificados;kWh-Factura - kWh-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.kwhBillTotal, this.kwhSourceTotal, this.kwhBillTotal - this.kwhSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nkWh Valle Totales";
			bw.write(sLine);
			sLine = "\nkWh-Factura;kWh-Verificados;kWh-Factura - kWh-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.kwhOffPeakBillTotal, this.kwhOffPeakSourceTotal, this.kwhOffPeakBillTotal - this.kwhOffPeakSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nkWh Llano Totales";
			bw.write(sLine);
			sLine = "\nkWh-Factura;kWh-Verificados;kWh-Factura - kWh-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.kwhStandardBillTotal, this.kwhStandardSourceTotal, this.kwhStandardBillTotal - this.kwhStandardSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nkWh Punta Totales";
			bw.write(sLine);
			sLine = "\nkWh-Factura;kWh-Verificados;kWh-Factura - kWh-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.kwhPeakBillTotal, this.kwhPeakSourceTotal, this.kwhPeakBillTotal - this.kwhPeakSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nEur Totales";
			bw.write(sLine);
			sLine = "\nEur-Factura;Eur-Verificados;Eur-Factura - Eur-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.eurBillTotal, this.eurSourceTotal, this.eurBillTotal - this.eurSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nEur Valle Totales";
			bw.write(sLine);
			sLine = "\nEur-Factura;Eur-Verificados;Eur-Factura - Eur-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.eurOffPeakBillTotal, this.eurOffPeakSourceTotal, this.eurOffPeakBillTotal - this.eurOffPeakSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nEur Llano Totales";
			bw.write(sLine);
			sLine = "\nEur-Factura;Eur-Verificados;Eur-Factura - Eur-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.eurStandardBillTotal, this.eurStandardSourceTotal, this.eurStandardBillTotal - this.eurStandardSourceTotal);
			bw.write(sLine);
			
			sLine = "\n\nEur Punta Totales";
			bw.write(sLine);
			sLine = "\nEur-Factura;Eur-Verificados;Eur-Factura - Eur-Verificados";
			bw.write(sLine);
			sLine = String.format("\n%f;%f;%f", this.eurPeakBillTotal, this.eurPeakSourceTotal, this.eurPeakBillTotal - this.eurPeakSourceTotal);
			bw.write(sLine);
			
			//Escribir consumos por hora
			sLine = "\n\nDetalle por horas";
			bw.write(sLine);
			sLine = "\nHora;Período;Eur/kWh;kWh-Factura;kWh-Verificados;kWh-Factura - kWh-Verificados;Eur-Factura;Eur-Verificados;Eur-Factura - Eur-Verificados";
			bw.write(sLine);
			
			for(ConsumptionCheckLine ccl : this.mCheckConsumptions.values())
			{						 
				sLine = String.format(
					"\n%s;%s;%f;%f;%f;%f;%f;%f;%f", 
					ccl.dateHour, 
					ccl.timeBand, 
					ccl.kwhCost,
					ccl.kwhBill,
					ccl.kwhVerified,
					ccl.kwhBill - ccl.kwhVerified,
					ccl.eurBill,
					ccl.eurVerified,
					ccl.eurBill - ccl.eurVerified
					);
				bw.write(sLine);
			}
		}
		catch(Exception e)
		{
			this.csvFileName = null;
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("generateCheckCsvFile");
			
			throw be;
		}
		finally
		{
			try
			{
				if(bw != null)
				{
					bw.close();
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
}
