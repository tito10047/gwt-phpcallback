package com.mostka.phprpc.phpLinker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PhpObjectLinker {
	   private static final String FILENAME = "objectLinker.ini";
       private static PhpObjectLinker linker = null;
       private Map<String, Integer> linkClassMap = new HashMap<String, Integer>();
       private Properties phpLinker = new Properties();
       private String serverName = "server";
	
       
       protected PhpObjectLinker(){
               addClassLinker("java.lang.String");
               addClassLinker("String");
               addClassLinker("int");
               addClassLinker("double");
               addClassLinker("char");
               addClassLinker("long");
               addClassLinker("byte");
               addClassLinker("short");
               addClassLinker("boolean");
               readProperities();
               saveLinker();
       }
       /**
        * create or get static linker
        * @return linker
        */
       public static PhpObjectLinker create(){
               if (linker == null) linker = new PhpObjectLinker();
               return linker;
       }
       private void readProperities(){
               File f=new File(serverName + "/" + FILENAME);
           if(!f.exists()){
               try {
                               f.createNewFile();
                       } catch (IOException e) {
                               e.printStackTrace();
                       }
           }
           f = null;
               try {
                       phpLinker.load(new FileInputStream(serverName + "/" + FILENAME));
               } catch (FileNotFoundException e) {
                       e.printStackTrace();
               } catch (IOException e) {
                       e.printStackTrace();
               }
               int linkClassCount = Integer.parseInt(phpLinker.getProperty("ClassCount", "9"));
               for (int i = 9; i < linkClassCount; i++) {
                       String classname = phpLinker.getProperty("ClassFullName_"+i);
                       linkClassMap.put(classname, i);
               }
       }
       private void saveLinker(){
               try {
                       phpLinker.store(new FileOutputStream(serverName + "/" + FILENAME), "Just a comment");
               } catch (FileNotFoundException e) {
                       e.printStackTrace();
               } catch (IOException e) {
                       e.printStackTrace();
               }
       }
       
       /**
        * Compress full class name to simple number, save full class name and number to php linker
        * @param classFullName
        * @return compresed fullClassName to number
        */
       public int addClassLinker(String classFullName){
               return this.addClassLinker(classFullName, classFullName);
       }
       /**
        * Compress full class name to simple number, save full class name and number to php linker
        * @param classFullName
        * @return compresed fullClassName to number
        */
       public int addClassLinker(String classFullName, String newName){
               if (classFullName.substring(classFullName.length()-2, classFullName.length()).equals("[]"))
                       classFullName = classFullName.substring(0, classFullName.length()-2);
               if (classFullName.length()>"PhpObjectGenerated".length())
               if (classFullName.substring(classFullName.length()-"PhpObjectGenerated".length(), classFullName.length()).equals("PhpObjectGenerated"))
                       classFullName = classFullName.substring(0, classFullName.length()-"PhpObjectGenerated".length());
               if (newName.substring(newName.length()-2, newName.length()).equals("[]"))
                       newName = newName.substring(0, newName.length()-2);
               if (newName.length()>"PhpObjectGenerated".length())
               if (newName.substring(newName.length()-"PhpObjectGenerated".length(), newName.length()).equals("PhpObjectGenerated"))
                       newName = newName.substring(0, newName.length()-"PhpObjectGenerated".length());
               if (linkClassMap.containsKey(classFullName)==false){
                       int linkClassCount=linkClassMap.size();
                       phpLinker.setProperty("ClassFullName_"+linkClassCount, classFullName);
                       phpLinker.setProperty("Class_"+linkClassCount,""+(newName));
                       linkClassMap.put(classFullName, linkClassCount);
                       phpLinker.setProperty("ClassCount",""+(linkClassCount+1));
                       saveLinker();
                       return linkClassCount;
               }else{
                       return linkClassMap.get(classFullName);
               }
       }
}