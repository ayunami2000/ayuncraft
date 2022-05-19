package net.lax1dude.eaglercraft;

import java.util.regex.Pattern;

public class ConfigConstants {
	
	public static boolean profanity = false;

	public static final String version = "22w20b";
	public static final String mainMenuString = "ayuncraft " + version;
	
	public static final String forkMe = "https://github.com/ayunami2000/ayuncraft";

	public static final boolean html5build = true;
	
	public static String ayonullTitle = null;
	public static String ayonullLink = null;

	public static String[] proxies = new String[]{"pproxy.rom1504.fr","webmcproxy.glitch.me","net-browserify.glitch.me"};

	public static Pattern ipPattern = Pattern.compile("^"
			+ "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domain name
			+ "|"
			+ "localhost" // localhost
			+ "|"
			+ "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // Ip
			+ "(:"
			+ "[0-9]{1,5})?$"); // Port
	public static int mainMenuItem = -1;
	public static String mainMenuItemLine0 = null;
	public static String mainMenuItemLine1 = null;
	public static String mainMenuItemLine2 = null;
	public static String mainMenuItemLink = null;
}
