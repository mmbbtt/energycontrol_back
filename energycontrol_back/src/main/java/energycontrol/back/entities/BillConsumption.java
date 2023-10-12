package energycontrol.back.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Clase que representa el consumo de una hora de una factura.
 */
@Entity
public class BillConsumption extends Consumption
{
	@Column(name = "kwhCost")
	private Double kwhCost = 0.0;
	
	@Column(name = "hourCost")
	private Double hourCost = 0.0;
	
//	@ManyToOne
//	@JoinColumn(name = "sourceId")
//	private Bill bill;
	
	//-> Getters / Setters
	
	public Double getKwhCost() { return kwhCost; }
	public void setKwhCost(Double kwhCost) { this.kwhCost = kwhCost; }

	public Double getHourCost() { return hourCost; }
	public void setHourCost(Double hourCost) { this.hourCost = hourCost; }
	
	@Override
	public Bill getSource() {	return (Bill) this.source; }
	public void setSource(Bill bill) { this.source = bill; }
	
	//<-
	
	//-> Constructores
	
	public BillConsumption(DateHour dateHour, Double kwh, Double kwhCost, Double hourCost, ECollectionMethod collectionMethod, ETimeBand timeBand, Bill bill)
	{
		super(dateHour, kwh, collectionMethod, timeBand, bill);
		
		this.kwhCost = kwhCost;
		this.hourCost = hourCost;
	}
	
	public BillConsumption(DateHour dateHour, Double kwh, Double kwhCost, Double hourCost, ETimeBand timeBand, Bill bill)
	{
		super(dateHour, kwh, timeBand, bill);
		
		this.kwhCost = kwhCost;
		this.hourCost = hourCost;
	}
	
	public BillConsumption(DateHour dateHour, Double kwh, Double kwhCost, Double hourCost, Bill bill)
	{
		super(dateHour, kwh, bill);
		
		this.kwhCost = kwhCost;
		this.hourCost = hourCost;
	}
	
	public BillConsumption(DateHour dateHour, Double kwh, Bill bill)
	{
		super(dateHour, kwh, bill);
	}
	
	public BillConsumption(Bill bill)
	{
		super(null, null, bill);
	}
	
	public BillConsumption(){}
	
	//<-
}
