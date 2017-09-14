package com.solartis.test.apiPackage;

import com.solartis.test.exception.APIException;
import com.solartis.test.util.common.*;

public interface API 
{
	public void LoadSampleRequest(DatabaseOperation InputData) throws APIException;
	public void PumpDataToRequest() throws APIException;
	public void AddHeaders() throws APIException;
	public void SendAndReceiveData() throws APIException;
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException;
	public DatabaseOperation CompareFunction(DatabaseOperation output) throws APIException;
	public String RequestToString() throws APIException;
	public String ResponseToString() throws APIException;
	
}
