package movewarp.listener;

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import movewarp.Main;

public class EventListener extends BaseListener<Main> {
	
	private HashMap<String, Integer> makeQueue;
	private HashMap<String, String> firstVector;

	public EventListener(Main plugin) {
		super(plugin);
		makeQueue = new HashMap<>();
		firstVector = new HashMap<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (! (sender instanceof Player)) {
			sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.ingame"));
			return true;
		}
		if (! makeQueue.containsKey(sender.getName())) {
			plugin.getDB().message(sender, "Touch start position.");
			makeQueue.put(sender.getName(), 1);
		} else {
			plugin.getDB().message(sender, "Cancel make warp");
			makeQueue.remove(sender.getName());
			firstVector.remove(sender.getName());
		}
		return true;
	}
	
	@EventHandler
	public void onTouchMake(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!makeQueue.containsKey(player.getName())) return;
		if (makeQueue.get(player.getName()) == 1) {
			firstVector.put(player.getName(), posToString(event.getBlock()));
			makeQueue.put(player.getName(), 2);
			plugin.getDB().message(player, "Completly set start position.");
			plugin.getDB().message(player, "Touch second position.");
		} else {
			String secondVector = posToString(event.getBlock());
			plugin.getDB().getDB("warplist").set(firstVector.get(player.getName()), secondVector);
			plugin.getDB().message(player, "Create warp successful.");
			makeQueue.remove(player.getName());
			firstVector.remove(player.getName());
		}
	}
	
	@EventHandler
	public void onTouchMove(PlayerInteractEvent event) {
		String destr = plugin.getDB().getDB("warplist").getString(posToString(event.getBlock()));
		if (destr == null || destr.equals("")) return;
		Position des = getUpPos(stringToPos(destr));
		event.getPlayer().teleport(des);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (!plugin.getDB().getDB("warplist").exists(posToString(block))) return;
		plugin.getDB().getDB("warplist").remove(posToString(block));
		plugin.getDB().message(player, "Remove warp at the pos.");
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String destr = plugin.getDB().getDB("warplist").getString(posToString(getBottomPos(player)));
		if (destr == null || destr.equals("")) return;
		Position des = getUpPos(stringToPos(destr));
		player.teleport(des);
	}
	
	private String posToString(Position pos) {
		return (int)pos.getX() + ":" + (int)pos.getY() + ":" + (int)pos.getZ() + ":" + pos.getLevel().getFolderName();
	}
	private Position stringToPos(String pos) {
		String[] args = pos.split(":");
		return new Position(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), plugin.getServer().getLevelByName(args[3]));
	}
	private Position getBottomPos(Position pos) {
		return new Position(pos.x, pos.y - 1, pos.z, pos.level);
	}
	private Position getUpPos(Position pos) {
		return new Position(pos.x, pos.y + 1, pos.z, pos.level);
	}
}