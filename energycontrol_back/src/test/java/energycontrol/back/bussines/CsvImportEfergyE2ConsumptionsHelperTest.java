package energycontrol.back.bussines;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

import mbt.utilities.BussinesException;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;

import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.Source;

public class CsvImportEfergyE2ConsumptionsHelperTest 
{
	CsvImportEfergyE2ConsumptionsHelper cieecHelper = null;
	
	public CsvImportEfergyE2ConsumptionsHelperTest()
	{
		try
		{
			this.cieecHelper = new CsvImportEfergyE2ConsumptionsHelper(null, "energycontrol-back.properties");
		}
		catch(Exception e)
		{
			this.cieecHelper = null;
			System.out.printf("Se ha producido una excepción en el constructor de CsvImportEfergyE2ConsumptionsHelperTest: %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void constructorTest()
	{
		boolean ok = true;
		
		if(this.cieecHelper == null)
		{
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void isConsumptionsHeaderRowTest()
	{
		boolean ok = true;
		
		try
		{
			String csvRow = "Fecha;Hora;Consumo (kWh);Coste;Mǭxima potenci;Comentarios (notas)";
			
			if(!this.cieecHelper.isConsumptionsHeaderRow(csvRow))
			{
				ok = false;
			}
			else
			{
				csvRow = "Informe diario;;;;;";
				
				if(this.cieecHelper.isConsumptionsHeaderRow(csvRow))
				{
					ok = false;
				}
			}
		}
		catch(BussinesException be)
		{
			System.out.println(be.getMessage());
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void csvRow2HourBillTest()
	{
		boolean ok = true;
		
		try
		{
			MSource ms = new MSource(ESourceType.ConsumptionMeter, "EE2", "Efergy E2 Meter");
			Source s = new Source(ms, LocalDate.now());
			
			String csvRow = "01-07-2023;0:00;0,342;0,034;;";
			GenericActionResult<Consumption> gar = this.cieecHelper.csvRow2Consumption(csvRow, s);
			
			if(gar.getResult() != EResult.OK)
			{
				ok = false;
			}
			else
			{
				Consumption cExpected = new Consumption(
					new DateHour(2023, 7, 1, 1)
					,0.342
					,ECollectionMethod.REAL
					,ETimeBand.OffPeak
					,s
					);
				
				Consumption c = gar.getResultObject();
				
				if(!cExpected.getId().equals(c.getId()))
				{
					ok = false;
				}
				
				if(cExpected.getCollectionMethod() != c.getCollectionMethod())
				{
					ok = false;
				}
				
				if(EntitiesHelper.dateHourCompare(cExpected.getDateHour(), c.getDateHour()) != 0)
				{
					ok = false;
				}
				
				if(!cExpected.getKwh().equals(c.getKwh()))
				{
					ok = false;
				}
				
				if(cExpected.getSource() != c.getSource())
				{
					ok = false;
				}
			}
		}
		catch(BussinesException be)
		{
			System.out.println(be.getMessage());
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	
}
