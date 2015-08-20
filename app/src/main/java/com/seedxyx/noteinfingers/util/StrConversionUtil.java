package com.seedxyx.noteinfingers.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.seedxyx.noteinfingers.R;
import com.seedxyx.noteinfingers.dbhelper.NoteDBHelper;
import com.seedxyx.noteinfingers.unity.Note;
import com.seedxyx.noteinfingers.unity.Page;

import java.io.File;

/**
 * Created by Ar_ghost on 2015/8/11.
 */
public class StrConversionUtil {
    public static final String IMGTAG="<Nimg>";
    public static final String VIDEOTAG="<Nvideo>";
    public static final String SOUNDTAG="<Nsound>";
    public static final String WORDTAG="<Nword>";
    public static final String SEPARATER="<Nsep>";

    public static boolean writePageFromDB(Activity activity,Note note,Page page,LinearLayout layout){
        //填写页眉页脚
        TextView bookName=(TextView)activity.findViewById(R.id.bookName);
        TextView pageNumber=(TextView)activity.findViewById(R.id.pageNumber);
        bookName.setText(note.getNoteName());
        pageNumber.setText("第"+page.getPageNumber()+"页");

        //填写主要内容
        String content[]=page.getContentString().split(SEPARATER);
        for(int i=0;i<content.length;i++){
            if(content[i].startsWith(WORDTAG)){
                //添加文字
                String tmp=content[i].substring(WORDTAG.length()-1);

                EditText editText=new EditText(activity);
                editText.setText(tmp);
                layout.addView(editText);
            }else if(content[i].startsWith(IMGTAG)){
                //添加图片
                String tmp=content[i].substring(IMGTAG.length()-1);
                Uri imagePath = Uri.fromFile(new File(tmp));
                ImageView imageView=new ImageView(activity);
                imageView.setImageURI(imagePath);
                layout.addView(imageView);
            }else if(content[i].startsWith(VIDEOTAG)){
                //添加视频
                String tmp=content[i].substring(VIDEOTAG.length()-1);
                VideoView videoView=new VideoView(activity);

                //ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(300,300);
                //videoView.setLayoutParams(layoutParams);
                videoView.setVideoPath(tmp);
                MediaController mController=new MediaController(activity);
                videoView.setMediaController(mController);
                mController.setMediaPlayer(videoView);
                layout.addView(videoView);
            }else if(content[i].startsWith(SOUNDTAG)){
                //添加音频
                String tmp=content[i].substring(SOUNDTAG.length()-1);

                //layout.addView();
            }
        }

        return true;
    }

    public static boolean addImageView(Activity activity,Page page,String filePath,LinearLayout layout){
        //获取当前交点坐标确定插入位置
        try {
            View view=activity.getCurrentFocus();
            if(view instanceof EditText)
            {
                EditText editText=(EditText)view;
                if(editText.getText().equals(""))
                {
                    //删除当前edittext
                    layout.removeView(editText);
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        ImageView imageView=new ImageView(activity);
        Uri imagePath = Uri.fromFile(new File(filePath));
        imageView.setImageURI(imagePath);
        layout.addView(imageView);


        return true;
    }
    public static boolean addVideoView(){
        return true;
    }
    public static boolean addSoundView(){
        return true;
    }


}
