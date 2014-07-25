package io.github.asienpanda.ShieldGenerator;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class ShieldManager {
	ShieldGenerator plugin;
	private Block b;
	private Block adjPower;
	private BlockFace signFace;
	private boolean isShieldEnabled = false;
	private ArrayList<UUID> members = new ArrayList<UUID>();
	public static ArrayList<ShieldManager> shieldList = new ArrayList<ShieldManager>();
	public static final String Line0 = ChatColor.AQUA + "[SHIELD]";

	public ShieldManager(Block b, Block adj, UUID playerUUID) {
		if (shieldList == null) {
			shieldList = new ArrayList<ShieldManager>();
		}
		this.b = b;
		adjPower = adj;
		shieldList.add(this);
		members.add(playerUUID);
		plugin = ShieldGenerator.getInstance();
	}

	public void updateShield() {
		if (b.isBlockPowered()) {
			isShieldEnabled = true;
		} else {
			isShieldEnabled = false;
		}
		setupSign();
	}

	private void setupSign() {
		BlockFace oppFace = b.getFace(adjPower).getOppositeFace();
		signFace = oppFace;
		Block oppBlock = b.getRelative(oppFace);

		oppBlock.setType(Material.WALL_SIGN);
		Sign s = (Sign) oppBlock.getState();
		org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(
				Material.WALL_SIGN);
		matSign.setFacingDirection(oppFace);
		s.setData(matSign);
		s.setLine(0, Line0);
		s.setLine(
				1,
				ChatColor.LIGHT_PURPLE
						+ plugin.getServer().getPlayer(members.get(0))
								.getName());
		if (isShieldEnabled) {
			s.setLine(3, ChatColor.GREEN + "[ONLINE]");
		} else {
			s.setLine(3, ChatColor.RED + "[OFFLINE]");
		}
		s.update();

		clearOldSigns();
	}

	private void clearOldSigns() {
		final BlockFace adjFaces[] = { BlockFace.NORTH, BlockFace.EAST,
				BlockFace.SOUTH, BlockFace.WEST };

		for (BlockFace bf : adjFaces) {
			Block adjBlock = b.getRelative(bf);
			if (adjBlock.getType() == Material.WALL_SIGN) {
				Sign s = (Sign) adjBlock.getState();
				if (s.getLine(0).equalsIgnoreCase(Line0) && !bf.equals(signFace)) {
					adjBlock.setType(Material.AIR);
				}
			}
		}
	}

	public static void deleteShield(Block b) {

		shieldList.remove(getInstance(b));
	}

	public void setAdjacentPower(Block b) {
		adjPower = b;

	}

	public Block getBlock() {
		return b;

	}

	public boolean isShieldEnabled() {
		return isShieldEnabled;
	}

	public static boolean isShieldEnabled(Block b) {
		ShieldManager sM = null;
		Location bLoc = b.getLocation();
		for (ShieldManager x : shieldList) {
			if (bLoc.equals(x.getBlock().getLocation())) {
				sM = x;
			}
		}
		if (sM == null) {
			return false;
		}
		return sM.isShieldEnabled;

	}

	public static boolean shieldListContains(Block b) {
		Location bLoc = b.getLocation();
		if (shieldList != null) {
			for (ShieldManager sM : shieldList) {
				if (bLoc.equals(sM.getBlock().getLocation())) {
					return true;
				}
			}
		}
		return false;

	}

	public static ShieldManager getInstance(Block b) {
		Location bLoc = b.getLocation();
		if (shieldList != null) {
			for (ShieldManager sM : shieldList) {
				if (bLoc.equals(sM.getBlock().getLocation())) {
					return sM;
				}
			}
		}
		return null;
	}
}
