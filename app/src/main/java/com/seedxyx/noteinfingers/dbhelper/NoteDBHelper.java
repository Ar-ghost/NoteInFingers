package com.seedxyx.noteinfingers.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public void addNewNote(Note note) {
        try {
            SQLiteDatabase db=this.getReadableDatabase();
            //开始事务
            db.beginTransaction();
            db.execSQL("insert into NoteList (noteName,pagesNumber,createTime,latestTime) values" +
                    " ('"+note.getNoteName()+"',0,"+note.getCreateTime()+","+note.getLatestTime()+")");
            db.execSQL("create table "+note.getNoteName()+"(noteName,createTime,latestTime,contentString,tagNumber)");
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void addNewPage(Page page){
        try{
            Note note=page.getNote();
            SQLiteDatabase db=this.getReadableDatabase();
            //开始事务
            db.beginTransaction();
            db.execSQL("insert into " + page.getNoteName() + " (noteName,createTime,latestTime,contentString,tagNumber)" +
                    " values( '" +page.getNoteName()+"',"+page.getCreateTime() + "," + page.getLatestTime() + ",'" + page.getContentString() +"',"+page.getTagNumber()+ ")");
            note.updataPagesNumber(note.getPagesNumber()+1,page.getLatestTime());
            Log.i(Integer.toString(note.getPagesNumber()),"----pagesnumber of note");
            updateNote(note);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void updatePage(Page page){
        try{
            //如果要更新的page内容为空,则删除本页
//            if(page.getContentString()==""){
//                deletePage(page);
//            }
            //否则更新
            Note note=page.getNote();
            Log.i("update now ","");
            if(page.getPageNumber()==1) {
                this.getReadableDatabase().execSQL("update "+page.getNoteName()+" set latestTime = "+page.getLatestTime()+", contentString = '" +
                        page.getContentString()+"', tagNumber = "+page.getTagNumber()+" where noteName in (select noteName from "+page.getNoteName()+" limit 1 offset 0)");
                Log.i("between uodata:",Integer.toString(note.getPagesNumber()));
                updateNote(note);

            }
            else{
                this.getReadableDatabase().execSQL("update "+page.getNoteName()+" set latestTime = "+page.getLatestTime()+", contentString ='" +
                       page.getContentString()+"', tagNumber = "+page.getTagNumber()+" where noteName in (select noteName from "+page.getNoteName()+" limit 1 offset "+Integer.toString(page.getPageNumber()-2)+")");
                updateNote(note);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }

    }
    public void updateNote(Note note){
        try{
            this.getReadableDatabase().execSQL("update NoteList set noteName = '"+note.getNoteName()+"', pagesNumber = "+
                note.getPagesNumber()+", createTime = "+note.getCreateTime()+", latestTime = "+note.getLatestTime()+" where noteName = '"
                +note.getNoteName()+"'");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void deletePage(Page page){
        try{
            Note note=page.getNote();
            int pageNumber=page.getPageNumber();

            SQLiteDatabase db=this.getReadableDatabase();
            //开始事务
            db.beginTransaction();
            if(pageNumber==1){
                db.execSQL("delete from "+note.getNoteName()+" where noteName in (select noteName from "+note.getNoteName()+" limit 1)");
            }else {
                db.execSQL("delete from " + note.getNoteName() + " where noteName in (select noteName from "+note.getNoteName()+" limit 1" +
                        " offset "+Integer.toString(pageNumber-2)+")");
            }
            //更新页码
            note.updataPagesNumber(note.getPagesNumber()-1);
            updateNote(note);
            db.setTransactionSuccessful();
            Log.i("before_delete","");
            db.endTransaction();
            Log.i("after_delete","");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void deleteNote(String noteName){
        try{
            SQLiteDatabase db=this.getReadableDatabase();
            db.beginTransaction();
            db.execSQL("delete from NoteList where noteName= '"+noteName+"'");
            db.execSQL("drop table "+noteName);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public ArrayList<Note> readNotes(){
        Cursor cursor=this.getReadableDatabase().rawQuery("select * from NoteList",null);
        //如果数据为空
        if(cursor==null)
        {
            return null;
        }
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
    //如果左滑到最小值不在这里处理,这里保证pageNumber为合理值
    //考虑到会出现最后一页+1的页码情况要返回空page
    public Page readPage(Note note,int pageNumber){
        //否则返回null
        //这是不应该出现的情况
        if(pageNumber<1||pageNumber>note.getPagesNumber()+1)
        {
            return null;
        }
        //如果是创建新的一页,返回新page
        Log.i("note.pageNumber",Integer.toString(note.getPagesNumber()));
        if(pageNumber==note.getPagesNumber()+1)
        {
            //
            return new Page(pageNumber,note);
        }
        //否则是1-最后一页之间的页码，正常返回查询到的page
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
        Log.i("I am returning an normal page...","");
        return new Page(pageNumber,Integer.parseInt(cursor.getString(cursor.getColumnIndex("tagNumber"))),
                cursor.getString(cursor.getColumnIndex("createTime")),
                cursor.getString(cursor.getColumnIndex("latestTime")),
                cursor.getString(cursor.getColumnIndex("contentString")),note);
    }

    //查询不到最后更新的page则返回空page
    public Page readPage(Note note){
        Cursor cursor=this.getReadableDatabase().rawQuery("select rowid,* from "+note.getNoteName()+" order by latestTime desc limit 1",null);
        if(cursor.getCount()==0){
            //页码为1,属于note
            Log.i("return new Page Page(1,note)","");
            return new Page(1,note);
        }
        cursor.moveToFirst();
        int pageNumber=Integer.parseInt(cursor.getString(0));

        Log.i("will use readPage2.....pageNumber",Integer.toString(pageNumber));
        return readPage(note,pageNumber);
    }


}
