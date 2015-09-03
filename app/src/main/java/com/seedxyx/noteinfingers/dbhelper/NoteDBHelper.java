package com.seedxyx.noteinfingers.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seedxyx.noteinfingers.unity.Note;
import com.seedxyx.noteinfingers.unity.Page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ar_ghost on 2015/8/10.
 */
public class NoteDBHelper extends SQLiteOpenHelper {


    public NoteDBHelper(Context context,String name,int Version){
        super(context,name,null,Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table NoteList(noteName,pagesNumber,createTime,latestTime)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

    //封装的数据库操作

    public boolean addNewNote(String noteName) {
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowTime = sDateFormat.format(new Date());
            SQLiteDatabase db=this.getReadableDatabase();
            //开始事务
            db.beginTransaction();
            db.execSQL("insert into NoteList(noteName,pagesNumber,createTime,latestTime) values" +
                    " ("+noteName+",0,"+nowTime+","+nowTime+")");
            db.execSQL("create table "+noteName+"(noteName,createTime,latestTime,contentString)");
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean addNewPage(Note note,Page page){
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            db.beginTransaction();
            db.execSQL("insert into " + page.getNoteName() + " (noteName,createTime,latestTime,contentString)" +
                    " values(" + page.getNoteName() + "," + page.getCreateTime() + "," + page.getLatestTime() + "," + page.getContentString() + ")");
            note.setPagesNumber(note.getPagesNumber() + 1);
            note.setLatestTime(page.getLatestTime());
            updateNote(note);
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updatePage(Note note,Page page){
        try{
            note.setLatestTime(page.getLatestTime());
            if(page.getPageNumber()==1) {
                this.getReadableDatabase().execSQL("update "+page.getNoteName()+" set latestTime"+page.getLatestTime()+" contentString=" +
                        page.getContentString()+" where noteName=(select noteName from "+page.getNoteName()+" limit 1 offset 0)");
                updateNote(note);
                return true;
            }
            else{
                this.getReadableDatabase().execSQL("update "+page.getNoteName()+" set latestTime"+page.getLatestTime()+" contentString=" +
                       page.getContentString()+" where noteName=(select noteName from "+page.getNoteName()+" limit 1 offset "+Integer.toString(page.getPageNumber()-2)+")");
                updateNote(note);
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }
    public boolean updateNote(Note note){
        try{
            this.getReadableDatabase().execSQL("update NoteList set noteName="+note.getNoteName()+" pagesNumber="+
                note.getPagesNumber()+" createTime="+note.getCreateTime()+" latestTime="+note.getLatestTime()+" where noteName="
                +note.getNoteName());
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    //-----
    public boolean insertPage(Note note,Page page){
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            db.beginTransaction();
            db.execSQL("insert into " + page.getNoteName() + " (noteName,createTime,latestTime,contentString)" +
                    " values(" + page.getNoteName() + "," + page.getCreateTime() + "," + page.getLatestTime() + "," + page.getContentString() + ")");
            note.setPagesNumber(note.getPagesNumber() + 1);
            note.setLatestTime(page.getLatestTime());
            updateNote(note);
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deletePage(Note note,int pageNumber){
        try{
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            String nowTime = sDateFormat.format(new Date());
            SQLiteDatabase db=this.getReadableDatabase();
            db.beginTransaction();
            if(pageNumber==1){
                db.execSQL("delete from "+note.getNoteName()+"where noteName=(select noteName from "+note.getNoteName()+"limit 1)");
            }else {
                db.execSQL("delete from " + note.getNoteName() + " where noteName=(select noteName from "+note.getNoteName()+"limit 1" +
                        "offset "+Integer.toString(pageNumber-2)+")");
            }
            note.setLatestTime(nowTime);
            note.setPagesNumber(note.getPagesNumber()-1);
            updateNote(note);
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteNote(String noteName){
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            db.beginTransaction();
            db.execSQL("delete from NoteList where noteName="+noteName);
            db.execSQL("drop table "+noteName);
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public ArrayList<Note> readNotes(){
        Cursor cursor=this.getReadableDatabase().rawQuery("select * from NoteList",null);
        if(cursor.getCount()==0)
            return null;
        ArrayList<Note> arrayList=new ArrayList<Note>();
        while(cursor.moveToNext()){
            Note note=new Note(cursor.getString(cursor.getColumnIndex("noteName")),
                    cursor.getString(cursor.getColumnIndex("createTime")),
                    cursor.getString(cursor.getColumnIndex("latestTime")),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex("pagesNumber"))));
            arrayList.add(note);
        }
        return arrayList;
    }
    public Page readPage(Note note,int pageNumber){
        String noteName=note.getNoteName();
        Cursor cursor;
        if(pageNumber==1)
        {
            cursor=this.getReadableDatabase().rawQuery("select * from "+noteName+" limit 1",null);
        }else{
            cursor=this.getReadableDatabase().rawQuery("select * from "+noteName+" limit 1 offset"+
                    Integer.toString(pageNumber-2),null);
        }
        cursor.moveToFirst();
        return new Page(pageNumber,Integer.parseInt(cursor.getString(cursor.getColumnIndex("tagNumber"))),
                cursor.getString(cursor.getColumnIndex("createTime")),
                cursor.getString(cursor.getColumnIndex("latestTime")),
                cursor.getString(cursor.getColumnIndex("contentString")),note);
    }
    public Page readPage(Note note){
        Cursor cursor=this.getReadableDatabase().rawQuery("select pageNumber from "+note.getNoteName()+" order by latestTime desc limit 1",null);
        cursor.moveToFirst();
        int pageNumber=Integer.parseInt(cursor.getString(0));
        return readPage(note,pageNumber);
    }


}
