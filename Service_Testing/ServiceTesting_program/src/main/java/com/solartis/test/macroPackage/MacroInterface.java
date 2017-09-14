package com.solartis.test.macroPackage;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.MacroException;
import com.solartis.test.exception.POIException;
import com.solartis.test.util.common.DatabaseOperation;

public interface MacroInterface 
{
	public void LoadSampleRatingmodel(PropertiesHandle configFile,DatabaseOperation inputData) throws MacroException;
	public void GenerateExpected(DatabaseOperation inputData,PropertiesHandle configFile) throws MacroException;
	public void PumpinData(DatabaseOperation inputData,PropertiesHandle configFile) throws DatabaseException, POIException, MacroException;
	public void PumpoutData(DatabaseOperation outputData,DatabaseOperation inputData,PropertiesHandle configFile) throws POIException, MacroException, DatabaseException;
	
}
