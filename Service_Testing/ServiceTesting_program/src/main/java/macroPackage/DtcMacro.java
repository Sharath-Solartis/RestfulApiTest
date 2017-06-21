package macroPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.read.biff.BiffException;
import util.api.DBColoumnVerify;
import util.common.DatabaseOperation;
import util.common.ExcelOperationsPOI;
import Configuration.PropertiesHandle;

public class DtcMacro extends DBColoumnVerify implements MacroInterface
{
	protected ExcelOperationsPOI sampleexcel=null;
	protected String Targetpath;
	protected String Samplepath;
	protected int numofplans;
	protected DtcMacro trans;
	protected DatabaseOperation configTable = null;
	protected PropertiesHandle configFile;
	protected LinkedHashMap<Integer,String> planname;
	protected LinkedHashMap<Integer,String> planpath;
	
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
	
	public DtcMacro(PropertiesHandle configFile) throws SQLException
	{
		super(" ");
		configTable = new DatabaseOperation();
		configTable.GetDataObjects(configFile.getProperty("config_query"));
		
	}
	@Override
	public void LoadSampleRatingmodel(PropertiesHandle configFile,DatabaseOperation inputData) throws SQLException 
	{
		// TODO Auto-generated method stub
		String RateingModelName = Lookup(inputData.ReadData("State_name"),configFile);
		System.out.println(inputData.ReadData("State_name"));
		Samplepath= configFile.getProperty("Samplepath")+RateingModelName+".xls";
		sampleexcel= new ExcelOperationsPOI(Samplepath);
	}

	@Override
	public void GenerateExpected(DatabaseOperation inputData,PropertiesHandle configFile) throws SQLException, BiffException,IOException 
	{
		// TODO Auto-generated method stub
		String[] plans=inputData.ReadData("Plan_name").split(";");
		numofplans = plans.length;
		planname=new LinkedHashMap<Integer,String>();
		planpath=new LinkedHashMap<Integer,String>();

		for (int j=0;j<numofplans;j++)
		{
			planname.put(j, plans[j]);
		}
		for(int i=0;i<numofplans;i++)
		{
		Targetpath =  configFile.getProperty("TargetPath")+inputData.ReadData("testdata")+planname.get(i)+".xls";
		sampleexcel.Copy(Samplepath, Targetpath);
		
		sampleexcel.save();
	
		
		System.out.println("generate expected rating over");
		planpath.put(i, Targetpath);
		}
	}

