package com.seedxyx.noteinfingers.activity;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.seedxyx.noteinfingers.R;
import com.seedxyx.noteinfingers.dbhelper.NoteDBHelper;
import com.seedxyx.noteinfingers.unity.Note;
import com.seedxyx.noteinfingers.unity.Page;
import com.seedxyx.noteinfingers.util.GetPathFromUri4kitkat;
import com.seedxyx.noteinfingers.util.StrConversionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OnePageAty extends Activity implements GestureDetector.OnGestureListener {
    //手势检测器
    GestureDetector detector;

    //上滑调出菜单的长度
    private final int FLIP_DISTANCE_MENU=100;

    private final int CAMERA_REQUEST_CODE=111;
    private final int VIDEO_REQUEST_CODE=222;
    private final int SOUND_REQUEST_CODE=333;
    private final int CAMERA_LONG_REQUEST_CODE=1111;
    private final int VIDEO_LONG_REQUEST_CODE=2222;
    private final int SOUND_LONG_REQUEST_CODE=3333;

    private Button btnCamera;
    private Button btnSound;
    private Button btnViedo;
    private Button btnDelete;
    //隐藏菜单的layout
    private LinearLayout hiddenLayout2;
    private RelativeLayout hiddenLayout;
    private LinearLayout layout;

   //app对应存储文件夹
    private String storage;
    //记录存储文件，便于OnResult后处理
    private String tmpFileName;
    NoteDBHelper dbHelper;
    //需要用到的全局数据
    Note note;
    Page page;
    int colorNum;

    //
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_one_page);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                Log.i("handle get!","");
                if(msg.what==111){
                    Log.i("will start alertDelete!","");
                    View view=(View)msg.obj;
                    alertDelete(view);
                }
            }
        };

        //加载界面资源
        btnDelete=(Button)findViewById(R.id.btnDelete);
        btnCamera=(Button)findViewById(R.id.btnCamera);
        btnViedo=(Button)findViewById(R.id.btnVideo);
        btnSound=(Button)findViewById(R.id.btnSound);
        layout=(LinearLayout)findViewById(R.id.layout);
        hiddenLayout2=(LinearLayout)findViewById(R.id.hiddenLayout2);
        hiddenLayout=(RelativeLayout)findViewById(R.id.hiddenLayout);
        //创建手势检测器
        detector=new GestureDetector(this,this);
        //加载数据
        dbHelper=new NoteDBHelper(OnePageAty.this,"note.db",1);

        //加载note
        note=getIntent().getParcelableExtra("note");
        //加载对应的page
        if(getIntent().getIntExtra("pageNumber",0)!=0)
        {
            Log.i("query--with pageNumber info:",Integer.toString(getIntent().getIntExtra("pageNumber",0)));
            page=dbHelper.readPage(note,getIntent().getIntExtra("pageNumber",0));
        }else{
            Log.i("query---no pageNubmer info","");
            Log.i("will query:note.pageNumber ",Integer.toString(note.getPagesNumber()));
            page=dbHelper.readPage(note);
        }
        colorNum=getIntent().getIntExtra("colorNum",1);
        //加载整个界面
        StrConversionUtil.writePageFromDB(OnePageAty.this,page,layout,colorNum,handler);


        //未找到sd卡则退出界面
        if(null==(storage=getSdCar()))
            OnePageAty.this.finish();
        storage+="/NoteInFingers/"+note.getNoteName()+"/";

        final File storageFile=new File(storage);
        //如果文件夹不存在则创建文件夹路径
        if(!storageFile.exists())
        {
            storageFile.mkdirs();
        }




        //删除当前页的监听器
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page.clearContentString();
                layout.removeAllViews();
                Log.i("page has been cleared","");
                dbHelper.deletePage(page);

                //删除之后
                Intent intent=new Intent(OnePageAty.this,OnePageAty.class);
                intent.putExtra("note",note);
                intent.putExtra("pageNumber",page.getPageNumber());
                startActivity(intent);
                OnePageAty.this.finish();
                //设置页面切换效果
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });

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
                //tmpFileName=storage+getTime()+".mp3";
                //intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(tmpFileName)));
                startActivityForResult(intent,SOUND_REQUEST_CODE);
            }
        });
        btnSound.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent,SOUND_LONG_REQUEST_CODE);
                return true;
            }
        });
        //启动摄像机，长按此按钮为添加现有视频
        btnViedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                tmpFileName=storage+getTime()+".mp4";
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(tmpFileName)));
                startActivityForResult(intent, VIDEO_REQUEST_CODE);
            }
        });
        btnViedo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,VIDEO_LONG_REQUEST_CODE);
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //如果为拍摄照片，如果对返回的压缩图片不满意可更改为原图
        if (resultCode == Activity.RESULT_OK&&requestCode==CAMERA_REQUEST_CODE) {
            //取得返回的数据包
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            FileOutputStream b = null;
            try {
                b = new FileOutputStream(storage+getTime()+".jpg");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //调用方法添加View及更改数据
            StrConversionUtil.addImageView(OnePageAty.this,page,storage+getTime()+".jpg",layout);
        }
        //如果为拍摄录像
        if(resultCode == Activity.RESULT_OK&&requestCode==VIDEO_REQUEST_CODE){
            //成功拍摄后调用方法添加数据
            if(tmpFileName!=null) {
                StrConversionUtil.addVideoView(OnePageAty.this, page, tmpFileName, layout);
            }
            tmpFileName=null;
        }
        if(resultCode == Activity.RESULT_OK&&requestCode==SOUND_REQUEST_CODE){
            //成功录音后调用方法添加数据
            String audioPath=GetPathFromUri4kitkat.getPath(OnePageAty.this,data.getData());
            String dest=storage+getTime()+".mp3";
            copyTo(audioPath,dest);
            StrConversionUtil.addSoundView(OnePageAty.this,page,dest,layout);
        }
        //如果为选择现有图片
        if(resultCode == Activity.RESULT_OK&&requestCode==CAMERA_LONG_REQUEST_CODE){
            addExist(data,".jpg","请选择图片文件！");
        }
        //选择现有视频
        if(resultCode == Activity.RESULT_OK&&requestCode==VIDEO_LONG_REQUEST_CODE){
            addExist(data,".mp4","请选择视频文件！");
        }
        //选择现有音频
        if(resultCode == Activity.RESULT_OK&&requestCode==SOUND_LONG_REQUEST_CODE){
            if(data.getData().toString().startsWith("file://"))
            {
                String audioPath=GetPathFromUri4kitkat.getPath(OnePageAty.this,data.getData());
                String dest=storage+getTime()+".mp3";
                copyTo(audioPath,dest);
                StrConversionUtil.addImageView(OnePageAty.this,page,dest,layout);
                return;
            }
            addExist(data,".mp3","请选择音频文件！");
        }
    }

    protected void addExist(Intent data,String suffix,String toastStr){
        Uri selected = data.getData();
        String[] filePathColumn = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getContentResolver().query(selected,null, null, null, null);
        if(cursor==null)
        {
            Toast.makeText(OnePageAty.this,toastStr,Toast.LENGTH_LONG).show();
            return;
        }
        cursor.moveToFirst();
        String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
        Log.i("path",picturePath);
        cursor.close();
        if(!picturePath.endsWith(suffix))
        {
            Toast.makeText(OnePageAty.this,toastStr,Toast.LENGTH_LONG).show();
            return;
        }
        String dest=storage+getTime()+suffix;
        copyTo(picturePath,dest);
        if(suffix.equals(".mp4")){
            StrConversionUtil.addVideoView(OnePageAty.this,page,dest,layout);
            return;
        }
        if(suffix.equals(".mp3")){
            StrConversionUtil.addSoundView(OnePageAty.this,page,dest,layout);
            return;
        }
        StrConversionUtil.addImageView(OnePageAty.this,page,dest,layout);
    }

    //每次activity暂停则自动更新一次数据库
    @Override
    protected void onPause(){
        Log.i("I am pausing ","\n");
        updateDB();
        if(dbHelper!=null){
            dbHelper.close();
        }
        super.onDestroy();
    }

    //将实时数据写入数据库
    protected void updateDB(){
        //首先更新page，保证最新数据
        updatePage();
        Log.i("contentString",page.getContentString());
        //更新完毕之后
        if(page.getContentString().replaceAll(StrConversionUtil.WORDTAG,"").replaceAll(StrConversionUtil.SEPARATER,"").equals("")){
            if(note.getPagesNumber()!=0){
                //如果这一页本来就不存在
                if(page.getPageNumber()>note.getPagesNumber()){
                    return;
                }
                //如果内容为空,删除page
                dbHelper.deletePage(page);
                return;
            }
            return;
        } //如果当前页是新的一页，执行插入新页操作
        else if(page.getPageNumber()>note.getPagesNumber()){
            Log.i("will insert :page",page.getContentString());
             dbHelper.addNewPage(page);
             return;
        }
        Log.i("will update soon","");
        //否则更新
        dbHelper.updatePage(page);
    }

    //执行一次数据更新到当前page
    public void updatePage(){
        //遍历layout中所有view，如果为EditText则更新内容
        View view;
        for(int i=0;i<page.getTagNumber();i++){
            view=layout.getChildAt(i);
            if(view instanceof EditText)
            {
                page.updateContentStr(i,StrConversionUtil.WORDTAG+((EditText) view).getText().toString());
            }
        }
    }

    //将图片写入SD卡

    //获取SD卡绝对路径
    protected String getSdCar(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.v("TestFile","SD card is not avaiable/writeable right now.");
            //------------需要添加SD卡存储无法使用时的处理-------------
            Toast.makeText(OnePageAty.this,"请插入SD卡使用！",Toast.LENGTH_LONG).show();
            return null;
        }
        //获取SD卡目录
        return Environment.getExternalStorageDirectory().toString();
    }

    //复制文件的方法
    protected boolean copyTo(String src,String dest){
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(dest);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    //获取当前时间
    protected String getTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return sDateFormat.format(new Date());
    }


    public void alertDelete(final View view){
        Log.i("alertDelete","");
        new AlertDialog.Builder(OnePageAty.this).setTitle("确认要删除吗？").setPositiveButton("确定",new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //删除此项View
            int index=view.getId();
            layout.removeView(view);
            page.deleteContentStr(index);
            //最后更改Id
            for(int n=index;n<page.getTagNumber();n++){
                layout.getChildAt(index).setId(index);
            }
        }
        }).setNegativeButton("取消",null).create().show();
    }



    //--------处理界面的触碰事件--------

    //触碰事件的优先级处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        if(detector.onTouchEvent(me))
            return true;
        return super.dispatchTouchEvent(me);
    }
    //--------重写手势检测器的方法----------
    @Override
    public boolean onDown(android.view.MotionEvent motionEvent){
        Log.i("onDown","");
        return false;
    }

    @Override
    public void onShowPress(android.view.MotionEvent motionEvent){

    }

    @Override
    public boolean onSingleTapUp(android.view.MotionEvent motionEvent){
        return false;
    }

    @Override
    public boolean onScroll(android.view.MotionEvent motionEvent, android.view.MotionEvent motionEvent1, float v, float v1){
        return false;
    }

    @Override
    public void onLongPress(android.view.MotionEvent motionEvent){

    }

    public boolean onFling(android.view.MotionEvent motionEvent1, android.view.MotionEvent motionEvent2, float vx, float vy){
        //向上划
        if(motionEvent1.getY()-motionEvent2.getY()>FLIP_DISTANCE_MENU&&vy<-500&&(hiddenLayout.getTranslationY()==0||hiddenLayout.getTranslationY()==-hiddenLayout.getHeight()))
        {
            if(hiddenLayout.getTranslationY()==0) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(hiddenLayout, "translationY", -hiddenLayout.getHeight());
                animator.setDuration(100);
                ObjectAnimator animator2=ObjectAnimator.ofFloat(hiddenLayout2,"translationY",-hiddenLayout.getHeight());
                animator2.setDuration(100);
                animator.start();
                animator2.start();
                return true;
            }
            if(hiddenLayout.getTranslationY()==-hiddenLayout.getHeight()){
                ObjectAnimator animator = ObjectAnimator.ofFloat(hiddenLayout, "translationY", -(hiddenLayout2.getHeight()+hiddenLayout.getHeight()));
                animator.setDuration(100);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(hiddenLayout2, "translationY", -(hiddenLayout2.getHeight()+hiddenLayout.getHeight()));
                animator.setDuration(100);
                animator.start();
                animator2.start();
                return true;
            }
        }
        else if(motionEvent2.getY()-motionEvent1.getY()>FLIP_DISTANCE_MENU&&vy>500&&(hiddenLayout.getTranslationY()==-hiddenLayout.getHeight()||hiddenLayout.getTranslationY()==-(hiddenLayout.getHeight()+hiddenLayout2.getHeight())))
        {
            ObjectAnimator animator=ObjectAnimator.ofFloat(hiddenLayout,"translationY",0);
            animator.setDuration(100);
            ObjectAnimator animator2=ObjectAnimator.ofFloat(hiddenLayout2,"translationY",0);
            animator.setDuration(100);
            animator.start();
            animator2.start();
            return true;
        }
        //向左滑,页码加1
        else if(motionEvent1.getX()-motionEvent2.getX()>FLIP_DISTANCE_MENU&&vx<-500)
        {
            //首先执行一次对page的更新
            updatePage();
            String tmp=page.getContentString().replaceAll(StrConversionUtil.WORDTAG,"").replaceAll(StrConversionUtil.SEPARATER,"");
            //再判断
            //如果内容为空且页码为note最后一页加一或最后一页，则不允许再向后翻
            if(tmp.equals("")&&((page.getPageNumber()==note.getPagesNumber()+1)||(page.getPageNumber()==note.getPagesNumber()))){
                return true;
            }
            //如果内容不为空且页码为最后一页加一，则添加笔记数据
            else if(!tmp.equals("")&&page.getPageNumber()==note.getPagesNumber()+1){
                dbHelper.addNewPage(page);
                //向后翻页
                pageTurning("l",note.getPagesNumber()+1);
            }
            //如果内容不为空且页码为最后一页，则更新笔记数据
            else if(!tmp.equals("")&&page.getPageNumber()==note.getPagesNumber()){
                dbHelper.updatePage(page);
                //向后翻页
                pageTurning("l",note.getPagesNumber()+1);
            }
            //如果内容为空，翻页时删除此页
            else if(tmp.equals("")){
                dbHelper.deletePage(page);
                //向后翻页
                pageTurning("l",page.getPageNumber());
            }
            //如果内容不为空，正常翻页
            else
            {
                //向后翻页
                pageTurning("l",page.getPageNumber()+1);
            }
            return true;
        }
        //向右滑，页码减1
        else if(motionEvent2.getX()-motionEvent1.getX()>FLIP_DISTANCE_MENU&&vx>500)
        {
            //如果已经为第一页，不做响应
            if(page.getPageNumber()==1)
            {
                Toast.makeText(OnePageAty.this,"已经是第一页！",Toast.LENGTH_LONG).show();
                return true;
            }
            //向前翻页
            pageTurning("f",page.getPageNumber()-1);
            return true;
        }
        return false;
    }

    //翻页
    public void pageTurning(String direction,int pageNumber){

        Intent intent = new Intent(OnePageAty.this, OnePageAty.class);
        intent.putExtra("note", note);
        intent.putExtra("pageNumber", pageNumber);
        intent.putExtra("colorNum",colorNum);
        startActivity(intent);
        //结束当前activity
        OnePageAty.this.finish();
        //设置切换动画
        if (direction.equals("f")) {
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        } else if (direction.equals("l")) {
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }
}



