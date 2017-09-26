package com.KevoSoftworks.BlockRunner;

import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	public ArrayList<String> playersInGame = new ArrayList<String>();
	
	
	public Map<String, Integer> playersModding = new HashMap<String, Integer>();
	public Map<String, Location> playerOriginalLoc = new HashMap<String, Location>();
	public Map<String, Integer> playerLevel = new HashMap<String, Integer>();
	public Map<String, Location> playerCheckpoint = new HashMap<String, Location>();
	public Map<String, ArrayList<ItemStack>> playerPrizes = new HashMap<String, ArrayList<ItemStack>>();
	public ArrayList<String> playersMusic = new ArrayList<String>();
	public ArrayList<String> playersMusicPlaying = new ArrayList<String>();
	
	public Map<Integer, Map<Integer, Map<Integer, Double>>> songArray = new HashMap<Integer, Map<Integer, Map<Integer, Double>>>();
	public long songTicks;
	
	
	Functions Functions;
	Commands Commands;
	PlayerFunctions PlayerFunctions;
	LevelFunctions LevelFunctions;
	MidiHandler MidiHandler;
	Configs PlayerConfig;
	
	File midi;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable(){
		PluginDescriptionFile pdffile = this.getDescription();
        midi = new File(getDataFolder(), "sound.mid");
        if(!midi.exists()){
        	 this.saveResource("sound.mid", false);
        }
		Functions = new Functions(this);
		Commands = new Commands(this);
		PlayerFunctions = new PlayerFunctions(this);
		LevelFunctions = new LevelFunctions(this);
		MidiHandler = new MidiHandler(this);
		PlayerConfig = new Configs(this, "players.yml");
		try {
			MidiHandler.MidiHandle();
		} catch (Exception e) {
			this.logger.log(Level.SEVERE, "An error occured: ");
			e.printStackTrace();
		}
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        
        
		if(!new File(getDataFolder(), "players.yml").exists()){
			saveResource("players.yml", false);
		}
        
		this.logger.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable(){
		PluginDescriptionFile pdffile = this.getDescription();
		for(String player:playersInGame){
			PlayerFunctions.playerLeaveGame(player);
		}
		this.logger.info(pdffile.getName() + " version is disabled!");
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String args[]){
		if(cmdLabel.equalsIgnoreCase("br") || cmdLabel.equalsIgnoreCase("blockrunner") || cmdLabel.equalsIgnoreCase("blockrun") || cmdLabel.equalsIgnoreCase("blockr") || cmdLabel.equalsIgnoreCase("blrun") || cmdLabel.equalsIgnoreCase("blr")){
			if(sender instanceof Player){
				Player player = (Player)sender;
				
				if(args.length > 0){
				if(args[0].equalsIgnoreCase("help")){
					//br help <page>
					if(args.length == 2){
						try{
							int num = Integer.parseInt(args[1]);
							Commands.commandHelp(player, num);
							return true;
						} catch (NumberFormatException e){
							player.sendMessage(Functions.getPrefix() + "That is not a valid page!");
							return false;
						}
					} else {
						Commands.commandHelp(player, 1);
						return true;
					}
					
				} else if(args[0].equalsIgnoreCase("join")){
					Commands.commandJoin(player);
					return true;
					
				} else if(args[0].equalsIgnoreCase("leave")){
					Commands.commandLeave(player);
					return true;
					
				} else if(args[0].equalsIgnoreCase("setworld")){
					Commands.commandSetWorld(player);
					return true;
					
				} else if(args[0].equalsIgnoreCase("setspawn")){
					//br setspawn <id> <gamemode>
					if(args.length == 3){
						Commands.commandSetSpawn(player, args[1], args[2]);
						return true;
					}else if(args.length == 2){
						Commands.commandSetSpawn(player, args[1], "default");
						return true;
					} else if(args.length == 1){
						Commands.commandSetSpawn(player, "none", "default");
						return true;
					} else {
						player.sendMessage(Functions.getPrefix() + "Invalid Syntax! Check the help page.");
						return false;
					}
				} else if(args[0].equalsIgnoreCase("setprize")){
					//br setprize <level> <amount> <item-id> <data>
					if(args.length == 5){
						Commands.commandSetPrize(player, args[1], args[2], args[3], args[4]);
					} else if(args.length == 4){
						Commands.commandSetPrize(player, args[1], args[2], args[3], "-1");
					} else if(args.length == 3){
						Commands.commandSetPrize(player, args[1], args[2], "264", "-1");
					} else if(args.length == 2){
						Commands.commandSetPrize(player, args[1], "1", "264", "-1");
					} else {
						player.sendMessage(Functions.getPrefix() + "Invalid Syntax! Check the help page.");
						return false;
					}
				} else if(args[0].equalsIgnoreCase("reload")){
					//br reload
					if(args.length == 2 && args[1].equalsIgnoreCase("all")){
						Commands.commandGameReload(player);
					} else {
						Commands.commandQuickReload(player);
					}
					
				} else if(args[0].equalsIgnoreCase("gamemode")){
					if(args.length > 1){
					if(args[1].equalsIgnoreCase("create")){
						Commands.commandGamemodeCreate(player);
					} else if(args[1].equalsIgnoreCase("list")){
						Commands.commandGamemodeList(player);
					} else if(args[1].equalsIgnoreCase("modify")){
						//br gamemode modify <gamemode> <function> <value>
						if(args.length == 5){
							Commands.commandGamemodeModify(player, args[2], args[3], args[4]);
							return true;
						} else {
							player.sendMessage(Functions.getPrefix() + "Invalid Syntax! Check the help page.");
							return false;
						}
					} else { 
						player.sendMessage(Functions.getPrefix() + "Invalid Syntax! Check the help page.");
						return false;
					}
					} else {
						player.sendMessage(Functions.getPrefix() + "Invalid Syntax! Check the help page.");
						return false;
					}
				} else if(args[0].equalsIgnoreCase("music")){
					Commands.commandToggleMusic(player);
				} else {
					Commands.commandUnknown(player);
					return true;
				}
				} else {
					player.sendMessage(Functions.getPrefix() + "Use '/br help' for help!");
					return false;
				}
				
			} else {
				sender.sendMessage("Player Expected!");
				return false;
			}
		}
		return false;		
	}

}
