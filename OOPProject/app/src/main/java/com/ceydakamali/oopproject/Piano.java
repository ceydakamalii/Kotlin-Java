package com.ceydakamali.oopproject;

public class Piano implements HouseDecor,Instrument{

    String brand;
    boolean digital;

    @Override
    public void info() {
        System.out.println("Override method");
    }
}
