package io.github.asienpanda.ShieldGenerator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class ShieldGeneratorStore {
	@SuppressWarnings("unchecked")
	public static void load(String path) {
		ArrayList<HashMap<String, Object>> result = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			if (br.readLine() == null) {
				result = new ArrayList<HashMap<String, Object>> ();
			} else {
				try {

					ObjectInputStream ois = new ObjectInputStream(
							new FileInputStream(path));
					result = (ArrayList<HashMap<String, Object>>) ois.readObject();
					ois.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(HashMap<String, Object> map : result){
			String name = (String) map.get("name");
			String world = (String)map.get("world");
			int x = (int) map.get("x");
			int y = (int) map.get("y");
			int z = (int) map.get("z");
			
			int ax = (int) map.get("ax");
			int ay = (int) map.get("ay");
			int az = (int) map.get("az");
			
			ArrayList<UUID> membersList = (ArrayList<UUID>) map.get("membersList");
			boolean isEnabled = (boolean) map.get("isEnabled");
			Block block = ShieldGenerator.getInstance().getServer().getWorld(world).getBlockAt(x, y, z);
			Block adjBlock = ShieldGenerator.getInstance().getServer().getWorld(world).getBlockAt(ax, ay, az);
			ShieldManager temp = new ShieldManager(name, block, adjBlock, membersList, isEnabled);
			
			temp.updateShield();
			
		}

	}

	public static void save(String path) {
		ArrayList<HashMap<String, Object>> saveList = new ArrayList<HashMap<String, Object>>();
		for (ShieldManager sm : ShieldManager.shieldList) {
			String name = sm.name;
			Location bLoc = sm.getBlock().getLocation();
			Location adjLoc = sm.getAdjPowerBlock().getLocation();
			ArrayList<UUID> membersList = sm.getMemberUUIDs();
			boolean isEnabled = sm.isShieldEnabled();
			
			String world = bLoc.getWorld().getName();
			int x = bLoc.getBlockX();
			int y = bLoc.getBlockY();
			int z = bLoc.getBlockZ();
			
			int ax = adjLoc.getBlockX();
			int ay = adjLoc.getBlockY();
			int az = adjLoc.getBlockZ();
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("membersList", membersList);
			map.put("isEnabled", isEnabled);
			map.put("world", world);
			map.put("x", x);
			map.put("y", y);
			map.put("z", z);
			map.put("ax", ax);
			map.put("ay", ay);
			map.put("az", az);
			
			saveList.add(map);
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(path));
			oos.writeObject(saveList);
			oos.flush();
			oos.close();
			// Handle I/O exceptions
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
