package io.github.asienpanda.ShieldGenerator.listeners;

import org.bukkit.event.Listener;

import io.github.asienpanda.ShieldGenerator.ShieldGenerator;

public class EntityListener implements Listener{
	ShieldGenerator plugin;
	
	public EntityListener(){
		plugin = ShieldGenerator.getInstance();
	}

}
