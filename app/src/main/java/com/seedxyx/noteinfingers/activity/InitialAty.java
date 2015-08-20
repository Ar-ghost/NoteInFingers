package com.seedxyx.noteinfingers.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

import com.seedxyx.noteinfingers.R;

public class InitialAty extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_initial);

        new CountDownTimer(1500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFinish() {
                //倒计时结束后在这里实现activity跳转
                Intent intent = new Intent();
                intent.setClass(InitialAty.this,OnePageAty.class);
                startActivity(intent);
                finish();    //跳转后销毁自身的activity  否则按返回 还会跳回到图片
            }
        }.start();


    }


}
