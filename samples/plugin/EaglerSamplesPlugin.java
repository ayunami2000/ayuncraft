package plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class EaglerSamplesPlugin extends JavaPlugin {
	
	public void onEnable() {
		getCommand("samplemap").setExecutor(new CommandSampleMap(this));
	}
	
	public void onDisable() {
	}
	
}
