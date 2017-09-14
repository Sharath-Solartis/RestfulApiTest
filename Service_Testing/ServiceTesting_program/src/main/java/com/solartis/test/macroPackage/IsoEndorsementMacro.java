package com.solartis.test.macroPackage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.MacroException;
import com.solartis.test.exception.POIException;
import com.solartis.test.util.common.DatabaseOperation;
import com.solartis.test.util.common.ExcelOperationsPOI;

public class IsoEndorsementMacro implements MacroInterface
{
		protected ExcelOperationsPOI sampleexcel=null;
		protected String Targetpath;
		protected IsoEndorsementMacro trans;
		protected String Samplepath;
		protected DatabaseOperation configTable = null;
		protected PropertiesHandle configFile;
		
		
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		public enum Alphabet 
		{
		    A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AQ,AR,AS,AT,AU,AV,AW,AX,AY,AZ;

		    public static int getNum(String targ) 
		    {
		        return valueOf(targ).ordinal();
		    }

		    public static int getNum(char targ)
		    {
		        return valueOf(String.valueOf(targ)).ordinal();
		    }
		}
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
		public IsoEndorsementMacro(PropertiesHandle configFile) throws MacroException
		{
			configTable = new DatabaseOperation();
			//configFile = new PropertiesHandle("A:/1 Projects/09 ISO/Release_24_UAT/RatingTrial/configuration_file/config_json.properties");
			System.out.println(configFile.getProperty("config_query"));
			try 
			{
				configTable.GetDataObjects(configFile.getProperty("config_query"));
			} 
			catch (DatabaseException e) 
			{
				throw new MacroException("ERROR OCCUR WHILE READING HT IN ISO ENDORSEMENT MACRO", e);
			}
			
		}
		public void LoadSampleRatingmodel(PropertiesHandle configFile,DatabaseOperation inputData) throws MacroException
		{
			try
			{
				String RateingModelName = Lookup(inputData.ReadData("RatingModel_version"),configFile);
				
				Samplepath= configFile.getProperty("Samplepath")+RateingModelName+".xls";
				System.out.println(inputData.ReadData("RatingModel_version")+"----------"+Samplepath);
				sampleexcel= new ExcelOperationsPOI(Samplepath);
			}
			catch (DatabaseException | POIException e)
			{
				throw new MacroException("ERROR OCCURS WHILE LOADING SAMPLE RATING MODEL ISO ENDORSEMENTMACRO", e);
			}
			
		}
		
