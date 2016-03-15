package com.philips.lighting.quickstart.devices;

import java.util.List;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class HueLamp extends Device {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    private static final int MAX_BRIGHTNESS=255;
    private static final int MAX_SATURATION=255;
    private static final int HUE_LEVELS=15;
    private static final int BRIGHTNESS_LEVELS=6;
    private static final int SATURATION_LEVELS=6;

    public HueLamp() {
        phHueSDK = phHueSDK.create();
    }

    @Override
    public void performAction (int gesture) {
        switch (gesture) {
            case 1:
                changeBrightness(true);
                break;
            case 2:
                changeLightColour();
                break;
            case 3:
                colorLoop();
                break;
        }
    }

    private void changeLightColour() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        List<PHLight> lights = cache.getAllLights();

        for(PHLight light : lights) {
            PHLightState lightState = light.getLastKnownLightState();
            PHLightState lightStateNew = new PHLightState();

            int sat = lightState.getSaturation();
            int hue = lightState.getHue();

            sat = sat + MAX_SATURATION/SATURATION_LEVELS;

            if (sat > MAX_SATURATION) {
                sat = sat - MAX_SATURATION;
                hue = hue + MAX_HUE/HUE_LEVELS;

                if (hue > MAX_HUE) {
                    hue = hue - MAX_HUE;
                }
            }

            lightStateNew.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
            lightStateNew.setHue(hue);
            lightStateNew.setSaturation(sat);
            bridge.updateLightState(light, lightStateNew);
        }
    }

    private void changeBrightness(boolean brighter) {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> lights = bridge.getResourceCache().getAllLights();

        for (PHLight light: lights){
            PHLightState lightState = light.getLastKnownLightState();
            PHLightState lightStateNew = new PHLightState();
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

            lightStateNew.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
            lightStateNew.setBrightness(brightness);
            bridge.updateLightState(light, lightStateNew);
        }
    }

    private void colorLoop() {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> lights = bridge.getResourceCache().getAllLights();

        for (PHLight light: lights) {
            PHLightState lightStateNew = new PHLightState();
            lightStateNew.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
            bridge.updateLightState(light, lightStateNew);
        }
    }

    @Override
    public void close() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
        }
    }
}
