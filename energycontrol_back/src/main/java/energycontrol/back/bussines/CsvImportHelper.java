package energycontrol.back.bussines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import mbt.utilities.BussinesException;
import mbt.utilities.Helper;
import mbt.utilities.PropertiesFileReader;
import mbt.utilities.PropertiesFileReaderFactory;

/**
 * Clase con utilidades para la importación de consumos desde archivos csv
 * 
 */
public class CsvImportHelper
{
	protected String propertiesFilenName;
	protected PropertiesFileReader pfr;
	protected String CSV_ROW_SEPARATOR = ";";
	protected String csvPathFileName;
	protected File csvFile;
	protected BufferedReader csvBufferedReader;
	
	//-> Inicialización y destrucción de instancias
	
	public CsvImportHelper(String csvPathFileName, String propertiesFilenName) throws IOException
	{
		this.csvPathFileName = csvPathFileName;
		this.propertiesFilenName = propertiesFilenName;
		pfr = PropertiesFileReaderFactory.getPropertiesFileReader(this.propertiesFilenName);
	}
	
	public CsvImportHelper(String csvPathFileName) throws IOException
	{
		this(csvPathFileName, "energycontrol-back.properties");
	}
	
	
	public void close()
	{
		this.closeCsv();
		
		if(this.pfr != null)
		{
			this.pfr.dispose();
			this.pfr = null;
		}
	}
	
	//<-
	
	/**
	 * Abre el fichero csv e incializa el buffer de lectura del mismo: csvBufferedReader
	 * 
	 * @throws BussinesException
	 */
	protected void openCsv() throws BussinesException
	{
		if(this.csvFile == null)
		{
			this.csvFile = new File(this.csvPathFileName);
			
			if(this.csvFile.exists())
			{
				try 
				{
					this.csvBufferedReader = new BufferedReader(new FileReader(this.csvFile));
				} 
				catch (FileNotFoundException e) 
				{
					BussinesException be = new BussinesException(
						 String.format("Se ha producido un error al ejecutar %s: %s", this.csvPathFileName, e.getMessage())
						,e
						,EUserMessagesKeys.InternalError.stringValue
						);
						be.addUserMessageArgument("CsvImportHelper.openCsv()");
					
					throw be;
				}
			}
			else
			{
				BussinesException be = new BussinesException(
					 String.format("El fichero %s no existe", this.csvPathFileName)
					,null
					,EUserMessagesKeys.FileNotExists.stringValue
					);
					be.addUserMessageArgument(this.csvPathFileName);
				
				throw be;
			}
		}
	}
	
