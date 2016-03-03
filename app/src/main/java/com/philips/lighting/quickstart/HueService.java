package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class HueService extends Service{
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    private static final int HUE_LEVELS=10;
    private static final int MAX_BRIGHTNESS=254;
    private static final int BRIGHTNESS_LEVELS=10;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        phHueSDK = PHHueSDK.getInstance();
    }

    @Override
    public void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
        }

        phHueSDK.disconnect(bridge);
        super.onDestroy();
    }


    public void changeLightColour() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        // maybe this can just be a private member?
        List<PHLight> lights = cache.getAllLights();

        for(PHLight light : lights) {
            PHLightState lightState = light.getLastKnownLightState();

            int hue = lightState.getHue();
            hue = hue + MAX_HUE/HUE_LEVELS;

            if (hue > MAX_HUE) {
                hue = hue - MAX_HUE;
            }

            lightState.setHue(hue);
            bridge.updateLightState(light, lightState);
        }
    }

    public void changeBrightness(boolean brighter) {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        List<PHLight> lights = cache.getAllLights();

        for (PHLight light: lights){
            PHLightState lightState = light.getLastKnownLightState();
            int brightness = lightState.getBrightness();

            if (brighter) {
                brightness = brightness + MAX_BRIGHTNESS/BRIGHTNESS_LEVELS;
                if (brightness > MAX_BRIGHTNESS) {
                    brightness = brightness - MAX_BRIGHTNESS;
                }
            } else if (brightness > 0) {
                brightness = brightness - MAX_BRIGHTNESS/BRIGHTNESS_LEVELS;
                if (brightness < 0) {
                    brightness = 0;
                }
            }

            lightState.setBrightness(brightness);
            bridge.updateLightState(light, lightState);
        }

    }

}
