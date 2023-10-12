package energycontrol.back.daos;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DaosFactoryProviderTest 
{
	@Test
	public void test()
	{
		boolean ok = true;
		
		DaosFactoryProvider dfp = null;
		try
		{
			dfp = new DaosFactoryProvider("HSQLDB_TEST");
		}
		catch(Exception e)
		{
			ok = false;
			System.out.printf("DaosFactoryProviderTest.test: DaosFactoryProvider() KO: %s\n", e.getMessage());
		}
		
		if(dfp == null)
		{
			System.out.println("DaosFactoryProviderTest.test: DaosFactoryProvider() KO");
		}
		else
		{
			try
			{
				DaosFactory df = dfp.getDaosFactory();
				
				if(df == null)
				{
					ok = false;
					System.out.println("DaosFactoryProviderTest.test: getDaosFactory() KO");
				}
			}
			catch(Exception e)
			{
				ok = false;
				System.out.printf("DaosFactoryProviderTest.test: getDaosFactory() KO: %s\n", e.getMessage());
			}
		}
		
		assertEquals(true, ok);
	}
} 
