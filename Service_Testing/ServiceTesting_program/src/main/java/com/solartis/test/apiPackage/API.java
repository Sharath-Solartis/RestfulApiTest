package com.solartis.test.apiPackage;

import java.util.LinkedHashMap;
import com.solartis.test.exception.APIException;

public interface API 
{
	public void LoadSampleRequest(LinkedHashMap<String, String> InputData) throws APIException;
	public void PumpDataToRequest(LinkedHashMap<String, String> commonmap,LinkedHashMap<String, String> InputData) throws APIException;
	public void AddHeaders(String Token) throws APIException;
	public void SendAndReceiveData() throws APIException;
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException;
	public LinkedHashMap<String, String> CompareFunction(LinkedHashMap<String, String> output) throws APIException;
	public String RequestToString(String Token) throws APIException;
	public String ResponseToString() throws APIException;	
}
