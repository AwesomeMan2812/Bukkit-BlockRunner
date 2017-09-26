package com.KevoSoftworks.BlockRunner;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class PlayerFunctions{
	
	Main plugin;
	PlayerFunctions(Main instance){
		plugin = instance;
	}
	
	
	//Player joins game
	
	public void playerJoinGame(String player){
		if(playerIsEmpty(player)){
			if(!plugin.playersInGame.contains(player)){
				plugin.playersInGame.add(player);
				if(!plugin.PlayerConfig.getConfig().isSet("players." + player + ".level")){
					plugin.playerLevel.put(player, 1);
				} else {
					plugin.playerLevel.put(player, plugin.PlayerConfig.getConfig().getInt("players." + player + ".level"));
				}
				plugin.playerOriginalLoc.put(player, plugin.getServer().getPlayer(player).getLocation());
				playerTpToLevel(player);
				plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You joined BlockRunner!");
				plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "Level: " + playerGetLevel(player) + ", Gamemode: " + plugin.LevelFunctions.getGamemode(playerGetLevel(player)));
			} else {
				plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You are already in-game!");
			}
		} else {
			plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "Clear your inventory first!");
		}
	}
	
	//Player leaves game
	
	public void playerLeaveGame(String player){
		if(plugin.playersInGame.contains(player)){
			plugin.playersInGame.remove(player);
			plugin.playerLevel.remove(player);
			plugin.getServer().getPlayer(player).getInventory().clear();
			plugin.getServer().getPlayer(player).setFallDistance(0);
			plugin.getServer().getPlayer(player).teleport(plugin.playerOriginalLoc.get(player));
			plugin.getServer().getPlayer(player).teleport(plugin.playerOriginalLoc.get(player));
			givePrize(player);
			if(plugin.playerCheckpoint.containsKey(player)){
				plugin.playerCheckpoint.remove(player);
			}
			plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You left BlockRunner!");
		} else {
			plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You are not in a game!");
		}
	}
	
	//Give player blocks
	
	public void playerGiveBlock(String player, int amount){
		plugin.getServer().getPlayer(player).getInventory().addItem(new ItemStack(Material.getMaterial(plugin.getConfig().getInt("block")), amount));
	}
	
	
	//Tp player to the Game
	
	public void playerTpToLevel(String player){
		int level = plugin.playerLevel.get(player);
		double x = plugin.getConfig().getDouble("levels." + level + ".x");
		double y = plugin.getConfig().getDouble("levels." + level + ".y");
		double z = plugin.getConfig().getDouble("levels." + level + ".z");
		int yaw = plugin.getConfig().getInt("levels." + level + ".yaw");
		int pitch = plugin.getConfig().getInt("levels." + level + ".pitch");
		Object world = plugin.getServer().getWorld(plugin.getConfig().getString("world"));
		
		if(!plugin.playerCheckpoint.containsKey(player)){
			Location loc = new Location((World) world, x, y, z, yaw, pitch);
			plugin.getServer().getPlayer(player).teleport(loc);
		} else {
			Location loc = plugin.playerCheckpoint.get(player);
			loc.setPitch(plugin.getServer().getPlayer(player).getLocation().getPitch());
			loc.setYaw(plugin.getServer().getPlayer(player).getLocation().getYaw());
			plugin.getServer().getPlayer(player).teleport(loc);
		}
		plugin.getServer().getPlayer(player).getInventory().clear();
		playerGiveBlock(player, plugin.getConfig().getInt("gamemodes." + plugin.LevelFunctions.getGamemode(plugin.playerLevel.get(player)) + ".amount"));
	}
	
	
	//Heal and feed the player
	
	public void playerHeal(String player){
		plugin.getServer().getPlayer(player).setHealth(20.0);
		plugin.getServer().getPlayer(player).setFoodLevel(20);
	}
	
	
	//check if player inventory is empty
	
	public boolean playerIsEmpty(String player){
		for(ItemStack item : plugin.getServer().getPlayer(player).getInventory().getContents())
		{
		    if(item != null){
		    	return false;
		    }
		}
		
		for (ItemStack item : plugin.getServer().getPlayer(player).getInventory().getArmorContents()){
			 if(item.getTypeId() != 0){
				 return false;
			 }
		}
		return true;
	}
	
	//set player level
	
	public void playerNextLevel(String player){
		savePrize(player, playerGetLevel(player));
		if(plugin.getConfig().isSet("levels." + (playerGetLevel(player) + 1))){
			if(plugin.playerCheckpoint.containsKey(player)){
				plugin.playerCheckpoint.remove(player);
			}
			plugin.playerLevel.put(player, playerGetLevel(player) + 1);
			plugin.PlayerConfig.getConfig().set("players." + player + ".level", playerGetLevel(player));
			plugin.PlayerConfig.saveConfig();
			playerTpToLevel(player);
			plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "Level: " + playerGetLevel(player) + ", Gamemode: " + plugin.LevelFunctions.getGamemode(playerGetLevel(player)));
		} else {
			playerWinGame(player);
			if(plugin.playerCheckpoint.containsKey(player)){
				plugin.playerCheckpoint.remove(player);
			}
		}
	}
	
	
	//set player win game
	public void playerWinGame(String player){
		plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You won! Congratulations!");
		playerLeaveGame(player);
	}
	
	//get player level
	public Integer playerGetLevel(String player){
		return plugin.playerLevel.get(player);
	}
	
	//Toggle player music
	public void setMusic(String player){
		if(plugin.playersMusic.contains(player)){
			plugin.playersMusic.remove(player);
		} else {
			plugin.playersMusic.add(player);
		}
	}
	
	//get if player is playing music
	public boolean getMusic(String player){
		return plugin.playersMusic.contains(player);
	}
	
	//set player checkpoint
	public void setCheckpoint(String player){
		if(!plugin.playerCheckpoint.containsKey(player) || !plugin.playerCheckpoint.get(player).equals(getRoundedLocation(plugin.getServer().getPlayer(player).getLocation()))){
			plugin.getServer().getPlayer(player).playSound(plugin.getServer().getPlayer(player).getLocation(), Sound.ORB_PICKUP, (float)1.0, (float)1.5);
			plugin.playerCheckpoint.put(player, getRoundedLocation(plugin.getServer().getPlayer(player).getLocation()));
		}
	}
	
	//remove a player's checkpoint
	public void removeCheckpoint(String player){
		if(plugin.playerCheckpoint.containsKey(player)){
			plugin.playerCheckpoint.remove(player);
		}
	}
	
	//return a rounded location
	public Location getRoundedLocation(Location location){
		return new Location(location.getWorld(), (Math.floor(location.getX()) + 0.5), (Math.floor(location.getY()) + 0.5), (Math.floor(location.getZ()) + 0.5));
	}
	
	//save a prize to the array
	public void savePrize(String player, int level){
		if(plugin.getConfig().getBoolean("levels." + level + ".prize.give")){
			
			if(!plugin.playerPrizes.containsKey(player)){
				plugin.playerPrizes.put(player, new ArrayList<ItemStack>());
			}
			
			int amount = plugin.getConfig().getInt("levels." + level + ".prize.amount");
			int id = plugin.getConfig().getInt("levels." + level + ".prize.itemid");
			ItemStack prize = new ItemStack(id, amount);
			if(plugin.getConfig().getInt("levels." + level + ".prize.dataval") != -1){
				MaterialData data = (MaterialData) plugin.getConfig().get("levels." + level + ".prize.dataval");
				prize.setData(data);
			}
			plugin.playerPrizes.get(player).add(prize);
		}
	}
	
	//give all the obtained prizes to the player
	public void givePrize(String player){
		if(plugin.playerPrizes.containsKey(player)){
			for(ItemStack item:plugin.playerPrizes.get(player)){
				plugin.getServer().getPlayer(player).getInventory().addItem(item);
			}
			plugin.playerPrizes.remove(player);
			plugin.getServer().getPlayer(player).sendMessage(plugin.Functions.getPrefix() + "You have obtained some prizes!");
		}
	}
	
	//clear all the prizes the player has got
	public void clearPrize(String player){
		if(plugin.playerPrizes.containsKey(player)){
			plugin.playerPrizes.remove(player);
		}
	}

}
