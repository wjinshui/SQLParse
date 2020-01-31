package dongfang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DongfangAudio {

	static String path = "D:/я╦ювобть/dongfang" ;
	static String newPath = "D:/я╦ювобть/dongfang/modified" ;
	List<String> titles = new ArrayList<String>();
	public static void main(String[] args) {		
		DongfangAudio dongfangAudio = new DongfangAudio();		
		dongfangAudio.readTitles();
		
		dongfangAudio.modifyTitle();
		
	}
	
	void modifyTitle()
	{
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File file : files) {
			String filename = file.getName();
			if(filename.contains(".mp3"))
			{
				String newName = getNewName(filename);
				File newFile = new File(newPath + "/" + newName);
				file.renameTo(newFile);
			}	
			
			
		}
	}
	
	private String getNewName(String filename) {
		String name = filename.substring(0, filename.indexOf('.'));
		for (String title : titles) {
			if(title.contains(name))
				return title + ".mp3";
		}
		return name + ".mp3";
	}

	void showTitles()
	{
		System.out.println("*******************");
		for (String string : titles) {
			System.out.println(string);
		}
	}
	
	
	
	void readTitles()
	{
		try {
			FileReader fr = new FileReader("resources/dongfangtitles.txt");
			BufferedReader br = new BufferedReader(fr);
			String line; 
			while((line = br.readLine()) != null)
			{				
				if(line.trim().length() > 3)
					titles.add(line.trim());
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

}
