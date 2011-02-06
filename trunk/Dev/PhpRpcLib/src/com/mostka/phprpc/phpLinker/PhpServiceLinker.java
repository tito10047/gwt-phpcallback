package com.mostka.phprpc.phpLinker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gwt.core.ext.typeinfo.JMethod;

public class PhpServiceLinker {
	   private static final String FILENAME = "linker.ini";
       private static PhpServiceLinker linker = null;
       private Map<String, Integer> linkServicsMap = new HashMap<String, Integer>();
       private Properties phpLinker = new Properties();
       private static String serverName = "";

       public PhpServiceLinker(String serverName) {
    	   readProperities();
           saveLinker();
       }
       /**
        * create or get static linker
     * @param serverName 
        * @return linker
        */
       public static PhpServiceLinker create(String p_serverName){
    	   serverName=p_serverName;
           if (linker == null) linker = new PhpServiceLinker(serverName);
           return linker;
       }
       private void readProperities(){
           System.out.println(serverName + "/" + FILENAME);
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
	       int linkServicesCount = Integer.parseInt(phpLinker.getProperty("ServicesCount", "9"));
	       for (int i = 9; i < linkServicesCount; i++) {
	               String classname = phpLinker.getProperty("ServiceFullName_"+i);
	               linkServicsMap.put(classname, i);
	       }
	   }

       private void saveLinker(){
           try {
        	   FileOutputStream fileStream = new FileOutputStream(serverName + "/" + FILENAME);
               phpLinker.store(fileStream, "Just a comment");
               fileStream.close();
           } catch (FileNotFoundException e) {
                   e.printStackTrace();
           } catch (IOException e) {
                   e.printStackTrace();
           }
       }
       
       /**
        * Compress full Service name nad methods names to simple number, save full Service with methods names name and number to php linker
        * @param serviceFullName
        * @return compresed fullClassName to number
        */
       public int addServiceLinker(String serviceFullName, JMethod[] methods){
               return this.addServiceLinker(serviceFullName, serviceFullName, methods);
       }
       /**
        * Compress full class name to simple number, save full class name and number to php linker
        * @param classFullName
        * @return compresed fullClassName to number
        */
	public int addServiceLinker(String serviceName,String serviceFullName, JMethod[] methods) {
	    if (linkServicsMap.containsKey(serviceFullName)==false){
	            int linkServicesCount=linkServicsMap.size();
	            phpLinker.setProperty("Service_"+linkServicesCount,""+(serviceFullName));
	            linkServicsMap.put(serviceFullName, linkServicesCount);
	            phpLinker.setProperty("ServicesCount",""+(linkServicesCount+1));
	            for (int i=0;i<methods.length;i++) {
	            	phpLinker.setProperty("Service_"+linkServicesCount + "_" + i,methods[i].getName());
	            }
	            saveLinker();
	            return linkServicesCount;
	    }else{
	            return linkServicsMap.get(serviceFullName);
	    }
	}
}
