package com.KevoSoftworks.BlockRunner;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.entity.Player;

public class Commands {
	Main plugin;
	
	Commands(Main instance){
		plugin = instance;
	}
	
	public void commandHelp(Player player, int page){
		if(player.hasPermission("blockrunner.help")){
			if(page == 1){
				player.sendMessage("\n\n\n" + plugin.Functions.getPrefix() + "===Blockrunner Help===(1/3)\n[] = required, <> = optional\n\n");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br help <page>': This help menu");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br join': Join the game");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br leave': Leave the game");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br music': Toggle in-game music");
			} else if(page == 2){
				player.sendMessage("\n\n\n" + plugin.Functions.getPrefix() + "===Blockrunner Help===(2/3)\n[] = required, <> = optional\n\n");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br setworld': Sets the current world as gameworld");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br setspawn <id> <gamemode>': Create a level");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br setprize [level-id] <amount> <item-id> <item-datavalue>': Set a level prize");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br reload': Reload the config");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br reload all': Reload the complete plugin");
			} else if (page == 3){
				player.sendMessage("\n\n\n" + plugin.Functions.getPrefix() + "===Blockrunner Help===(3/3)\n[] = required, <> = optional\n\n");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br gamemode list': List all gamemodes");
				player.sendMessage(plugin.Functions.getPrefix() + "'/br gamemode modify [gamemode] [data] [value]': Modify a gamemode value");
			}
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandJoin(Player player){
		if(player.hasPermission("blockrunner.join")){
			plugin.PlayerFunctions.playerJoinGame(player.getName());
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}		
	}
	
	public void commandLeave(Player player){
		if(player.hasPermission("blockrunner.leave")){
			plugin.PlayerFunctions.playerLeaveGame(player.getName());
		} else { 
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandSetSpawn(Player player, String id, String gamemode){
		if(player.hasPermission("blockrunner.admin.setspawn")){
			plugin.LevelFunctions.gameSetSpawn(player.getName(), id, gamemode);
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandSetWorld(Player player){
		if(player.hasPermission("blockrunner.admin.setworld")){
			plugin.LevelFunctions.gameSetWorld(player.getName());
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandUnknown(Player player){
		player.sendMessage(plugin.Functions.getPrefix() + "Unknown command, please use '/blockrunner help'");
	}
	
	public void commandGamemodeCreate(Player player){
		
	}
	
	public void commandGamemodeList(Player player){
		if(player.hasPermission("blockrunner.admin.gamemode.list")){
			player.sendMessage(plugin.Functions.getPrefix() + "Gamemodes:\n");
			for(String gamemode:plugin.getConfig().getConfigurationSection("gamemodes").getKeys(false)){
				player.sendMessage(plugin.Functions.getPrefix() + gamemode);
			}
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public boolean commandGamemodeModify(Player player, String gamemode, String function, String value){
		if(player.hasPermission("blockrunner.admin.gamemode.modify")){
		gamemode.toLowerCase();
			if(plugin.getConfig().isSet("gamemodes." + gamemode)){
				if(function.equalsIgnoreCase("amount") || function.equalsIgnoreCase("time") || function.equalsIgnoreCase("return")){
					try{
						int val = Integer.parseInt(value);
						function.toLowerCase();
						plugin.getConfig().set("gamemodes." + gamemode + "." + function, val);
						player.sendMessage(plugin.Functions.getPrefix() + "Gamemode function " + function + " successfully set to " + val);
						return true;
					} catch (NumberFormatException e){
						player.sendMessage(plugin.Functions.getPrefix() + "The entered value is not a number!");
						return false;
					}
				} else {
					player.sendMessage(plugin.Functions.getPrefix() + "Invalid Function!");
					return false;
				}
			} else {
				player.sendMessage(plugin.Functions.getPrefix() + "Unknown Gamemode!");
				return false;
			}
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
			return false;
		}
	}
	
	public void commandToggleMusic(Player player){
		if(player.hasPermission("blockrunner.music")){
			if(plugin.playersInGame.contains(player.getName())){
				plugin.PlayerFunctions.setMusic(player.getName());
				if(plugin.PlayerFunctions.getMusic(player.getName()) && !plugin.playersMusicPlaying.contains(player.getName())){
					plugin.Functions.playSong(player.getName());
				}
				player.sendMessage(plugin.Functions.getPrefix() + "Music toggled!");
			} else {
				player.sendMessage(plugin.Functions.getPrefix() + "You are not in a game!");
			}
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandSetPrize(Player player, String levels, String amounts, String ids, String datas){
		if(player.hasPermission("blockrunner.admin.setprize")){
			try{
				int level = Integer.parseInt(levels);
				int amount = Integer.parseInt(amounts);
				int id = Integer.parseInt(ids);
				int data = Integer.parseInt(datas);
			
				plugin.getConfig().set("levels." + level + ".prize.give", true);
				plugin.getConfig().set("levels." + level + ".prize.itemid", id);
				plugin.getConfig().set("levels." + level + ".prize.dataval", data);
				plugin.getConfig().set("levels." + level + ".prize.amount", amount);
			
				plugin.saveConfig();
				player.sendMessage(plugin.Functions.getPrefix() + "The prize has been set!");
			
			} catch(NumberFormatException e){
				player.sendMessage(plugin.Functions.getPrefix() + "You must enter numbers, not text!");
			} catch(Exception e){
				e.printStackTrace();
			}
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandGameReload(Player player){
		if(player.hasPermission("blockrunner.admin.reload")){
			plugin.logger.info("Blockrunner is reloading... Please wait...");
			for(String splayer:plugin.playersInGame){
				plugin.playerLevel.remove(splayer);
				plugin.getServer().getPlayer(splayer).getInventory().clear();
				plugin.getServer().getPlayer(splayer).setFallDistance(0);
				plugin.getServer().getPlayer(splayer).teleport(plugin.playerOriginalLoc.get(splayer));
				plugin.PlayerFunctions.givePrize(splayer);
				if(plugin.playerCheckpoint.containsKey(player)){
					plugin.playerCheckpoint.remove(player);
				}
				plugin.getServer().getPlayer(splayer).sendMessage(plugin.Functions.getPrefix() + "You left BlockRunner!");
			}
		
			plugin.playersInGame.clear();
		
		
			plugin.playersModding.clear();
			plugin.playerOriginalLoc.clear();
			plugin.playerLevel.clear();
			plugin.playerCheckpoint.clear();
			plugin.playerPrizes.clear();
			plugin.playersMusic.clear();
			plugin.playersMusicPlaying.clear();
		
			plugin.songArray.clear();
		
			plugin.reloadConfig();
			plugin.midi = new File(plugin.getDataFolder(), "sound.mid");
			if(!plugin.midi.exists()){
				plugin.saveResource("sound.mid", false);
			}
	    
			try {
				plugin.MidiHandler.MidiHandle();
			} catch (Exception e) {
				plugin.logger.log(Level.SEVERE, "An error occured: ");
				e.printStackTrace();
			}
		
			player.sendMessage(plugin.Functions.getPrefix() + "Reload complete!");
			plugin.logger.info("Blockrunner reload complete!");
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
	
	public void commandQuickReload(Player player){
		if(player.hasPermission("blockrunner.admin.reload")){
			plugin.reloadConfig();
			player.sendMessage(plugin.Functions.getPrefix() + "Config reloaded!");
		} else {
			player.sendMessage(plugin.Functions.getPrefix() + "No permission!");
		}
	}
}
