package io.github.asienpanda.ShieldGenerator;

import io.github.asienpanda.ShieldGenerator.listeners.*;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ShieldGenerator extends JavaPlugin {
	public static ShieldGenerator instance;

	public void onEnable() {
		instance = this;

		registerEvents(this, new BeaconListener(), new PlayerListener(), new BlockListener(), new PlayerListener(), new EntityListener());
	}

	public void onDisable() {
		instance = null;
	}

	public static void registerEvents(Plugin instance, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager()
					.registerEvents(listener, instance);
		}
	}

	public static ShieldGenerator getInstance() {
		return instance;
	}
}
