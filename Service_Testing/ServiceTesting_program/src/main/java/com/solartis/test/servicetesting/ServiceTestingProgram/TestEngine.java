package com.solartis.test.servicetesting.ServiceTestingProgram;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.Dtc.DtcPreviewPDF;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.PropertiesHandleException;
import com.solartis.test.listener.FireEventAPI;
import com.solartis.test.listener.Listener;
import com.solartis.test.listener.LogListener;
import com.solartis.test.util.common.DatabaseOperation;

public class TestEngine 
{
	static API api=null;
	static FireEventAPI fireEventAPI;
	
	public  void ServiceTest( String[] args ) throws DatabaseException, PropertiesHandleException, APIException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {   
		System.setProperty("jsse.enableSNIExtension", "false");

		PropertiesHandle config = new PropertiesHandle(args[0], args[1],args[2], args[3], args[4], args[5], args[6], args[7], args[8],args[9]);

		DatabaseOperation.ConnectionSetup(config);
		
		String actualchoice = config.getProperty("actual");
		String statuschoice = config.getProperty("status");
		String outputtable = config.getProperty("output_in_same_table");
		String classname = config.getProperty("ClassName");
		
		DatabaseOperation input = new DatabaseOperation();
		
		input.GetDataObjects(config.getProperty("input_query"));
		
		DatabaseOperation output = new DatabaseOperation();
		
		output.GetDataObjects(config.getProperty("output_query"));
		
		try 
		{
			System.out.println("com.solartis.test.apiPackage."+classname);
			try{
				Class<?> cl = Class.forName("com.solartis.test.apiPackage."+classname);
				System.out.println("com.solartis.test.apiPackage."+classname);
				Constructor<?> cons = cl.getConstructor(com.solartis.test.Configuration.PropertiesHandle.class);
			    api = (API) cons.newInstance(config);
				//api=new DtcPreviewPDF(config);
			    fireEventAPI = new FireEventAPI(api);
			}
			catch(Exception e)
			{
				System.out.println("Exception occured");
				e.printStackTrace();
			}
			
			
		    Listener listener = new LogListener();
		    fireEventAPI.addListener(listener);
		    
		} 
		catch (SecurityException | IllegalArgumentException e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("Error in Selecting API");
		}
	    

			try 
			{

				do
				{
					System.out.println("TestData : " + input.ReadData("S.No"));  	
							if(input.ReadData("Flag_for_execution").equals("Y"))
							{
							   // System.out.println("TestData" + input.ReadData("S.No") + "flag_for_execution = Y" );					 
								
							    if(fireEventAPI==null)
							    {
							    	System.out.println("API is null");
							    }
							    fireEventAPI.LoadSampleRequest(input);//LOADING SAMPLE REQUEST
                                
							    fireEventAPI.PumpDataToRequest();//PUMPING TESTDATA TO SAMPLEREQUEST 
							    
							    fireEventAPI.RequestToString();//SHOWING REQUEST IN LOG 
							
							    fireEventAPI.AddHeaders();//ADDING HEADER || TOKENS || EVENTS FOR HITTING REQUEST
								
							    fireEventAPI.SendAndReceiveData();//RECIEVING AND STORING RESPONSE TO THE FILE
								

							    fireEventAPI.ResponseToString();//SHOWING RESPONSE IN LOG 											

								
								if(actualchoice.equals("Y"))
								{
									  
									if(outputtable.equals("Y"))//INPUT AND OUT DB TABLE ARE SAME
									{	
								    	input = fireEventAPI.SendResponseDataToFile(input);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
									
								    	input.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA
									}
									else//INPUT AND OUT DB TABLE ARE DIFFERENT
									{	
										output = fireEventAPI.SendResponseDataToFile(output);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
									
										output.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA	
									}
								} 
								
								if(statuschoice.equals("Y"))
								{
									if(outputtable.equals("Y"))
									{
										 input = fireEventAPI.CompareFunction(input);//CALLING COMPARING FUNCTION
									     
										 input.UpdateRow();
									}
									else
									{
										output = fireEventAPI.CompareFunction(output);//CALLING COMPARING FUNCTION
									    
										output.UpdateRow();
									}
								} 
								
								input.WriteData("Flag_for_execution", "Completed");
								input.UpdateRow();//UPDATE DB TABLE ROWS AFTER COMPARSION
								}
							else
							{
								System.out.println("TestData" + input.ReadData("S.No") + "---flag_for_execution N");
							}
					
					if(actualchoice.equals("Y") || statuschoice.equals("Y"))
					{
						output.MoveForward();
					}
				}while(input.MoveForward());
			} 
			catch (APIException e1)
			{
				e1.getCause().getMessage();
				System.exit(0);	

			}

		DatabaseOperation.CloseConn();
   }
	

}	
