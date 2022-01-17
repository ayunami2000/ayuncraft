package me.ayunami2000.ayuncraft;

public class File {
    String fileName="";

    public File(String name){
        fileName=name;
    }

    public boolean exists(){
        return true;
    }

    public String getFileName(){
        return fileName;
    }
}
