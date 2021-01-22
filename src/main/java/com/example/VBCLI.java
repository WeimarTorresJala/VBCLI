package com.example;

public class VBCLI {

    public static void main(String[] args) {
        VBManager box = new VBManager();

        System.out.println("VirtualBox version: " + box.getVBoxVersion());
    }

}
