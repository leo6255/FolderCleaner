/*
FolderCleaner - сканирует папку и вложенные папки и удаляет файлы, дата редактирования которых старше количества часов, указанных в properties
Если папка не является корневой, в ней нет файлов, и дата изменнения ее старше количества часов, указанных в properties, она также удаляется.
Предварительно структура папок очищается от файлов Thumbs.db
Утилиту необходимо запускать из Планировщака заданий Windows
*/
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import static java.lang.System.out;

public class FolderCleaner {
	
	public static void main(String[] args) throws IOException{
		
		//cчитываем properties
		Properties prop = LoadProperties.loadprop("FolderCleaner.properties");
		
		//cканируем папки, заданные в properties
		int i = 1;
		while(prop.getProperty("SourcePath" + i) != null) {
			String SourcePath = prop.getProperty("SourcePath" + i);
			String keeph = prop.getProperty("keephours" + i);
			int keephours = Integer.parseInt(keeph);
			
			File directory = new File(SourcePath);
			
			//очищаем структуру каталогов от Thumbs.db
			System.out.println("----- Delete from Thumbs.db -----");
			ThumbsCleaner(directory, true);
			System.out.println("----- ----- -----");
			
			//очищаем структуру каталогов от просроченных файлов и папок
			CheckDir(directory, keephours, 0, true);
			
			i++;
        }
	}
	
	//рекурсивный метод, заходит в директорию, если видит просроченный файл удаляет, а если видит папку заходит в нее и запускает сам себя в ней
    public static void CheckDir(File directory, Integer keephours, long timeleft, boolean root) throws IOException {
        File[] files = directory.listFiles();
		//сколько файлов в разделе
		int fcount = files.length;
		
		out.println("files in " + directory.getAbsolutePath() + ": " + fcount + "; ");

		//если папка не корневая и файлов в папке нет, то удаляем папку и переходим к следующей итерации цикла
		out.println("root: " + root + "; timeleft: " + timeleft);
		if(!root && fcount == 0){
			int z = (int)timeleft;
			Cleaner(directory.getAbsolutePath(), z);
		}
		
		//основной цикл перебора файлов и папок
        for(File file: files){
				
			//вермя создания файла, переводим в часы /3600000
            Long FileModifTime = file.lastModified()/3600000;
			//текущее вермя, переводим в часы /3600000
			Long nowTime = System.currentTimeMillis()/3600000;
				
			long tleft = nowTime - FileModifTime;
			out.println(file.getName() + " создано: " + tleft + " часов назад "); 
	
            //если папка, то заходим и проверяем тоже самое заново, для этого создаем новый путь для метода и вызываем этот же метод
            if(file.isDirectory() == true){
			
				out.println(file.getName() + " is DIRECTORY");
				
				File NxtDirectory = new File(file.getAbsolutePath());
                CheckDir(NxtDirectory, keephours, tleft, false);
            } else {
				//если файл старый, удаляем его
				if(tleft > keephours){
                    //if(!file.getName().equals("Thumbs.db")){
                        String sourcefile = file.getAbsolutePath();
						int y = (int)tleft;
                        Cleaner(sourcefile, y);
					//}		
                }
            }
        }
    }
	
	//Рекурсивный метод очистки структуры каталогов от Thumbs.db
	public static void ThumbsCleaner(File directory, boolean root)throws IOException {
		File[] files = directory.listFiles();
		for(File file: files){
			if(file.isDirectory() == true){
				out.println(file.getName() + " is DIRECTORY");
				File NxtDirectory = new File(file.getAbsolutePath());
                ThumbsCleaner(NxtDirectory, false);
            } else {
				if(file.getName().equals("Thumbs.db")){
					String sourcefile = file.getAbsolutePath();
					int y = 11111;
					Cleaner(sourcefile, y);
				}
			}
		}
	}
	
	//метод удаления    
	public static void Cleaner(String sourcePath, int forlog) throws IOException {
		
		Date date = new Date();
		DateFormat DFormat = new SimpleDateFormat("HH:mm_dd-MM-yyyy");
		String thisdate = DFormat.format(date);
		
		String pth = new File("").getAbsolutePath();
		String messg = thisdate + " delete " + sourcePath + " (modified " + forlog + " hours ago) \r";
		
		File source = new File(sourcePath);
		if (source.delete()) { 
			System.out.println("Delete: " + source.getName());
			LogWrite(pth, messg);
		} else {
			System.out.println("Failed to delete");
			messg = messg + "Failed to delete";
			LogWrite(pth, messg);
		}
	}
	
	//метод записи лога
	public static void LogWrite(String logFilePath, String messg) throws IOException{
		
		Date dat = new Date();
		DateFormat LFormat = new SimpleDateFormat("dd-MM-yyyy");
		String logdate = LFormat.format(dat);
		
		String logFileName = logdate + "-FolderCleaner.log";
		String logFile = logFilePath + "\\logs\\" + logFileName;

		FileWriter myWriter = new FileWriter(logFile, true);
		myWriter.write(messg);
		myWriter.close();
	}
	
}