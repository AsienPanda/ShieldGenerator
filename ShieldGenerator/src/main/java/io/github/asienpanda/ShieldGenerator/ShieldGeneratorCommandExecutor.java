package io.github.asienpanda.ShieldGenerator;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShieldGeneratorCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("shieldgenerator")
				|| command.getName().equalsIgnoreCase("shield")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("setname")) {
					if (sender.hasPermission("shieldgenerator.player.setname")
							|| sender
									.hasPermission("shieldgenerator.admin.setname")) {
						if (args.length == 2) {
							if (sender instanceof Player) {
								Player p = (Player) sender;
								if (ShieldManager.mustNameBeaconMap
										.containsKey(p.getUniqueId())) {
									if (!ShieldManager
											.shieldListContains(args[1])) {
										Location loc = ShieldManager.mustNameBeaconMap
												.get(p.getUniqueId());
										ShieldManager sm = ShieldManager
												.getShieldManager(loc);
										sm.name = args[1];
										p.sendMessage(String
												.format("%sYour shield has been named to %s%s%s!",
														ChatColor.GREEN,
														ChatColor.AQUA,
														sm.name,
														ChatColor.GREEN));

										ShieldManager.mustNameBeaconMap
												.remove(p.getUniqueId());
										sm.updateShield();
										return true;
									} else {
										p.sendMessage(String
												.format("%sThe name, %s%s%s, already exists!",
														ChatColor.RED,
														ChatColor.GOLD,
														args[1], ChatColor.RED));
										return true;
									}
								} else {
									sender.sendMessage(ChatColor.RED
											+ "You must specify the shield to be renamed!");
									sender.sendMessage(ChatColor.RED
											+ "/shieldgenerator setname [OldName] [NewName]");
									return true;
								}
							} else {
								sender.sendMessage(ChatColor.RED
										+ "You must specify the shield to be renamed!");
								sender.sendMessage(ChatColor.RED
										+ "/shieldgenerator [OldName] [NewName]");
								return true;
							}
						} else if (args.length > 2) {

							if (ShieldManager.shieldListContains(args[1])) {
								ShieldManager tempShield = ShieldManager
										.getShieldManager(args[1]);
								if (sender instanceof Player) {
									Player p = (Player) sender;
									if (p.getUniqueId().equals(
											tempShield.getMemberUUIDs().get(0))
											|| sender
													.hasPermission("shieldgenerator.admin.setname")) {
										tempShield.name = args[2];
										sender.sendMessage(String
												.format("%sYour shield has been renamed to %s%s%s!",
														ChatColor.GREEN,
														ChatColor.AQUA,
														tempShield.name,
														ChatColor.GREEN));
										return true;
									} else {
										p.sendMessage(String
												.format("%sYou are not the owner of %s%s%s!",
														ChatColor.RED,
														ChatColor.LIGHT_PURPLE,
														tempShield.name,
														ChatColor.RED));
										return true;
									}
								} else {
									if (sender
											.hasPermission("shieldgenerator.admin.setname")) {
										tempShield.name = args[2];
										sender.sendMessage(String
												.format("%sYour shield has been renamed to %s%s%s!",
														ChatColor.GREEN,
														ChatColor.AQUA,
														tempShield.name,
														ChatColor.GREEN));
										return true;
									}

								}
							} else {
								sender.sendMessage(String.format(
										"%s%s %sdoes not exist!",
										ChatColor.GOLD, args[1], ChatColor.RED));
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "Not enough arguments!");
							sender.sendMessage(ChatColor.RED
									+ "/shieldgenerator <OldName> [NewName]");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				}

				else if (args[0].equalsIgnoreCase("addmember")) {
					if (sender
							.hasPermission("shieldgenerator.player.editmembers")
							|| sender
									.hasPermission("shieldgenerator.admin.editmembers")) {
						if (args.length >= 3) {
							if (ShieldManager.shieldListContains(args[1])) {
								boolean isPlayerAndOwner = false;
								ShieldManager sm = ShieldManager
										.getShieldManager(args[1]);
								if (sender instanceof Player) {
									Player p = (Player) sender;
									if (p.getUniqueId().equals(
											sm.getMemberUUIDs().get(0))) {
										isPlayerAndOwner = true;
									}

								}
								if (sender
										.hasPermission("shieldgenerator.admin.editmembers")
										|| isPlayerAndOwner) {
									Player[] pList = ShieldGenerator
											.getInstance().getServer()
											.getOnlinePlayers();
									boolean isOnline = false;
									UUID newMemberUUID = null;
									for (Player p : pList) {
										if (p.getName().equalsIgnoreCase(
												args[2])) {
											newMemberUUID = p.getUniqueId();
											isOnline = true;
										}
									}

									if (isOnline) {
										if (!sm.getMemberUUIDs().contains(
												newMemberUUID)) {

											sm.addMember(newMemberUUID);
											sender.sendMessage(String
													.format("%s%s%s, has been added as a member of %s%s%s!",
															ChatColor.AQUA,
															args[2],
															ChatColor.GREEN,
															ChatColor.LIGHT_PURPLE,
															args[1],
															ChatColor.GREEN));
											return true;

										} else {
											sender.sendMessage(String
													.format("%s%s%s is already a member of %s%s%s!",
															ChatColor.GOLD,
															args[2],
															ChatColor.RED,
															ChatColor.LIGHT_PURPLE,
															args[1],
															ChatColor.RED));
											return true;
										}

									} else {
										sender.sendMessage(String
												.format("%s%s%s is not an online player!",
														ChatColor.GOLD,
														args[2], ChatColor.RED));
										return true;
									}
								} else {
									sender.sendMessage(String
											.format("%sYou do not have permission to add a member to %s%s%s!",
													ChatColor.RED,
													ChatColor.LIGHT_PURPLE,
													args[1], ChatColor.RED));
									return true;
								}

							} else {
								sender.sendMessage(String
										.format("%sThe shield, %s%s%s, does not exist!",
												ChatColor.RED, ChatColor.GOLD,
												args[1], ChatColor.RED));
								sender.sendMessage(ChatColor.RED
										+ "/shieldgenerator addmember [ShieldName] [NewMemberName]");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "Not enough arguments!");
							sender.sendMessage(ChatColor.RED
									+ "/shieldgenerator addmember [ShieldName] [NewMemberName]");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("delmember")) {
					if (sender
							.hasPermission("shieldgenerator.player.editmembers")
							|| sender
									.hasPermission("shieldgenerator.admin.editmembers")) {
						if (args.length >= 3) {
							if (ShieldManager.shieldListContains(args[1])) {
								boolean isPlayerAndOwner = false;
								ShieldManager sm = ShieldManager
										.getShieldManager(args[1]);
								if (sender instanceof Player) {
									Player p = (Player) sender;
									if (p.getUniqueId().equals(
											sm.getMemberUUIDs().get(0))) {
										isPlayerAndOwner = true;
									}

								}
								if (sender
										.hasPermission("shieldgenerator.admin.editmembers")
										|| isPlayerAndOwner) {
									OfflinePlayer[] pList = ShieldGenerator
											.getInstance().getServer()
											.getOfflinePlayers();
									boolean wasOnline = false;
									UUID memberUUID = null;
									for (OfflinePlayer p : pList) {
										if (p.getName().equalsIgnoreCase(
												args[2])) {
											memberUUID = p.getUniqueId();
											wasOnline = true;
										}
									}

									if (wasOnline) {
										if (sm.getMemberUUIDs().contains(
												memberUUID)) {
											if (memberUUID.equals(sm
													.getMemberUUIDs().get(0))) {
												sender.sendMessage(ChatColor.RED
														+ "The owner of a shield cannot be removed!");
												return true;
											} else {
												sm.delMember(memberUUID);
												sender.sendMessage(String
														.format("%s%s%s, has been deleted as a member of %s%s%s!",
																ChatColor.AQUA,
																args[2],
																ChatColor.GREEN,
																ChatColor.LIGHT_PURPLE,
																args[1],
																ChatColor.GREEN));
												return true;
											}

										} else {
											sender.sendMessage(String
													.format("%s%s%s is not a valid member of %s%s%s!",
															ChatColor.GOLD,
															args[2],
															ChatColor.RED,
															ChatColor.LIGHT_PURPLE,
															args[1],
															ChatColor.RED));
											return true;
										}

									} else {
										sender.sendMessage(String
												.format("%s%s%s has never been online!",
														ChatColor.GOLD,
														args[2], ChatColor.RED));
										return true;
									}
								} else {
									sender.sendMessage(String
											.format("%sYou do not have permission to delete a member from %s%s%s!",
													ChatColor.RED,
													ChatColor.LIGHT_PURPLE,
													args[1], ChatColor.RED));
									return true;
								}

							} else {
								sender.sendMessage(String
										.format("%sThe shield, %s%s%s, does not exist!",
												ChatColor.RED, ChatColor.GOLD,
												args[1], ChatColor.RED));
								sender.sendMessage(ChatColor.RED
										+ "/shieldgenerator delmember [ShieldName] [MemberName]");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "Not enough arguments!");
							sender.sendMessage(ChatColor.RED
									+ "/shieldgenerator delmember [ShieldName] [MemberName]");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}

				} else if (args[0].equalsIgnoreCase("list")) {
					if (sender.hasPermission("shieldgenerator.admin.list")) {
						String shield = "shield";
						if (ShieldManager.shieldList.size() > 1) {
							shield = "shields";
						}
						sender.sendMessage(String.format(
								"%s%s %s total in the server!",
								ShieldManager.shieldList.size(),
								ChatColor.DARK_AQUA, shield));
						ArrayList<String> nameList = new ArrayList<String>();
						for (ShieldManager sm : ShieldManager.shieldList) {
							nameList.add(sm.name);
						}
						sender.sendMessage(ChatColor.LIGHT_PURPLE
								+ nameList.toString());
						return true;
					} else if (sender
							.hasPermission("shieldgenerator.player.list")) {
						if (sender instanceof Player) {
							Player p = (Player) sender;
							UUID pUUID = p.getUniqueId();
							ArrayList<String> smList = new ArrayList<String>();
							for (ShieldManager sm : ShieldManager.shieldList) {
								if (sm.getMemberUUIDs().get(0).equals(pUUID)) {
									smList.add(sm.name);
								}
							}
							String shield = "shield";
							if (smList.size() > 1) {
								shield = "shields";
							}
							sender.sendMessage(String.format(
									"%sYou have %s%s%s %s!",
									ChatColor.DARK_AQUA, ChatColor.RESET,
									smList.size(), ChatColor.DARK_AQUA, shield));
							sender.sendMessage(ChatColor.LIGHT_PURPLE
									+ smList.toString());
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("info")) {
					if (sender.hasPermission("shieldgenerator.admin.info")) {
						if (args.length >= 2) {
							if (ShieldManager.shieldListContains(args[1])) {
								ShieldManager sm = ShieldManager
										.getShieldManager(args[1]);

								sender.sendMessage(String.format(
										"%sName : %s%s",
										ChatColor.LIGHT_PURPLE,
										ChatColor.RESET, sm.name));
								String status = "";
								Location tempLoc = sm.getBlock().getLocation();
								String worldName = tempLoc.getWorld()
										.toString();
								String[] list = {
										worldName.substring(
												worldName.lastIndexOf("=") + 1,
												worldName.length() - 1),
										tempLoc.getBlockX() + "",
										tempLoc.getBlockY() + "",
										tempLoc.getBlockZ() + "" };
								sender.sendMessage(String.format(
										"%sLocation : %s%s, %s, %s, %s",
										ChatColor.LIGHT_PURPLE,
										ChatColor.RESET, list[0], list[1],
										list[2], list[3]));
								if (sm.isShieldEnabled())
									status = String.format("%s[ONLINE]",
											ChatColor.GREEN);
								else
									status = String.format("%s[OFFLINE]",
											ChatColor.RED);
								sender.sendMessage(String.format(
										"%sCurrent Status : %s%s",
										ChatColor.LIGHT_PURPLE,
										ChatColor.RESET, status));
								sender.sendMessage(String.format(
										"%sOwner : %s%s",
										ChatColor.LIGHT_PURPLE,
										ChatColor.RESET,
										ShieldGenerator
												.getInstance()
												.getServer()
												.getOfflinePlayer(
														sm.getMemberUUIDs()
																.get(0))
												.getName()));
								sender.sendMessage(String.format(
										"%sMembers : %s%s",
										ChatColor.LIGHT_PURPLE,
										ChatColor.RESET, sm.getMemberNames()));

								sender.sendMessage(String.format(
										"%sRadius : %s%s",
										ChatColor.LIGHT_PURPLE,
										ChatColor.RESET, sm
												.getProtectionInstance()
												.getRadius()));
								return true;

							} else {
								sender.sendMessage(String.format(
										"%s%s %sdoes not exist!",
										ChatColor.GOLD, args[1], ChatColor.RED));
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "Not enough arguments!");
							sender.sendMessage(ChatColor.RED
									+ "/shieldgenerator info <Shield_Name>");
							return true;
						}
					} else if (sender
							.hasPermission("shieldgenerator.player.info")) {
						if (args.length >= 2) {
							if (sender instanceof Player) {
								Player p = (Player) sender;
								if (ShieldManager.shieldListContains(args[1])) {
									ShieldManager sm = ShieldManager
											.getShieldManager(args[1]);
									if (ShieldManager.isMember(p, sm)) {

										sender.sendMessage(String.format(
												"%sName : %s%s",
												ChatColor.LIGHT_PURPLE,
												ChatColor.RESET, sm.name));
										String status = "";
										Location tempLoc = sm.getBlock()
												.getLocation();
										String worldName = tempLoc.getWorld()
												.toString();
										String[] list = {
												worldName.substring(worldName
														.lastIndexOf("=") + 1,
														worldName.length() - 1),
												tempLoc.getBlockX() + "",
												tempLoc.getBlockY() + "",
												tempLoc.getBlockZ() + "" };
										sender.sendMessage(String
												.format("%sLocation : %s%s, %s, %s, %s",
														ChatColor.LIGHT_PURPLE,
														ChatColor.RESET,
														list[0], list[1],
														list[2], list[3]));
										if (sm.isShieldEnabled())
											status = String.format(
													"%s[ONLINE]",
													ChatColor.GREEN);
										else
											status = String.format(
													"%s[OFFLINE]",
													ChatColor.RED);
										sender.sendMessage(String.format(
												"%sCurrent Status : %s%s",
												ChatColor.LIGHT_PURPLE,
												ChatColor.RESET, status));
										sender.sendMessage(String
												.format("%sOwner : %s%s",
														ChatColor.LIGHT_PURPLE,
														ChatColor.RESET,
														ShieldGenerator
																.getInstance()
																.getServer()
																.getOfflinePlayer(
																		sm.getMemberUUIDs()
																				.get(0))
																.getName()));
										sender.sendMessage(String.format(
												"%sMembers : %s%s",
												ChatColor.LIGHT_PURPLE,
												ChatColor.RESET, sm
														.getMemberNames()
														.toString()));

										sender.sendMessage(String.format(
												"%sRadius : %s%s",
												ChatColor.LIGHT_PURPLE,
												ChatColor.RESET,
												sm.getProtectionInstance()
														.getRadius()));
										return true;

									} else {
										p.sendMessage(String
												.format("%sYou are not a member of %s%s%s!",
														ChatColor.RED,
														ChatColor.LIGHT_PURPLE,
														sm.name, ChatColor.RED));
									}
								} else {
									sender.sendMessage(String.format(
											"%s%s %sdoes not exist!",
											ChatColor.GOLD, args[1],
											ChatColor.RED));
									return true;
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "Not enough arguments!");
							sender.sendMessage(ChatColor.RED
									+ "/shieldgenerator info <ShieldName>");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("toggle")) {
					if (args.length >= 3) {
						if (ShieldManager.shieldListContains(args[1])) {
							switch (args[2].toLowerCase()) {
							case "antigrief":
								ShieldManager.getShieldManager(args[1])
										.toggleAntiGriefEnabled();
							case "forcefield":
								ShieldManager.getShieldManager(args[1])
										.toggleForceFieldEnabled();
							default:
								sender.sendMessage(ChatColor.RED
										+ "Not a valid toggle name!");
								sender.sendMessage(ChatColor.RED
										+ "/shieldgenerator toggle [ShieldName] <antigrief/forcefield>");
							}
						} else {
							sender.sendMessage(String.format(
									"%s%s %sdoes not exist!",
									ChatColor.GOLD, args[1], ChatColor.RED));
							return true;

						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ "Not enough arguments!");
						sender.sendMessage(ChatColor.RED
								+ "/shieldgenerator toggle [ShieldName] <antigrief/forcefield>");
						return true;
					}
				}
				return false;
			}
			return false;

		}
		return false;
	}
}
