package com.philips.lighting.quickstart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ControlActivity extends Activity {

    private GestureHandler gestureHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        gestureHandler = GestureHandler.getInstance(this);  //context needs to be the Service!

        Button switchDevicesButton, actionOneButton,
                actionTwoButton, actionThreeButton,
                connectHueButton;
        switchDevicesButton = (Button) findViewById(R.id.switchDevicesButton);
        actionOneButton = (Button) findViewById(R.id.actionOneButton);
        actionTwoButton = (Button) findViewById(R.id.actionTwoButton);
        actionThreeButton = (Button) findViewById(R.id.actionThreeButton);
        connectHueButton = (Button) findViewById(R.id.connectHueButton);

        switchDevicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(6);
            }

        });

        actionOneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(1);
            }

        });

        actionTwoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(2);
            }

        });

        actionThreeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(3);
            }

        });

        connectHueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PHHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        gestureHandler.close();
        super.onDestroy();
    }
}
