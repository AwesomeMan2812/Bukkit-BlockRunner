package com.KevoSoftworks.BlockRunner;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
//import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
//import org.bukkit.event.player.AsyncPlayerChatEvent;
//import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener{
	Main plugin;
	
	EventListener(Main instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event){
		final Player player = event.getPlayer();
		if(plugin.playersInGame.contains(player.getName())){
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				public void run(){
					player.getWorld().getBlockAt(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()).setTypeId(0);
					plugin.PlayerFunctions.playerHeal(player.getName());
				}
			}, (long)plugin.getConfig().getLong("gamemodes." + plugin.LevelFunctions.getPlayerGamemode(player.getName()) + ".time"));
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				public void run(){
					plugin.PlayerFunctions.playerGiveBlock(player.getName(),1);
					plugin.PlayerFunctions.playerHeal(player.getName());
				}
			}, (long)plugin.getConfig().getLong("gamemodes." + plugin.LevelFunctions.getPlayerGamemode(player.getName()) + ".return"));
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event){
		if(plugin.playersInGame.contains(event.getPlayer().getName())){
			plugin.PlayerFunctions.playerLeaveGame(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(plugin.playersInGame.contains(event.getPlayer().getName())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if(plugin.playersInGame.contains(player.getName())){
				if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = (Player)event.getPlayer();
		if(plugin.playersInGame.contains(player.getName())){
			if(Math.floor(player.getLocation().getY()) <= 0 || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.DIAMOND_BLOCK){
				plugin.PlayerFunctions.playerNextLevel(player.getName());
			} else if(player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_WATER){
				plugin.PlayerFunctions.playerTpToLevel(player.getName());
			} else if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK){
				plugin.PlayerFunctions.setCheckpoint(player.getName());
			} else if(player.getLocation().getBlock().getType() == Material.LAVA || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAVA || player.getLocation().getBlock().getType() == Material.STATIONARY_LAVA || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.STATIONARY_LAVA){
				plugin.playerLevel.put(player.getName(),1);
				plugin.PlayerFunctions.removeCheckpoint(player.getName());
				plugin.PlayerFunctions.clearPrize(player.getName());
				plugin.PlayerFunctions.playerTpToLevel(player.getName());
				player.sendMessage(plugin.Functions.getPrefix() + "Level: " + plugin.PlayerFunctions.playerGetLevel(player.getName()) + ", Gamemode: " + plugin.LevelFunctions.getGamemode(plugin.PlayerFunctions.playerGetLevel(player.getName())));
			}
		}
	}
	
	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		if(plugin.playersInGame.contains(event.getPlayer().getName()) && event.getFrom() == plugin.LevelFunctions.getWorld()){
			plugin.PlayerFunctions.playerLeaveGame(event.getPlayer().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		String msg = event.getMessage();
		String command = msg.split(" ", 2)[0];
		if(plugin.playersInGame.contains(player.getName())){
			if(!command.contains("/br") || !command.contains("/blockrunner") || !command.contains("/blockrun") || !command.contains("/blockr") || !command.contains("/blrun") || !command.contains("/blr")){
				if(!event.getPlayer().isOp()){
					event.setCancelled(true);
					player.sendMessage(plugin.Functions.getPrefix() + "You may not use this command in-game!");
				} else {
					player.sendMessage(plugin.Functions.getPrefix() + "<OP OVERRIDE> Command executed while in-game. It is recommended to leave the game first.");
				}
			}
		}
	}
	
	/*@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		if(plugin.playersModding.containsKey(player)){
			event.setCancelled(true);
		}
	}*/
}
