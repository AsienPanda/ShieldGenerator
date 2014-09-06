package io.github.asienpanda.ShieldGenerator.listeners;

import io.github.asienpanda.ShieldGenerator.ShieldGenerator;

import org.bukkit.event.Listener;
public class EntityListener implements Listener{
	ShieldGenerator plugin;
	
	public EntityListener(){
		plugin = ShieldGenerator.getInstance();
	}
	
	
}
