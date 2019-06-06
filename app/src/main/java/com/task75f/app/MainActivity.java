package com.task75f.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.task75f.app.view.ArcSeekBar;

/**
 * MainActivity
 *
 * @author Mahesh
 */
public class MainActivity extends AppCompatActivity {

    private TextView mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = findViewById(R.id.progress);
        //
        ArcSeekBar arcSeekBar = findViewById(R.id.arch);

        findViewById(R.id.homeBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        arcSeekBar.setOnSeekArcChangeListener(new ArcSeekBar.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(ArcSeekBar seekArc, float progress) {
                mProgress.setText(String.valueOf(Math.round(progress)));
            }

            @Override
            public void onStartTrackingTouch(ArcSeekBar seekArc) {

            }

            @Override
            public void onStopTrackingTouch(ArcSeekBar seekArc) {

            }
        });

    }
}