		public void GenerateExpected(DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException
		{
			try
			{
				Targetpath =  configFile.getProperty("TargetPath")+inputData.ReadData("testdata")+".xls";
				sampleexcel.Copy(Samplepath, Targetpath);
				sampleexcel.save();
				System.out.println("generate expected rating over");
			}
			catch(DatabaseException | POIException e)
			{
				throw new MacroException("ERROR OCCURS WHILE GENERATING THE EXPECTED RATING MODEL", e);
			}
		}
		
		public void PumpinData(DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException 
		{
			try
			{
			//DatabaseOperation configTable = new DatabaseOperation();
			configTable.GetDataObjects(configFile.getProperty("config_query"));
			ExcelOperationsPOI excel=new ExcelOperationsPOI(Targetpath);
			trans= new IsoEndorsementMacro(configFile);
			do
			{								
				if (configTable.ReadData("flag_for_execution").equalsIgnoreCase("Y"))
				{
					if (configTable.ReadData("Type").equals("input"))
					{
						String Datacolumntowrite = configTable.ReadData("Input_DB_column");
						String CellAddress = configTable.ReadData("Cell_Address");
						
						String  Datatowrite = inputData.ReadData(Datacolumntowrite);
						String[] part = CellAddress.split("(?<=\\D)(?=\\d)");
						int columnNum=Alphabet.getNum(part[0].toUpperCase());
						int rowNum = Integer.parseInt(part[1]);
						System.out.println(columnNum+"----"+rowNum+"-----"+configTable.ReadData("Sheet_Name")+"-----"+Datatowrite);
						excel.getsheets(configTable.ReadData("Sheet_Name"));
						excel.getcell(rowNum, columnNum);
						
						if(configTable.ReadData("Translation_Flag").equals("Y"))
						{
							excel.write_data(rowNum-1, columnNum, trans.Translation1(Datatowrite, configTable, configFile));
						}
						else
						{
							if(trans.isInteger(Datatowrite))
							{
								int datadata =Integer.parseInt(Datatowrite);
								excel.write_data(rowNum-1, columnNum, datadata);	
							}
							else if(trans.isFloat(Datatowrite))
							{
								float floatdata = Float.parseFloat(Datatowrite);
								excel.write_data(rowNum-1, columnNum, floatdata);
							}
							else
							{
								excel.write_data(rowNum-1, columnNum, Datatowrite);
							}
						}
					}
				}
			}while(configTable.MoveForward());
			excel.refresh();
			excel.save();	
		}
		catch(DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS WHILE PUMP-IN THE DATA TO RATING MODEL OF ISO ENDORSEMENTMACRO", e);
		}
		catch(POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE OPENING AND CLOSING THE RATING MODEL OF ISO ENDORSEMENTMACRO", e);
		}
		}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		public void PumpoutData(DatabaseOperation outputData,DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException
		{
			try
			{
			ExcelOperationsPOI excel=new ExcelOperationsPOI(Targetpath);
			configTable.GetDataObjects(configFile.getProperty("config_query"));
			excel.refresh();
			do
			{
				
				if (configTable.ReadData("flag_for_execution").equals("Y"))
				{
					if (configTable.ReadData("Type").equals("output"))
					{
						String Datacolumntowrite = configTable.ReadData("Input_DB_column");
						String CellAddress = configTable.ReadData("Cell_Address");
						String[] part = CellAddress.split("(?<=\\D)(?=\\d)");
						int columnNum=Alphabet.getNum(part[0].toUpperCase());
						int rowNum = Integer.parseInt(part[1]);
						excel.getsheets(configTable.ReadData("Sheet_Name"));
						excel.getcell(rowNum-1, columnNum);
						String Datatowrite = excel.read_data(rowNum-1, columnNum);
						System.out.println(Datacolumntowrite+"----------" +Datatowrite+"--------"+rowNum+"-------"+columnNum);
						outputData.WriteData(Datacolumntowrite, Datatowrite);
						//outputData.WriteData(Datacolumntowrite, "poda");
					}
				}
				outputData.UpdateRow();
			}while(configTable.MoveForward());
			excel.save();
		}
		catch(DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS WHILE PUMPOUT THE OUTPUT FROM RATING MODEL OF ISO ENDORSEMENTMACRO", e);
		}
		catch (POIException e)
		{
			throw new MacroException("ERROR OCCURS 	WHILE OPENING/CLOSING THE RATING MODEL OF ISO ENDORSEMENTMACRO", e);
		}
		}
		
		@SuppressWarnings("unchecked")
		protected <T> T Translation1(String Datatowrite, DatabaseOperation configTable,  PropertiesHandle configFile) throws MacroException 
		{
			T outputdata = null;
			try
			{
			switch(configTable.ReadData("Translation_Function"))
			{
			case "Date": 
				Date DateData = Date(Datatowrite,"yyyy-mm-dd",configTable.ReadData("Translation_Format"));
				outputdata = (T) DateData;
				break;
			case "Lookup":
				String LookupData = Lookup(Datatowrite, configFile);
				outputdata = (T) LookupData;
				break;
			case "ISOBOPWindhail":
				Integer ISOBOPWindhail = ISOBOPWindhail(Datatowrite);
				outputdata =  (T) ISOBOPWindhail;
				break;
			}
		}
		catch (DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS 	IN TRANSLATION OF ISOENDORSEMENTMACRO", e);
		}
			return outputdata;
			
		}
		
		
		protected  Date Date(String Date,String InputFormat,String ExpectedFormat) throws MacroException
		{
			String value ="";
			Date Date1=null;
			try 
			{
				Pattern p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(InputFormat);
				String InputDelimiter="";
				if(m.find())
				{
					InputDelimiter=String.valueOf(InputFormat.charAt(m.start()));
				}
				
				Matcher m1 = p.matcher(ExpectedFormat);
				String ExpectedDelimiter="";
				if(m1.find())
				{
					ExpectedDelimiter=String.valueOf(ExpectedFormat.charAt(m1.start()));
				}
				
				String[] DateInputFormat = InputFormat.split(InputDelimiter);
				String[] DateOutputFormat = ExpectedFormat.split(ExpectedDelimiter);
				String[] date = Date.split(InputDelimiter); //yyyy-mm-dd
				
				HashMap<String,String> DateMaping = new HashMap<String,String>();
				DateMaping.put(DateInputFormat[0].toLowerCase(), date[0]);
				DateMaping.put(DateInputFormat[1].toLowerCase(), date[1]);
				DateMaping.put(DateInputFormat[2].toLowerCase(), date[2]);
				value =  DateMaping.get(DateOutputFormat[0].toLowerCase())+ExpectedDelimiter+DateMaping.get(DateOutputFormat[1].toLowerCase())+ExpectedDelimiter+DateMaping.get(DateOutputFormat[2].toLowerCase());     
				DateFormat format = new SimpleDateFormat(ExpectedFormat, Locale.ENGLISH);
				Date1=format.parse(value);  			
			   // System.out.println(value+"\t"+Date1);  						
			}
			
			catch (NumberFormatException | ParseException e) 
			{
				throw new MacroException("ERROR OCCURS DATE TRANSLATION OF ISOENDORSEMENTMACRO", e);
			}
			return Date1;
			
		}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
		protected String  Lookup(String Lookup1, PropertiesHandle configFile) throws MacroException
		{
			DatabaseOperation Lookup = new DatabaseOperation();
			try 
			{
				Lookup.GetDataObjects(configFile.getProperty("lookup_query"));
			} 
			catch (DatabaseException e) 
			{
				throw new MacroException("ERROR OCCURS INITILIZE THE OBJECT OF ISOENDORSEMENTMACRO", e);
			}
			HashMap<String,String> LookupMap = new HashMap<String,String>();
			try {
				do
				{
					try 
					{
						LookupMap.put(Lookup.ReadData("LookupData"), Lookup.ReadData("LookupValue"));
					} catch (DatabaseException e) 
					{
						// TODO Auto-generated catch block
						throw new MacroException("ERROR OCCURS 	IN LOOKUP QUERY OF ISOENDORSEMENTMACRO", e);
					}
				}while(Lookup.MoveForward());
			} catch (DatabaseException e) 
			{
				// TODO Auto-generated catch block
				throw new MacroException("ERROR OCCURS 	IN LOOKUP TABLE OF ISOENDORSEMENTMACRO", e);
			}
		
			if (LookupMap.get(Lookup1)==null)
			{
				return "Other";
			}
			else
			{
				return LookupMap.get(Lookup1);
			}
		}			
		
		protected int ISOBOPWindhail(String Data)
		{
			int percentageData=0;
			if(Data.equals("Not Applicable"))
			{
				percentageData = 0;
			}
			else
			{
				String[] windhail = Data.split("%");
				percentageData = Integer.parseInt(windhail[0])/100;
			}
			return percentageData;		
		}
		
		protected boolean isInteger(String s) 
		{
		    try 
		    { 
		    	
		        Integer.parseInt(s); 
		    } 
		    catch(NumberFormatException e) 
		    { 
		        return false; 
		    } 
		    catch(NullPointerException e) 
		    {
		        return false;
		    }
		    // only got here if we didn't return false
		    return true;
		}
		protected boolean isFloat(String s)
		{
			try
			{
				Float.parseFloat(s);
			}
			catch(NumberFormatException e) 
		    { 
		        return false; 
		    } 
		    catch(NullPointerException e) 
		    {
		        return false;
		    }
			 return true;
		}
}
