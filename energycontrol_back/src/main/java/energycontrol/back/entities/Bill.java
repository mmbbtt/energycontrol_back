package energycontrol.back.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Clase que representa una factura.
 */
@Entity
@Table(name = "Bill")
public class Bill extends Source
{
	@Column(name = "billNumber")
	private String billNumber = "0000000000000000";
	
	@ManyToOne
	@JoinColumn(name = "dateHourFrom", referencedColumnName = "id")
	private DateHour dateHourFrom;
	
	@ManyToOne
	@JoinColumn(name = "dateHourTo", referencedColumnName = "id")
	private DateHour dateHourTo;
	
	@Column(name = "kwhTotal")
	private Double kwhTotal;
	
	@Column(name = "kwhOffPeakTotal")
	private Double kwhOffPeakTotal;
	
	@Column(name = "kwhStandardTotal")
	private Double kwhStandardTotal;
	
	@Column(name = "kwhPeakTotal")
	private Double kwhPeakTotal;
	
	@Column(name = "costKwhTotal")
	private Double costKwhTotal;
	
	@Column(name = "costKwhOffPeakTotal")
	private Double costKwhOffPeakTotal;
	
	@Column(name = "costKwhStandardTotal")
	private Double costKwhStandardTotal;
	
	@Column(name = "costKwhPeakTotal")
	private Double costKwhPeakTotal;

	//-> Getters/Setters
	
	public String getBillNumber() { return billNumber; }
	public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
	
	public DateHour getDateHourFrom() { return dateHourFrom; }
	public void setDateHourFrom(DateHour dateHourFrom) { this.dateHourFrom = dateHourFrom; }
	
	public DateHour getDateHourTo() { return dateHourTo; }
	public void setDateHourTo(DateHour dateHourTo) { this.dateHourTo = dateHourTo; }
	
	public Double getKwhTotal() { return kwhTotal; }
	public void setKwhTotal(Double kwhTotal) { this.kwhTotal = kwhTotal; }
	
	public Double getKwhOffPeakTotal() { return kwhOffPeakTotal; }
	public void setKwhOffPeakTotal(Double kwhOffPeakTotal) { this.kwhOffPeakTotal = kwhOffPeakTotal; }
	
	public Double getKwhStandardTotal() { return kwhStandardTotal; }
	public void setKwhStandardTotal(Double kwhStandardTotal) { this.kwhStandardTotal = kwhStandardTotal; }
	
	public Double getKwhPeakTotal() { return kwhPeakTotal; }
	public void setKwhPeakTotal(Double kwhPeakTotal) { this.kwhPeakTotal = kwhPeakTotal; }
	
	public Double getCostKwhTotal() { return costKwhTotal; }
	public void setCostKwhTotal(Double costKwhTotal) { this.costKwhTotal = costKwhTotal; }
	
	public Double getCostKwhOffPeakTotal() { return costKwhOffPeakTotal; }
	public void setCostKwhOffPeakTotal(Double costKwhOffPeakTotal) { this.costKwhOffPeakTotal = costKwhOffPeakTotal; }
	
	public Double getCostKwhStandardTotal() { return costKwhStandardTotal; }
	public void setCostKwhStandardTotal(Double costKwhStandardTotal) { this.costKwhStandardTotal = costKwhStandardTotal; }
	
	public Double getCostKwhPeakTotal() { return costKwhPeakTotal; }
	public void setCostKwhPeakTotal(Double costKwhPeakTotal) { this.costKwhPeakTotal = costKwhPeakTotal; }
	
	//<-
	
	//-> Constructores
	
	public Bill(MSource mSource, LocalDate dateOfData, String billNumber, DateHour dateHourFrom, DateHour dateHourTo)
	{
		super(mSource, dateOfData);
		
		this.billNumber = billNumber;
		this.dateHourFrom = dateHourFrom;
		this.dateHourTo = dateHourTo;
	}
	
	public Bill(MSource mSource, LocalDate dateOfData, String billNumber)
	{
		this(mSource, dateOfData, billNumber, null, null);
	}
	
	public Bill() {}
	
	//<-

}