	@Override
	public void PumpinData(DatabaseOperation inputData,	PropertiesHandle configFile) throws NumberFormatException,BiffException, SQLException, IOException, ParseException 
	{
		// TODO Auto-generated method stub
		for(int i=0;i<numofplans;i++)
		{
			configTable.GetDataObjects(configFile.getProperty("config_query"));
			ExcelOperationsPOI excel=new ExcelOperationsPOI(planpath.get(i));
			//System.out.println(planname.get(i));
			
			//inputData.UpdateRow();
			//System.out.println(inputData.ReadData("Plan_name"));
			trans= new DtcMacro(configFile);
			do
			{	
				String condition = configTable.ReadData("Condition");
				String tempdata = inputData.ReadData("Plan_name");
				inputData.WriteData("Plan_name", planname.get(i));
				inputData.UpdateRow();
				if (configTable.ReadData("flag_for_execution").equalsIgnoreCase("Y")&&ConditionReading(condition,inputData))
				{
					//System.out.println(inputData.ReadData("Plan_name"));
					inputData.WriteData("Plan_name", tempdata);
					inputData.UpdateRow();
					//System.out.println(inputData.ReadData("Plan_name"));
					if (configTable.ReadData("Type").equals("input"))
					{
						
						String CellAddress = configTable.ReadData("Cell_Address");
						String Datacolumntowrite = configTable.ReadData("Input_DB_column");
						String  Datatowrite = null;
						if (Datacolumntowrite.equals("Plan_name"))
						{
							Datatowrite=planname.get(i);
						}
						else
						{
						Datatowrite = inputData.ReadData(Datacolumntowrite);
						}
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
							else
							{
								excel.write_data(rowNum-1, columnNum, Datatowrite);
							}
						}
					}
				}
				else
				{
					inputData.WriteData("Plan_name", tempdata);
					inputData.UpdateRow();
				}
			}while(configTable.MoveForward());
			excel.refresh();
			excel.save();	
		}
	}

	@Override
	public void PumpoutData(DatabaseOperation outputData,DatabaseOperation inputData,PropertiesHandle configFile) throws NumberFormatException,SQLException, ParseException 
	{
		// TODO Auto-generated method stub
		for(int i=0;i<numofplans;i++)
		{
		ExcelOperationsPOI excel=new ExcelOperationsPOI(planpath.get(i));
		configTable.GetDataObjects(configFile.getProperty("config_query"));
		excel.refresh();
		do
		{
			String condition = configTable.ReadData("Condition");
			String tempdata = inputData.ReadData("Plan_name");
			inputData.WriteData("Plan_name", planname.get(i));
			inputData.UpdateRow();
			if (configTable.ReadData("flag_for_execution").equals("Y")&&ConditionReading(condition,inputData))
			{
				//System.out.println(inputData.ReadData("Plan_name"));
				inputData.WriteData("Plan_name", tempdata);
				inputData.UpdateRow();
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
			else
			{
				inputData.WriteData("Plan_name", tempdata);
				inputData.UpdateRow();
			}
			outputData.UpdateRow();
		}while(configTable.MoveForward());
		excel.save();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T Translation1(String Datatowrite, DatabaseOperation configTable,  PropertiesHandle configFile) throws SQLException, ParseException
	{
		T outputdata = null;
		switch(configTable.ReadData("Translation_Function"))
		{
		case "Date": 
			Date DateData = Date(Datatowrite,"mm/dd/yyyy",configTable.ReadData("Translation_Format"));
			outputdata = (T) DateData;
			break;
		case "Lookup":
			String LookupData = Lookup(Datatowrite, configFile);
			outputdata = (T) LookupData;
			break;
		}
			return outputdata;
			
	}
	
	protected  Date Date(String Date,String InputFormat,String ExpectedFormat) throws ParseException
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
		
		catch (NumberFormatException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Date1;
		
	}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	protected String  Lookup(String Lookup1, PropertiesHandle configFile) throws SQLException
	{
		DatabaseOperation Lookup = new DatabaseOperation();
		Lookup.GetDataObjects(configFile.getProperty("lookup_query"));
		HashMap<String,String> LookupMap = new HashMap<String,String>();
		do
		{
			String LookupData=Lookup.ReadData("LookupData");
			String LookupValue=Lookup.ReadData("LookupValue");
			LookupMap.put(LookupData, LookupValue);
		}while(Lookup.MoveForward());
		if (LookupMap.get(Lookup1)==null)
		{
			return "Other";
		}
		else
		{
			return LookupMap.get(Lookup1);
		}
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
	
	/*public static void main(String args[]) throws Exception, SQLException
	{
		System.setProperty("jsse.enableSNIExtension", "false");	
		configFile = new PropertiesHandle("A:/1 Projects/08 DTC/Release16/RatingEnhancement - Copy/configuration_file/config_exp_json.properties");
	
		DatabaseOperation inputData = new DatabaseOperation();
		DatabaseOperation outputData = new DatabaseOperation();
		DatabaseOperation.ConnectionSetup(configFile);
		inputData.GetDataObjects(configFile.getProperty("input_query"));
		outputData.GetDataObjects(configFile.getProperty("output_query"));
		DtcMacro rating = new DtcMacro();
		do
		{			
			if (inputData.ReadData("Flag_for_execution").equals("Y"))
			{
				rating.LoadSampleRatingmodel(configFile, inputData);
				rating.GenerateExpected(inputData,configFile);
				rating.PumpinData(inputData,configFile);
				rating.PumpoutData(configTable,outputData,inputData);
			}
			else
			{
				//inputData.move_next();
			}
			
		}while (inputData.MoveForward()&& outputData.MoveForward());
		
		DatabaseOperation.CloseConn();
	}*/
}
