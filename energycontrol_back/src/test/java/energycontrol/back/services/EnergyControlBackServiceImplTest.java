package energycontrol.back.services;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import mbt.utilities.ActionResult;
import mbt.utilities.EResult;
import mbt.utilities.GenericActionResult;

import energycontrol.back.entities.Bill;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.ECollectionMethod;
import energycontrol.back.entities.ESourceType;
import energycontrol.back.entities.MSource;
import energycontrol.back.entities.Source;

public class EnergyControlBackServiceImplTest 
{
	private EnergyControlBackServiceImpl service = null;
	private Bill mockBill = null;
	private Source mockSource = null;
	
	public EnergyControlBackServiceImplTest()
	{
		try
		{
			this.service = new EnergyControlBackServiceImpl();
		}
		catch(Exception e)
		{
			this.service = null;
			System.out.printf("Se ha producido una excepción en constructor de EnergyControlBackServiceImpl: %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public void tearDown()
	{
		try
		{
			if(this.mockBill != null)
			{
				this.service.deleteBill(this.mockBill.getBillNumber());
			}
			
			if(this.mockSource != null)
			{
				this.service.deleteSource(this.mockSource);
			}
		}
		catch(Exception e)
		{
			System.out.printf("Se ha producido una excepción en EnergyControlBackServiceImpl.tearDown(): %s\n", e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void constructorTest()
	{
		boolean ok = true;
		
		if(this.service == null)
		{
			ok = false;
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void generateDateHours()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.generateDateHours(): ");
		
		DateHour dhFrom = new DateHour(2023, 2, 28, 1);
		DateHour dhTo= new DateHour(2023, 3, 1, 24);
		
		System.out.printf("  Generar DateHours desde %s hasta %s ...\n", dhFrom.getId(), dhTo.getId());
		
		GenericActionResult<Integer> gar = this.service.generateDateHours(dhFrom, dhTo);
		
		System.out.printf(" Generados %d DateHours\n", gar.getResultObject());
		
		assertEquals(48, gar.getResultObject().intValue());
	}
	
	@Test
	public void saveMSourceTest()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.saveMSource(): ");
		
		MSource ms = new MSource(ESourceType.Bill, "FAC", "Factura");
		
		GenericActionResult<MSource> gar = this.service.saveMSource(ms);
		
		System.out.printf(" Test %s\n", gar.getResult());
		
		assertEquals(EResult.OK, gar.getResult());
	}
	
	@Test
	public void getMSourcesTest()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.getMSources(): ");
		
		GenericActionResult<List<MSource>> gar = this.service.getMSources();
		
		System.out.printf(" Test %s\n", gar.getResult());
		
		assertEquals(EResult.OK, gar.getResult());
	}
	
	@Test
	public void saveSourceTest()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.saveSource(): ");
		
		MSource ms = new MSource(ESourceType.ConsumptionMeter, "EE2", "Efergy E2 Classic");
		Source s = new Source(ms, LocalDate.of(2023, 8, 18));
		s.setCups("ES0022000004124187BK1P");
		
		GenericActionResult<Source> gar = this.service.saveSource(s);
		
		System.out.printf(" Test %s\n", gar.getResult());
		
		assertEquals(EResult.OK, gar.getResult());
	}
	
	@Test
	public void getSourcesTest()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.getSources(): ");
		
		GenericActionResult<List<Source>> gar = this.service.getSources();
		
		System.out.printf(" Test %s\n", gar.getResult());
		
		assertEquals(EResult.OK, gar.getResult());
	}
	
	@Test
	public void crudBillTest()
	{
		boolean ok = true;
		
		try
		{
			System.out.println("Inicio del test del método EnergyControlBackServiceImpl.saveBill(): ");
			
			MSource ms = new MSource(ESourceType.Bill, "FAC", "Factura");
			this.mockBill = new Bill(
					ms, 
					LocalDate.of(2023, 3, 23), 
					"FE22137015493312",
					new DateHour(2023, 2, 9, 1),
					new DateHour(2023, 2, 24, 24)
					);
			this.mockBill.setCups("ES0022000004124187BK1P");
			
			GenericActionResult<Bill> gar = this.service.saveBill(this.mockBill);
			
			System.out.printf(" Test %s\n", gar.getResult());
			
			if(EResult.OK != gar.getResult())
			{
				ok = false;
			}
			else
			{
				System.out.println("Inicio del test del método EnergyControlBackServiceImpl.getBill(): ");
				
				gar = this.service.getBill(this.mockBill.getBillNumber());
			
				System.out.printf(" Test %s\n", gar.getResult());
				
				if(EResult.OK != gar.getResult())
				{
					ok = false;
				}
				else
				{
					System.out.println("Inicio del test del método EnergyControlBackServiceImpl.deleteBill(): ");
					
					ActionResult ar = this.service.deleteBill(this.mockBill.getBillNumber());
					
					System.out.printf(" Test %s\n", ar.getResult());
						
					if(EResult.OK != gar.getResult())
					{
						ok = false;
					}
				}
			}
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("Se ha producido una excepción en EnergyControlBackServiceImplTest.crudBillTest(): %s\n", e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			this.tearDown();
		}
		
		assertEquals(true, ok);
	}
	
	
	@Test
	public void loadBillNaturgyFromCsvTest()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.loadBillNaturgyFromCsv(): ");
		boolean ok = true;
		
		try
		{
			URL res = getClass().getClassLoader().getResource("FacturaNaturgyEjemplo.csv"); //BillNumber=FE22137015493312
			File file = Paths.get(res.toURI()).toFile();
			
			GenericActionResult<Bill> gar = this.service.loadBillNaturgyFromCsv(
					file.getAbsolutePath(), 
					LocalDate.of(2022, 6, 10), 
					ECollectionMethod.SIMULATED
					);
			
			this.mockBill = gar.getResultObject();
			
			if(gar.getResult() != EResult.OK)
			{
				ok = false;
				System.out.printf("El método loadBillNaturgyFromCsv() ha devuelto errores: %s", gar.getExceptionsMessages());
			}
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("Se ha producido una excepción en loadBillNaturgyFromCsvTest(): %s", e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			this.tearDown();
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void loadEfergyE2ConsumptionsFromCsv()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.loadEfergyE2ConsumptionsFromCsv(): ");
		boolean ok = true;
		
		try
		{
			URL res = getClass().getClassLoader().getResource("ConsumosEE2ejemplo.csv"); 
			File file = Paths.get(res.toURI()).toFile();
			
			GenericActionResult<Source> gar = this.service.loadEfergyE2ConsumptionsFromCsv(
					file.getAbsolutePath(), 
					LocalDate.of(2023, 7, 23)
					);
			
			this.mockSource= gar.getResultObject();
			
			if(gar.getResult() != EResult.OK)
			{
				ok = false;
				System.out.printf("El método loadEfergyE2ConsumptionsFromCsv() ha devuelto errores: %s", gar.getExceptionsMessages());
			}
			else
			{
				System.out.println("Test OK.");
			}
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("Se ha producido una excepción en loadEfergyE2ConsumptionsFromCsv(): %s", e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			this.tearDown();
		}
		
		assertEquals(true, ok);
	}
	
	@Test
	public void checkBill()
	{
		System.out.println("Inicio del test del método EnergyControlBackServiceImpl.checkBill(): ");
		boolean ok = true;
		
		try
		{
			//Cargar factura
			URL resBill = getClass().getClassLoader().getResource("FacturaNaturgyEjemplo.csv"); //BillNumber=FE22137015493312
			File fileBill = Paths.get(resBill.toURI()).toFile();
			
			GenericActionResult<Bill> garBill = this.service.loadBillNaturgyFromCsv(
					fileBill.getAbsolutePath(), 
					LocalDate.of(2022, 6, 10), 
					ECollectionMethod.SIMULATED
					);
			
			this.mockBill = garBill.getResultObject();
			
			//Cargar consumos de verificación
			URL resSource = getClass().getClassLoader().getResource("ConsumosEE2factura.csv"); 
			File fileSource = Paths.get(resSource.toURI()).toFile();
			
			GenericActionResult<Source> garSource = this.service.loadEfergyE2ConsumptionsFromCsv(
					fileSource.getAbsolutePath(), 
					LocalDate.of(2022, 6, 10)
					);
			
			this.mockSource= garSource.getResultObject();
			
			
			//Comprobar factura
			GenericActionResult<String> garCheck = this.service.checkBill(
					"FE22137015493312", 
					"EE2"
					);
			
			System.out.println(garCheck.getResultObject());
			
			if(garCheck.getResult() != EResult.OK)
			{
				ok = false;
				System.out.printf("El método checkBill() ha devuelto errores: %s", garCheck.getExceptionsMessages());
			}
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("Se ha producido una excepción en checkBill(): %s", e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			this.tearDown();
		}
		
		assertEquals(true, ok);
	}
	
}
