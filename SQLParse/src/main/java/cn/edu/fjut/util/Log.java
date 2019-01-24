package cn.edu.fjut.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {

	public static void main(String[] args) {
		Log log = new Log();
		
	}
	
	
	File file ;
	public Log(String sfile)
	{
		file = new File(sfile);
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Log()
	{
		this("./log.txt");
	}
	
	public void log(String content)
	{
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter( file, true));
			bw.write(content + "\r\n");
			bw.close();			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
