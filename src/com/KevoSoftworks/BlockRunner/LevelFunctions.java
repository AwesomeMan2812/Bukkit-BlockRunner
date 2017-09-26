package com.KevoSoftworks.BlockRunner;

import org.bukkit.World;


public class LevelFunctions{

		Main plugin;
		LevelFunctions(Main instance){
			plugin = instance;
		}
		
		//set level spawn
		public void gameSetSpawn(String player, String str, String gamemode){
			int string = 1;
			try{
				string = plugin.getConfig().getConfigurationSection("levels").getKeys(false).size() + 1;
			} catch(Exception e){
				string = 1;
			}
			gamemode.toLowerCase();
				try{
					if(str != "none"){
						string = Integer.parseInt(str);
					}
					double x = plugin.getServer().getPlayer(player).getLocation().getX();
					double y = plugin.getServer().getPlayer(player).getLocation().getY();
					double z = plugin.getServer().getPlayer(player).getLocation().getZ();
					float yaw = plugin.getServer().getPlayer(player).getLocation().getYaw();
					float pitch = plugin.getServer().getPlayer(player).getLocation().getPitch();
				
					plugin.getConfig().set("levels." + string + ".x", x);
					plugin.getConfig().set("levels." + string + ".y", y);
					plugin.getConfig().set("levels." + string + ".z", z);
					plugin.getConfig().set("levels." + string + ".yaw", yaw);
					plugin.getConfig().set("levels." + string + ".pitch", pitch);
					plugin.getConfig().set("levels." + string + ".gamemode", gamemode);
					plugin.getConfig().set("levels." + string + ".prize.give", false);
					
					plugin.saveConfig();
					
					plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "Spawn set!");
					plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You can set a level prize by using: \n'/br setprize " + string + " <amount> <item-id> <item-datavalue>'");
				} catch (NumberFormatException e){
					plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "Invalid number!");
				}
		}
		

		//set game world
		public void gameSetWorld(String player){
			Object world = plugin.getServer().getPlayer(player).getLocation().getWorld().getName();
			plugin.getConfig().set("world", world);
			plugin.saveConfig();
			plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "Game world set to: " + world);
		}
		
		
		//get level gamemode
		public String getGamemode(int level){
			return plugin.getConfig().getString("levels." + level + ".gamemode");
		}
		
		//get level gamemode via player
		
		public String getPlayerGamemode(String player){
			return getGamemode(plugin.playerLevel.get(player));
		}
		
		//get world
		
		public World getWorld(){
			return plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		}
}
