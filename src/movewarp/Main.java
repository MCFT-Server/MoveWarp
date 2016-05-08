package movewarp;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import movewarp.database.DataBase;
import movewarp.listener.EventListener;

public class Main extends PluginBase {
	private EventListener listener;
	private DataBase db;
	
	@Override
	public void onEnable() {
		listener = new EventListener(this);
		db = new DataBase(this);
		
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	@Override
	public void onDisable() {
		db.save();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return listener.onCommand(sender, command, label, args);
	}
	
	public EventListener getListener() {
		return listener;
	}
	
	public DataBase getDB() {
		return db;
	}
}
