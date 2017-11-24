package com.lenovo.smartcastvoice.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lenovo.smartcastvoice.R;
import com.lenovo.smartcastvoice.utils.AnimationUtils;
import com.lenovo.smartcastvoice.utils.StatusBarUtils;

public class SpeekHintActivity extends Activity implements View.OnClickListener, AnimationUtils.AnimationListener{

    private ImageView mic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speek_hint);
        StatusBarUtils.hideNavgationBar(this);
        initViews();
        initEvents();

    }

    private void initViews() {
        mic = findViewById(R.id.mic_iv);
    }

    private void initEvents() {
        mic.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mic_iv:
                AnimationUtils.playAnimation(mic, this);
                break;
        }
    }

    @Override
    public void EndAnimation(View view) {
        startActivity(new Intent(SpeekHintActivity.this, RecordActivity.class));
        finish();
    }
}
