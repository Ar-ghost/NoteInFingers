package com.seedxyx.noteinfingers.custom;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seedxyx.noteinfingers.R;
import com.seedxyx.noteinfingers.unity.Note;

import java.util.Random;
import java.util.zip.Inflater;

/**
 * Created by Ar_ghost on 2015/9/3.
 */
public class BookButton extends RelativeLayout {
    Note note;
    TextView noteName;
    ImageView imageView;
    int colorNum;
    float density;
    int height;
    int width;

    public BookButton(Context context,Note note){
        super(context);
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;  // 屏幕宽度（像素）
        height = metric.heightPixels;  // 屏幕高度（像素）
        density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        this.note=note;
        //生成随机数
        Random  random=new Random();
        colorNum=random.nextInt(4);
        int[] images={R.drawable.book1,R.drawable.book2,R.drawable.book3,R.drawable.book4};

        LayoutInflater.from(context).inflate(R.layout.book_button,this);
        noteName=(TextView)findViewById(R.id.noteName);
        imageView=(ImageView)findViewById(R.id.imageView);
        noteName.setText(note.getNoteName());
        //生成的时候随机确定背景图片
        imageView.setImageResource(images[colorNum]);
    }

//    view的大小由note的大小决定
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        //没有效果？
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int w=(int)((100+note.getPagesNumber()*10)*density);
        int h=(int)((500+note.getPagesNumber()*10)*density);


        //需要设置上限
        setMeasuredDimension(w>width/2?width/2:w,h>height/10*7?height/10*7:h);
    }


    public void setNoteName(String noteName){
        note.setNoteName(noteName);
        //更改textView,完成界面的更新
        this.noteName.setText(noteName);
    }

    public Note getNote(){
        return note;
    }
    public int getColorNum(){
        return colorNum;
    }

}
