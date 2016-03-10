package com.philips.lighting.quickstart;

import com.philips.lighting.quickstart.devices.Device;

/**
 * Created by andersenyang on 3/9/16.
 */

public class DeviceController {
    Device device;

    public DeviceController(Device d) {
        device = d;
    }

    public void performDeviceAction(int gesture) {
        device.performAction(gesture);
    }

    public void setDevice(Device d) {
        device = d;
    }
}
