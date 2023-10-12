package energycontrol.back.entities;

import energycontrol.back.bussines.EntitiesHelper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Consumption")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 
public class Consumption
{
	@Id
	@Column(name = "id")
	protected String id = "0000000000_?_0";
	
	@Column(name = "kwh")
	protected Double kwh = 0.0;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "collectionMethod")
	protected ECollectionMethod collectionMethod = ECollectionMethod.UNKNOWN;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "timeBand")
	protected ETimeBand timeBand = ETimeBand.Unknown;
	
	@ManyToOne
	@JoinColumn(name = "sourceId")
	protected Source source;
	
	@ManyToOne
	@JoinColumn(name = "dateHour", referencedColumnName = "id")
	protected DateHour dateHour;
	
	//-> Getters/Setters
	
	public String getId() {return id;}
	public void setId(String id){ this.id = id; }
	
	public Double getKwh() { return kwh; }
	public void setKwh(Double kwh) { this.kwh = kwh; }

	public ECollectionMethod getCollectionMethod() { return collectionMethod; }
	public void setCollectionMethod(ECollectionMethod collectionMethod) { this.collectionMethod = collectionMethod; }

	public ETimeBand getTimeBand() { return timeBand; }
	public void setTimeBand(ETimeBand timeBand) { this.timeBand = timeBand; }
	
	public Source getSource() {	return source; }
	public void setSource(Source source) { this.source = source; }

	public DateHour getDateHour() { return dateHour; }
	public void setDateHour(DateHour dateHour) { this.dateHour = dateHour; }
	
	//<-
	
	//-> Constructores
	
	public Consumption(DateHour dateHour, Double kwh, ECollectionMethod collectionMethod, ETimeBand timeBand, Source source)
	{
		this.dateHour= dateHour;
		this.kwh = kwh;
		this.collectionMethod = collectionMethod;
		this.timeBand = timeBand;
		this.source = source;
				
		this.id = EntitiesHelper.generateIdComsumption(this);
	}
	
	public Consumption(DateHour dateHour, Double kwh, ETimeBand timeBand, Source source)
	{
		this(dateHour, kwh, ECollectionMethod.UNKNOWN, timeBand, source);
	}
	
	public Consumption(DateHour dateHour, Double kwh, Source source)
	{
		this(dateHour, kwh, ECollectionMethod.UNKNOWN, ETimeBand.Unknown, source);
	}
	
	public Consumption()
	{
		this(null, 0.0, ECollectionMethod.UNKNOWN, ETimeBand.Unknown, null);
	}
	
	//<-
}
