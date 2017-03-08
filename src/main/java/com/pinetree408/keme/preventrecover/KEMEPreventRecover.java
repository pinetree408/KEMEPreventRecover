package com.pinetree408.keme.preventrecover;

/**
 * Created by user on 2017-03-08.
 */
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.AWTException;
import java.awt.Robot;

import com.pinetree408.keme.util.ModeErrorLogger;
import com.pinetree408.keme.util.TopProcess;
import com.pinetree408.keme.recover.Recover;
import com.pinetree408.keme.prevent.Prevent;

public class KEMEPreventRecover implements NativeKeyListener {
    static Prevent prevent;
    static Recover recover;
    /** buffer writer to save log */
    private static ModeErrorLogger meLogger;
    static TopProcess topProcess;

    static Robot robot;

    public KEMEPreventRecover() {
        meLogger = new ModeErrorLogger("result.txt");
        topProcess = new TopProcess();

        prevent = new Prevent();
        recover = new Recover();

        // Initialize Robot for prevent word injection
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (prevent.isStateChecking()) {
            recover.keyPressed(e, robot);
        } else {
            prevent.keyPressed(e, robot);
        }
        meLogger.log(e, topProcess.getNowLanguage(), topProcess.getNowTopProcess(), recover.getRecoverState(), prevent.getPreventState());
    }

    public static void main(String[] args) {

        // Set jnativehook logger level to off state
        Logger EventLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        EventLogger.setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new KEMEPreventRecover());

        Timer jobScheduler = new Timer();
        jobScheduler.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (topProcess.isChangeProcess()) {
                            recover.initialize();
                            prevent.injection(topProcess.getNowLanguage(), robot);
                        }
                    }
                },
                0,
                100);
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }
}
