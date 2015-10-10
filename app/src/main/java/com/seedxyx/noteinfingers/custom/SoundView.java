package com.seedxyx.noteinfingers.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seedxyx.noteinfingers.R;

/**
 * Created by Ar_ghost on 2015/10/6.
 */
public class SoundView extends ImageView{

    boolean isPlay=false;
    String filePath;
    MediaPlayer mediaPlayer;

    public SoundView(Context context,String filePath){
        super(context);
        this.filePath=filePath;
        mediaPlayer=new MediaPlayer();
        setImageResource(R.drawable.sound_play);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
        }
        catch (Exception e)
        {
            Log.i("SoundView","Exceptiton");
            Toast.makeText(context,"出错！",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeState();
            }
        });
    }

    //改变当前状态
    public void changeState(){
        if(!isPlay){
            isPlay=true;
        }
        else
        {
            isPlay=false;
        }

        //根据当前状态设置图片并改变状态
        if(isPlay){
            mediaPlayer.start();
            setImageResource(R.drawable.sound_pause);
        }
        else
        {
            mediaPlayer.pause();
            setImageResource(R.drawable.sound_play);
        }
        //重绘
        invalidate();

    }
}
