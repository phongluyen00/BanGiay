package com.example.retrofitrxjava.activity;

import android.content.Intent;
import android.view.View;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatAct<ActivitySplashBinding>{
    @Override
    protected void initLayout() {
        bd.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_splash;
    }
}
