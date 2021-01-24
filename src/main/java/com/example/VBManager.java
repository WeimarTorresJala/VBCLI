package com.example;

import org.virtualbox_6_1.*;

import java.util.*;

public class VBManager {
    private final VirtualBoxManager boxManager;
    private final IVirtualBox vbox;

    public VBManager(String url, String user, String password) {
        boxManager = VirtualBoxManager.createInstance(null);

        try {
            boxManager.connect(url, user, password);
        } catch (VBoxException e) {
            e.printStackTrace();
            System.err.println("Cannot connect, start webserver first!");
        }

        vbox = boxManager.getVBox();
    }

    public void testVB() {
        try {
            // Version of VBox
            getVBoxVersion();
            // Test machines
            testEnumeration(this.boxManager, this.vbox);
            // Test log
            testReadLog(this.boxManager, this.vbox);
            // Test to start a VM
            testStart(this.boxManager, this.vbox, 1);
            // Test events
            testEvents(this.boxManager, this.vbox.getEventSource());
            // Test shutdown
            testShutdown(this.boxManager, this.vbox, 1);

            System.out.println("\nEverything is ok!");
        } catch (VBoxException e)
        {
            printErrorInfo(e);
            System.out.println("Java stack trace:");
            e.printStackTrace();
        }
        catch (RuntimeException e)
        {
            System.out.println("Runtime error: " + e.getMessage());
            e.printStackTrace();
        }

        // process system event queue
        this.boxManager.waitForEvents(0);

        try {
            this.boxManager.disconnect();
        } catch (VBoxException e) {
            e.printStackTrace();
        }

        this.boxManager.cleanup();
    }

    public void getVBoxVersion() {
        System.out.println("VirtualBox version: " + this.vbox.getVersion());
    }

    // For test all machines
    private void testEnumeration(VirtualBoxManager boxManager, IVirtualBox vbox) {
        List<IMachine> machines = vbox.getMachines();
        for (IMachine m : machines)
        {
            String name;
            Long ram = 0L;
            boolean hwvirtEnabled = false;
            boolean hwvirtNestedPaging = false;
            boolean paeEnabled = false;
            boolean inaccessible = false;
            String osType = "";

            try
            {
                name = m.getName();
                ram = m.getMemorySize();
                hwvirtEnabled = m.getHWVirtExProperty(HWVirtExPropertyType.Enabled);
                hwvirtNestedPaging = m.getHWVirtExProperty(HWVirtExPropertyType.NestedPaging);
                paeEnabled = m.getCPUProperty(CPUPropertyType.PAE);
                osType = m.getOSTypeId();
                IGuestOSType foo = vbox.getGuestOSType(osType);
            }
            catch (VBoxException e)
            {
                name = "<inaccessible>";
                inaccessible = true;
            }
            System.out.println("\nVM name: " + name);
            if (!inaccessible)
            {
                System.out.println(" RAM size: " + ram + "MB"
                        + ", HWVirt: " + hwvirtEnabled
                        + ", Nested Paging: " + hwvirtNestedPaging
                        + ", PAE: " + paeEnabled
                        + ", OS Type: " + osType);
            }
        }

        System.out.println("");
        // process system event queue
        boxManager.waitForEvents(0);
    }

    // To see a log of a machine
    private void testReadLog(VirtualBoxManager boxManager, IVirtualBox vbox) {
        IMachine machine =  vbox.getMachines().get(0);
        long logNo = 0;
        long off = 0;
        long size = 16 * 1024;
        while (true)
        {
            byte[] buf = machine.readLog(logNo, off, size);
            if (buf.length == 0)
                break;
            System.out.print(new String(buf));
            off += buf.length;
        }

        // process system event queue
        boxManager.waitForEvents(0);
    }

    // Test start a VM
    private void testStart(VirtualBoxManager boxManager, IVirtualBox vbox, int nMachine) {
        IMachine m = vbox.getMachines().get(nMachine);
        String name = m.getName();

        start(m, name);
    }

