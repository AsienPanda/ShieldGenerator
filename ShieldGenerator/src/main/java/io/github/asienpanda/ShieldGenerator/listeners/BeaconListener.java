package io.github.asienpanda.ShieldGenerator.listeners;

import io.github.asienpanda.ShieldGenerator.ShieldGenerator;
import io.github.asienpanda.ShieldGenerator.ShieldManager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BeaconListener implements Listener {
	ShieldGenerator plugin;
	private HashMap<Block, Block> onlineQueueMap = new HashMap<Block, Block>();
	private HashMap<Block, Block> offlineQueueMap = new HashMap<Block, Block>();
	private HashMap<Location, UUID> newOwnerMap = new HashMap<Location, UUID>();

	public BeaconListener() {
		plugin = ShieldGenerator.getInstance();
	}

	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent e) {
		Block triggeringBlock = e.getBlock();

		// Block b must a redstonewire, a redstone torch, a lever, or button

		if (e.getOldCurrent() == 0 && e.getNewCurrent() > 0) {
			final BlockFace adjFaces[] = { BlockFace.NORTH, BlockFace.EAST,
					BlockFace.SOUTH, BlockFace.WEST };

			for (BlockFace bf : adjFaces) {
				Block adjBlock = triggeringBlock.getRelative(bf);
				if (adjBlock.getType() == Material.BEACON) {

					if (!adjBlock.isBlockPowered()) {
						onlineQueueMap.put(adjBlock, triggeringBlock);
					} else {
						triggeringBlock.breakNaturally();
					}

				}
			}
		} else if (e.getOldCurrent() > 0 && e.getNewCurrent() == 0) {
			final BlockFace adjFaces[] = { BlockFace.NORTH, BlockFace.EAST,
					BlockFace.SOUTH, BlockFace.WEST };

			for (BlockFace bf : adjFaces) {
				Block adjBlock = triggeringBlock.getRelative(bf);
				if (adjBlock.getType() == Material.BEACON) {
					if (adjBlock.isBlockPowered()) {
						offlineQueueMap.put(adjBlock, triggeringBlock);
					}

				}
			}
		}
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e) {
		if (e.getBlock().getType() == Material.BEACON) {
			if (onlineQueueMap.containsKey(e.getBlock())) {

				if (e.getBlock().isBlockPowered()) {
					boolean alreadyExists = false;
					if (ShieldManager.shieldList != null) {
							if (ShieldManager.shieldListContains(e.getBlock())) {
								alreadyExists = true;
								ShieldManager.getInstance(e.getBlock()).setAdjacentPower(onlineQueueMap.get(e.getBlock()));
								ShieldManager.getInstance(e.getBlock()).updateShield();
							}
						
					}

					if (!alreadyExists) {
						if (newOwnerMap.containsKey(e.getBlock().getLocation())) {
							UUID newOwnerUUID = newOwnerMap.get(e.getBlock()
									.getLocation());
							ShieldManager sM = new ShieldManager(e.getBlock(),
									onlineQueueMap.get(e.getBlock()),
									newOwnerUUID);
							sM.updateShield();
							newOwnerMap.remove(e.getBlock().getLocation());
						}
						else{
							e.getBlock().breakNaturally();
						}
					}

				}
				onlineQueueMap.remove(e.getBlock());
			} else if (offlineQueueMap.containsKey(e.getBlock())) {
				if (!e.getBlock().isBlockPowered()) {
					ShieldManager sM = null;
					if(ShieldManager.shieldListContains(e.getBlock())){
						sM = ShieldManager.getInstance(e.getBlock());
					}
					if (sM != null) {
						sM.updateShield();
					}
				}
				offlineQueueMap.remove(e.getBlock());
			}

		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.BEACON) {
			newOwnerMap.put(e.getBlock().getLocation(), e.getPlayer()
					.getUniqueId());
		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (b.getType() == Material.WALL_SIGN) {
			Sign s = (Sign) b.getState();
			if (s.getLine(0).equalsIgnoreCase(ShieldManager.Line0)) {
				final BlockFace adjFaces[] = { BlockFace.NORTH, BlockFace.EAST,
						BlockFace.SOUTH, BlockFace.WEST };

				for (BlockFace bf : adjFaces) {
					Block adjBlock = b.getRelative(bf);
					if (adjBlock.getType() == Material.BEACON) {
						if (ShieldManager.shieldListContains(adjBlock)){
							e.setCancelled(true);
						}
					}
				}
			}
		} else if (b.getType() == Material.BEACON) {
			if (ShieldManager.shieldListContains(b)) {
				if (ShieldManager.isShieldEnabled(b)) {
					e.setCancelled(true);
				}
			}
			if(newOwnerMap.containsKey(b.getLocation())){
				newOwnerMap.remove(b.getLocation());
			}
		}
	}
}
