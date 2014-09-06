package io.github.asienpanda.ShieldGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.github.asienpanda.ShieldGenerator.listeners.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ShieldGenerator extends JavaPlugin {
	public static ShieldGenerator instance;
	public static String path;
	public static File data;
	public static FileConfiguration config;
	public void onEnable() {
		path = getDataFolder() + File.separator + "data.bin";
		instance = this;
		getCommand("shieldgenerator").setExecutor(
				new ShieldGeneratorCommandExecutor());
		getCommand("shield").setExecutor(new ShieldGeneratorCommandExecutor());
		registerEvents(this, new BeaconListener(), new PlayerListener(),
				new BlockListener(), new PlayerListener(), new EntityListener());

		data = new File(path);

		if (!data.exists()) {
			File newDataFile = new File(path);
			try {
				File dir = new File(getDataFolder() + File.separator);
				dir.mkdir();
				newDataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			ShieldGeneratorStore.load(path);
		}

		config = getConfig();
		config.options().copyDefaults(true);
		saveDefaultConfig();
		
		List<String> toggleList = config.getStringList("Toggle Features");
		if (toggleList.size() > 0) {
			for (String s : toggleList) {
				switch (s.toLowerCase()) {
				case "forcefield":
					ShieldManager.globalForceFieldToggle = true;
				case "antigrief":
					ShieldManager.globalAntiGriefToggle = true;
				case "antimobspawn":
					ShieldManager.globalMobSpawnToggle = true;
				case "antipvp":
					ShieldManager.globalPVPToggle = true;
				default:
					// do nothing
				}
			}
		}
	}

	public void onDisable() {
		instance = null;
		if (data.exists()) {
			ShieldGeneratorStore.save(path);
		}
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
