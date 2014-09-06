package io.github.asienpanda.ShieldGenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ProtectionManager {
	ShieldManager sMInstance;
	private int radius;
	private Location highestLoc;
	private Location lowestLoc;

	public ProtectionManager(ShieldManager s) {
		sMInstance = s;
		setRadius();
		defineProtectRegion();

	}

	public boolean scanForOtherShields() {
		boolean foundOther = false;
		World world = sMInstance.getBlock().getWorld();
		for (int x = lowestLoc.getBlockX(); x <= highestLoc.getBlockX(); x++) {
			for (int y = lowestLoc.getBlockY(); y <= highestLoc.getBlockY(); y++) {
				for (int z = lowestLoc.getBlockZ(); z <= highestLoc.getBlockZ(); z++) {
					Location loc = new Location(world, x, y, z);
					int pX = loc.getBlockX();
					int pY = loc.getBlockY();
					int pZ = loc.getBlockZ();
					String pW = loc.getWorld().getName();
					ShieldManager shield = null;
					for (ShieldManager sm : ShieldManager.shieldList) {
						if (sm != sMInstance) {
							int hX = sm.getProtectionInstance()
									.getHighestLocation().getBlockX();
							int hY = sm.getProtectionInstance()
									.getHighestLocation().getBlockY();
							int hZ = sm.getProtectionInstance()
									.getHighestLocation().getBlockZ();
							int lX = sm.getProtectionInstance()
									.getLowestLocation().getBlockX();
							int lY = sm.getProtectionInstance()
									.getLowestLocation().getBlockY();
							int lZ = sm.getProtectionInstance()
									.getLowestLocation().getBlockZ();
							String w = sm.getProtectionInstance()
									.getHighestLocation().getWorld().getName();

							if (pW.equals(w) && pX <= hX && pY <= hY
									&& pZ <= hZ && pX >= lX && pY >= lY
									&& pZ >= lZ) {
								shield = sm;
							}
						}
					}

					if (shield == null) {
						foundOther = false;
					} else if (shield != sMInstance) {
						return true;
					} else {
						foundOther = false;
					}
				}
			}
		}
		return foundOther;
	}

	private void setRadius() {
		Block startingBlock = sMInstance.getBlock().getRelative(0, -1, 0);
		Material pyramidMaterial = startingBlock.getType();
		int powerLevel;
		if (pyramidMaterial == Material.DIAMOND_BLOCK
				|| pyramidMaterial == Material.EMERALD_BLOCK
				|| pyramidMaterial == Material.GOLD_BLOCK
				|| pyramidMaterial == Material.IRON_BLOCK) {
			powerLevel = ShieldGenerator.getInstance().getConfig()
					.getConfigurationSection("Power Levels")
					.getInt(pyramidMaterial.toString());

			Block currentBlock = startingBlock;
			int range = 1;
			int lowerRange = -1;
			int completedLevels = 0;
			boolean completeBase = true;
			while (completeBase) {

				for (int x = lowerRange; x <= range; x++) {
					for (int z = lowerRange; z <= range; z++) {
						currentBlock = startingBlock.getRelative(x, 0, z);
						if (currentBlock.getType() != pyramidMaterial) {
							completeBase = false;
						}
					}
				}
				if (completeBase) {
					completedLevels = completedLevels + 1;
				}
				startingBlock = startingBlock.getRelative(0, -1, 0);
				range++;
				lowerRange--;
			}

			radius = powerLevel * completedLevels;
		} else {
			radius = 0;
		}

	}

	public void defineProtectRegion() {
		Location origin = sMInstance.getBlock().getLocation();
		int originX = origin.getBlockX();
		int originZ = origin.getBlockZ();
		World originWorld = origin.getWorld();
		if (radius < 1) {
			highestLoc = sMInstance.getBlock().getLocation();
			lowestLoc = sMInstance.getBlock().getLocation();
		} else {
			highestLoc = new Location(originWorld, originX + radius, 255,
					originZ + radius);
			lowestLoc = new Location(originWorld, originX - radius, 0, originZ
					- radius);
		}

	}

	public int getRadius() {
		return radius;
	}

	public Location getHighestLocation() {
		return highestLoc;
	}

	public Location getLowestLocation() {
		return lowestLoc;
	}

}
