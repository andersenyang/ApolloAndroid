package com.philips.lighting.quickstart;

import com.philips.lighting.quickstart.devices.*;

/**
 * Created by andersenyang on 3/9/16.
 */

public class GestureHandler {
    private DeviceController controller;
    private DeviceEnum selectedDevice;

    public GestureHandler() {
        selectedDevice = DeviceEnum.HUE;
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