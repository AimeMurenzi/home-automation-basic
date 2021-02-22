/**
 * @author Aimé
 * @email 
 * @create date 2021-01-26 23:16:47
 * @modify date 2021-01-26 23:16:47
 * @desc [description]
 */

package home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;

import com.pi4j.io.gpio.*;

@Provider
@Singleton
public class StateMap implements IStateMap {
    @Inject
    private ISseResource sseResource;
    private final GpioController gpio;
    private final GpioPinDigitalOutput speakersPin;
    private final GpioPinDigitalOutput lightsPin;
    // private final GpioPinDigitalOutput speakersPin = null;
    // private final GpioPinDigitalOutput lightsPin = null;
    private Map<String, Boolean> stateMap;
    private final String saveName = "state-map";
    public StateMap() {
        gpio = GpioFactory.getInstance();
        speakersPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24);
        lightsPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25); 

        if ((stateMap = open(saveName)) == null) {
            stateMap = new HashMap<>();
            // TODO:DONE change this to get actual GPIO state
            // stateMap.put("speakers", false);
            // stateMap.put("lights", false);
            stateMap.put("speakers", speakersPin.isHigh());
            stateMap.put("lights", lightsPin.isHigh());
        }

    }

    public Map<String, Boolean> getState() {
        return stateMap;
    }

    private String lightsString = "lights";
    private String speakersString = "speakers";

    public void speakersOn() {
        stateMap.put("speakers", turnOn(speakersPin));
        notifySubscribers(speakersString, turnOn(speakersPin));
    }

    public void speakersOff() {
        stateMap.put("speakers", turnOff(speakersPin));
        notifySubscribers(speakersString, turnOff(speakersPin));
    }

    public void lightsOn() {
        stateMap.put("lights", turnOn(lightsPin));
        notifySubscribers(lightsString, turnOn(lightsPin));
    }

    public void lightsOff() {
        stateMap.put("lights", turnOff(lightsPin));
        notifySubscribers(lightsString, turnOff(lightsPin));

    }

    private boolean turnOn(GpioPinDigitalOutput pinDigitalOutput) {
        pinDigitalOutput.high();
        save();
        return true;

    }

    private boolean turnOff(GpioPinDigitalOutput pinDigitalOutput) {
        pinDigitalOutput.low();
        save();
        return false;
    }

    private boolean saving = false;

    private void save() {
        if (!saving) {
            new Thread(() -> {
                saving = true;
                try (FileOutputStream stateMapFileOutputStream = new FileOutputStream(saveName);
                        ObjectOutputStream stateMapObjectOutputStream = new ObjectOutputStream(
                                stateMapFileOutputStream);) {
                    stateMapObjectOutputStream.writeObject(stateMap);
                    System.out.printf("stateMap save success");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                saving = false;
                saveComplete();
            }, "saving").start();
        } else {
            saveQueue.add(false);
        }
    }

    // why boolean? its just a place holder and it takes less space in memory
    private Queue<Boolean> saveQueue = new LinkedList<>();

    private void saveComplete() {
        // if not saving and there is still something on queue
        if (!saving && saveQueue.poll() != null)
            save();
    }

    private Map<String, Boolean> open(String saveName) { 
        String dir = System.getProperty("user.dir");
        System.out.println("CURRENT WORKING DIR: " + dir);
        File file = new File(dir);
        System.out.println("FILES IN CURRENT WORKING DIR: " + file.list());

        if (Files.exists(Paths.get(saveName))) {
            try (FileInputStream fileIn = new FileInputStream(saveName);
                    ObjectInputStream in = new ObjectInputStream(fileIn);) {
                return (Map<String, Boolean>) in.readObject();
            } catch (IOException | ClassNotFoundException | ClassCastException icc) {
                icc.printStackTrace();
            }
        }
        return null; 
    }

    private void notifySubscribers(String stateNameString, boolean state) {
        stateMap.put(stateNameString, state);
        sseResource.broadcast(getState(), "stateMap");
    }

}

// ##################################### DEBUG MODE
/**
 * // * @author Aimé // * @email // * @create date 2021-01-26 22:18:15 //
 * * @modify date 2021-01-26 22:18:15 // * @desc [description] //
 */
