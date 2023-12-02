package com.jtconnors.multisocketserverfx;

import java.util.ArrayList;

public class Player {
    private String name;

    private boolean isInvisible;

    private boolean isdead = false;

    private long playerdeathtime;

    private ArrayList<Weapons> weaponsininventory = new ArrayList<>();

    private ArrayList<Items> itemsinventory = new ArrayList<>();

    private Drop thedroppickedup;

    private int amtresources;

    private ArrayList<Factories> factories = new ArrayList<>();

    private String playercolor;
    private int health;

    private long invistime;

    private long shrinktime;

    private boolean isshrunk;

    private int moneypts;
    private ArrayList<Location> playerlocation = new ArrayList<>();

    public Player(String name, int helth, Location locgiven){
        this.name = name;
        this.health = helth;
        this.playerlocation.add(locgiven);//bottomleft
        this.playerlocation.add(new Location(locgiven.getRow()-1, locgiven.getColumn()));//topleft
        this.playerlocation.add(new Location(locgiven.getRow(), locgiven.getColumn()+1));//bottomright
        this.playerlocation.add(new Location(locgiven.getRow()-1, locgiven.getColumn()+1));//topright
        this.moneypts =0;
        this.playercolor = "DOCOLORSTIFF";
    }

    public ArrayList<Location> getPlayerlocation() {
        return playerlocation;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public String getPlayercolor() {
        return playercolor;
    }

    public int getMoneypts() {
        return moneypts;
    }

    public void changeplayerloc(String direction){

        if(direction.equals("up")){
            for (int i = 0; i < this.getPlayerlocation().size(); i++) {
                this.playerlocation.get(i).changerow(-1);
            }
        }

        if(direction.equals("down")){
            for (int i = 0; i < this.getPlayerlocation().size(); i++) {
                this.playerlocation.get(i).changerow(1);
            }
        }

        if(direction.equals("left")){
            for (int i = 0; i < this.getPlayerlocation().size(); i++) {
                this.playerlocation.get(i).changecol(-1);
            }
        }

        if(direction.equals("right")){
            for (int i = 0; i < this.getPlayerlocation().size(); i++) {
                this.playerlocation.get(i).changecol(1);
            }
        }

    }

    public ArrayList<Factories> getFactories() {
        return factories;
    }

    public ArrayList<Items> getItemsinventory() {
        return itemsinventory;
    }

    public ArrayList<Weapons> getWeaponsininventory() {
        return weaponsininventory;
    }


    public boolean isIsdead() {
        return isdead;
    }

    public void setIsdead(boolean isdead) {
        this.isdead = isdead;
    }

    public long getPlayerdeathtime() {
        return playerdeathtime;
    }

    public void setPlayerdeathtime(long playerdeathtime) {
        this.playerdeathtime = playerdeathtime;
    }

    public void addmoney(int given){
        this.moneypts+=given;
    }

    public void droppickedup(Drop givendrop){
        if(givendrop.getTypeofdrop().equals("Ite")){
            this.getItemsinventory().add(givendrop.getGivenitem());
            this.thedroppickedup = givendrop;
        }else{
            this.thedroppickedup = givendrop;
        }
        this.amtresources+=givendrop.getAmountresource();

    }

    public int getAmtresources() {
        return amtresources;
    }

    public Drop getThedroppickedup() {
        return thedroppickedup;
    }

    public void setAmtresources(int amtresources) {
        this.amtresources = amtresources;
    }


    public void setshrunked(long given){
        this.isshrunk = true;
        this.shrinktime = given;
    }

    public void setInvisible(long given){
        this.isInvisible = true;
        this.invistime = given;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public boolean isIsshrunk() {
        return isshrunk;
    }

    public void invisibilitygone(boolean given){
        this.isInvisible = given;
    }
    public void shrinkgoner(boolean given){
        this.isshrunk = given;
    }

    public long getInvistime() {
        return invistime;
    }

    public long getShrinktime() {
        return shrinktime;
    }
}
