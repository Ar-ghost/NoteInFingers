package com.seedxyx.noteinfingers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seedxyx.noteinfingers.R;
import com.seedxyx.noteinfingers.custom.BookButton;
import com.seedxyx.noteinfingers.dbhelper.NoteDBHelper;
import com.seedxyx.noteinfingers.unity.Note;

import java.util.ArrayList;

public class SelectAty extends Activity {

    NoteDBHelper dbHelper;
    LinearLayout layout;
    Button btnAddNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Log.i("SelectAty has been created!","");
        btnAddNote=(Button)findViewById(R.id.btnAddNote);

        layout=(LinearLayout)findViewById(R.id.layout);
        //创建数据库helper
        dbHelper=new NoteDBHelper(SelectAty.this,"note.db",1);

        //添加笔记的监听器
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出提示输入笔记名称的窗口
                final EditText editText=new EditText(SelectAty.this);
                new AlertDialog.Builder(SelectAty.this).setTitle("请输入笔记名").setView(editText).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String noteName=editText.getText().toString();
                        if(noteName.equals("")){
                            Toast.makeText(SelectAty.this,"请输入笔记名！",Toast.LENGTH_LONG).show();
                            return;
                        }
                        //审查笔记是否以数字开头
                        char[] tmpChar=noteName.toCharArray();
                        if(Character.isDigit(tmpChar[0])){
                            Toast.makeText(SelectAty.this,"请勿以数字开头！",Toast.LENGTH_LONG).show();
                            return;
                        }


                        //审查书名是否重复
                        Cursor cursor=dbHelper.getReadableDatabase().rawQuery("select noteName from noteList",null);
                        while(cursor.moveToNext()){
                            if(cursor.getString(0).equals(noteName))
                            {
                                Toast.makeText(SelectAty.this,"已有此笔记！",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Note newNote=new Note(noteName);
                        //数据库中添加笔记
                        dbHelper.addNewNote(newNote);
                        //界面更新
                        BookButton bookButton=new BookButton(SelectAty.this,newNote);
                        addListener(bookButton);
                        layout.addView(bookButton);
                    }
                }).setNegativeButton("取消", null).create().show();

            }
        });

       loadBooks();

    }
    public void loadBooks(){
        Log.i("loadBooks","");
        //添加已有笔记按钮
        final ArrayList<Note> noteArrayList=dbHelper.readNotes();
        //如果现有笔记不为空
        if(noteArrayList!=null) {
            Log.i("note.number",""+noteArrayList.size());
            for (int i = 0; i < noteArrayList.size(); i++) {
                Log.i("这是第"+i+"本书","\n");
                final BookButton bookButton = new BookButton(SelectAty.this, noteArrayList.get(i));
                addListener(bookButton);
                layout.addView(bookButton);
            }
        }
        Log.i("loadBook over","");
    }


    private void addListener(final BookButton bookButton){
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectAty.this, OnePageAty.class);
                intent.putExtra("note", bookButton.getNote());
                intent.putExtra("colorNum",bookButton.getColorNum());
                startActivity(intent);
            }
        });
        bookButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //弹出选择对话框
                new AlertDialog.Builder(SelectAty.this).setItems(new String[]{"重命名", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //如果是重命名
                        if(i==0){
                            final EditText editText=new EditText(SelectAty.this);
                            //弹出重命名对话框
                            new AlertDialog.Builder(SelectAty.this).setTitle("重命名").setView(editText)
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //更新笔记数据
                                            dbHelper.updateNote(bookButton.getNote());
                                            //更新界面
                                            bookButton.setNoteName(editText.getText().toString());
                                        }
                                    }).setNegativeButton("取消",null).create().show();
                        }
                        //如果是删除笔记
                        else if(i==1){
                            new AlertDialog.Builder(SelectAty.this).setTitle("确定删除？").setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //删除笔记数据
                                    dbHelper.deleteNote(bookButton.getNote().getNoteName());
                                    //更新界面
                                    layout.removeView(bookButton);

                                }
                            }).setNegativeButton("取消",null).create().show();
                        }

                    }
                }).create().show();
                return true;
            }
        });
    }

    @Override
    public void onResume(){
        Log.i("resume","");
        if(layout.getChildCount()>0){
            layout.removeViews(0,layout.getChildCount());
        }
        loadBooks();
        super.onResume();
    }

}
