package energycontrol.back.bussines;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mbt.utilities.BussinesException;
import mbt.utilities.Helper;

import energycontrol.back.entities.Bill;
import energycontrol.back.entities.BillConsumption;
import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.MSource;

/**
 * Clase con utilidades para generar el fichero de datos y el script para GnuPlot con 
 * los conusmo de un día de una factura vs otro origen de consumos.
 * 
 */
public class GnuplotHelper 
{
	protected String sDay = null;
	protected Map<String, BillConsumption> mBillConsumptions = new HashMap<String, BillConsumption>();
	protected Map<String, Consumption> mSourceConsumptions = new HashMap<String, Consumption>();
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
	protected Bill bill;
	protected MSource mSource;
	protected boolean dataOkForGnuPlot = false;
	protected List<BussinesException> lbeDataNotOkForGnuPlot = new ArrayList<BussinesException>();
	protected String datFileName = null;
	protected String scriptFileName = null;
	
	public String getDay() { return this.sDay; }
	public boolean isDataOkForGnuPlot() { return dataOkForGnuPlot; }
	public List<BussinesException> getDataNotOkForGnuPlot() { return lbeDataNotOkForGnuPlot; }
	public String getDatFileName() { return this.datFileName; }
	public String getScriptFileName() { return this.scriptFileName; }
	
	/**
	 * Constructor obligatorio.
	 * 
	 * @param bill Factura a verificar
	 * @param mSource Maestro del origen de consumos verificados
	 */
	public GnuplotHelper(Bill bill, MSource mSource)
	{
		this.bill = bill;
		this.mSource = mSource;
	}
	
	/**
	 * Devuelve la fecha del día formateada como dd/MM/yyyy
	 * 
	 * @return
	 */
	public String getDateOfDay()
	{
		return String.format("%s/%s/%s", this.sDay.substring(6, 8), this.sDay.substring(4, 6), this.sDay.substring(0, 4));
	}
	
