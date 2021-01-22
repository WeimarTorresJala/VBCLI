package com.example;

import org.virtualbox_6_1.*;

public class VBManager {

    private final org.virtualbox_6_1.VirtualBoxManager boxManager;
    private final IVirtualBox vbox;
    private IProgress progress;

    public VBManager() {
        boxManager = org.virtualbox_6_1.VirtualBoxManager.createInstance(null);
        boxManager.connect("http://localhost:18083", null, null);
        vbox = boxManager.getVBox();
    }

    public String getVBoxVersion() {
        return vbox.getVersion();
    }

}
