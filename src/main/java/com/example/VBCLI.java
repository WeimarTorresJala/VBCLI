package com.example;

public class VBCLI {

    public static void main(String[] args) {
        String  url = "http://localhost:18083";
        String  user = null;
        String  password = null;
        String machineName = null;
        String osType = null;
        long memory = 0;
        int hardDisk = 0;
        String iso = null;

        // Test variables
        boolean test = false;

        // List variables
        boolean list = false;

        // Start variables
        boolean start = false;

        // Shutdown variables
        boolean shutdown = false;

        // Create variables
        boolean create = false;

        // Delete variables
        boolean delete = false;

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
                case "-l":
                    list = true;
                    break;
                case "-s":
                    start = true;
                    machineName = args[++i];
                    break;
                case "-sd":
                    shutdown = true;
                    machineName = args[++i];
                    break;
                case "-c":
                    create = true;
                    machineName = args[i + 1];
                    osType = args[i + 2];
                    memory = Long.parseLong(args[i + 3]);
                    hardDisk = Integer.parseInt(args[i + 4]);
                    iso = args[i + 5];
                    break;
                case "-d":
                    delete = true;
                    machineName = args[++i];
                    break;
            }
        }

        VBManager box = new VBManager(url, user, password);

        if (test) {
            box.testVB();
        } else if (list) {
            box.list();
        } else if (start) {
            box.startMachine(machineName);
        } else if (shutdown) {
            box.shutdownMachine(machineName);
        } else if (create) {
            box.createMachine(machineName, osType, memory, hardDisk, iso);
        } else if (delete) {
            box.deleteMachine(machineName);
        } else {
            box.getVBoxVersion();
        }
    }
}
