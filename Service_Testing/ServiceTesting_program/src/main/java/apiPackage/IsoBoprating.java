package apiPackage;

import java.sql.SQLException;

import util.api.DBColoumnVerify;
import util.common.DatabaseOperation;
import util.common.PropertiesHandle;

public class IsoBoprating extends BaseClass implements API 
{
	public IsoBoprating(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new DatabaseOperation();
		jsonElements.GetDataObjects(config.getProperty("json_query"));
		
		actualColumnCol = config.getProperty("actual_column").split(";");
		inputColumnCol = config.getProperty("input_column").split(";");
		actualColumnSize = actualColumnCol.length;
		inputColumnSize = inputColumnCol.length;
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
		
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
		OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
	}
}