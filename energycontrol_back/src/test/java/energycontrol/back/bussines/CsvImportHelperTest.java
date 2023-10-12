package energycontrol.back.bussines;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import mbt.utilities.BussinesException;
import mbt.utilities.Helper;


public class CsvImportHelperTest 
{
	private CsvImportHelper ciHelper = null;
	
	public CsvImportHelperTest()
	{
		try
		{
			this.ciHelper = new CsvImportHelper(null, "energycontrol-back.properties");
		}
		catch(Exception e)
		{
			this.ciHelper = null;
			System.out.printf("Se ha producido una excepción en CsvImportHelperTest.setup(): %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void constructorTest()
	{
		boolean ok = true;
		
		if(this.ciHelper == null)
		{
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void getValueOfPropertyTest()
	{
		boolean ok = true;
		
		try
		{
			if(Helper.stringIsNullOrEmpty(ciHelper.getValueOfProperty("DefaultDatePattern")))
			{
				ok = false;
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
	public void getIntValueOfPropertyTest()
	{
		boolean ok = true;
		
		try
		{
			if(ciHelper.getIntValueOfProperty("YEAR_MIN") == null)
			{
				ok = false;
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
	public void isEmptyCsvRowTest()
	{
		boolean ok = true;
		
		String csvRow = null;
		
		if(!this.ciHelper.isEmptyCsvRow(csvRow))
		{
			ok = false;
		}
		else
		{
			csvRow = "";
			
			if(!this.ciHelper.isEmptyCsvRow(csvRow))
			{
				ok = false;
			}
			else
			{
				csvRow = " ";
				
				if(!this.ciHelper.isEmptyCsvRow(csvRow))
				{
					ok = false;
				}
				else
				{
					csvRow = ";;;";
					
					if(!this.ciHelper.isEmptyCsvRow(csvRow))
					{
						ok = false;
					}
				}
			}
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void getValueOfColumnTest()
	{
		boolean ok = true;
		
		try
		{
			String columnName = "Tipo consumo";
			String nameOfIndexProperty = "BILL_NATURGY_ConsumptionsTypeColIndex";
			String csvRow = "07/01/2022;0;1;Energí­a Activa Valle;0,597;0,277119548640;0,165440370538";
			
			String sValue = this.ciHelper.getValueOfColumn(columnName, nameOfIndexProperty, csvRow);
			
			if(Helper.stringIsNullOrEmpty(sValue))
			{
				ok = false;
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
	public void getDateValueOfColumnTest()
	{
		boolean ok = true;
		
		try
		{
			String columnName = "Fecha";
			String nameOfIndexProperty = "BILL_NATURGY_ConsumptionsDateColIndex";
			String nameOfDatePatternProperty = "BILL_NATURGY_DatePattern";
			String csvRow = "07/01/2022;0;1;Energí­a Activa Valle;0,597;0,277119548640;0,165440370538";
			
			LocalDate ldValue = this.ciHelper.getDateValueOfColumn(columnName, nameOfIndexProperty, nameOfDatePatternProperty, csvRow);
			
			if(ldValue == null)
			{
				ok = false;
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
	public void getHourValueOfColumnTest()
	{
		boolean ok = true;
		
		try
		{
			String columnName = "Hora";
			String nameOfIndexProperty = "EFERGYE2CONSUMPTION_ROWINDEX_Hour";
			String nameOfHourPatternProperty = "EFERGYE2CONSUMPTION_HOUR_PATTERN";
			String csvRow = "21-01-2023;00:00;0,122;0,012;;";
			
			LocalTime ldValue = this.ciHelper.getHourValueOfColumn(
					columnName, 
					nameOfIndexProperty, 
					nameOfHourPatternProperty, 
					csvRow
					);  
			
			if(ldValue == null)
			{
				ok = false;
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
	public void getIntValueOfColumnTest()
	{
		boolean ok = true;
		
		try
		{
			String columnName = "Hora Hasta";
			String nameOfIndexProperty = "BILL_NATURGY_ConsumptionsHourColIndex";
			String csvRow = "07/01/2022;0;1;Energí­a Activa Valle;0,597;0,277119548640;0,165440370538";
			
			Integer iValue = this.ciHelper.getIntValueOfColumn(columnName, nameOfIndexProperty, csvRow);
			if(iValue == null)
			{
				ok = false;
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
	public void getDoubleValueOfColumnTest()
	{
		boolean ok = true;
		
		try
		{
			String columnName = "Precio horario de energí­a (Eur/kWh)";
			String nameOfIndexProperty = "BILL_NATURGY_ConsumptionskWhCostColIndex";
			String csvRow = "07/01/2022;0;1;Energí­a Activa Valle;0,597;0,277119548640;0,165440370538";
			
			Double dValue = this.ciHelper.getDoubleValueOfColumn(columnName, nameOfIndexProperty, csvRow);
			if(dValue == null)
			{
				ok = false;
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
