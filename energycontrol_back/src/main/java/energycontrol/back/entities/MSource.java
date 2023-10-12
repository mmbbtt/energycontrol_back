package energycontrol.back.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maestro de orígenes de los consumos: Factura, Compañía distribuidora, Medidor de consumo propio, ...
 */
@Entity
@Table(name = "MSource")
public class MSource 
{
	@Id
	@Column(name = "code")
	private String code;
	
	@Column(name = "description")
	private String description;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private ESourceType type = ESourceType.Other;
	
	public String getCode() {return code;}
	public void setCode(String code) {this.code = code;}
	
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	
	public ESourceType getType() { return type; }
	public void setType(ESourceType type) { this.type = type; }
	
	public MSource() {}
	public MSource(ESourceType type, String code, String description) 
	{
		this.type = type;
		this.code = code;
		this.description = description;
	}
	
	@Override
	public String toString() 
	{
	    return String.format("%s - %s, del tipo %s", this.code, this.description, this.type);
	}
}
