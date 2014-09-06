package io.github.asienpanda.ShieldGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class ShieldManager {
	ShieldGenerator plugin = ShieldGenerator.getInstance();
	public static boolean globalAntiGriefToggle = false;
	public static boolean globalForceFieldToggle = false;
	public static boolean globalMobSpawnToggle = false;
	public static boolean globalPVPToggle = false;
	
	private boolean isAntiGriefEnabled = false;
	private boolean isForceFieldEnabled = true;
	private boolean isAntiMobSpawnEnabled = false;
	private boolean isAntiPVPEnabled = false;
	
	private Block b;
	private Block adjPower;
	private Block oppBlock;
	private BlockFace signFace;
	private boolean shieldEnabled = false;
	private ArrayList<UUID> members = new ArrayList<UUID>();
	public static ArrayList<ShieldManager> shieldList = new ArrayList<ShieldManager>();
	public static HashMap<UUID, Location> mustNameBeaconMap = new HashMap<UUID, Location>();
	public static final String Line0 = ChatColor.AQUA + "[SHIELD]";
	private ProtectionManager pm;
	public String name = "";

	public ShieldManager(Block b, Block adj, UUID playerUUID) {
		this.b = b;
		adjPower = adj;
		shieldList.add(this);
		members.add(playerUUID);
		pm = new ProtectionManager(this);
		mustNameBeaconMap.put(playerUUID, b.getLocation());
		shieldEnabled = true;
		if (pm.scanForOtherShields()) {
			mustNameBeaconMap.remove(playerUUID);

			for (UUID uuid : members) {
				Player p = plugin.getServer().getPlayer(uuid);
				p.sendMessage(ChatColor.RED
						+ "Shield generator cannot be created here! Shields cannot overlap!");

			}
			shieldList.remove(this);
			b.breakNaturally();
		}
	}

	public ShieldManager(String name, Block block,Block adj, ArrayList<UUID> members,
			Boolean isEnabled) {
		this.name = name;
		b = block;
		adjPower = adj;
		this.members = members;
		if (b.isBlockPowered()) {
			shieldEnabled = true;
		} else {
			shieldEnabled = false;
		}
		shieldList.add(this);
		

	}

	public void updateShield() {
		if (b.getType() != Material.BEACON) {

			shieldList.remove(this);

		} else if (b.isBlockPowered()) {
			pm = new ProtectionManager(this);
			if (mustNameBeaconMap.containsKey(members.get(0))) {
				if (mustNameBeaconMap.get(members.get(0)).equals(
						b.getLocation())
						&& shieldEnabled) {
					plugin.getServer()
							.getPlayer(members.get(0))
							.sendMessage(
									ChatColor.GOLD
											+ "You must name your shield generator!");
					plugin.getServer()
							.getPlayer(members.get(0))
							.sendMessage(
									ChatColor.AQUA
											+ "/shieldgenerator setname <name>");
					shieldEnabled = false;
				}
			} else if (pm.getRadius() < 1) {
				plugin.getServer()
						.getPlayer(members.get(0))
						.sendMessage(
								ChatColor.GOLD
										+ "No pyramid detected! Shield generator automatically put "
										+ ChatColor.RED + "[OFFLINE]"
										+ ChatColor.GOLD + "!");
				shieldEnabled = false;
			} else {
				shieldEnabled = true;
			}

		} else {
			boolean allOffline = true;
			for (UUID uuid : members) {

				if (plugin.getServer().getPlayer(uuid).isOnline()) {
					allOffline = false;
				}
			}

			if (allOffline) {
				shieldEnabled = true;
				plugin.getServer().broadcastMessage(
						String.format("%sThe shield, %s%s%s, is now online!",
								ChatColor.AQUA, ChatColor.LIGHT_PURPLE, name,
								ChatColor.AQUA));
			} else {
				shieldEnabled = false;
			}
		}
		setupSign();
	}

	private void setupSign() {
		BlockFace oppFace = b.getFace(adjPower).getOppositeFace();
		signFace = oppFace;
		oppBlock = b.getRelative(oppFace);
		if (oppBlock.getType() == Material.REDSTONE_WIRE) {
			oppBlock.setType(Material.AIR);
		}
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
		if (name.length() > 15) {
			s.setLine(2, name.substring(0, 15));
		} else {
			s.setLine(2, name);
		}
		if (shieldEnabled) {
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
				if (s.getLine(0).equalsIgnoreCase(Line0)
						&& !bf.equals(signFace)) {
					adjBlock.setType(Material.AIR);
				}
			}
		}
	}

	public static void deleteShield(Block b) {
		getShieldManager(b).oppBlock.setType(Material.AIR);
		shieldList.remove(getShieldManager(b));
	}

	public void setShieldEnabled(boolean enabled) {
		shieldEnabled = enabled;
	}

	public void setAdjacentPower(Block b) {
		adjPower = b;
	}

	public Block getBlock() {
		return b;

	}

	public boolean isShieldEnabled() {
		return shieldEnabled;
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
		return sM.shieldEnabled;

	}

	public static boolean shieldListContains(Block beaconBlock) {
		Location bLoc = beaconBlock.getLocation();
		if (shieldList != null) {
			for (ShieldManager sM : shieldList) {
				if (bLoc.equals(sM.getBlock().getLocation())) {
					return true;
				}
			}
		}
		return false;

	}

	public static boolean shieldListContains(String name) {
		for (ShieldManager sm : shieldList) {
			if (sm.name.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;

	}

	public static ShieldManager getShieldManager(Block b) {
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

	public static boolean isMember(Player player, ShieldManager sm) {
		return (sm.members.contains(player));
	}

	public static ShieldManager getShieldManager(Location loc) {
		int pX = loc.getBlockX();
		int pY = loc.getBlockY();
		int pZ = loc.getBlockZ();
		String pW = loc.getWorld().getName();
		ShieldManager shield = null;
		for (ShieldManager sm : ShieldManager.shieldList) {
			int hX = sm.pm.getHighestLocation().getBlockX();
			int hY = sm.pm.getHighestLocation().getBlockY();
			int hZ = sm.pm.getHighestLocation().getBlockZ();
			int lX = sm.pm.getLowestLocation().getBlockX();
			int lY = sm.pm.getLowestLocation().getBlockY();
			int lZ = sm.pm.getLowestLocation().getBlockZ();
			String w = sm.pm.getHighestLocation().getWorld().getName();

			if (pW.equals(w) && pX <= hX && pY <= hY && pZ <= hZ && pX >= lX
					&& pY >= lY && pZ >= lZ) {
				shield = sm;
			}
		}
		return shield;
	}

	public static ShieldManager getShieldManager(String name) {
		for (ShieldManager sm : shieldList) {
			if (sm.name.equalsIgnoreCase(name)) {
				return sm;
			}
		}
		return null;
	}

	public static boolean isPlayerAllowed(Player p, Location loc) {

		ShieldManager shield = getShieldManager(loc);
		if (shield != null && shield.isShieldEnabled()) {
			if (shield.members.contains(p.getUniqueId())) {
				return true;
			} else {
				return false;
			}
		}
		return true;

	}

	public ProtectionManager getProtectionInstance() {
		return pm;
	}

	public ArrayList<UUID> getMemberUUIDs() {
		return members;
	}

	public ArrayList<String> getMemberNames() {
		ArrayList<String> namesList = new ArrayList<String>();
		for (UUID uuid : members) {
			namesList.add(plugin.getServer().getOfflinePlayer(uuid).getName());
		}
		return namesList;
	}

	public void addMember(UUID uuid) {
		members.add(uuid);
	}

	public void delMember(UUID uuid) {
		members.remove(uuid);
	}
	
	public Block getAdjPowerBlock (){
		return adjPower;
	}
	
	public Block getSignBlock(){
		return oppBlock;
	}
	
	public boolean isAntiGriefEnabled(){
		return isAntiGriefEnabled;
	}
	
	public boolean isForceFieldEnabled(){
		return isForceFieldEnabled;
	}
	
	public boolean isAntiMobSpawnEnabled(){
		return isAntiMobSpawnEnabled();
	}
	
	public boolean isAntiPVPEnabled(){
		return isAntiPVPEnabled();
	}
	
	public void toggleAntiGriefEnabled(){
		isAntiGriefEnabled = !isAntiGriefEnabled;
	}
	
	public void toggleForceFieldEnabled(){
		 isForceFieldEnabled = !isForceFieldEnabled;
	}
	
	public void toggleAntiMobSpawnEnabled(){
		isAntiMobSpawnEnabled = !isAntiMobSpawnEnabled;
	}
	
	public void toggleAntiPVPEnabled(){
		isAntiPVPEnabled = !isAntiPVPEnabled;
	}
}
