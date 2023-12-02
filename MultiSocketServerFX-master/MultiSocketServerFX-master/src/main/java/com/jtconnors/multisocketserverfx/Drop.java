package com.jtconnors.multisocketserverfx;

public class Drop {

    private Location droplocation;

    private int amountresource;

    private Items givenitem = null;

    private String typeofdrop;

    private String colorofresource;

    private long thisgiventime;

    private boolean ispickedup;

    public Drop(int amountgiven, String whichcolor, Location givenlocation){
        this.droplocation = givenlocation;
        this.amountresource = amountgiven;
        this.colorofresource = whichcolor;
        thisgiventime = System.nanoTime();
        ispickedup = false;
        this.typeofdrop = "Res";
    }

    public Drop(Items giventhing, String whichcolor, Location givenlocation){
        this.droplocation = givenlocation;
        this.givenitem = giventhing;
        this.colorofresource = whichcolor;
        thisgiventime = System.nanoTime();
        ispickedup = false;
        this.typeofdrop = "Ite";
    }

    public int getAmountresource() {
        return amountresource;
    }

    public Location getDroplocation() {
        return droplocation;
    }

    public String getColorofresource() {
        return colorofresource;
    }

    public boolean isIspickedup() {
        return ispickedup;
    }

    public Items getGivenitem() {
        return givenitem;
    }

    public String getTypeofdrop() {
        return typeofdrop;
    }

    public long getThisgiventime() {
        return thisgiventime;
    }
}
