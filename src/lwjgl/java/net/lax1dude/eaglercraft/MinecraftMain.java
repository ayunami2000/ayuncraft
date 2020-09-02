package net.lax1dude.eaglercraft;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.LocalStorageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ServerList;

public class MinecraftMain {

	public static void main(String[] par0ArrayOfStr) {
		
		EaglerAdapter.initializeContext();
		LocalStorageManager.loadStorage();
		ServerList.loadDefaultServers(Base64.encodeBase64String(EaglerAdapter.loadLocalStorage("forced")));
		
		Minecraft mc = new Minecraft();
		mc.run();
		
	}
}
