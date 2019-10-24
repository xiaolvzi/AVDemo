package com.example.avdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * desc: App入口 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btAudioCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        btAudioCapture = findViewById(R.id.bt_audio_capture);
    }

    private void initListener() {
        btAudioCapture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_audio_capture:
                break;
            default:
                break;
        }
    }
}