	/**
	 * Chequea que están informados todos los datos necesarios para generar el fichero GnuPlot.
	 * Añade a la lista lbeDataNotOkForGnuPlot BussinesException con los errores encontrados.
	 * 
	 * También calcula los consumos totales.
	 * 
	 * @return Devuelve true si no hay errores, o si los que hay no son graves. En caso contrario devuelve false.
	 */
	public boolean checkDataForGnuPlot()
	{
		this.dataOkForGnuPlot = true;
		
		//Errores graves:
		if(this.bill == null)
		{
			this.dataOkForGnuPlot = false;
			
			BussinesException be = new BussinesException(
				"Factura no informada"
				,null
				,EUserMessagesKeys.GnuPlotNoBill.stringValue
				);
			
			this.lbeDataNotOkForGnuPlot.add(be);
		}
		
		if(this.mSource == null)
		{
			this.dataOkForGnuPlot = false;
			
			BussinesException be = new BussinesException(
				"Maestro de origen de consumo verificados no informado"
				,null
				,EUserMessagesKeys.GnuPlotNoMSource.stringValue
				);
			
			this.lbeDataNotOkForGnuPlot.add(be);
		}
		
		if(
			Helper.stringIsNullOrEmpty(this.sDay)
			|| this.mBillConsumptions.size() == 0
		)
		{
			this.dataOkForGnuPlot = false;
			
			BussinesException be = new BussinesException(
				"Consumos de la factura sin añadir"
				,null
				,EUserMessagesKeys.GnuPlotNoBillConsumptions.stringValue
				);
			
			this.lbeDataNotOkForGnuPlot.add(be);
		}
		
		if(this.mSourceConsumptions.size() == 0)
		{
			this.dataOkForGnuPlot = false;
			
			BussinesException be = new BussinesException(
				"Consumos del origen verificador sin añadir"
				,null
				,EUserMessagesKeys.GnuPlotNoSourceConsumptions.stringValue
				);
			
			this.lbeDataNotOkForGnuPlot.add(be);
		}
		
		//Errores leves, que permiten genera el fichero GnuPlot
		int missingBillConsumptions = 0;
		int missingSourceConsumptions = 0;
		for(int i = 1; i <= 24; i++)
		{
			String sDateHourId = String.format("%s%02d", this.sDay, i);
			ETimeBand timeBand = ETimeBand.Unknown;
			
			if(!this.mBillConsumptions.containsKey(sDateHourId))
			{
				missingBillConsumptions ++;
			}
			else
			{
				BillConsumption bc = this.mBillConsumptions.get(sDateHourId);
				timeBand= bc.getTimeBand();
				double dValue = bc.getKwh() != null ? bc.getKwh().doubleValue() : 0.0;
				
				this.kwhBillTotal += dValue;
						
				if(timeBand == ETimeBand.OffPeak)
				{
					this.kwhOffPeakBillTotal += dValue;
				}
				else if(timeBand == ETimeBand.Standard)
				{
					this.kwhStandardBillTotal += dValue;
				}
				else if(timeBand == ETimeBand.Peak)
				{
					this.kwhPeakBillTotal += dValue;
				}
				else
				{
					this.kwhUnknownBillTotal += dValue;
				}
			}
			
			if(!this.mSourceConsumptions.containsKey(sDateHourId))
			{
				missingSourceConsumptions ++;
			}
			else
			{
				Consumption c = this.mSourceConsumptions.get(sDateHourId);
				double dValue = c.getKwh() != null ? c.getKwh().doubleValue() : 0.0;
				
				this.kwhSourceTotal += dValue;
				
				if(timeBand == ETimeBand.OffPeak)
				{
					this.kwhOffPeakSourceTotal += dValue;
				}
				else if(timeBand == ETimeBand.Standard)
				{
					this.kwhStandardSourceTotal += dValue;
				}
				else if(timeBand == ETimeBand.Peak)
				{
					this.kwhPeakSourceTotal += dValue;
				}
				else
				{
					this.kwhUnknownSourceTotal += dValue;
				}
			}
		}
		
		if(missingBillConsumptions > 0)
		{
			BussinesException be = new BussinesException(
				String.format("Faltan los consumos de factura de %d horas.", missingBillConsumptions)
				,null
				,EUserMessagesKeys.GnuPlotMissingBillConsumptions.stringValue
				);
			be.addUserMessageArgument(missingBillConsumptions);
			
			this.lbeDataNotOkForGnuPlot.add(be);
		}
		
		if(missingSourceConsumptions > 0)
		{
			BussinesException be = new BussinesException(
				String.format("Faltan los consumos verificados de %d horas.", missingSourceConsumptions)
				,null
				,EUserMessagesKeys.GnuPlotMissingSourceConsumptions.stringValue
				);
			be.addUserMessageArgument(missingSourceConsumptions);
			
			this.lbeDataNotOkForGnuPlot.add(be);
		}
		
		return this.dataOkForGnuPlot;
	}
	
	/**
	 * Añade el consumo de la factura a la lista de consumos de la factura.
	 * Si el día del consumo a añadir es igual al de los consumos preivos deuvelve true.
	 * Si el día del consumo a añadir es distinto de los preivos, no lo añade, y retorna false.
	 * 
	 * @param billConsumption
	 * @return 
	 * @throws BussinesException Cuando el consumo a añadir no pertenece a la factura.
	 */
	public boolean addBillConsumption(BillConsumption billConsumption) throws BussinesException
	{
		boolean added = false;
		
		if(this.sDay == null)
		{
			this.sDay = EntitiesHelper.dateHour2StringDate(billConsumption.getDateHour());
		}
		
		String sConsumptionDay = EntitiesHelper.dateHour2StringDate(billConsumption.getDateHour());
		
		if(this.sDay.equals(sConsumptionDay))
		{
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
		}
		else
		{
			added = false;
		}
		
		return added;
	}
	
	/**
	 * Añade los consumos del origen verificado a la lista de consumos verificados.
	 * 
	 * @param lSourceConsumptions Lista con los consumos del origen verificado.
	 * @throws BussinesException Cuando aún no se ha añadido ningún consumo de la factura, o cuando nignuno de los consumos añadir son del mismo día que los de la factura.
	 */
	public void addSourceConsumptions(List<Consumption> lSourceConsumptions) throws BussinesException
	{
		if(this.sDay == null)
		{
			BussinesException be = new BussinesException(
				 "Antes de usar este método debe añadir los consumos de la factura."
				,null
				,EUserMessagesKeys.GnuPlotConsumptionBeforeBillConsumption.stringValue
				);
			
			throw be;
		}
		
		boolean anyConsumptionAdded = false;
		for(Consumption c : lSourceConsumptions)
		{
			String sConsumptionDay = EntitiesHelper.dateHour2StringDate(c.getDateHour());
			
			if(this.sDay.equals(sConsumptionDay))
			{
				this.mSourceConsumptions.put(c.getDateHour().getId(), c);
				anyConsumptionAdded = true;
			}
		}
		
		if(!anyConsumptionAdded)
		{
			BussinesException be = new BussinesException(
				 "Ninguno de los consumos a añadir son del día de los consumos de la factura."
				,null
				,EUserMessagesKeys.GnuPlotConsumptionOtherDay.stringValue
				);
			
			throw be;
		}
	}
	
