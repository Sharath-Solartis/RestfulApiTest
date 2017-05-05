package util.api;
import java.sql.SQLException;
import util.common.DatabaseOperation;

public class ConditionsChecking 
{	
	public boolean condition_reading(String condition,DatabaseOperation input) throws SQLException
	{
		boolean condition_reading=false;
		
			if(condition.equals(""))
			{
				condition_reading=true;
				return condition_reading;
			}
			else
			{
				String[] splits=condition.split(";");
				int length=splits.length;
				
				for(int i=0;i<length;i++)
					{
						condition_reading=false;
						String[] cond_value = new String[10];
						String operator = null;
						 
							if(splits[i].contains("="))
							{
								cond_value=splits[i].split("=");
								operator = "=";
							}
							else if(splits[i].contains("<>"))
							{
								cond_value=splits[i].split("<>");
								operator = "<>";
							}
							
						String cond=cond_value[0];
						String value=cond_value[1];
						String[] individualValue = value.split("\\|");
			
							for(int j=0;j<individualValue.length;j++)
							{
								switch(operator)
								{
								case "=": if((input.ReadData(cond).equals(individualValue[j])))
										   {
									 			condition_reading=true;
											}
											break;
								case "<>": if((input.ReadData(cond).equals(individualValue[j])))
											{
						 						condition_reading=false;
						 						return condition_reading;
											}
											else
											{
												condition_reading=true;
											}
											break;
								}
							}
							
						if(!condition_reading)
						{
							return condition_reading;
						}
					}	
			}
		
	return condition_reading;
		
	}
}