// package home;
//
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.HashMap;
// import java.util.LinkedList;
// import java.util.Map;
// import java.util.Queue;
//
// import javax.inject.Inject;
// import javax.inject.Singleton;
// import javax.ws.rs.ext.Provider;
//
// import com.pi4j.io.gpio.*;
//
// @Provider
// @Singleton
// public class StateMap implements IStateMap {
// @Inject
// private ISseResource sseResource;
// private final GpioController gpio;
// private final GpioPinDigitalOutput speakersPin;
// private final GpioPinDigitalOutput lightsPin;
// // private final GpioPinDigitalOutput speakersPin=null;
// // private final GpioPinDigitalOutput lightsPin=null;
// private Map<String, Boolean> stateMap;
// private final String saveName = "state-map";
// private static int testNumberOfCallsToCostructor = 0;
//
// public StateMap() {
// System.out.println("TEST:::::::::::::::::::::::: " +
// ++testNumberOfCallsToCostructor);
// gpio = GpioFactory.getInstance();
// speakersPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24);
// lightsPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25);
//
// // stateMap = open(saveName);
// if ((stateMap = open(saveName)) == null) {
// stateMap = new HashMap<>();
// // TODO: change this to get actual GPIO state
// // stateMap.put("speakers", false);
// stateMap.put("lights", false);
// stateMap.put("speakers", speakersPin.isHigh());
// stateMap.put("lights", lightsPin.isHigh());
// }
//
// }
//
// public Map<String, Boolean> getState() {
// return stateMap;
// }
// private String lightsString="lights";
// private String speakersString="speakers";
// public void speakersOn() {
// stateMap.put("speakers", turnOn(speakersPin));
// notifySubscribers(speakersString, turnOn(speakersPin));
// }
// public void speakersOff() {
// stateMap.put("speakers", turnOff(speakersPin));
// notifySubscribers(speakersString, turnOff(speakersPin));
// }
// public void lightsOn() {
// stateMap.put("lights", turnOn(lightsPin));
// notifySubscribers(lightsString, turnOn(lightsPin));
// }
// public void lightsOff() {
// stateMap.put("lights", turnOff(lightsPin));
// notifySubscribers(lightsString,turnOff(lightsPin));
//
// }
// private boolean turnOn(GpioPinDigitalOutput pinDigitalOutput) {
// pinDigitalOutput.high();
// save();
// return true;
// }
//
// private boolean turnOff(GpioPinDigitalOutput pinDigitalOutput) {
// pinDigitalOutput.low();
// save();
// return false;
// }
//
// private boolean saving = false;
//
// private void save() {
// if (!saving) {
// new Thread(() -> {
// saving = true;
// try (FileOutputStream stateMapFileOutputStream = new
// FileOutputStream(saveName);
// ObjectOutputStream stateMapObjectOutputStream = new ObjectOutputStream(
// stateMapFileOutputStream);) {
// stateMapObjectOutputStream.writeObject(stateMap);
// System.out.printf("stateMap save success");
// } catch (IOException ioException) {
// ioException.printStackTrace();
// }
// saving = false;
// saveComplete();
// }, "saving").start();
// } else {
// saveQueue.add(false);
// }
// }
//
// // why boolean? its just a place holder and it takes less space in memory
// private Queue<Boolean> saveQueue = new LinkedList<>();
//
// private void saveComplete() {
// // if not saving and there is still something on queue
// if (!saving && saveQueue.poll() != null)
// save();
// }
//
// private Map<String, Boolean> open(String saveName) {
//
// String dir = System.getProperty("user.dir");
// System.out.println("CURRENT WORKING DIR: " + dir);
// File file = new File(dir);
// System.out.println("FILES IN CURRENT WORKING DIR: " + file.list());
//
// if (Files.exists(Paths.get(saveName))) {
// try (FileInputStream fileIn = new FileInputStream(saveName);
// ObjectInputStream in = new ObjectInputStream(fileIn);) {
// return (Map<String, Boolean>) in.readObject();
// } catch (IOException | ClassNotFoundException | ClassCastException icc) {
// icc.printStackTrace();
// }
// }
// return null;
//
// }
// private void notifySubscribers(String stateNameString,boolean state){
// stateMap.put(stateNameString, state);
// sseResource.broadcastLightEvent(getState(), "stateMap");
// }
//
// }
//
//
//
//