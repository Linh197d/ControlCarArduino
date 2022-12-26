package com.example.myappcarcontrol.model;

public  class Function {
    private String Name;
    private int mImage;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }



    public Function(String name, int image) {
        Name = name;
        this.mImage = image;
    }
}