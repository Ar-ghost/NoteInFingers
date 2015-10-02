package com.seedxyx.noteinfingers.custom;

import android.content.Context;
import android.util.AttributeSet;
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

    public BookButton(Context context,Note note){
        super(context);
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
        //需要设置上限
        setMeasuredDimension(100+note.getPagesNumber()*10,500+note.getPagesNumber()*10);
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