	/**
	 * Genera un fichero .dat, procesable por gnuplot, con los consumos del día de la factura vs los verificados.
	 * 
	 * - Nombre del fichero: <yyyyMMdd>_<Num. facutura>_<Origen consumos verificados>.dat
	 * - Esturctura del fichero:
	 *   - Linea 1: Comentario con el número de factura.
	 *   - Linea 2: Comentario con el origen de los consumos que se usan para verificar.
	 *   - Linea 3: Comentario con el día (dd/MM/yyyy) al cual se refieren los consumos.
	 *   - Linea 4: Comentario con los kWh totales del día según la factura.
	 *   - Linea 5: Comentario con los kWh totales del día según el origen de verificación.
	 *   - Linea 6: Comentario con los kWh totales del día en período valle según la factura.
	 *   - Linea 7: Comentario con los kWh totales del día en período valle según el origen de verificación.
	 *   - Linea 8: Comentario con los kWh totales del día en período llano según la factura.
	 *   - Linea 9: Comentario con los kWh totales del día en período llano según el origen de verificación.
	 *   - Linea 10: Comentario con los kWh totales del día en período punta según la factura.
	 *   - Linea 11: Comentario con los kWh totales del día en período punta según el origen de verificación.
	 *   - Linea 12: Comentario con los kWh totales del día en período desconocido según la factura.
	 *   - Linea 13: Comentario con los kWh totales del día en período desconocido según el origen de verificación.
	 *   - Linea 14: Encabezados de las columnas
	 *   - Resto de líneas (una por cada hora del día):
	 * 	 	- Columna 1: Hora del día
	 *   	- Columna 2: kWh consumidos según factura
	 *   	- Columna 3: kWh consumidos según el origen de los consumos que se usan para verificar la factura
	 *      - Columna 4: Período de consumo
	 * 
	 * @throws BussinesException
	 */
	public void generateGnuPlotDatFile() throws BussinesException
	{
		if(!this.dataOkForGnuPlot)
		{
			BussinesException be = new BussinesException(
				 "Faltan datos o hay errores graves que impiden generar la factura."
				,null
				,EUserMessagesKeys.GnuPlotDataNotReady.stringValue
				);
			
			throw be;
		}
		
		BufferedWriter bw = null;
		
		try
		{
			//Crear el nombre de fichero
			this.datFileName = String.format(
				"%s_%s_%s.dat", 
				this.sDay,
				this.bill.getBillNumber(),
				this.mSource.getCode()
				);
			
			//Abrir el fichero para escritura
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.datFileName ), StandardCharsets.ISO_8859_1));
			
			
			//Escribir comentarios de las primeras líneas
			String sLine = String.format("#Factura: %s", this.bill.getBillNumber());
			bw.write(sLine);
			
			sLine = String.format("\n#Día: %s", this.getDateOfDay());
			bw.write(sLine);
			
			sLine = String.format("\n#kWh totales factura: %.3f", this.kwhBillTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh totales origen verificado: %.3f", this.kwhSourceTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh valle totales factura: %.3f", this.kwhOffPeakBillTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh valle totales origen verificado: %.3f", this.kwhOffPeakSourceTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh llano totales factura: %.3f", this.kwhStandardBillTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh llano totales origen verificado: %.3f", this.kwhStandardSourceTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh pico totales factura: %.3f", this.kwhPeakBillTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh pico totales origen verificado: %.3f", this.kwhPeakSourceTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh desconocido totales factura: %.3f", this.kwhUnknownBillTotal);
			bw.write(sLine);
			
			sLine = String.format("\n#kWh desconocido totales origen verificado: %.3f", this.kwhUnknownSourceTotal);
			bw.write(sLine);
			
			//Escribir la cabecera con los nombres de las columnas
			sLine = "\nHora kWh-Factura kWh-Verificados Período";
			bw.write(sLine);
			
			//Escribir los consumo por hora
			for(int i = 1; i <= 24; i ++)
			{
				String sDateHourId = String.format("%s%02d", this.sDay, i);
				double dKwhBill = 0.0;
				double dKwhSource = 0.0;
				ETimeBand tb = ETimeBand.Unknown;
				
				if(
					   this.mBillConsumptions.containsKey(sDateHourId)
					&& this.mBillConsumptions.get(sDateHourId).getKwh() != null
					)
				{
					dKwhBill = this.mBillConsumptions.get(sDateHourId).getKwh().doubleValue();
					tb = this.mBillConsumptions.get(sDateHourId).getTimeBand();
				}
				
				if(
					   this.mSourceConsumptions.containsKey(sDateHourId)
					&& this.mSourceConsumptions.get(sDateHourId).getKwh() != null
					)
				{
					dKwhSource = this.mSourceConsumptions.get(sDateHourId).getKwh().doubleValue();
				}
				
				sLine = String.format("\n%02d    %.3f            %.3f                 %s", i, dKwhBill, dKwhSource, tb);
				sLine = sLine.replace(",", ".");
				bw.write(sLine);
			}
		}
		catch(Exception e)
		{
			this.datFileName = null;
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("generateGnuPlotDatFile");
			
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
	
	/**
	 * Genera un script GnuPlot que dibuja un gráfico con el consumo, por hora, de la factura vs el de verifación
	 * a partir del fichero de dat generado con generateGnuPlotDatFile().
	 * 
	 * Una vez generado el script, hay que invocar el comando gnuplot:
	 * 		gnuplot -p "nombre_del_script_generado"
	 * En donde nombre_del_script_generado es: <yyyyMMdd>_<número de factura>_<Código MSource>.gp
	 * Lo cual generará un pdf con el gráfico, de nombre <yyyyMMdd>_<número de factura>_<Código MSource>.pdf
	 * 
	 * @throws BussinesException
	 */
	public void generateGnuPlotScriptPdfFile() throws BussinesException
	{
		if(Helper.stringIsNullOrEmpty(this.datFileName))
		{
			BussinesException be = new BussinesException(
				 "Primero debe genenerar el fichero de datos"
				,null
				,EUserMessagesKeys.GnuPlotMissingDatFileName.stringValue
				);
			
			throw be;
		}
		
		BufferedWriter bw = null;
		InputStream isScriptTemplate = null;
		BufferedReader br = null;
		
		try
		{
			//Crear el nombre del pdf que generará el script
			String outPdfName = String.format("%s.pdf", this.datFileName.replace(".dat", ""));
				
			//Crear el nombre del script
			this.scriptFileName = String.format("%s.gp", this.datFileName.replace(".dat", ""));
			
			//Crear el título del gráfico
			String graphTitle = String.format("%s %s", this.bill.getBillNumber(), this.getDateOfDay());
			
			//Abrir la plantilla del script
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			isScriptTemplate = loader.getResourceAsStream("GnuPlotPdfScriptTemplate.gp");
			br = new BufferedReader(new InputStreamReader(isScriptTemplate));
			
			//Abrir el fichero para escritura
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.scriptFileName ), StandardCharsets.ISO_8859_1));
			
			//Escribir las líneas de la plantilla en el fichero del script
			String line;
	        while ((line = br.readLine()) != null)
	        {
	        	//Susbtituir _TittleOfGraph_ de la plantilla por el título del gráfico
	        	line = line.replace("_TittleOfGraph_", graphTitle);
	        	
	        	//Susbtituir _OutputFileName_ por el nombre de pdf que generará el script
	        	line = line.replace("_OutputFileName_", outPdfName);
	        	
	        	//Susbtituir _DatFileName_ por el nombre del archivo de dat
	        	line = line.replace("_DatFileName_", this.datFileName);
	        	
	        	//Escribir la línea
	        	bw.write(line);
	        	bw.newLine();
	        }
		}
		catch(Exception e)
		{
			this.datFileName = null;
			
			BussinesException be = new BussinesException(
				 e.getMessage()
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
			be.addUserMessageArgument("generateGnuPlotScriptPdfFile");
			
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
				
				if(br != null)
				{
					br.close();
				}
				
				if(isScriptTemplate != null)
				{
					isScriptTemplate.close();
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
}
