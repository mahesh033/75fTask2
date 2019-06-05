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

    //
    private ArcSeekBar mArcSeekBar;
    private TextView mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = findViewById(R.id.progress);
        mArcSeekBar = findViewById(R.id.arch);

        findViewById(R.id.homeBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mArcSeekBar.setAngle(50);
        mArcSeekBar.setOnProtractorViewChangeListener(new ArcSeekBar.OnProtractorViewChangeListener() {
            @Override
            public void onProgressChanged(ArcSeekBar protractorView, int progress, boolean fromUser) {
                mProgress.setText(String.valueOf(progress));

                if (progress == 100) {
                    mArcSeekBar.setArcBackgroundPaintColor(getResources().getColor(R.color.default_red), progress);
                } else if (progress == 50) {
                    mArcSeekBar.setArcBackgroundPaintColor(getResources().getColor(R.color.default_blue), progress);
                } else {
                    mArcSeekBar.setArcBackgroundPaintColor(getResources().getColor(R.color.default_red), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(ArcSeekBar protractorView) {

            }

            @Override
            public void onStopTrackingTouch(ArcSeekBar protractorView) {

            }
        });

    }
}
