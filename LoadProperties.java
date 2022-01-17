//#######################################
//Properties prop = LoadProperties.loadprop(propfile);
//#######################################

import java.io.*;
import java.util.Properties;

public class LoadProperties {

	//Properties-тип данных, loadprop-метод
    public static Properties loadprop(String propfile) throws IOException {
        //подгрузка параметров из roperties
        //поиск пути к файлу properties
		//указать основной класс, для поиска пути
        String PropPath = FolderCleaner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //PropPath = new File(PropPath).getParent();
        String PathToProp = PropPath + propfile;
        //получение properties
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        fileInputStream = new FileInputStream(PathToProp);
        try {
            Reader reader = new InputStreamReader(fileInputStream, "UTF-8");
            try {
                prop.load(reader);
            } finally {
                reader.close();
            }
        } finally {
            fileInputStream.close();
        }
        return prop;
    }

}