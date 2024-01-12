package com.example.beachingns;

public class BeachItem {
    private String name;
    private String description;
    private String location;
    private String wheelchairAccess;
    private String floatingWheelchair;
    private String capacity;
    private String sandyOrRocky;
    private String imageSource;
    private String visualWaterConditions;

    public BeachItem(){

    }

    public BeachItem(String name,String imageSource,String capacity,String visualWaterConditions,String wheelchairAccess,String sandyOrRocky, String floatingWheelchair){
        this.name = name;
        this.wheelchairAccess = wheelchairAccess;
        this.floatingWheelchair = floatingWheelchair;
        this.capacity = capacity;
        this.sandyOrRocky = sandyOrRocky;
        this.imageSource = imageSource;
        this.visualWaterConditions = visualWaterConditions;
    }

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getvisualWaterConditions(){return visualWaterConditions;}
    public void setvisualWaterConditions(String visualWaterConditions){this.visualWaterConditions = visualWaterConditions;}

    public String getwheelchairAccess(){return wheelchairAccess;}
    public void setwheelchairAccess(String wheelchairAccess){this.wheelchairAccess = wheelchairAccess;}

    public String getFloatingWheelchair(){return floatingWheelchair;}
    public void setFloatingWheelchair(String floatingWheelchair){this.floatingWheelchair = floatingWheelchair;}

    public String getcapacity(){return capacity;}
    public void setcapacity(String capacity){this.capacity = capacity;}

    public String getsandyOrRocky(){return sandyOrRocky;}
    public void setsandyOrRocky(String sandyOrRocky){this.sandyOrRocky = sandyOrRocky;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getImageSource(){return imageSource;}
    public void setImageSource(String imageSource){this.imageSource = imageSource;}

    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}
}