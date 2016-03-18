package com.philips.lighting.quickstart;

import com.philips.lighting.quickstart.devices.*;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.TextView;
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
    private GestureHandler self = this;

    private GestureHandler() {}
    private GestureHandler(Context context) {
        DeviceEnum.PHONE.getDevice().setContext(context);  // Phone needs context to control volume
        this.selectedDevice = DeviceEnum.PHONE;
        this.context = context;

        controller = new DeviceController(this.selectedDevice.getDevice());
    }

    public static GestureHandler getInstance(Context context) {
        Log.d("Imperium", context.toString());
        if (instance == null) {
            instance = new GestureHandler(context);
        }
        return instance;
    }

    public void handleGesture(int gesture) {
        if (gesture == 6) {
            switchDevices();
        } else {
            controller.performDeviceAction(gesture);
        }
    }

    private void switchDevices() {
        switch (this.selectedDevice) {
            case PHONE:
                this.selectedDevice = DeviceEnum.HUE;
                break;
            case HUE:
                this.selectedDevice = DeviceEnum.PHONE;
                break;
        }
        controller.setDevice(this.selectedDevice.getDevice());
        final Activity activity = (Activity) this.context;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                TextView textView = (TextView) activity.findViewById(R.id.gesture);
                textView.setText(self.selectedDevice.toString().toLowerCase());
                //Toast.makeText(activity, self.selectedDevice.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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