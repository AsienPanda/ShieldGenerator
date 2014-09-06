package io.github.asienpanda.ShieldGenerator.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import io.github.asienpanda.ShieldGenerator.ShieldGenerator;
import io.github.asienpanda.ShieldGenerator.ShieldManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class PlayerListener implements Listener {
	ShieldGenerator plugin;
	private static HashMap<UUID, ArrayList<Location>> map = new HashMap<UUID, ArrayList<Location>>();
	private static HashMap<UUID, Location> lastLoc = new HashMap<UUID, Location>();

	public PlayerListener() {
		plugin = ShieldGenerator.getInstance();
	}

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {

	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {

	}

	public void onPlayerBucketFill(PlayerBucketFillEvent e) {

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		Player p = e.getPlayer();
		if (ShieldManager.globalAntiGriefToggle) {
			if (ShieldManager.shieldList != null) {
				if (e.getClickedBlock() != null) {
					if (!ShieldManager.isPlayerAllowed(p, e.getClickedBlock()
							.getLocation())) {
						if (ShieldManager.getShieldManager(e.getClickedBlock())
								.isAntiGriefEnabled()) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent e) {

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		for (ShieldManager sm : ShieldManager.shieldList) {
			if (ShieldManager.isMember(e.getPlayer(), sm)) {
				sm.updateShield();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {

		Player p = e.getPlayer();
		if (ShieldManager.globalForceFieldToggle) {
			ArrayList<Location> newList = new ArrayList<Location>();
			ArrayList<Location> oldList = new ArrayList<Location>();
			if (map.containsKey(p.getUniqueId())) {
				oldList = map.get(p.getUniqueId());
			}
			for (int x = -2; x <= 2; x++) {
				for (int y = 0; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
						Location scanLoc = p.getLocation().add(x, y, z);
						if (!ShieldManager.isPlayerAllowed(p, scanLoc)) {
							ShieldManager shield = ShieldManager
									.getShieldManager(scanLoc);
							Location highPt = shield.getProtectionInstance()
									.getHighestLocation();
							Location lowPt = shield.getProtectionInstance()
									.getLowestLocation();

							if (scanLoc.getBlockX() == highPt.getBlockX()
									|| scanLoc.getBlockZ() == highPt
											.getBlockZ()
									|| scanLoc.getBlockX() == lowPt.getBlockX()
									|| scanLoc.getBlockZ() == lowPt.getBlockZ()) {
								if (ShieldManager.getShieldManager(scanLoc)
										.isForceFieldEnabled()) {
									p.sendBlockChange(scanLoc,
											Material.STAINED_GLASS, (byte) 11);
									oldList.remove(scanLoc);
									newList.add(scanLoc);

								}
							}

						}

					}

				}
			}

			for (Location x : oldList) {
				p.sendBlockChange(x, x.getBlock().getType(), x.getBlock()
						.getData());
			}
			map.put(p.getUniqueId(), newList);
			if (!ShieldManager.isPlayerAllowed(p, p.getLocation())) {
				if (!lastLoc.containsKey(p.getUniqueId())) {
					lastLoc.put(p.getUniqueId(), p.getLocation());
				} else {
					if (ShieldManager.getShieldManager(p.getLocation())
							.isForceFieldEnabled()) {
						p.teleport(lastLoc.get(p.getUniqueId()));
					}
				}

			} else {
				lastLoc.put(p.getUniqueId(), p.getLocation());
			}
		}
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
