package com.seedxyx.noteinfingers.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.seedxyx.noteinfingers.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OnePageAty extends Activity {

    private final int CAMERA_REQUEST_CODE=111;
    private final int VIDEO_REQUEST_CODE=222;
    private final int SOUND_REQUEST_CODE=333;
    private final int CAMERA_LONG_REQUEST_CODE=1111;
    private final int VIDEO_LONG_REQUEST_CODE=2222;
    private final int SOUND_LONG_REQUEST_CODE=3333;

    private EditText test;
    private Button btnCamera;
    private Button btnSound;
    private Button btnViedo;

    //app对应存储路径
    private String storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_one_page);


        final LinearLayout ly=(LinearLayout)findViewById(R.id.ly);

        Button add=(Button)findViewById(R.id.add);


        VideoView videoView=(VideoView)findViewById(R.id.videoView);
        String sdDir=Environment.getExternalStorageDirectory().toString();
        String fileName = sdDir+"/"+"Movies/QQ视频20150710101843.mp4";
        File file=new File(fileName);
        videoView.setVideoPath(file.getAbsolutePath());
        MediaController mediaController=new MediaController(OnePageAty.this);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView=new ImageView(OnePageAty.this);
                imageView.setImageResource(R.drawable.ic_launcher);
                ly.addView(imageView);


                VideoView videoView=new VideoView(OnePageAty.this);
                ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(300,300);
                videoView.setLayoutParams(layoutParams);
                String sdDir=Environment.getExternalStorageDirectory().toString();
                String fileName = sdDir+"/"+"Movies/QQ视频20150710101843.mp4";
                File file=new File(fileName);
                videoView.setVideoPath(file.getAbsolutePath());
                MediaController mController=new MediaController(OnePageAty.this);
                videoView.setMediaController(mController);
                mController.setMediaPlayer(videoView);
                ly.addView(videoView);
            }
        });



        test=(EditText)findViewById(R.id.test);
        SpannableString ss = new SpannableString("123456789");
        Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        //创建ImageSpan
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        //用ImageSpan替换文本
        ss.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        test.setText(ss);
        test.append("<img src=");



        btnCamera=(Button)findViewById(R.id.btnCamera);
        btnViedo=(Button)findViewById(R.id.btnVideo);
        btnSound=(Button)findViewById(R.id.btnSound);

        //启动相机，长按此按钮为添加现有图片
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        btnCamera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent,CAMERA_LONG_REQUEST_CODE);
                return true;
            }
        });
        //启动录音机，长按此按钮为添加现有音频
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent,SOUND_REQUEST_CODE);
            }
        });
        btnSound.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        //启动摄像机，长按此按钮为添加现有视频
        btnViedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent,VIDEO_REQUEST_CODE);
            }
        });
        btnViedo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK&&requestCode==CAMERA_REQUEST_CODE) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.v("TestFile",
                        "SD card is not avaiable/writeable right now.");
                //------------需要添加SD卡存储无法使用时的处理-------------
                Toast.makeText(OnePageAty.this,"请插入SD卡使用！",Toast.LENGTH_LONG).show();
                return;
            }

            //获取SD卡本app设立的目录
            String sdDir=Environment.getExternalStorageDirectory().toString();
            File file = new File(sdDir+"/"+"FreeNote/");
            file.mkdirs();// 创建文件夹
            String fileName = sdDir+"/"+"FreeNote/test.jpg";


            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            FileOutputStream b = null;
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里
        }
        if(resultCode == Activity.RESULT_OK&&requestCode==VIDEO_REQUEST_CODE){

        }
        if(resultCode == Activity.RESULT_OK&&requestCode==SOUND_REQUEST_CODE){

        }

        if(resultCode == Activity.RESULT_OK&&requestCode==CAMERA_LONG_REQUEST_CODE){
            Toast.makeText(OnePageAty.this,"选择图片",Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_one_page_aty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
