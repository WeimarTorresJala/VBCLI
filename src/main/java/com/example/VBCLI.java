package com.example;

public class VBCLI {

    public static void main(String[] args) {
        String  url = "http://localhost:18083";
        String  user = null;
        String  password = null;

        boolean test = false;
        boolean start = false;
        String nameMachine = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-url":
                    url = args[++i];
                    break;
                case "-u":
                    user = args[++i];
                    break;
                case "-p":
                    password = args[++i];
                    break;
                case "-t":
                    test = true;
                    break;
                case "-s":
                    start = true;
                    nameMachine = args[++i];
                    break;
            }
        }

        VBManager box = new VBManager(url, user, password);

        if (test) {
            box.testVB();
        } else if (start) {
            box.startMachine(nameMachine);
        }
    }

}
