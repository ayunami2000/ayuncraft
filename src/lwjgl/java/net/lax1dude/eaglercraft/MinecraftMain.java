package net.lax1dude.eaglercraft;

import java.util.Arrays;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.LocalStorageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ServerList;

public class MinecraftMain {

	public static void main(String[] par0ArrayOfStr) {
		
		JOptionPane.showMessageDialog(null, "launch renderdoc (optionally) and press ok to continue", "eaglercraft", JOptionPane.PLAIN_MESSAGE);
		
		EaglerAdapter.initializeContext();
		LocalStorageManager.loadStorage();
		ServerList.loadDefaultServers(Base64.encodeBase64String(EaglerAdapter.loadLocalStorage("forced")));
		
		Minecraft mc = new Minecraft();
		mc.run();
		
	}
}
