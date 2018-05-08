package com.jade.skipview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SkipView mSkipView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSkipView = (SkipView) findViewById(R.id.skipView);


        mSkipView.setOnSkipListener(new SkipView.OnSkipListener() {
            @Override
            public void onSkip() {
                startActivity(new Intent(getApplicationContext(), TwoActivity.class));
                finish();
            }

            @Override
            public void setProgress(int progress) {
                Log.w("jade","progress--->" + progress);
            }
        });

    }

    public void click(View view) {
        mSkipView.start();
    }
}
