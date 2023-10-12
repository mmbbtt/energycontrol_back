package energycontrol.back.entities;

import java.time.LocalDate;

import energycontrol.back.bussines.EntitiesHelper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Clase para registrar los orígenes de los consumos:
 * Una factura concreta, una exportación de datos de la compañía distribuidora, ...
 */
@Entity
@Table(name = "Source")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) 
public class Source 
{
	@Id
	@Column(name = "id")
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "msourceId")
	private MSource mSource;
	
	@Column(name = "dateOfData")
	private LocalDate dateOfData;
	
	/**
	 * Universal Supply Point Code
	 */
	@Column(name = "cups")
	private String cups;
	
	public String getId() { return id; }
	public void setId(String sourceId) { this.id = sourceId; }
	
	public MSource getMSource() { return mSource; }
	public void setMSource(MSource msource) { this.mSource = msource; }
	
	public String getCode() {return this.mSource.getCode(); }
	
	public String getDescription() {return this.mSource.getDescription(); }
	
	public LocalDate getDateOfData() { return dateOfData; }
	public void setDateOfData(LocalDate dateOfData) { this.dateOfData = dateOfData; }
	
	public String getCups() { return cups; }
	public void setCups(String cups) { this.cups = cups; }
	
	
	//-> Constructores
	
	public Source(MSource msource, LocalDate dateOfData) 
	{
		this.setMSource(msource);
		this.dateOfData = dateOfData;
		
		this.id = EntitiesHelper.generateIdSource(this);
	}
	
	public Source(MSource msource) 
	{
		this(msource, null);
	}
	
	public Source() {}
	
	//<-
	
	public String toString()
	{
		return String.format("%s - %s - %s", 
			this.mSource != null ? this.mSource.getCode() : "?",
			this.mSource != null ? this.mSource.getDescription() : "?",
			this.dateOfData != null ? this.dateOfData.toString() : "?"				
			);
	}
}
