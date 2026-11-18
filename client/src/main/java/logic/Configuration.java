package logic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public static final String CONFIGS_ADDRESS = "./src/main/resources/Configurations/configs.xml";
    public static Properties configs;
    public static Properties addresses;

    public static Properties graphics;



    public static void saveConfigs(Properties properties){
        saveByName(properties,"configs");
    }
    public static void saveAddresses(Properties properties){
        saveByName(properties,"addresses");
    }
    public static void saveGraphics(Properties properties){
        saveByName(properties,"graphics");
    }




    public static void load(){
        configs = loadConfigs();
        graphics = loadByAddress((String) configs.get("graphics"));

        addresses = loadByAddress((String) configs.get("addresses"));

    }


    public static void saveByName(Properties properties,String name){
        try(FileOutputStream output = new FileOutputStream("./src/main/resources/Configurations/"+name+".xml")){
            properties.storeToXML(output,"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static void saveConfigs(Properties properties,String name){
        try(FileOutputStream output = new FileOutputStream(CONFIGS_ADDRESS)){
            properties.storeToXML(output,"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadConfigs(){
        Properties properties = new Properties();
        try(FileInputStream fileInputStream = new FileInputStream(CONFIGS_ADDRESS)){
            properties.loadFromXML(fileInputStream);
        } catch(IOException e){
            e.printStackTrace();
        }
        return properties;
    }


    public static Properties loadByAddress(String address){
        Properties properties = new Properties();
        try(FileInputStream fileInputStream = new FileInputStream(address)){
            properties.loadFromXML(fileInputStream);
        } catch(IOException e){
            e.printStackTrace();
        }
        return properties;
    }

}
