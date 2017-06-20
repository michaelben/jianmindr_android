package com.jianmindr.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.jianmindr.R;

public class StartActivity extends Activity {
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏

        final View startView = View.inflate(this, R.layout.splash, null);
        setContentView(startView);

        context = this;
        //渐变
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        startView.setAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                 

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                 

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                 
                redirectto();
            }
        }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void redirectto() {
        updateScWidth();

        Intent intent = new Intent(this,OverlayActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateScWidth() {
        /*
        int i = getWindowManager().getDefaultDisplay().getWidth();
        int j = getWindowManager().getDefaultDisplay().getHeight();
        int width = Math.min(i, j);
        int height = Math.max(i, j);
        if (width > 0)
        {
            AppUtils.setScWidth(this, width);
            AppUtils.setScHeight(this, height);
        }
        */
    }
}
