package com.jtconnors.multisocketserverfx;

public class Weapons {

    private String weaponname;
    private int wapondamage;
    private int weaponrange;
    private int weaponspread;
    private int weaponcost;

    //private Location weaponlocation;

    public Weapons(String name, int damage, int range, int spread, int cost){
        this.wapondamage = damage;
        this.weaponname = name;
        this.weaponrange = range;
        this.weaponspread = spread;
        this.weaponcost = cost;
    }

    public String getWeaponname() {
        return weaponname;
    }

    public int getWapondamage() {
        return wapondamage;
    }

    public int getWeaponrange() {
        return weaponrange;
    }

    public int getWeaponspread() {
        return weaponspread;
    }

    public int getWeaponcost() {
        return weaponcost;
    }
}
