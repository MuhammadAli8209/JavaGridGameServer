package com.jtconnors.multisocketserverfx;

import java.util.ArrayList;

public class Factories {
    private int moneymultiplier;
    private ArrayList<Location> factorylocations = new ArrayList<>();
    private int level;
    private int health;

    private int resourceamt = 0;

    private Player factoryowner;

    //HAVE A LOCATION MAKE THING IN THE CONSTRUCTOR FOR THIS
    public Factories(Player owner, Location factorylocation){
        this.factoryowner = owner;
        this.level=1;
        this.health=100;
        this.moneymultiplier = 1;

        this.factorylocations.add(factorylocation);//bottomleft
        this.factorylocations.add(new Location(factorylocation.getRow()-1, factorylocation.getColumn()));//topleft
        this.factorylocations.add(new Location(factorylocation.getRow(), factorylocation.getColumn()+1));//bottomright
        this.factorylocations.add(new Location(factorylocation.getRow()-1, factorylocation.getColumn()+1));//topright
        //this.factorylocations = setFactoryLocations();
    }


//    public int givebaseamount(){
//        return 50;
//    }


    public int getHealth() {
        return health;
    }

    public int getLevel() {
        return level;
    }

    public void increaselevel(){

        this.level++;
    }

    public int getMoneymultiplier() {
        return moneymultiplier;
    }

    public void setMoneymultiplier(int given){
        this.moneymultiplier = given;
    }

    public void setHealth(int given){
        this.health=given;
    }

    public void addresourceamt(int given){
        this.resourceamt += given;
    }

    public int getResourceamt() {
        return resourceamt;
    }

    public String resourceamtneeded(){
        return (this.resourceamt+"/"+this.level*100);
    }

    public Player getFactoryowner() {
        return factoryowner;
    }

    public int thismoneygivenbyfactory(){
        return (this.getMoneymultiplier()*50 + this.resourceamt);
    }

    public ArrayList<Location> getFactorylocations() {
        return factorylocations;
    }
}
