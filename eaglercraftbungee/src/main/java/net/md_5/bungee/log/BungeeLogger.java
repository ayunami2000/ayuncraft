// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.log;

import java.util.logging.LogRecord;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import net.md_5.bungee.BungeeCord;
import java.util.logging.Logger;

public class BungeeLogger extends Logger {
	private final BungeeCord bungee;
	private final ColouredWriter writer;
	private final Formatter formatter;
	private final LogDispatcher dispatcher;

	public BungeeLogger(final BungeeCord bungee) {
		super("BungeeCord", null);
		this.formatter = new ConciseFormatter();
		this.dispatcher = new LogDispatcher(this);
		this.bungee = bungee;
		this.writer = new ColouredWriter(bungee.getConsoleReader());
		try {
			final FileHandler handler = new FileHandler("proxy.log", 16777216, 8, true);
			handler.setFormatter(this.formatter);
			this.addHandler(handler);
		} catch (IOException ex) {
			System.err.println("Could not register logger!");
			ex.printStackTrace();
		}
		this.dispatcher.start();
	}

	@Override
	public void log(final LogRecord record) {
		this.dispatcher.queue(record);
	}

	void doLog(final LogRecord record) {
		super.log(record);
		this.writer.print(this.formatter.format(record));
	}
}
