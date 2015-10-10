package com.seedxyx.noteinfingers.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.os.Handler;

import com.seedxyx.noteinfingers.R;
import com.seedxyx.noteinfingers.activity.VideoAty;
import com.seedxyx.noteinfingers.custom.SoundView;
import com.seedxyx.noteinfingers.unity.Page;

import java.io.File;
import java.util.logging.LogRecord;

/**
 * Created by Ar_ghost on 2015/8/11.
 */
public class StrConversionUtil {
    public static final String IMGTAG="<Nimg>";
    public static final String VIDEOTAG="<Nvideo>";
    public static final String SOUNDTAG="<Nsound>";
    public static final String WORDTAG="<Nword>";
    public static final String SEPARATER="<Nsep>";
    //加载背景颜色
    public static final int head[]={R.drawable.head1,R.drawable.head2,R.drawable.head3,R.drawable.head4};
    public static final int content[]={R.drawable.content1,R.drawable.content2,R.drawable.content3,R.drawable.content4};
    public static final int end[]={R.drawable.end1,R.drawable.end2,R.drawable.end3,R.drawable.end4};
    public static android.os.Handler handler;

    public static boolean writePageFromDB(final Activity activity,Page page,LinearLayout layout,int colorNum, android.os.Handler handler){
        //填写页眉页脚
        TextView bookName=(TextView)activity.findViewById(R.id.bookName);
        TextView pageNumber=(TextView)activity.findViewById(R.id.pageNumber);
        bookName.setText(page.getNoteName());
        pageNumber.setText("第"+page.getPageNumber()+"页");
        StrConversionUtil.handler=handler;

        //填写颜色
        bookName.setBackgroundResource(head[colorNum]);
        pageNumber.setBackgroundResource(end[colorNum]);
        layout.setBackgroundResource(content[colorNum]);

        //填写主要内容
        String content[]=page.getContentString().split(SEPARATER);
        Log.i("content",page.getContentString());
        if(content[0].equals("")){
            //如果是空的一页，即为新建的note，应执行插入EditText操作
            Log.i("I am empty page begin ","");
            EditText editText=new EditText(activity);
            editText.setBackground(null);
            insertView(activity,layout,editText,page,0,WORDTAG);
            return true;
        }
        //否则读取信息,读取操作
        try
        {
            for(int i=0;i<content.length;i++){
                Log.i("content+i",i+"  "+content[i]);
                if(content[i].startsWith(WORDTAG)){
                    //添加文字
                    String tmp=content[i].substring(WORDTAG.length());
                    Log.i("I am adding EditText..","");
                    EditText editText=new EditText(activity);
                    editText.setBackground(null);
                    editText.setId(i);
                    editText.setText(tmp);
                    addDeleteListner(editText);
                    layout.addView(editText);
                }else if(content[i].startsWith(IMGTAG)){
                    //添加图片
                    String tmp=content[i].substring(IMGTAG.length());
                    Uri imagePath = Uri.fromFile(new File(tmp));
                    ImageView imageView=new ImageView(activity);
                    imageView.setImageURI(imagePath);
                    imageView.setId(i);
                    addDeleteListner(imageView);
                    setImageAttr(imageView);
                    layout.addView(imageView);
                }else if(content[i].startsWith(VIDEOTAG)){
                    //添加视频
                    final String tmp=content[i].substring(VIDEOTAG.length());
                    ImageView videoView=new ImageView(activity);
                    videoView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(tmp, MediaStore.Images.Thumbnails.MINI_KIND));
                    addDeleteListner( videoView);
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
                    setImageAttr(videoView);
                    layout.addView(videoView);
                }else if(content[i].startsWith(SOUNDTAG)){
                    //添加音频
                    final String tmp=content[i].substring(SOUNDTAG.length());
                    ImageView soundView=new ImageView(activity);
                    //soundView.setImageResource(R.drawable.soundIcon);
                    addDeleteListner(soundView);
                    soundView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            //--------------------
                        }
                    });
                    soundView.setId(i);
                    setImageAttr(soundView);
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
    private static void insertView(Activity activity,LinearLayout layout,View view,Page page,int index,String tagStr){
        Log.i("insertVIew:index+tagStr:",index+tagStr);
        layout.addView(view, index);
        view.setId(index);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(layoutParams);
        addDeleteListner(view);
        if(view instanceof ImageView){
            setImageAttr((ImageView)view);
        }
        page.insertIntoContentStr(index,tagStr);
    }
    //封装的选择位置并完成插入操作的方法，包括page数据的更新
    private static void addView(Activity activity,Page page,ImageView imageView,LinearLayout layout,String tagStr){
        Log.i("addView....tagStr",tagStr);
        int tagNumber=page.getTagNumber();
        //获取当前交点坐标确定插入位置
            View view=activity.getCurrentFocus();
            if(view instanceof EditText&&view.getId()!=tagNumber-1)
            {
                int id=view.getId();
                insertView(activity,layout,imageView,page,id+1,tagStr);
                //判断紧随其后的view是  否为EditText同时更改其id为index
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
                    editText.setBackground(null);
                    insertView(activity,layout,editText,page,id+2,WORDTAG+"");
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
                editText.setBackground(null);
                //如果末尾EditText为空
                if(editText.getText().toString().equals(""))
                {
                    insertView(activity,layout,imageView,page,tagNumber-1,tagStr);
                    editText.setId(tagNumber-1);
                }
                //如果不为空
                else
                {
                    EditText tmpEditText=new EditText(activity);
                    tmpEditText.setBackground(null);
                    insertView(activity,layout,imageView,page,tagNumber,tagStr);
                    insertView(activity,layout,tmpEditText,page,tagNumber+1,WORDTAG+"");
                }
            }
    }

    public static boolean addImageView(Activity activity,Page page,String filePath,LinearLayout layout){
        //加载图片
        Log.i("addImageView.......","");
        //可能会找不到资源
        try {
            ImageView imageView = new ImageView(activity);
            Uri imagePath = Uri.fromFile(new File(filePath));
            imageView.setImageURI(imagePath);
//            layout.setGravity(Gravity.LEFT);
            //调用重载方法
            addView(activity, page, imageView, layout, IMGTAG + filePath);
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
            SoundView soundView=new SoundView(activity,filePath);

            //调用重载方法
            addView(activity, page, soundView, layout, SOUNDTAG + filePath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(activity,"加载资源出错！",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public static void setImageAttr(ImageView imageView){
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(800);
        imageView.setMaxWidth(600);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
    }

    //？？？？？？？？？？？？静态？？？非静态？？？
    //每一个layout中的view都要注册的监听器
    public static void addDeleteListner(final View view){
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View tmpview) {
                Log.i("NO method has been started!","");
                //弹出窗口，提供删除选项

                //使用handler发送消息
                Message msg=new Message();
                msg.what=111;
                msg.obj=view;
                handler.sendMessage(msg);

                Log.i("NO method has end!","");
                return true;
            }
        });
    }

}
