package com.KevoSoftworks.BlockRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiHandler {
	
	Main plugin;
	MidiHandler(Main instance){
		plugin = instance;
	}
			
	   	public static final int NOTE_ON = 0x90;
	    public static final int NOTE_OFF = 0x80;
	    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	    public void MidiHandle() throws Exception {
	        Sequence sequence = MidiSystem.getSequence(new File(plugin.getDataFolder(), "sound.mid"));

	        int trackNumber = 0;
	        for (Track track :  sequence.getTracks()) {
	            //trackNumber++;
	            for (int i=0; i < track.size(); i++) { 
	                MidiEvent event = track.get(i);
	                MidiMessage message = event.getMessage();
	                if (message instanceof ShortMessage) {
	                    ShortMessage sm = (ShortMessage) message;
	                    if (sm.getCommand() == NOTE_ON) {
	                        int key = sm.getData1();
	                        int octave = (key / 12)-1;
	                        int note = key % 12;
	                        String noteName = NOTE_NAMES[note];                     
	                        
	                        long tick = track.ticks();
	                        long seconds = sequence.getMicrosecondLength()/1000000;
	                        long tickssec = tick/seconds/10;
	                        int ticks = (int) (event.getTick()/tickssec);
	                        
	                        plugin.songTicks = (tick/tickssec) + 60;
	                        
	                        if(plugin.getConfig().getBoolean("debug")){
	                        	System.out.println("<br:midi>" + noteName + octave + " ticks=" + ticks + " tickssec=" + tickssec + " sec=" + seconds + " trackticks=" + tick + " tracknum=" + trackNumber + " chan=" + (sm.getChannel()+1));
	                        }
	                        
	                        Map<Integer, Double> tmp = new HashMap<Integer, Double>();
	                        	if(noteName == "F#"){
	                        		if(octave == 3){
	                        			tmp.put(sm.getChannel()+1, 0.5);
	                        		} else if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, (double) 1);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, (double) 2);
	                        		}
	                        	} else if(noteName == "G"){
	                        		if(octave == 3){
	                        			tmp.put(sm.getChannel()+1, 0.53);
	                        		} else if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 1.06);
	                        		}
	                        	} else if(noteName == "G#"){
	                        		if(octave == 3){
	                        			tmp.put(sm.getChannel()+1, 0.56);
	                        		} else if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 1.12);
	                        		}
	                        	} else if(noteName == "A"){
	                        		if(octave == 3){
	                        			tmp.put(sm.getChannel()+1, 0.6);
	                        		} else if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 1.18);
	                        		}
	                        	} else if(noteName == "A#"){
	                        		if(octave == 3){
	                        			tmp.put(sm.getChannel()+1, 0.63);
	                        		} else if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 1.26);
	                        		}
	                        	} else if(noteName == "B"){
	                        		if(octave == 3){
	                        			tmp.put(sm.getChannel()+1, 0.67);
	                        		} else if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 1.34);
	                        		}
	                        	} else if(noteName == "C"){
	                        		if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 0.7);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, 1.42);
	                        		}
	                        	} else if(noteName == "C#"){
	                        		if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 0.76);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, 1.5);
	                        		}
	                        	} else if(noteName == "D"){
	                        		if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 0.8);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, 1.6);
	                        		}
	                        	} else if(noteName == "D#"){
	                        		if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 0.84);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, 1.68);
	                        		}
	                        	} else if(noteName == "E"){
	                        		if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 0.9);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, 1.78);
	                        		}
	                        	} else if(noteName == "F"){
	                        		if(octave == 4){
	                        			tmp.put(sm.getChannel()+1, 0.94);
	                        		} else if(octave == 5){
	                        			tmp.put(sm.getChannel()+1, 1.88);
	                        		}
	                        	}
	                        	Map<Integer, Map<Integer, Double>> tmpticks = new HashMap<Integer, Map<Integer, Double>>();
	                        	tmpticks.put(ticks, tmp);
	                        	plugin.songArray.put(plugin.songArray.size() + 1, tmpticks);
	                    }
	                }
	            }
	        }

	    }
}
