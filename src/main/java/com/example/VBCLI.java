package com.example;

public class VBCLI {

    public static void main(String[] args) {
        VBManager box = new VBManager();

        System.out.println("Virtual Box version: " + box.getVBoxVersion());
    }

}
