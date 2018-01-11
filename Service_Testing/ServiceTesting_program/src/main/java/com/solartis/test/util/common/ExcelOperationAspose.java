package com.solartis.test.util.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import com.aspose.cells.Cell;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;

public class ExcelOperationAspose implements ExcelOperationInterface
{
	protected String path = null;
	protected Workbook workbook = null;
	protected Worksheet worksheet=null;
	protected String sheet_name = null;
	protected FileInputStream inputfilestream;
	protected Cell cell=null;
	protected int row_number;
	protected int column_number;
	
	public ExcelOperationAspose(String path) throws Exception
	{
		this.path=path;
		LoadOptions opt = new LoadOptions();
		opt.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		workbook = new Workbook(path, opt);	
	}
	
	public void openWorkbook() throws Exception 
	{
		LoadOptions opt = new LoadOptions();
		opt.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		workbook = new Workbook(path, opt);		
	}

	public void getsheets(String sheet_name) 
	{
		this.sheet_name=sheet_name;
		worksheet = workbook.getWorksheets().get(sheet_name);
	}

	@SuppressWarnings("deprecation")
	public void getcell(int row_number, int column_number) 
	{
		this.row_number=row_number;
		this.column_number=column_number;
		cell = worksheet.getCells().getCell(row_number, column_number);
	}

	public String read_data() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	public String read_data(int rowNumber, int columnNumber) 
	{
		String cellvalue=null;
		cell = worksheet.getCells().getCell(rowNumber, columnNumber);
		
		switch(cell.getType())
		{
		case com.aspose.cells.CellValueType.IS_BOOL:
			cellvalue="Err-Boolean";
		case com.aspose.cells.CellValueType.IS_DATE_TIME:
			cellvalue=cell.getDateTimeValue().toString();
		case com.aspose.cells.CellValueType.IS_ERROR:
			cellvalue="Err";
		case com.aspose.cells.CellValueType.IS_NUMERIC:
			cellvalue=String.valueOf(cell.getIntValue());
		case com.aspose.cells.CellValueType.IS_STRING:
			cellvalue=cell.getDisplayStringValue();
		case com.aspose.cells.CellValueType.IS_UNKNOWN:
			cellvalue="Err-Unknown";
		}
		return cellvalue;
	}

	public int get_rownumber() 
	{
		return cell.getRow();
	}

	public int get_columnnumber() 
	{
		return cell.getColumn();
	}

	public void set_rownumber(int row_number) 
	{
	
	}

	public void set_columnnumber(int column_number) 
	{
		// TODO Auto-generated method stub
		
	}

	public void next_row() 
	{
		// TODO Auto-generated method stub
		
	}

	public void next_col() 
	{
		// TODO Auto-generated method stub
		
	}

	public boolean has_next_row() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean has_next_row(int columnnumberr) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean has_next_column() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void write_data(String strData) 
	{
		// TODO Auto-generated method stub
		
	}

	public void write_data(int strData) 
	{
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	public void write_data(int rownum, int columnnum, Object strData) 
	{
		cell = worksheet.getCells().getCell(rownum, columnnum); 
		cell.setValue(strData);
	}

	public void refresh() 
	{
		workbook.calculateFormula(true);
	}

	public void save() throws Exception 
	{
		workbook.save(path);
	}

	public void saveAs(String Targetexpectedpath) throws Exception 
	{
		workbook.save(Targetexpectedpath);
	}

	@SuppressWarnings("resource")
	public void Copy(String Sampleexcelpath, String Targetexpectedpath) 
	{
		FileChannel source = null;
		FileChannel destination = null;

		try 
		{

			source = new FileInputStream(Sampleexcelpath).getChannel();

			destination = new FileOutputStream(Targetexpectedpath).getChannel();

			if (destination != null && source != null) 
			{
				destination.transferFrom(source, 0, source.size());
			}

		}
		
		catch (FileNotFoundException e) 
		{
			//throw new POIException("ERROR OCCURS WHILE COPYING THE WORKBOOK -- FILENOTFOUND", e);
		} 
		catch (IOException e)
		{
			//throw new POIException("ERROR OCCURS WHILE COPYING THE WORKBOOK -- I/O OPERATION FAILED", e);
		
		}
		finally 
		{
			try 
			{
				if (source != null) 
				{					
					source.close();					
				}
				if (destination != null) 
				{					
					destination.close();					
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public static void main (String args[]) throws Exception
	{
		ExcelOperationAspose excel = new ExcelOperationAspose("D:\\ftl\\sample\\STARR GL Rating Calculator_FebLCV.xlsx");
		excel.openWorkbook();
		excel.getsheets("Cov Calculation");
		System.out.println(excel.read_data(4, 4)+excel.read_data(3, 3));
		excel.refresh();
		excel.save();
	}
}
