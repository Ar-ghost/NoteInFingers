package com.seedxyx.noteinfingers.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.seedxyx.noteinfingers.R;
import com.seedxyx.noteinfingers.activity.VideoAty;
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

    public static boolean writePageFromDB(final Activity activity,Page page,LinearLayout layout){
        //填写页眉页脚
        TextView bookName=(TextView)activity.findViewById(R.id.bookName);
        TextView pageNumber=(TextView)activity.findViewById(R.id.pageNumber);
        bookName.setText(page.getNoteName());
        pageNumber.setText("第"+page.getPageNumber()+"页");

        //填写主要内容
        String content[]=page.getContentString().split(SEPARATER);
        try
        {
            for(int i=0;i<content.length;i++){
                if(content[i].startsWith(WORDTAG)){
                    //添加文字
                    String tmp=content[i].substring(WORDTAG.length()-1);

                    EditText editText=new EditText(activity);
                    editText.setId(i);
                    editText.setText(tmp);
                    layout.addView(editText);
                }else if(content[i].startsWith(IMGTAG)){
                    //添加图片
                    String tmp=content[i].substring(IMGTAG.length()-1);
                    Uri imagePath = Uri.fromFile(new File(tmp));
                    ImageView imageView=new ImageView(activity);
                    imageView.setImageURI(imagePath);
                    imageView.setId(i);
                    layout.addView(imageView);
                }else if(content[i].startsWith(VIDEOTAG)){
                    //添加视频
                    final String tmp=content[i].substring(VIDEOTAG.length()-1);
                    ImageView videoView=new ImageView(activity);
                    videoView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(tmp,MediaStore.Images.Thumbnails.MINI_KIND));
                    videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(activity, VideoAty.class);
                            intent.putExtra("videoPath", tmp);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            activity.startActivity(intent);
                        }
                    });
                    videoView.setId(i);
                    layout.addView(videoView);
                }else if(content[i].startsWith(SOUNDTAG)){
                    //添加音频
                    final String tmp=content[i].substring(SOUNDTAG.length()-1);
                    ImageView soundView=new ImageView(activity);
                    //soundView.setImageResource(R.drawable.soundIcon);
                    soundView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            //--------------------
                        }
                    });
                    soundView.setId(i);
                    layout.addView(soundView);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(activity,"加载资源出错！",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //封装的用于完成指定位置插入的方法，包括page数据的更新
    private static void insertView(LinearLayout layout,View view,Page page,int index,String tagStr){
        layout.addView(view,index);
        view.setId(index);
        page.insertIntoContentStr(index,tagStr);
    }
    //封装的选择位置并完成插入操作的方法，包括page数据的更新
    private static void addView(Activity activity,Page page,ImageView imageView,LinearLayout layout,String tagStr){
        int tagNumber=page.getTagNumber();
        //获取当前交点坐标确定插入位置
            View view=activity.getCurrentFocus();
            if(view instanceof EditText&&view.getId()!=tagNumber-1)
            {
                int id=view.getId();
                insertView(layout,imageView,page,id+1,tagStr);
                //判断紧随其后的view是否为EditText同时更改其id为index
                if(layout.getChildAt(id+2) instanceof EditText)
                {
                    for(int i=id+2;i<=tagNumber;i++)
                    {
                        layout.getChildAt(i).setId(i);
                    }
                }
                else
                {
                    EditText editText=new EditText(activity);
                    insertView(layout,editText,page,id+2,WORDTAG+"");
                    for(int i=id+3;i<=tagNumber+1;i++)
                    {
                        layout.getChildAt(i).setId(i);
                    }
                }
            }
            //无焦点或者有焦点不为edittext或者有焦点为edittext在末尾
            else if(view==null||!(view instanceof EditText)||view.getId()==tagNumber-1)
            {
                EditText editText=(EditText)layout.getChildAt(tagNumber-1);
                //如果末尾EditText为空
                if(editText.getText().toString()=="")
                {
                    insertView(layout,imageView,page,tagNumber-1,tagStr);
                    editText.setId(tagNumber);
                }
                //如果不为空
                else
                {
                    EditText tmpEditText=new EditText(activity);
                    insertView(layout,imageView,page,tagNumber,tagStr);
                    insertView(layout,tmpEditText,page,tagNumber+1,WORDTAG+"");
                }
            }
    }

    public static boolean addImageView(Activity activity,Page page,String filePath,LinearLayout layout){
        //加载图片

        //可能会找不到资源
        try {
            ImageView imageView = new ImageView(activity);
            Uri imagePath = Uri.fromFile(new File(filePath));
            imageView.setImageURI(imagePath);

            //调用重载方法
            addView(activity,page,imageView,layout,IMGTAG+filePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(activity,"加载资源出错！",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    public static boolean addVideoView(final Activity activity,Page page, final String filePath,LinearLayout layout){

        try {
            ImageView videoView=new ImageView(activity);
            videoView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(filePath,MediaStore.Images.Thumbnails.MINI_KIND));
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, VideoAty.class);
                    intent.putExtra("videoPath", filePath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
            //调用重载方法
            addView(activity,page,videoView,layout,VIDEOTAG+filePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(activity,"加载资源出错！",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    //
    public static boolean addSoundView(Activity activity,Page page,String filePath,LinearLayout layout){

        try {
            ImageView soundView = new ImageView(activity);



            //调用重载方法
            addView(activity,page,soundView,layout,SOUNDTAG+filePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(activity,"加载资源出错！",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //每一个view都要注册的监听器
    private class NOnLongClickListsner implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){


            return true;
        }

    }

}
