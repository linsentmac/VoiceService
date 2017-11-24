package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.VoiceService;

/**
 * Created by linsen on 17-11-22.
 */

public class StartActivity extends Activity {

    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        start = findViewById(R.id.startActivity);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(StartActivity.this, VoiceService.class));
            }
        });
    }

}
