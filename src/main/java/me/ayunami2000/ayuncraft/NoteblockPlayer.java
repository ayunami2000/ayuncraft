package me.ayunami2000.ayuncraft;

import me.ayunami2000.ayuncraft.nbsapi.Layer;
import me.ayunami2000.ayuncraft.nbsapi.Note;
import me.ayunami2000.ayuncraft.nbsapi.Song;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NoteblockPlayer {
    public static boolean playing=false;
    public static byte[] songdata=null;
    public static Song song=null;
    public static Thread thr=null;
    private static final int[] nb2in=new int[]{0,4,1,2,3,7,5,6,8,9,10,11,12,13,14,15};
    private static int[] in2old=new int[]{0,1,2,3,4,0,0,4,0,0,0,0,4,0,4,0};
    private static Block[] instruments=new Block[]{Block.dirt,Block.stone,Block.sand,Block.glass,Block.planks};
    private static Vec3 startingPos = null;
    private static Minecraft mc=Minecraft.getMinecraft();
    public static boolean legit=false;

    private static boolean playingSong=false;
    private static String[] songLiness=null;
    private static HashMap<Integer,HashMap<Integer,Vec3>> instrNoteToBlocks=null;
    private static int tickPassed=0;
    public static void tick(){
        if(playing&&playingSong){
            int linesPlayed=0;
            for (String songLine : songLiness) {
                String[] songInfo = songLine.split(":");
                int tick = Integer.parseInt(songInfo[0])-tickPassed;
                int note = Integer.parseInt(songInfo[1]);
                int instr = Integer.parseInt(songInfo[2]);
                if(tick<=0) {
                    Vec3 blockPos=instrNoteToBlocks.get(instr).get(note);
                    try {
                        mc.playerController.clickBlock((int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1);
                        if(legit)rotateToBlock(blockPos.addVector(0,-1,0));
                    }catch(NullPointerException e){}
                    //mc.getNetHandler().addToSendQueue(new Packet14BlockDig(0, (int) blockPos.xCoord, (int) blockPos.yCoord, (int) blockPos.zCoord,1));
                    //mc.thePlayer.swingItem();
                    linesPlayed++;
                }else{
                    break;
                }
            }
            tickPassed++;
            if(linesPlayed!=0)songLiness=Arrays.copyOfRange(songLiness,linesPlayed,songLiness.length);
        }
    }

    public static void rotateToBlock(Vec3 block){
        block=block.addVector(0.5,0.5,0.5);

        Vec3 eyesPos = mc.thePlayer.getPosition(1).addVector(0,mc.thePlayer.getEyeHeight(),0);

        double diffX = block.xCoord - eyesPos.xCoord;
        double diffY = block.yCoord - eyesPos.yCoord;
        double diffZ = block.zCoord - eyesPos.zCoord;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        mc.thePlayer.prevRotationYaw = mc.thePlayer.rotationYaw;
        mc.thePlayer.prevRotationPitch = mc.thePlayer.rotationPitch;
        mc.thePlayer.rotationYaw=yaw%360.0F;
        mc.thePlayer.rotationPitch=pitch%360.0F;
    }

    public static void play(){
        playingSong=false;
        playing=true;
        String[] songLines=loadSong().split("\n");
        HashMap<Integer, HashMap<Integer, Vec3>> songBlocks=songLinesToBlocks(songLines);
        if(playing)placeAndTuneNoteblocks(songBlocks);
        if(playing) {
            songLiness=songLines;
            instrNoteToBlocks=songBlocks;
            tickPassed=0;
            playingSong=true;
        }
    }

    public static Vec3 notePos=null;
    public static int targetNote=-1;
    public static boolean correctNote=false;
    public static int currentNote=-1;
    public static int theoreticalNote=-1;
    public static void notePlayed(int x,int y,int z,int note){
        if(!playing){
            correctNote=false;
            notePos=null;
            targetNote=-1;
            currentNote=-1;
            theoreticalNote=-1;
        }
        if(notePos!=null&&targetNote!=-1){
            if((int)notePos.xCoord==x&&(int)notePos.yCoord==y+1&&(int)notePos.zCoord==z){
                currentNote=note;
                if(theoreticalNote<0)theoreticalNote=currentNote;
                mc.thePlayer.sendChatToPlayer("Tuning: "+note+"/"+targetNote);
                if(targetNote==note) {
                    notePos = null;
                    targetNote = -1;
                    currentNote = -1;
                    theoreticalNote = -1;
                    correctNote = true;
                }
            }
        }
    }

    public static boolean building=false;
    public static void placeAndTuneNoteblocks(HashMap<Integer, HashMap<Integer, Vec3>> instrNoteToBlock){
        if(!playing)return;
        building=true;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        for(Map.Entry<Integer, HashMap<Integer, Vec3>> entry : instrNoteToBlock.entrySet()) {
            Integer instr = entry.getKey();
            HashMap<Integer, Vec3> noteBlockPos = entry.getValue();
            for(Map.Entry<Integer, Vec3> entryy : noteBlockPos.entrySet()) {
                Integer note = entryy.getKey();
                Vec3 blockPos = entryy.getValue();
                if(playing) {
                    Vec3 currPos=mc.thePlayer.getPosition(1);
                    //double rangeBlockDist=currPos.addVector(0,mc.thePlayer.getEyeHeight(),0).distanceTo(blockPosVec3d);
                    double blockDist=currPos.distanceTo(blockPos);
                    double oldBlockDist=32767;
                    boolean hasBeenPlaced=false;
                    correctNote=false;
                    while (playing&&((!hasBeenPlaced)||(!correctNote))/*!(rangeBlockDist <= mc.playerController.getBlockReachDistance()+1.0F && (blockDist >= 1.25F))*/) {
                        if(Math.abs(oldBlockDist-blockDist)>0.1){
                            mc.thePlayer.sendChatToPlayer("Need noteblock with instrument "+instruments[instr].getLocalizedName()+" and tuning "+note+". Distance: "+blockDist+". XYZ: "+blockPos);
                        }
                        oldBlockDist=blockDist;
                        int id1=mc.theWorld.getBlockId((int)blockPos.xCoord,(int)(blockPos.yCoord-1),(int)blockPos.zCoord);
                        int id2=mc.theWorld.getBlockId((int)blockPos.xCoord,(int)(blockPos.yCoord-2),(int)blockPos.zCoord);
                        boolean check1=id1==Block.music.blockID;
                        boolean check2=id2==instruments[instr].blockID;
                        int slot1id=mc.thePlayer.inventory.mainInventory[0]==null?-1:mc.thePlayer.inventory.mainInventory[0].itemID;
                        if(mc.playerController.isInCreativeMode()) {
                            if (check2 && (!check1)) {
                                if(slot1id != Block.music.blockID) {
                                    //give block
                                    ItemStack item = new ItemStack(Block.music);
                                    mc.thePlayer.inventory.mainInventory[0] = item;
                                    mc.playerController.sendSlotPacket(item, mc.thePlayer.inventoryContainer.inventorySlots.size() - 9);
                                    mc.getNetHandler().addToSendQueue(new Packet16BlockItemSwitch(0));
                                    try{
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {}
                                }
                                rotateToBlock(blockPos.addVector(0,-1,0));
                                if(id1!=0) {
                                    //attempt to break
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(0, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1));
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(1, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1));
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(2, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1));
                                    PlayerControllerMP.clickBlockCreative(mc, mc.playerController, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1);
                                    try{
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {}
                                }
                                /*
                                //attempt to place
                                if(legit)rotateToBlock(blockPos.addVector(0,-1,0));
                                mc.thePlayer.inventory.currentItem=0;
                                mc.getNetHandler().addToSendQueue(new Packet15Place((int) blockPos.xCoord, (int) (blockPos.yCoord-1), (int) blockPos.zCoord,1,mc.thePlayer.inventory.getCurrentItem(),0,1,0));
                                */
                            }else if ((!check2) && (!check1)) {
                                if(slot1id != instruments[instr].blockID) {
                                    //give block
                                    ItemStack item = new ItemStack(instruments[instr]);
                                    mc.thePlayer.inventory.mainInventory[0] = item;
                                    mc.playerController.sendSlotPacket(item, mc.thePlayer.inventoryContainer.inventorySlots.size() - 9);
                                    mc.getNetHandler().addToSendQueue(new Packet16BlockItemSwitch(0));
                                    try{
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {}
                                }
                                rotateToBlock(blockPos.addVector(0,-2,0));
                                if(id1!=0) {
                                    //attempt to break block above
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(0, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1));
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(1, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1));
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(2, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1));
                                    PlayerControllerMP.clickBlockCreative(mc, mc.playerController, (int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1);
                                    try{
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {}
                                }
                                if(id2!=0) {
                                    //attempt to break
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(0, (int) blockPos.xCoord, (int) (blockPos.yCoord - 2), (int) blockPos.zCoord, 1));
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(1, (int) blockPos.xCoord, (int) (blockPos.yCoord - 2), (int) blockPos.zCoord, 1));
                                    mc.getNetHandler().addToSendQueue(new Packet14BlockDig(2, (int) blockPos.xCoord, (int) (blockPos.yCoord - 2), (int) blockPos.zCoord, 1));
                                    PlayerControllerMP.clickBlockCreative(mc, mc.playerController, (int) blockPos.xCoord, (int) (blockPos.yCoord - 2), (int) blockPos.zCoord, 1);
                                    try{
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {}
                                }
                                /*
                                //attempt to place
                                if(legit)rotateToBlock(blockPos.addVector(0,-2,0));
                                mc.thePlayer.inventory.currentItem=0;
                                mc.getNetHandler().addToSendQueue(new Packet15Place((int) blockPos.xCoord, (int) (blockPos.yCoord-2), (int) blockPos.zCoord,1,mc.thePlayer.inventory.getCurrentItem(),0,1,0));
                                */
                            }
                        }
                        if(check1&&check2){
                            hasBeenPlaced=true;
                            notePos=blockPos;
                            targetNote=note;
                            if(theoreticalNote==-1){
                                theoreticalNote=-2;
                                //find out the note
                                if(mc.playerController.isInCreativeMode()) {
                                    mc.getNetHandler().addToSendQueue(new Packet15Place((int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1, mc.thePlayer.inventory.getCurrentItem(), 0, 1, 0));
                                }else{
                                    mc.playerController.clickBlock((int) blockPos.xCoord, (int) blockPos.yCoord-1, (int) blockPos.zCoord,1);
                                }
                            }else if(theoreticalNote>=0&&theoreticalNote!=targetNote) {
                                theoreticalNote=(theoreticalNote+1)%25;
                                mc.getNetHandler().addToSendQueue(new Packet15Place((int) blockPos.xCoord, (int) (blockPos.yCoord - 1), (int) blockPos.zCoord, 1, mc.thePlayer.inventory.getCurrentItem(), 0, 1, 0));
                            }
                        }else{
                            notePos=null;
                            targetNote=-1;
                            currentNote=-1;
                            hasBeenPlaced=false;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {}
                        currPos=mc.thePlayer.getPosition(1);
                        //rangeBlockDist=currPos.addVector(0,mc.thePlayer.getEyeHeight(),0).distanceTo(blockPosVec3d);
                        blockDist=currPos.distanceTo(blockPos);
                    }
                    correctNote=false;
                }
            }
        }
        if(playing) {
            mc.thePlayer.sendChatMessage("/gamemode 0");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }
        building = false;
    }

    public static List<int[]> spiral(int size){
        List<int[]> result=new ArrayList<int[]>();
        int x = 0; // current position; x
        int y = 0; // current position; y
        int d = 0; // current direction; 0=RIGHT, 1=DOWN, 2=LEFT, 3=UP
        int c = 0; // counter
        int s = 1; // chain size

        // starting point
        x = ((int)Math.floor(size/2.0))-1;
        y = ((int)Math.floor(size/2.0))-1;

        for (int k=1; k<=(size-1); k++)
        {
            for (int j=0; j<(k<(size-1)?2:3); j++)
            {
                for (int i=0; i<s; i++)
                {
                    result.add(new int[]{x,y});
                    c++;

                    switch (d)
                    {
                        case 0: y = y + 1; break;
                        case 1: x = x + 1; break;
                        case 2: y = y - 1; break;
                        case 3: x = x - 1; break;
                    }
                }
                d = (d+1)%4;
            }
            s = s + 1;
        }
        return result;
    }

    public static HashMap<Integer, HashMap<Integer, Vec3>> songLinesToBlocks(String[] songLines){
        HashMap<Integer,HashMap<Integer,Vec3>> instrNoteToBlock=new HashMap<>();
        int uniqueNotes=0;
        startingPos=Vec3.createVectorHelper(Math.floor(mc.thePlayer.posX),Math.floor(mc.thePlayer.posY),Math.floor(mc.thePlayer.posZ));
        Vec3 centerPos=startingPos.addVector(0,-1,0);
        for (String songLine : songLines) {
            String[] songInfo = songLine.split(":");
            int tick = Integer.parseInt(songInfo[0]);
            int note = Integer.parseInt(songInfo[1]);
            int instr = Integer.parseInt(songInfo[2]);
            if(!(instrNoteToBlock.containsKey(instr)&&instrNoteToBlock.get(instr).containsKey(note))){
                HashMap<Integer,Vec3> theVal=instrNoteToBlock.containsKey(instr)?instrNoteToBlock.get(instr):new HashMap<Integer,Vec3>();
                theVal.put(note,centerPos);
                instrNoteToBlock.put(instr,theVal);
                uniqueNotes++;
            }
        }
        AtomicInteger counter= new AtomicInteger();
        //fuck you, add 1 to spiral size. excess is already disposed of, so does it really matter if it only runs once?
        List<int[]> spiralCoords=spiral(1+Math.min(9,(int)Math.ceil(Math.sqrt(uniqueNotes))));
        List<int[]> spiralCoordsTwo=uniqueNotes>81?spiral(1+(int)Math.ceil(Math.sqrt(uniqueNotes-81))):new ArrayList<int[]>();

        for(Map.Entry<Integer, HashMap<Integer, Vec3>> entry : instrNoteToBlock.entrySet()) {
            Integer instr = entry.getKey();
            HashMap<Integer, Vec3> noteBlockPos = entry.getValue();
            HashMap<Integer, Vec3> noteBlockPosOrig = (HashMap<Integer, Vec3>) noteBlockPos.clone();
            for(Map.Entry<Integer, Vec3> entryy : noteBlockPos.entrySet()) {
                Integer note = entryy.getKey();
                Vec3 blockPos = entryy.getValue();
                int currNum=counter.getAndIncrement();
                Vec3 theBlock=Vec3.createVectorHelper(blockPos.xCoord,blockPos.yCoord,blockPos.zCoord);
                if(currNum>=81){
                    theBlock=theBlock.addVector(spiralCoordsTwo.get(currNum-81)[0]-spiralCoordsTwo.get(0)[0],4,spiralCoordsTwo.get(currNum-81)[1]-spiralCoordsTwo.get(0)[1]);
                }else{
                    //todo: fix corners
                    theBlock=theBlock.addVector(spiralCoords.get(currNum)[0]-spiralCoords.get(0)[0],0,spiralCoords.get(currNum)[1]-spiralCoords.get(0)[1]);
                    Vec3 offset=theBlock.subtract(blockPos);
                    if(Math.abs(offset.xCoord)==4&&Math.abs(offset.zCoord)==4){
                        theBlock=theBlock.addVector(0,1,0);
                    }
                }
                noteBlockPos.replace(note,blockPos,theBlock);
            }
            instrNoteToBlock.replace(instr,noteBlockPosOrig,noteBlockPos);
        }
        return instrNoteToBlock;
    }

    public static String loadSong(){
        try{
            String resSongFile="";
            Map<Integer, ArrayList<String>> songLines=new HashMap<>();
            Song nbsSong = new Song(songdata);
            List<Layer> nbsSongBoard = nbsSong.getSongBoard();
            for (int i = 0; i < nbsSongBoard.size(); i++) {
                Layer layer=nbsSongBoard.get(i);
                HashMap<Integer, Note> noteList = layer.getNoteList();
                for (Map.Entry note : noteList.entrySet()) {
                    Note noteInfo = (Note) note.getValue();
                    Integer noteKey=(int)((double)(int)note.getKey()/(5.0*((double)nbsSong.getTempo()/10000.0)));
                    if(!songLines.containsKey(noteKey))songLines.put(noteKey,new ArrayList<String>());
                    ArrayList<String> tickLines=songLines.get(noteKey);
                    //keep notes within 2-octave range
                    Integer notePitch=Math.max(33,Math.min(57,noteInfo.getPitch()))-33;
                    int instrId=noteInfo.getInstrument().getID();
                    if(instrId!=-1)instrId=in2old[nb2in[instrId]];
                    tickLines.add(noteKey + ":" + notePitch + ":" + instrId + "\n");
                    songLines.put(noteKey,tickLines);
                }
            }
            SortedSet<Integer> ticks = new TreeSet<>(songLines.keySet());
            for (Integer tick : ticks) {
                ArrayList<String> tickLines = songLines.get(tick);
                for(int i=0;i<tickLines.size();i++){
                    resSongFile+=tickLines.get(i);
                }
            }
            if(resSongFile.endsWith("\n"))resSongFile=resSongFile.substring(0,resSongFile.length()-1);
            return resSongFile;
        }catch(Exception e){
            //e.printStackTrace();
            playing=false;
            return null;
        }
    }
}
