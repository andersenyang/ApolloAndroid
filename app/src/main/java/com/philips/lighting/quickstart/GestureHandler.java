package com.philips.lighting.quickstart;

import com.philips.lighting.quickstart.devices.*;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by andersenyang on 3/9/16.
 */

public class GestureHandler {
    private DeviceController controller;
    private DeviceEnum selectedDevice;
    private Context context;
    private static GestureHandler instance = null;

    private GestureHandler() {}
    private GestureHandler(Context context) {
        DeviceEnum.PHONE.getDevice().setContext(context);  // Phone needs context to control volume
        this.selectedDevice = DeviceEnum.PHONE;
        this.context = context;

        controller = new DeviceController(this.selectedDevice.getDevice());
    }

    public static GestureHandler getInstance(Context context) {
        if (instance == null) {
            Log.d("Imperium", "GestureHandler create new instance");
            instance = new GestureHandler(context);
        }
        Log.d("Imperium", "GestureHandler return existing instance");
        return instance;
    }

    public void handleGesture(int gesture) {
        Log.d("Imperium", "handleGesture");
        if (gesture == 6) {
            Log.d("Imperium", "desperate Log");
            switchDevices();
        } else {
            controller.performDeviceAction(gesture);
        }
    }

    private void switchDevices() {
        Log.d("Imperium", String.valueOf(this.selectedDevice));
        switch (this.selectedDevice) {
            case PHONE:
                this.selectedDevice = DeviceEnum.HUE;
                break;
            case HUE:
                this.selectedDevice = DeviceEnum.PHONE;
                break;
        }
        Toast.makeText(this.context, this.selectedDevice.toString(), Toast.LENGTH_SHORT).show();
        controller.setDevice(this.selectedDevice.getDevice());
    }

    public void close() {
        DeviceEnum.HUE.getDevice().close();
    }
}

enum DeviceEnum {
    HUE (new HueLamp()),
    PHONE (new Phone());

    private Device device;
    DeviceEnum(Device d) {
        this.device = d;
    }

    Device getDevice() {
        return this.device;
    }
}