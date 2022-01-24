package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.client.Minecraft;

public class SoundManager {
	
	private static class EntitySoundEvent {
		private Entity e;
		private int id;
		public EntitySoundEvent(Entity e, int id) {
			this.e = e;
			this.id = id;
		}
	}
	
	private static class QueuedSoundEvent {
		private String sound;
		private float x;
		private float y;
		private float z;
		private float volume;
		private float pitch;
		private int timer;
		public QueuedSoundEvent(String sound, float x, float y, float z, float volume, float pitch, int timer) {
			this.sound = sound;
			this.x = x;
			this.y = y;
			this.z = z;
			this.volume = volume;
			this.pitch = pitch;
			this.timer = timer;
		}
	}
	
	private GameSettings options;
	private ArrayList<EntitySoundEvent> soundevents;
	private ArrayList<QueuedSoundEvent> queuedsoundevents;
	private HashMap<String,Integer> sounddefinitions;
	private Random soundrandom;

	public SoundManager() {
		this.soundevents = new ArrayList();
		this.queuedsoundevents = new ArrayList();
		this.sounddefinitions = null;
		this.soundrandom = new Random();
	}

	/**
	 * Used for loading sound settings from GameSettings
	 */
	public void loadSoundSettings(GameSettings par1GameSettings) {
		this.options = par1GameSettings;
		if(this.sounddefinitions == null) {
			this.sounddefinitions = new HashMap();
			try {
				NBTTagCompound file = CompressedStreamTools.readUncompressed(EaglerAdapter.loadResourceBytes("/sounds/sounds.dat"));
				NBTTagList l = file.getTagList("sounds");
				int c = l.tagCount();
				for(int i = 0; i < c; i++) {
					NBTTagCompound cc = (NBTTagCompound)l.tagAt(i);
					this.sounddefinitions.put(cc.getString("e"), (int)cc.getByte("c") & 0xFF);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when one of the sound level options has changed.
	 */
	public void onSoundOptionsChanged() {
		
	}

	/**
	 * Called when Minecraft is closing down.
	 */
	public void closeMinecraft() {
		
	}

	/**
	 * If its time to play new music it starts it up.
	 */
	public void playRandomMusicIfReady() {
		
	}

	/**
	 * Sets the listener of sounds
	 */
	public void setListener(EntityLiving par1EntityLiving, float par2) {
		if(par1EntityLiving == null) {
			EaglerAdapter.setListenerPos(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
		}else {
			float x = (float)(par1EntityLiving.prevPosX + (par1EntityLiving.posX - par1EntityLiving.prevPosX) * par2); if(!Float.isFinite(x)) x = 0.0f;
			float y = (float)(par1EntityLiving.prevPosY + (par1EntityLiving.posY - par1EntityLiving.prevPosY) * par2); if(!Float.isFinite(y)) y = 0.0f;
			float z = (float)(par1EntityLiving.prevPosZ + (par1EntityLiving.posZ - par1EntityLiving.prevPosZ) * par2); if(!Float.isFinite(z)) z = 0.0f;
			float pitch = (float)(par1EntityLiving.prevRotationPitch + (par1EntityLiving.rotationPitch - par1EntityLiving.prevRotationPitch) * par2); if(!Float.isFinite(pitch)) pitch = 0.0f;
			float yaw = (float)(par1EntityLiving.prevRotationYaw + (par1EntityLiving.rotationYaw - par1EntityLiving.prevRotationYaw) * par2); if(!Float.isFinite(yaw)) yaw = 0.0f;
			EaglerAdapter.setListenerPos(x, y, z, (float)par1EntityLiving.motionX, (float)par1EntityLiving.motionY, (float)par1EntityLiving.motionZ, pitch, yaw);
		}
	}

	/**
	 * Stops all currently playing sounds
	 */
	public void stopAllSounds() {
		for(EntitySoundEvent e : soundevents) {
			EaglerAdapter.endSound(e.id);
		}
	}

	public void playStreaming(String par1Str, float par2, float par3, float par4) {

	}

	/**
	 * Updates the sound associated with the entity with that entity's position and
	 * velocity. Args: the entity
	 */
	public void updateSoundLocation(Entity par1Entity) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par1Entity)) {
				EaglerAdapter.moveSound(e.id, (float)par1Entity.posX, (float)par1Entity.posY, (float)par1Entity.posZ, (float)par1Entity.motionX, (float)par1Entity.motionY, (float)par1Entity.motionZ);
			}
		}
	}

	/**
	 * Updates the sound associated with soundEntity with the position and velocity
	 * of trackEntity. Args: soundEntity, trackEntity
	 */
	public void updateSoundLocation(Entity par1Entity, Entity par2Entity) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par1Entity)) {
				EaglerAdapter.moveSound(e.id, (float)par2Entity.posX, (float)par2Entity.posY, (float)par2Entity.posZ, (float)par2Entity.motionX, (float)par2Entity.motionY, (float)par2Entity.motionZ);
			}
		}
	}

	/**
	 * Returns true if a sound is currently associated with the given entity, or
	 * false otherwise.
	 */
	public boolean isEntitySoundPlaying(Entity par1Entity) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par1Entity)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Stops playing the sound associated with the given entity
	 */
	public void stopEntitySound(Entity par1Entity) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par1Entity)) {
				EaglerAdapter.endSound(e.id);
			}
		}
	}

	/**
	 * Sets the volume of the sound associated with the given entity, if one is
	 * playing. The volume is scaled by the global sound volume. Args: the entity,
	 * the volume (from 0 to 1)
	 */
	public void setEntitySoundVolume(Entity par1Entity, float par2) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par1Entity)) {
				EaglerAdapter.setVolume(e.id, par2);
			}
		}
	}

	/**
	 * Sets the pitch of the sound associated with the given entity, if one is
	 * playing. Args: the entity, the pitch
	 */
	public void setEntitySoundPitch(Entity par1Entity, float par2) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par1Entity)) {
				EaglerAdapter.setPitch(e.id, par2);
			}
		}
	}

	/**
	 * If a sound is already playing from the given entity, update the position and
	 * velocity of that sound to match the entity. Otherwise, start playing a sound
	 * from that entity. Setting the last flag to true will prevent other sounds
	 * from overriding this one. Args: The sound name, the entity, the volume, the
	 * pitch, priority
	 */
	public void playEntitySound(String par1Str, Entity par2Entity, float par3, float par4, boolean par5) {
		for(EntitySoundEvent e : soundevents) {
			if(e.e.equals(par2Entity)) {
				EaglerAdapter.moveSound(e.id, (float)par2Entity.posX, (float)par2Entity.posY, (float)par2Entity.posZ, (float)par2Entity.motionX, (float)par2Entity.motionY, (float)par2Entity.motionZ);
				return;
			}
		}
		float v = par3 * this.options.soundVolume;
		if(v > 0.0F) {
			Integer ct = this.sounddefinitions.get(par1Str);
			if(ct != null) {
				int c = ct.intValue();
				String path;
				if(c <= 1) {
					path = "/sounds/"+par1Str.replace('.', '/')+".mp3";
				}else {
					int r = soundrandom.nextInt(c) + 1;
					path = "/sounds/"+par1Str.replace('.', '/')+r+".mp3";
				}
				int id = 0;
				soundevents.add(new EntitySoundEvent(par2Entity, id = EaglerAdapter.beginPlayback(path, 0f, 0f, 0f, v, par4)));
				EaglerAdapter.moveSound(id, (float)par2Entity.posX, (float)par2Entity.posY, (float)par2Entity.posZ, (float)par2Entity.motionX, (float)par2Entity.motionY, (float)par2Entity.motionZ);
			}else {
				System.err.println("unregistered sound effect: "+par1Str);
			}
		}
	}

	/**
	 * Plays a sound. Args: soundName, x, y, z, volume, pitch
	 */
	public void playSound(String par1Str, float par2, float par3, float par4, float par5, float par6) {
		float v = par5 * this.options.soundVolume;
		if(v > 0.0F) {
			Integer ct = this.sounddefinitions.get(par1Str);
			if(ct != null) {
				int c = ct.intValue();
				String path;
				if(c <= 1) {
					path = "/sounds/"+par1Str.replace('.', '/')+".mp3";
				}else {
					int r = soundrandom.nextInt(c) + 1;
					path = "/sounds/"+par1Str.replace('.', '/')+r+".mp3";
				}
				EaglerAdapter.beginPlayback(path, par2, par3, par4, v, par6);
			}else {
				System.err.println("unregistered sound effect: "+par1Str);
			}
		}
	}

	/**
	 * Plays a sound effect with the volume and pitch of the parameters passed. The
	 * sound isn't affected by position of the player (full volume and center
	 * balanced)
	 */
	public void playSoundFX(String par1Str, float par2, float par3) {
		float v = par3 * this.options.soundVolume;
		if(v > 0.0F) {
			Integer ct = this.sounddefinitions.get(par1Str);
			if(ct != null) {
				int c = ct.intValue();
				String path;
				if(c <= 1) {
					path = "/sounds/"+par1Str.replace('.', '/')+".mp3";
				}else {
					int r = soundrandom.nextInt(c) + 1;
					path = "/sounds/"+par1Str.replace('.', '/')+r+".mp3";
				}
				EaglerAdapter.beginPlaybackStatic(path, v, par3);
			}else {
				System.err.println("unregistered sound effect: "+par1Str);
			}
		}
	}

	/**
	 * Pauses all currently playing sounds
	 */
	public void pauseAllSounds() {

	}

	/**
	 * Resumes playing all currently playing sounds (after pauseAllSounds)
	 */
	public void resumeAllSounds() {

	}
	
	private int resetTimer = 0;

	public void func_92071_g() {
		++resetTimer;
		if(resetTimer % 20 == 0) {
			ArrayList<EntitySoundEvent> e = this.soundevents;
			this.soundevents = new ArrayList();
			for(EntitySoundEvent e2 : e) {
				if(EaglerAdapter.isPlaying(e2.id)) {
					soundevents.add(e2);
				}
			}
		}
		Iterator<QueuedSoundEvent> itr = queuedsoundevents.iterator();
		while(itr.hasNext()) {
			QueuedSoundEvent e = itr.next();
			if(--e.timer <= 0) {
				playSound(e.sound, e.x, e.y, e.z, e.volume, e.pitch);
				itr.remove();
			}
		}
	}

	public void func_92070_a(String par1Str, float par2, float par3, float par4, float par5, float par6, int par7) {
		queuedsoundevents.add(new QueuedSoundEvent(par1Str, par2, par3, par4, par5, par6, par7));
	}
	
	private int titleMusic = -1;
	
	public void playTheTitleMusic() {
		if(titleMusic == -1 || !EaglerAdapter.isPlaying(titleMusic)) {
			titleMusic = EaglerAdapter.beginPlaybackStatic("/sounds/gta.mp3", this.options.musicVolume, 1.0f);
		}
	}
	
	public void stopTheTitleMusic() {
		if(EaglerAdapter.isPlaying(titleMusic)) {
			EaglerAdapter.endSound(titleMusic);
		}
		titleMusic = -1;
	}
	
}
