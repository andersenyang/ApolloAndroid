package com.philips.lighting.quickstart;

import com.philips.lighting.quickstart.devices.*;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by andersenyang on 3/9/16.
 */

public class GestureHandler {
    private DeviceController controller;
    private DeviceEnum selectedDevice;

    private Context context;

    public GestureHandler(Context context) {
        DeviceEnum.PHONE.getDevice().setContext(context);  // Phone needs context to control volume
        selectedDevice = DeviceEnum.PHONE;
        this.context = context;
        controller = new DeviceController(selectedDevice.getDevice());
    }

    public void handleGesture(int gesture) {
        if (gesture == 6) {
            switchDevices();
        } else {
            controller.performDeviceAction(gesture);
        }
    }

    private void switchDevices() {
        switch (selectedDevice) {
            case PHONE:
                selectedDevice = DeviceEnum.HUE;
                break;
            case HUE:
                selectedDevice = DeviceEnum.PHONE;
                break;
        }
        Toast.makeText(this.context, selectedDevice.toString(), Toast.LENGTH_SHORT).show();
        controller.setDevice(selectedDevice.getDevice());
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