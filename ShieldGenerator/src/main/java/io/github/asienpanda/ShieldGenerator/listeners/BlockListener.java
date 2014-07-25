package io.github.asienpanda.ShieldGenerator.listeners;

import io.github.asienpanda.ShieldGenerator.ShieldGenerator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener{
	ShieldGenerator plugin;
	
	public BlockListener(){
		plugin = ShieldGenerator.getInstance();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		
	}
	
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e){
		
	}
	
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent e){
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		
	}
}
