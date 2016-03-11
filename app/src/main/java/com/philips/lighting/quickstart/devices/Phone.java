package com.philips.lighting.quickstart.devices;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by andersenyang on 3/9/16.
 */
public class Phone extends Device {
    private Context context;

    @Override
    public void performAction(int gesture) {
        Log.d("Imperium", String.valueOf(gesture));
        switch (gesture) {
            case 1:
                changeVolume(2);
                break;
            case 2:
                changeVolume(-2);
                break;
            case 3:
                // Need to implement this; what do we want to do?
                break;
        }
    }

    public void changeVolume(int volumeStep) {
        AudioManager mgr = null;

        mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //int maxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //int minVolume = 0;
        int volume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);

        mgr.setStreamVolume(AudioManager.STREAM_MUSIC, (volume+volumeStep), AudioManager.FLAG_SHOW_UI);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
