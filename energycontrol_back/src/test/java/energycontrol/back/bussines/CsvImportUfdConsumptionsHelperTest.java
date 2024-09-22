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

public class CsvImportUfdConsumptionsHelperTest 
{
	CsvImportUfdConsumptionsHelper ciucHelper = null;
	
	public CsvImportUfdConsumptionsHelperTest()
	{
		try
		{
			this.ciucHelper = new CsvImportUfdConsumptionsHelper(null, "energycontrol-back.properties");
		}
		catch(Exception e)
		{
			this.ciucHelper = null;
			System.out.printf("Se ha producido una excepción en el constructor de CsvImportUfdConsumptionsHelperTest: %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void constructorTest()
	{
		boolean ok = true;
		
		if(this.ciucHelper == null)
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
			String csvRow = "CUPS;Fecha;Hora;Consumo;Metodo_obtencion";
			
			if(!this.ciucHelper.isConsumptionsHeaderRow(csvRow))
			{
				ok = false;
			}
			else
			{
				csvRow = "Informe diario;;;;;";
				
				if(this.ciucHelper.isConsumptionsHeaderRow(csvRow))
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
	public void csvRow2ConsumptionTest()
	{
		boolean ok = true;
		
		try
		{
			MSource ms = new MSource(ESourceType.DistributionCompany, "UFD", "UFD Distribución Electricidad, S.A.");
			Source s = new Source(ms, LocalDate.now());
			
			String csvRow = "ES0022000004124187BK1P;06/01/2022;24;0,258;R";
			GenericActionResult<Consumption> gar = this.ciucHelper.csvRow2Consumption(csvRow, s);
			
			if(gar.getResult() != EResult.OK)
			{
				ok = false;
			}
			else
			{
				Consumption cExpected = new Consumption(
					new DateHour(2022, 1, 6, 24)
					,0.258
					,ECollectionMethod.REAL
					,ETimeBand.Unknown
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
