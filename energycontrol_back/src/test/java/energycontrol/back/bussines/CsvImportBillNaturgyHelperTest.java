package energycontrol.back.bussines;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

import mbt.utilities.BussinesException;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;

import energycontrol.back.entities.Bill;
import energycontrol.back.entities.BillConsumption;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.ETimeBand;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.DateHour;

public class CsvImportBillNaturgyHelperTest 
{
	CsvImportBillNaturgyHelper cibnHelper = null;
	

	public CsvImportBillNaturgyHelperTest()
	{
		try
		{
			this.cibnHelper = new CsvImportBillNaturgyHelper(null, "energycontrol-back.properties");
		}
		catch(Exception e)
		{
			this.cibnHelper = null;
			System.out.printf("Se ha producido una excepción en el constructor de CsvImportBillNaturgyHelperTest: %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void constructorTest()
	{
		boolean ok = true;
		
		if(this.cibnHelper == null)
		{
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void isBillNumberRowTest()
	{
		boolean ok = true;
		
		try
		{
			String csvRow = ";;;;Nº factura: ;FE22137015493312;";
			
			if(!this.cibnHelper.isBillNumberRow(csvRow))
			{
				ok = false;
			}
			else
			{
				csvRow = ";;;;DATOS DE LA FACTURA DE ELECTRICIDAD;;";
				
				if(this.cibnHelper.isBillNumberRow(csvRow))
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
	public void isCupsRowTest()
	{
		boolean ok = true;
		
		try
		{
			String csvRow = ";;;;Código unificado de punto de suministro CUPS: ;ES0022000004124187BK1P;";
			
			if(!this.cibnHelper.isCupsRow(csvRow))
			{
				ok = false;
			}
			else
			{
				csvRow = ";;;;Dirección de suministro: ;LUG ALDEA A FRAGA 0043   Piso:BJ  (15319) A CORUÑA BERGONDO A FRAGA -BERGONDO-;";
				
				if(this.cibnHelper.isCupsRow(csvRow))
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
	public void isConsumptionsHeaderRowTest()
	{
		boolean ok = true;
		
		try
		{
			String csvRow = "Fecha;Hora Desde;Hora Hasta;Tipo consumo;Consumo (kWh);Precio horario de energía (EUR/kWh);Importe horario de energía (EUR)";
			
			if(!this.cibnHelper.isConsumptionsHeaderRow(csvRow))
			{
				ok = false;
			}
			else
			{
				csvRow = "Periodo;;Tipo consumo;Consumo (kWh);Precio por coste de energía: Importe de energía / Consumo (EUR/kWh);Importe de energía (EUR);";
				
				if(this.cibnHelper.isConsumptionsHeaderRow(csvRow))
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
	public void getBillNumberTest()
	{
		boolean ok = true;
		
		try
		{
			String csvRow = ";;;;Nº factura: ;FE22137015493312;";
			String value = this.cibnHelper.getBillNumber(csvRow);
			
			ok = "FE22137015493312".equals(value);
		}
		catch(BussinesException be)
		{
			System.out.println(be.getMessage());
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void getCupsTest()
	{
		boolean ok = true;
		
		try
		{
			String csvRow = ";;;;Código unificado de punto de suministro CUPS: ;ES0022000004124187BK1P;";
			String value = this.cibnHelper.getCups(csvRow);
			
			ok = "ES0022000004124187BK1P".equals(value);
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
			MSource ms = new MSource(ESourceType.Bill, "FAC", "Factura");
			Bill bill = new Bill(ms, LocalDate.of(2022, 7, 2), "FE22137015493312");
			
			String csvRow = "07/01/2022;0;1;Energía Activa Valle;0,597;0,277119548640;0,165440370538";
			GenericActionResult<BillConsumption> gar = this.cibnHelper.csvRow2HourBill(csvRow, bill, ECollectionMethod.SIMULATED);
			
			if(gar.getResult() != EResult.OK)
			{
				ok = false;
			}
			else
			{
				BillConsumption bcExpected = new BillConsumption(
					new DateHour(2022, 1, 7, 1)
					,0.597
					,0.277119548640
					,0.165440370538
					,ECollectionMethod.SIMULATED
					,ETimeBand.OffPeak
					,bill
					);
				
				BillConsumption bc = gar.getResultObject();
				
				if(!bcExpected.getId().equals(bc.getId()))
				{
					ok = false;
				}
				
				if(bcExpected.getCollectionMethod() != bc.getCollectionMethod())
				{
					ok = false;
				}
				
				if(EntitiesHelper.dateHourCompare(bcExpected.getDateHour(), bc.getDateHour()) != 0)
				{
					ok = false;
				}
				
				if(!bcExpected.getHourCost().equals(bc.getHourCost()))
				{
					ok = false;
				}
				
				if(!bcExpected.getKwh().equals(bc.getKwh()))
				{
					ok = false;
				}
				
				if(!bcExpected.getKwhCost().equals(bc.getKwhCost()))
				{
					ok = false;
				}
				
				if(bcExpected.getTimeBand() != bc.getTimeBand())
				{
					ok = false;
				}
				
				if(bcExpected.getSource() != bc.getSource())
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
