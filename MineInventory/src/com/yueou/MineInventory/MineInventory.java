package com.yueou.MineInventory;

/*
 * MineInventory by yueou
 * MainClass for MineInventory
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
 
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
 
public class MineInventory extends JavaPlugin{
	
    Logger log = Logger.getLogger("Minecraft");
    
    private File files = new File( "plugins" + File.separatorChar +  "MineInventory");
    private File configFile = new File("plugins" + File.separatorChar + "MineInventory" + File.separatorChar + "MineInventory.properties");
//    private File messagesFile = new File("plugins" + File.separatorChar + "MineInventory" + File.separatorChar + "MineInventory.messages");
    private MineInventoryConfigReader reader;
    private MineInventoryDataBaser databaseHandler;
    private MineInventoryCommandExecutor executor;
    private MineInventoryHash mineinventorymap;
    private MineInventoryListener mineinventorylistener;
    //private static PermissionHandler permHandler;
    private MineInventoryCommandHash commandmap;
    private MineInventorySignListener signlistener;
    

	public static Permission permission = null;
	public static Economy economy = null;
	

    @Override
    public void onEnable()
    {
            if(!(configFile.exists()))
            {
                    log.info("[MineInventory] Config file does not exist,created.");
                    createFile(configFile, "MineInventory.properties");
            }
            
           //check if messageFile exists else create
/*            if(!(messagesFile.exists()))
            {
                    log.info("[MineInventory] Messages file does not exist. Will be created now.");
                    createFile(messagesFile, "MineInventory.messages");
            }
            
*/                      
            reader = new MineInventoryConfigReader();
            
            databaseHandler = new MineInventoryDataBaser(reader.getHostname(),reader.getDataBase(),reader.getUsername(),reader.getPassword(),reader.getPort(),reader.getPrefix(),this);

            mineinventorymap = new MineInventoryHash(this);
            
            executor = new MineInventoryCommandExecutor(this);
            
            mineinventorylistener = new MineInventoryListener(this);
            
            commandmap = new MineInventoryCommandHash(); 
            
            signlistener = new MineInventorySignListener(this);
            
            checkVault();
            setupPerms();
            setupEconomy();
            
            PluginManager pm = this.getServer().getPluginManager();
            pm.registerEvents(mineinventorylistener, this);
            pm.registerEvents(signlistener, this);
            
            
            getCommand("mi").setExecutor(executor);
            
            PluginDescriptionFile pdfFile = this.getDescription();
            try {
				loadInventory();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
            log.info("MineInventory v" + pdfFile.getVersion() + " by yueou loaded.");
    }
    
    @Override
    public void onDisable()
    {
        //TODO clean variables and lists @command
    	
    	getMap().saveAll();
    	log.info("[MineInventory]MineInventory disabled");
    }
    
    public void onReload(){
    	
        if(!(configFile.exists()))
        {
                log.info("[MineInventory] Config file does not exist,created.");
                createFile(configFile, "MineInventory.properties");
        }
        reader = new MineInventoryConfigReader();     
        databaseHandler = new MineInventoryDataBaser(reader.getHostname(),reader.getDataBase(),reader.getUsername(),reader.getPassword(),reader.getPort(),reader.getPrefix(),this);  
        mineinventorymap = new MineInventoryHash(this);       
        executor = new MineInventoryCommandExecutor(this);
        mineinventorylistener = new MineInventoryListener(this);        
        commandmap = new MineInventoryCommandHash();        
        signlistener = new MineInventorySignListener(this); 
        
        //setupPerms();
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(mineinventorylistener, this);        
        getCommand("mi").setExecutor(executor);
        PluginDescriptionFile pdfFile = this.getDescription();
        try {
			loadInventory();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
        log.info("MineInventory v" + pdfFile.getVersion() + " by yueou reloaded.");
    }
    
    private void createFile(File file, String source)
    {
       files.mkdirs();
            
	   try 
	   {
	        file.createNewFile();
	   } 
	   catch (IOException e) 
	   {
	        e.printStackTrace();
	   }
	    
	   InputStream is = this.getClass().getResourceAsStream("/" + source);
	   BufferedInputStream buffIn = new BufferedInputStream(is);
	    
	   BufferedOutputStream bufOut = null;
	    
	   try
	   {  
		   bufOut = new BufferedOutputStream(new FileOutputStream(file));
	   } 
	   catch (FileNotFoundException e1) 
	   {
	        e1.printStackTrace();
	   }
	    
	   byte[] inByte = new byte[4096];
	   int count = -1;
	   
	   try 
	   {
	       while ((count = buffIn.read(inByte))!=-1) 
	       {
	    	   bufOut.write(inByte, 0, count);
	       }
	    } 
	   
	   catch (IOException e) 
	   {
	        e.printStackTrace();
	   }
	
	   try 
	   {
	        bufOut.close();
	   } 
	   catch (IOException e) 
	   {
	        e.printStackTrace();
	   }
	   try 
	   {
	        buffIn.close();
	   } 
	   catch (IOException e) 
	   {
		   e.printStackTrace();
	   }
        
   }
    private void checkVault() {
		if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
			this.log.warning("δ�ҵ�vault!���������.");
			this.setEnabled(false);
			return;
		}
    }
    
    private void setupPerms()
    {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		
    	/*if (permHandler != null) {
            return;
        }
        
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
        
        if (permissionsPlugin == null) {
            log.info("[MineInventory]Permission system not found");
            return;
        }
        
       permHandler = ((Permissions) permissionsPlugin).getHandler();
        log.info("[MineInventory] Permission system found. Will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());*/
    }
    
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}
    
    public MineInventoryHash getMap(){
    	
    	return mineinventorymap;
    }
    
    public void registerInventory(String sql){
    	
    	databaseHandler.registerInventory(sql);
    }
    
    public void updateInventory(String sql){
    	
    	databaseHandler.updateInventory(sql);
    }
    
    public void removeInventory(String sql){
    	
    	databaseHandler.removeInventory(sql);
    }
    
    public String getInventory(String sql){
    	
    	String invdata;
    	
    	invdata = databaseHandler.getInventory(sql);
    	
    	return invdata;
    }
    
    public String getUserName(String sql){
    	
    	String username;
    	
    	username = databaseHandler.getUserName(sql);
    	
    	return username;
    }
    
    public  Permission getPermissionHandler()
    {
    	return permission;
    }
    
    public MineInventoryConfigReader getreader(){
    	
    	return reader;
    }
    
    public MineInventoryCommandHash getCommandMap(){
    	
    	return commandmap;
    }
    
    public void loadInventory() throws IOException{
    	
    	System.out.println("[MineInventory]DataBase connected,Inventory loading..");
    	String sql = "Select * From prefix_InventoryData;";
    	
		String playername = null;
		int inventorynumber = 0;
		String inventorydata = null;
		int tool[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		
    	ResultSet result = databaseHandler.getAll(sql);
    	if(result==null){
    		return;
    	}
    	int i = 0;
    	try {
			for (i=0;result.next();i++){
				playername = result.getString("UserName");
					if(playername == null)return;
				inventorynumber = result.getShort("Amount");
				inventorydata = result.getString("invData");
				tool[0] = result.getShort("CanChest");
				tool[1] = result.getShort("CanDrop");
				tool[2] = result.getShort("CanSort");
				tool[3] = result.getShort("CanSend");
				mineinventorymap.loadInventory(playername.toLowerCase(), inventorynumber, inventorydata ,tool);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    	System.out.println("[MineInventory] "+i +" inventories loaded.");
    }

}
