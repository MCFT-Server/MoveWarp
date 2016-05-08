package movewarp.database;

import cn.nukkit.utils.Config;
import movewarp.Main;

public class DataBase extends BaseDB<Main> {

	public DataBase(Main plugin) {
		super(plugin);
		initDB("warplist", plugin.getDataFolder().getPath() + "/warplist.json", Config.JSON);
		setPrefix("[워프]");
	}
}