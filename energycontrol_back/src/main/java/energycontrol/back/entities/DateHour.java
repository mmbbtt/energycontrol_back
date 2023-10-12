package energycontrol.back.entities;


import energycontrol.back.bussines.EntitiesHelper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase para identificar la fecha y hora del consumo
 */
@Entity
@Table(name = "DateHour")
public class DateHour 
{
	@Id
	@Column(name = "id")
	private String id = "0000000000";
	
	@Column(name = "year")
	private Integer year = 0;
	
	@Column(name = "month")
	private Integer month = 0;
	
	@Column(name = "day")
	private Integer day = 0;
	
	@Column(name = "hour")
	private Integer hour = 0;
	
	//-> Getters / Setters
	
	public String getId() { return id;}
	public void setId(String dateHourId) { this.id = dateHourId; }
	
	public Integer getYear() { return year;	}
	public void setYear(Integer year) { this.year = year; }

	public Integer getMonth() {	return month; }
	public void setMonth(Integer month) { this.month = month; }

	public Integer getDay() { return day; }
	public void setDay(Integer day) { this.month = day; }
	
	public Integer getHour() { return hour; }
	public void setHour(Integer hour) { this.hour = hour; }
	
	//<-
	
	//-> Constructores
	
	public DateHour() {}
	
	public DateHour(Integer year, Integer month, Integer day, Integer hour) 
	{
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		
		this.id = EntitiesHelper.generateIdDateHour(this);
	}
	
	//<-
	
	@Override
	public boolean equals(Object o) 
	{
        if (this == o) 
        		return true;
        
        if (o == null || getClass() != o.getClass()) 
        	return false;
  
        
        return this.id.equals(((DateHour) o).id);
    }
	
	@Override
    public int hashCode() 
	{
		return this.id.hashCode();
	}
}