    // Test event
    private void testEvents(VirtualBoxManager boxManager, IEventSource es) {
        // active mode for Java doesn't fully work yet, and using passive
        // is more portable (the only mode for MSCOM and WS) and thus generally
        // recommended
        IEventListener listener = es.createListener();

        es.registerListener(listener, Arrays.asList(VBoxEventType.Any), false);

        try {
            for (int i = 0; i < 125; i++)
            {
                System.out.print(".");
                IEvent ev = es.getEvent(listener, 500);
                if (ev != null)
                {
                    processEvent(ev);
                    es.eventProcessed(listener, ev);
                }
                // process system event queue
                boxManager.waitForEvents(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        es.unregisterListener(listener);
    }

    // Process event
    private void processEvent(IEvent ev)
    {
        System.out.println("got event: " + ev);
        VBoxEventType type = ev.getType();
        System.out.println("type = " + type);
        if (type == VBoxEventType.OnMachineStateChanged) {
            IMachineStateChangedEvent mcse = IMachineStateChangedEvent.queryInterface(ev);
            if (mcse == null)
                System.out.println("Cannot query an interface");
            else
                System.out.println("mid=" + mcse.getMachineId());
        }
    }

    // Test shutdown
    private void testShutdown(VirtualBoxManager boxManager, IVirtualBox vbox, int nMachine) {
        IMachine machine = vbox.getMachines().get(nMachine);
        String name = machine.getName();

        shutdown(name, machine);
    }

    // Wait while the progress finish
    private void wait(IProgress progress) {
        progress.waitForCompletion(-1);
        if (progress.getResultCode() != 0) {
            System.err.println("Operation failed: " + progress.getErrorInfo().getText());
        }
    }

    // Wait until the machine is unlocked
    private void waitToUnlock(ISession session, IMachine machine) {
        session.unlockMachine();
        SessionState sessionState = machine.getSessionState();
        while (!SessionState.Unlocked.equals(sessionState)) {
            sessionState = machine.getSessionState();
            try {
                System.out.println("Waiting for session unlock...[" + sessionState.name() + "][" + machine.getName() + "]");
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for session to be unlocked");
            }
        }
    }

    // Print error
    private void printErrorInfo(VBoxException e) {
        System.err.println("VBox error: " + e.getMessage());
        System.err.println("Error cause message: " + e.getCause());
        System.err.println("Overall result code: " + Integer.toHexString(e.getResultCode()));
        int i = 1;
        for (IVirtualBoxErrorInfo ei = e.getVirtualBoxErrorInfo(); ei != null; ei = ei.getNext(), i++)
        {
            System.err.println("Detail information #" + i);
            System.err.println("Error message: " + ei.getText());
            System.err.println("Result code:  " + Integer.toHexString(ei.getResultCode()));
            // optional, usually provides little additional information:
            System.err.println("Component:    " + ei.getComponent());
            System.err.println("Interface ID: " + ei.getInterfaceID());
        }
    }

    // Start machine
    public void startMachine(String name) {
        if (!machineExists(name)) {
            System.err.println("The machine doesn't exist");
        } else {
            IMachine machine = vbox.findMachine(name);

            start(machine, name);
        }
    }

    private void start(IMachine machine, String name) {
        System.out.println("\nAttempting to start VM '" + name + "'\n");

        ISession session = boxManager.getSessionObject();
        try {
            ArrayList<String> env = new ArrayList<String>();
            IProgress progress = machine.launchVMProcess(session, "gui", null);
            wait(progress);
        } finally {
            session.unlockMachine();
            System.out.println("Machine [" + name + "] start successfully");

            // process system event queue
            boxManager.waitForEvents(0);
        }
    }

    // Verify if machine exist
    private boolean machineExists(String machineName) {
        ///VBOX_E_OBJECT_NOT_FOUND
        //kind of "exists"
        if (machineName == null) {
            return false;
        }
        //since the method findMachine returns org.virtualbox_5_2.VBoxExceptio
        //if the machine doesn't exists we will need to find it by
        //ourselves iterating over all the machines
        List<IMachine> machines = vbox.getMachines();
        for (IMachine machine : machines) {
            if (machine.getName().equals(machineName)) {
                return true;
            }
        }
        return false;
    }

    // Shutdown machine
    public void shutdownMachine(String machineName) {
        if (!machineExists(machineName)) {
            System.err.println("The machine doesn't exist");
        } else {
            IMachine machine = vbox.findMachine(machineName);

            shutdown(machineName, machine);
        }
    }

    private void shutdown(String name, IMachine machine) {
        System.out.println("\n\nAttempting to shutdown VM '" + name + "'\n");

        MachineState state = machine.getState();
        ISession session = boxManager.getSessionObject();
        machine.lockMachine(session, LockType.Shared);
        try {
            if (state.value() >= MachineState.FirstOnline.value() && state.value() <= MachineState.LastOnline.value()) {
                IProgress progress = session.getConsole().powerDown();
                wait(progress);
            }
        } finally {
            waitToUnlock(session, machine);

            System.out.println("Machine [" + name + "] shutdown successfully");

            // process system event queue
            boxManager.waitForEvents(0);
        }
    }
}
