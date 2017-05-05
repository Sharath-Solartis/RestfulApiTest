package util.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;

public class HttpHandle 
{
	private URL url;
	private HttpURLConnection conn;
	
	public HttpHandle(String url,String type) throws IOException
	{
		
			this.url = new URL(url);
			conn = (HttpURLConnection) this.url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(type);
		
	}
	
	public void AddHeader(String attr,String value)
	{
		conn.setRequestProperty(attr, value);
		
	}
	
	public void SendData(String data) throws IOException
	{
		OutputStream os;
		
			os = conn.getOutputStream();
			os.write(data.getBytes());
			os.flush();
		
		
	}
	
	public String ReceiveData() throws Exception 
	{
		String output,output2 = null;
		BufferedReader br;
		
			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
					
			while ((output = br.readLine()) != null) 
			{
				if(output2 == null)
				{
					output2 = output;
				}
				else
				{
					output2 += output;
				}
			}
		
		
		if (output2 == null)
		{
			throw new Exception("Send request once again");
		}
		
		//System.out.println("Output from Server .... \n");
		
		return output2;
	}
	
}
