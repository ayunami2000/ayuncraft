package net.md_5.bungee.api.config;

import java.io.File;

public class AuthServiceInfo {

	private final boolean enabled;
	private final String limbo;
	private final File authfile;
	private final int timeout;
	
	public AuthServiceInfo(boolean enabled, String limbo, File authfile, int timeout) {
		this.enabled = enabled;
		this.limbo = limbo;
		this.authfile = authfile;
		this.timeout = timeout;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getLimbo() {
		return limbo;
	}

	public File getAuthfile() {
		return authfile;
	}

	public int getTimeout() {
		return timeout;
	}

}
