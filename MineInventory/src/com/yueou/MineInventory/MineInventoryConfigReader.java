package com.yueou.MineInventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


public class MineInventoryConfigReader {


     private String host;
     private String port;
     private String dbName;
     private String userName;
     private String pw;
     private String pre;
     private File config = new File("plugins" + File.separatorChar + "MineInventory" + File.separatorChar + "MineInventory.properties");
     private float createprice;
     //private float updateprice;
     private Map<Integer,Float> updateprice;
     
     
    public MineInventoryConfigReader()
     {
        String thisLine;
        
       try
        {
               BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(config)));
                
               while((thisLine = read.readLine()) != null)
                {
                        if(thisLine.startsWith("#")) continue; 
                       if(thisLine.equals("")) continue;
                        else if(thisLine.contains("SQLHost"))
                        {
                                host = thisLine.replace("SQLHost=", "");
                        }
                        else if(thisLine.contains("SQLPort"))
                        {
                                port = thisLine.replace("SQLPort=", "");
                        }
                        else if(thisLine.contains("SQLDataBase"))
                        {
                                dbName = thisLine.replace("SQLDataBase=", "");
                        }
                        else if(thisLine.contains("SQLUserName"))
                        {
                                userName = thisLine.replace("SQLUserName=", "");
                        }
                        else if(thisLine.contains("SQLPassword"))
                        {
                                pw = thisLine.replace("SQLPassword=", "");
                        }
                        else if(thisLine.contains("SQLTablePrefix"))
                        {
                                pre = thisLine.replace("SQLTablePrefix=", "");
                        }
                        else if(thisLine.contains("InventoryCreatePrice"))
                        {
                        		createprice = Float.parseFloat(thisLine.replace("InventoryCreatePrice=", ""));
                        }
                        else if(thisLine.contains("InventoryLevelupPrice:"))
                        {
                        	while((thisLine = read.readLine()) != null) {
                        		String[] s1=thisLine.split(":");
                        		updateprice.put(Integer.parseInt(s1[0]), Float.parseFloat(s1[1]));
                        	}
                        	//updateprice = Float.parseFloat(thisLine.replace("InventoryLevelupPrice=", ""));
                        }
                }
                read.close();

       }
        catch (IOException e)
        {
                e.printStackTrace();
        }

     }

     
     public String getHostname()
     {
             return host;
     }
     
     public String getDataBase()
     {
             return dbName;
     }
     
     public String getPort()
     {
             return port;
     }
     
     public String getUsername()
     {
             return userName;
     }
     
     public String getPassword()
     {
             return pw;
     }
     
     public String getPrefix()
     {
             return pre;
     }
     public float getCreatePrice()
     {
             return createprice;
     }
     
     public Map<Integer,Float> getUpdatePrice()
     {
             return updateprice;
     }    
}
