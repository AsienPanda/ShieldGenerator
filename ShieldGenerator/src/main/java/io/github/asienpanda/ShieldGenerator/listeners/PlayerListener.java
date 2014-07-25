package io.github.asienpanda.ShieldGenerator.listeners;

import io.github.asienpanda.ShieldGenerator.ShieldGenerator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class PlayerListener implements Listener {
	ShieldGenerator plugin;

	public PlayerListener() {
		plugin = ShieldGenerator.getInstance();
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {

	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {

	}
	
	public void onPlayerBucketFill(PlayerBucketFillEvent e){
		
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

	}

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent e) {

	}

	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent e) {

	}

	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {

	}

	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {

	}
}
