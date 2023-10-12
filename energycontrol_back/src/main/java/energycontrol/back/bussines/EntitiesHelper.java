package energycontrol.back.bussines;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

import energycontrol.back.entities.Consumption;
import energycontrol.back.entities.DateHour;
import energycontrol.back.entities.Source;

public class EntitiesHelper 
{
	/**
	 * Genera un identficador para el objeto DateHour pasado por argumento con el siguiente patron "yyyyMMddHH"
	 * 
	 * @param o
	 * @return
	 */
	public static String generateIdDateHour(DateHour o)
	{
		String gid = "0000000000";
		
		if(o != null)
		{
			gid =  String.format("%04d%02d%02d%02d", 
				o.getYear() == null ? 0 : o.getYear(), 
				o.getMonth() == null ? 0 : o.getMonth(), 
				o.getDay() == null ? 0 : o.getDay(),  
				o.getHour() == null ? 0 : o.getHour()
				);
		}
		
		return gid;
	}
	
	/**
	 * Compara los dos DateHour pasados por argumento.
	 * Devuelve 0 si son iguales
	 * Devuelve 1 si dateHour1 es mayor que dateHour2
	 * Devuelve 2 si dateHour1 es menor que dateHour2
	 * 
	 * @param dateHour1
	 * @param dateHour2
	 * @return
	 */
	public static int dateHourCompare(DateHour dateHour1, DateHour dateHour2)
	{
		long lhi1 = Long.parseLong(dateHour1.getId());
		long lhi2 = Long.parseLong(dateHour2.getId());
		
		return Long.compare(lhi1, lhi2);
	}
	
	/**
	 * Devulve un DateHour resultado de sumar un día al dateHour pasado por argumento.
	 * 
	 * @param dateHour
	 * @return
	 */
	public static DateHour addOneDay(DateHour dateHour)
	{
		GregorianCalendar gc = new GregorianCalendar();
		
		int year = dateHour.getYear();
		int month = dateHour.getMonth();
		int day = dateHour.getDay();
		int hour = dateHour.getHour();
		
		int maxDaysFrebuary = gc.isLeapYear(year)?29:28;
		
		switch(month)
		{
			case 1:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 2:
				if(day < maxDaysFrebuary)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 3:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 4:
				if(day < 30)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 5:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 6:
				if(day < 30)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 7:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 8:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 9:
				if(day < 30)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 10:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 11:
				if(day < 30)
				{
					day ++;
				}
				else
				{
					day = 1;
					month ++;
				}
				break;
			case 12:
				if(day < 31)
				{
					day ++;
				}
				else
				{
					day = 1;
					month = 1;
					year ++;
				}
				break;
		}
		
		return new DateHour(year, month, day, hour);	
	}
	
	/**
	 * Devulve un DateHour resultado de sumar las horas pasadas por argumento al dateHour pasado por argumento.
	 * 
	 * @param dateHour
	 * @param hours
	 * @return
	 * @throws ParseException
	 */
	public static DateHour addHours(DateHour dateHour, int hours) throws ParseException
	{
		int quotient = hours / 24;
		int mod = hours % 24;
		
		DateHour dh = dateHour;
		for(int i = 1; i <= quotient; i++)
		{
			dh = EntitiesHelper.addOneDay(dh);
		}
		
		if(mod != 0)
		{
			if((dh.getHour() + mod) > 24)
			{
				int hour = (dh.getHour() + mod) - 24;
				dh = EntitiesHelper.addOneDay(dh);
				dh = new DateHour(dh.getYear(), dh.getMonth(), dh.getDay(), hour);
			}
			else
			{
				dh = new DateHour(dh.getYear(), dh.getMonth(), dh.getDay(), (dh.getHour() + mod));
			}
		}
		
		return dh;
	}
	
	/**
	 * Devuelve un LocalDateTime equivale al dateHour pasado por argmento.
	 * 
	 * @param dateHour
	 * @return
	 */
	public static LocalDateTime dateHour2Date(DateHour dateHour)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				
		return LocalDateTime.parse(
				String.format("%02d/%02d/%04d %02d:00", dateHour.getDay(), dateHour.getMonth(), dateHour.getYear(), dateHour.getHour()),
				dtf
				);
	}
	
	/**
	 * Devuelve una cadena de texto equivalente a la fecha del dateHour en formato yyyyMMdd
	 * 
	 * @param dateHour
	 * @return
	 */
	public static String dateHour2StringDate(DateHour dateHour)
	{
		String sDate = "00000000";
		
		if(dateHour != null)
		{
			sDate =  String.format("%04d%02d%02d", 
				dateHour.getYear() == null ? 0 : dateHour.getYear(), 
				dateHour.getMonth() == null ? 0 : dateHour.getMonth(), 
				dateHour.getDay() == null ? 0 : dateHour.getDay()
				);
		}
		
		return sDate;
	}
	
	/**
	 * Genera un identficador para el objeto Source pasado por argumento con el siguiente patron "<o.getMSource().getCode()>_yyyyMMdd",
	 * donde "yyyyMMdd" se corresponde con o.getDateOfData().
	 * 
	 * @param o
	 * @return
	 */
	public static String generateIdSource(Source o)
	{
		String gid = "";
		
		if(o != null)
		{
			if(o.getMSource() == null)
			{
				gid="?_";
			}
			else
			{
				gid = o.getMSource().getCode();
			}
			
			if(o.getDateOfData() == null)
			{
				gid = gid + "?";
			}
			else
			{
				gid = gid + String.format(
						"%04d%02d%02d", 
						o.getDateOfData().getYear(),
						o.getDateOfData().getMonthValue(),
						o.getDateOfData().getDayOfMonth()
						);
			}
		}
		
		return gid;
	}
	
	/**
	 *  Genera un identficador para el objeto Consumption pasado por argumento con el siguiente patrón "<o.getDateHour().getId()>_<o.getSource().getId()>",
	 * @param o
	 * @return
	 */
	public static String generateIdComsumption(Consumption o)
	{
		String gid = "?_?";
		
		if(o != null)
		{
			gid = String.format(
					"%s_%s", 
					(o.getDateHour() != null) ? o.getDateHour().getId() : "?",			
					((o.getSource() != null) && (o.getSource().getId() != null)) ? o.getSource().getId() : "?"
					);
		}
		
		return gid;
	}
	
	
}
