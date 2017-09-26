package com.KevoSoftworks.BlockRunner;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

public class Functions {
	Main plugin;
	
	Functions(Main instance){
		plugin = instance;
	}
	
	
	//TODO (REFERENCE): GET functions	
	
	//returns chatprefix
	public String getPrefix(){
		return ChatColor.GREEN + "[BR] " + ChatColor.AQUA;
	}
	
	//play sound
	public void FplaySound(final String player, final double note, final long delay, final Sound sound){
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				if(plugin.playersInGame.contains(player) && plugin.PlayerFunctions.getMusic(player)){
					plugin.getServer().getPlayer(player).playSound(plugin.getServer().getPlayer(player).getLocation(), sound, (float) 1.0, (float)note);
				}
			}
		}, delay);	
	}
	
	//TODO: Song playing/ending handling
	public void playSong(final String player){
		plugin.playersMusicPlaying.add(player);
		for(Entry<Integer, Map<Integer, Map<Integer, Double>>> track : plugin.songArray.entrySet()){
			for(Entry<Integer, Map<Integer, Double>> data:track.getValue().entrySet()){
				for(Entry<Integer, Double> data2:data.getValue().entrySet()){
					double note = data2.getValue();
					long delay = data.getKey()*2;
					FplaySound(player, note, delay, getSoundFromChannel(data2.getKey()));
				}
			}
		}
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				if(plugin.playersInGame.contains(player) && plugin.PlayerFunctions.getMusic(player)){
					playSong(player);
				} else {
					plugin.playersMusicPlaying.remove(player);
				}
			}
		}, plugin.songTicks*2);		
	}
	
	public Sound getSoundFromChannel(int channel){
		Sound sound = null;
		if(channel == 1){
			sound = Sound.NOTE_PIANO;
		} else if(channel == 2){
			sound = Sound.NOTE_BASS;
		} else if(channel == 3){
			sound = Sound.NOTE_BASS_DRUM;
		} else if(channel == 4){
			sound = Sound.NOTE_SNARE_DRUM;
		} else if(channel == 5){
			sound = Sound.NOTE_STICKS;
		} else {
			sound = Sound.NOTE_PIANO;
		}
		
		return sound;
	}
}