	/**
	 * Cierra el buffer de lectura y el fichero csv.
	 * 
	 */
	protected void closeCsv()
	{
		if(this.csvBufferedReader != null)
		{
			try 
			{
				this.csvBufferedReader.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(this.csvFile != null)
		{
			this.csvFile  = null;
		}
	}
	
	/**
	 * Lee la siguiente fila del fichero csv.
	 * Si se trata de la primera fila, abre primero el ficharo.
	 * Si ya no hay más filas que leer, cierra el fichero y devuelve null.
	 * 
	 * @return Un String con el contenido de la fila leida
	 * @throws BussinesException
	 */
	public String readNextRowOfCsv() throws BussinesException
	{
		String csvRow = "";
		
		if(this.csvFile == null)
		{
			this.openCsv();
		}
		
		try 
		{
			csvRow = this.csvBufferedReader.readLine();
		} 
		catch (IOException e) 
		{
			BussinesException be = new BussinesException(
				 String.format("Se ha producido un error al ejecutar %s: %s", this.csvPathFileName, e.getMessage())
				,e
				,EUserMessagesKeys.InternalError.stringValue
				);
				be.addUserMessageArgument("CsvImportHelper.readNextRowOfCsv()");
			
			throw be;
		}
		
		if(csvRow == null)
		{
			//Ya no hay más filas que leer, cerramos el buffer de lectura.
			this.closeCsv();
		}
		
		return csvRow;
	}
	
	/**
	 * Busca en el archivo de propiedades el valor la propiedad pasasa por argumento 
	 * y devuelve su valor como un String.
	 * Si no lo encuentra, o su valor es nulo o cadena vacía lanza una BussinesException.
	 * 
	 * @param propertyName
	 * @return
	 * @throws BussinesException
	 */
	public String getValueOfProperty(String propertyName) throws BussinesException
	{
		String sPropValue = this.pfr.getProperty(propertyName);
		
		if(sPropValue == null)
		{		
			BussinesException be = new BussinesException(
					String.format("No se ha encontrado la propiedad %s. en el archivo %s.", propertyName)
					,null
					,EUserMessagesKeys.PropertyNotFound.stringValue
					);
			be.addUserMessageArgument(propertyName);
			be.addUserMessageArgument(this.propertiesFilenName);
			
			throw be;
		}
		else if(Helper.stringIsNullOrEmpty(sPropValue))
		{		
			BussinesException be = new BussinesException(
					String.format("La propiedad %s no tiene valor en el archivo %s.", propertyName)
					,null
					,EUserMessagesKeys.EmptyProperty.stringValue
					);
			be.addUserMessageArgument(propertyName);
			be.addUserMessageArgument(this.propertiesFilenName);
			
			throw be;
		}

		
		return sPropValue;
	}
	
	/**
	 * Busca en el archivo de propiedades el valor la propiedad pasada por argumento 
	 * y deveulve su valor como un entero.
	 * Si no lo encuentra, o su valor es nulo o no se pueden convertir a un entero, 
	 * lanza una BussinesException.
	 * 
	 * @param propertyName
	 * @return
	 * @throws BussinesException
	 */
	public Integer getIntValueOfProperty(String propertyName) throws BussinesException
	{
		int iValue = -1;
		String sPropValue = this.pfr.getProperty(propertyName);
		
		if(Helper.stringIsNullOrEmpty(sPropValue))
		{		
			BussinesException be = new BussinesException(
					String.format("No se ha encontrado la propiedad %s. en el archivo %s", propertyName)
					,null
					,EUserMessagesKeys.PropertyNotFound.stringValue
					);
			be.addUserMessageArgument(propertyName);
			be.addUserMessageArgument(this.propertiesFilenName);
			
			throw be;
		}
		else
		{
			try
			{
				iValue = Integer.parseInt(sPropValue);			
			}
			catch(NumberFormatException e)
			{
				BussinesException be = new BussinesException(
						String.format(
							 "El valor de la propiedad %s, definda en el archivo %s, no es correcto. Se espera un %s."
							,propertyName
							,this.propertiesFilenName
							,"número entero"
							)
						,e
						,EUserMessagesKeys.IncorrectValueOfPropery.stringValue
						);
				be.addUserMessageArgument(propertyName);
				be.addUserMessageArgument(propertyName);
				be.addUserMessageArgument(EUserWordKeys.IntegerNumber.stringValue);
				
				throw be;
			}
		}
		
		return iValue;
	}

	/**
	 * Devuelve true si csvRow es una cadena vacía, o nula, o sólo contiente los caractatres usados como delimitadores de columna
	 * 
	 * @param csvRow Fila de un archivo csv.
	 * @param rowSeparator Caracter o cadena de caracteres usados como delimitadores de columnas en el archivo csv.
	 * @return
	 */
	public boolean isEmptyCsvRow(String csvRow)
	{
		boolean bResult = true;
		
		if(!Helper.stringIsNullOrEmpty(csvRow))
		{
			csvRow = csvRow.trim();
			
			String[] cols = csvRow.split(this.CSV_ROW_SEPARATOR);
			
			for(int i = 0; i < cols.length; i ++)
			{
				if(!Helper.stringIsNullOrEmpty(cols[i]))
				{
					bResult = false;
					break;
				}
			}
		}
		
		return bResult;
	}
	
	
	/**
	 * Extrae el valor una la columna de una fila del archivo csv de los consumos.
	 * 
	 * @param columnName Nombre de la columna donde está el dato a obtener
	 * @param nameOfIndexProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el índice de la columna del dato a obtener.
	 * @param csvRow csrRow Fila del csv donde está el dato a obtener.
	 * @return 
	 * @throws BussinesException
	 */
	public String getValueOfColumn(String columnName, String nameOfIndexProperty, String csvRow) throws BussinesException
	{
		String result = null;
		
		if(this.isEmptyCsvRow(csvRow))
		{
			BussinesException be = new BussinesException(
				 String.format("La fila de la cual se quiere leer la columna %s está vacía.", columnName)
				,EUserMessagesKeys.ReadColumnInEmptyRow.stringValue
				);
			be.addUserMessageArgument(columnName);
			
			throw be;
		}
		
		//Obtener las columnas de la fila:
		String[] cols = csvRow.split(this.CSV_ROW_SEPARATOR);
		
		//Obtener el valor de la columna donde esta el dato:
		Integer indexOfColumn = this.getIntValueOfProperty(nameOfIndexProperty);;	
		if(cols.length < indexOfColumn + 1)
		{
			BussinesException be = new BussinesException(
				String.format("El índice, %d, de la columna %s es mayor que el número de columnas %d.", indexOfColumn, columnName, cols.length)
				,EUserMessagesKeys.IdenxOfColumnOutOfRange.stringValue
				);
			be.addUserMessageArgument(indexOfColumn);
			be.addUserMessageArgument(columnName);
			be.addUserMessageArgument(cols.length);
			
			throw be;
		}
		
		result = cols[indexOfColumn];
		
		if(Helper.stringIsNullOrEmpty(result))
		{
			BussinesException be = new BussinesException(
				 String.format("Columna %s no informada", columnName)
				,EUserMessagesKeys.EmptyColumn.stringValue
				);
			be.addUserMessageArgument(columnName);
								
			throw be;
		}
		
		return result;
	}
	
	/**
	 * Extrae un dato de tipo fecha de la fila csv pasada por argumento.
	 * 
	 * @param columnName Nombre de la columna donde está el dato a obtener
	 * @param nameOfIndexProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el índice de la columna del dato a obtener.
	 * @param nameOfDatePatternProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el patrón de fecha del dato a obtener.
	 * @param csrRow Fila del csv donde está el dato a obtener.
	 * @return El dato parasedo a un LocalDate
	 * @throws BussinesException
	 */
	public LocalDate getDateValueOfColumn(String columnName, String nameOfIndexProperty, String nameOfDatePatternProperty, String csrRow) throws BussinesException
	{
		LocalDate result = null;
		
		String sValue = this.getValueOfColumn(columnName, nameOfIndexProperty, csrRow);
		
		//Convertir a LocalDate
		try 
		{
			String datePattern = this.getValueOfProperty(nameOfDatePatternProperty);	
			result= Helper.string2Date(sValue, datePattern);
		} 
		catch (Exception e) 
		{
			BussinesException be = new BussinesException(
				 String.format("El formato de la columna %s no es correcto", columnName)
				,e
				,EUserMessagesKeys.IncorrectFormatOfColumn.stringValue
				);
			be.addUserMessageArgument(columnName);
			
			throw be;
		}
		
		return result;
	}
	
	/**
	 * Extrae un dato de tipo hora de la fila csv pasada por argumento.
	 * 
	 * @param columnName Nombre de la columna donde está el dato a obtener
	 * @param nameOfIndexProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el índice de la columna del dato a obtener.
	 * @param nameOfHourPatternProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el patrón de hora del dato a obtener.
	 * @param csrRow Fila del csv donde está el dato a obtener.
	 * @return
	 * @throws BussinesException
	 */
	public LocalTime getHourValueOfColumn(String columnName, String nameOfIndexProperty, String nameOfHourPatternProperty, String csrRow) throws BussinesException
	{
		LocalTime result = null;
		
		DateTimeFormatter dtf = null;
		String sValue = this.getValueOfColumn(columnName, nameOfIndexProperty, csrRow);
		String hourPattern = this.getValueOfProperty(nameOfHourPatternProperty);
		
		try
		{
			dtf = DateTimeFormatter.ofPattern(hourPattern);
			
			result = LocalTime.parse(sValue, dtf);
		}
		catch (Exception ee) 
		{
			try
			{
				String[] partsValue = sValue.split(":");
				
				result = LocalTime.of(
					Integer.parseInt(partsValue[0]), 
					Integer.parseInt(partsValue[1])
					);
			}
			catch (Exception eee) 
			{
				BussinesException be = new BussinesException(
					 String.format("El formato de la columna %s no es correcto", columnName)
					,eee
					,EUserMessagesKeys.IncorrectFormatOfColumn.stringValue
					);
				be.addUserMessageArgument(columnName);
				
				throw be;
			}
		}
		
		return result;
	}
	
	/**@throws BussinesException
	 * Extrae un dato de tipo Integer de la fila csv pasada por argumento.
	 * 
	 * @param columnName Nombre de la columna donde está el dato a obtener
	 * @param nameOfIndexProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el índice de la columna del dato a obtener.
	 * @param csrRow Fila del csv donde está el dato a obtener.
	 * @return El dato parasedo a un Integer
	 * @throws BussinesException
	 */
	public Integer getIntValueOfColumn(String columnName, String nameOfIndexProperty, String csrRow) throws BussinesException
	{
		Integer result = null;
		
		String sValue = this.getValueOfColumn(columnName, nameOfIndexProperty, csrRow);
		
		try
		{
			result = Integer.parseInt(sValue);
		}
		catch (NumberFormatException e) 
		{
			BussinesException be = new BussinesException(
				 String.format("El formato de la columna %s no es correcto", columnName)
				,e
				,EUserMessagesKeys.IncorrectFormatOfColumn.stringValue
				);
			be.addUserMessageArgument(columnName);
			
			throw e;
		}
		
		return result;
	}
	
	/**
	 * Extrae un dato de tipo Double de la fila csv pasada por argumento.
	 * 
	 * @param columnName Nombre de la columna donde está el dato a obtener
	 * @param nameOfIndexProperty Nombre de la propiedad del archivo de propiedades en el que está alamcenado el índice de la columna del dato a obtener.
	 * @param csrRow Fila del csv donde está el dato a obtener.
	 * @return El dato parasedo a un Double
	 * @throws BussinesException
	 */
	public Double getDoubleValueOfColumn(String columnName, String nameOfIndexProperty, String csrRow) throws BussinesException
	{
		Double result = null;
		
		String sValue = this.getValueOfColumn(columnName, nameOfIndexProperty, csrRow);
		
		try
		{
			sValue = sValue.replace(",", ".");
			result = Double.parseDouble(sValue);
		}
		catch (NumberFormatException e) 
		{
			BussinesException be = new BussinesException(
				 String.format("El formato de la columna %s no es correcto", columnName)
				,e
				,EUserMessagesKeys.IncorrectFormatOfColumn.stringValue
				);
			be.addUserMessageArgument(columnName);
			
			throw e;
		}
		
		return result;
	}
	
}
