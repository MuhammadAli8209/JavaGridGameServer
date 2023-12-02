package com.jtconnors.multisocketserverfx;

public class Items {

    private String name;
    private int damage;

    private int cost;

    public Items(String givenname, int givendamage, int givencost){
        this.name=givenname;
        this.damage = givendamage;
        this.cost = givencost;
    }

    public String getItemname() {
        return name;
    }

    public int getItemCost() {
        return cost;
    }

    public int getItemDamage() {
        return damage;
    }

}
