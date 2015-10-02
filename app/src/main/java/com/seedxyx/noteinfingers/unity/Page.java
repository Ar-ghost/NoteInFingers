package com.seedxyx.noteinfingers.unity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.seedxyx.noteinfingers.util.StrConversionUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ar_ghost on 2015/8/10.
 */
public class Page implements Parcelable {
    private String createTime;
    private String latestTime;
    private int pageNumber;
    private int tagNumber;
    private String contentString;
    private Note note;

    public Page(){
        createTime=getTime();
        latestTime=createTime;
        pageNumber=0;
        tagNumber=0;
        contentString="";
        note=null;
    }
    public Page(int pageNumber,Note note){
        this();
        this.pageNumber=pageNumber;
        this.note=note;
    }
    public Page(int pageNumber,int tagNumber,String createTime,String latestTime,String contentString){
        this.createTime=createTime;
        this.tagNumber=tagNumber;
        this.latestTime=latestTime;
        this.pageNumber=pageNumber;
        this.contentString=contentString;
        note=null;
    }
    public Page(int pageNumber,int tagNumber,String createTime,String latestTime,String contentString,Note note)
    {
        this(pageNumber,tagNumber,createTime,latestTime,contentString);
        this.note=note;
    }

    public void clearContentString(){contentString="";}

    public int getTagNumber(){
        return tagNumber;
    }
    public String getNoteName(){
        return note.getNoteName();
    }
    public String getCreateTime(){
        return createTime;
    }
    public String getLatestTime(){
        return latestTime;
    }
    public int getPageNumber(){
        return pageNumber;
    }
    public String getContentString(){
        return contentString;
    }
    public Note getNote(){
        return note;
    }
    //更改contentString,index为待插入的位置,waitStr应为<tag>+内容的形式（不含separater）
    //封装了tagNumber的改变
    public void insertIntoContentStr(int index,String waitStr){
//        Log.i("waitStr",waitStr);
        String content[]=contentString.split(StrConversionUtil.SEPARATER);
        StringBuilder tmp=new StringBuilder(contentString);
        //记录第index个tag前的字符数
        int total=0;
        for(int i=0;i<index-1;i++)
        {
            total+=content[i].length()+StrConversionUtil.SEPARATER.length();
        }
//        Log.i("tmp",tmp.toString());
//        Log.i("total",contentString);
//        Log.i("index",Integer.toString(index));
        tmp.insert(total,waitStr+StrConversionUtil.SEPARATER);
        contentString=tmp.toString();
        tagNumber++;
        updateLatestTime();
    }
    //更新contentString，index为待更新的位置,waitStr应为<tag>+内容的形式（不含separater）
    public void updateContentStr(int index,String waitStr){
//        Log.i("index",Integer.toString(index));
//        Log.i("waitStr",waitStr);
        String content[]=contentString.split(StrConversionUtil.SEPARATER);
        StringBuilder tmp=new StringBuilder(contentString);
        //记录第index个tag前的字符数
        int total=0;
        for(int i=0;i<index-1;i++)
        {
            total+=content[i].length()+StrConversionUtil.SEPARATER.length();
        }
        tmp.replace(total,total+content[index].length(),waitStr);
        contentString=tmp.toString();
        //更新最后更改时间
        updateLatestTime();
    }


    //删除自己，更新note
    public void deleteThis(){
        if(this.pageNumber>note.getPagesNumber()) {
            note.updataLatestTime();
            return;
        }
        note.updataPagesNumber(note.getPagesNumber()-1);
    }
    //更新自己的时间
    public void updateLatestTime(){
        String time=getTime();
        latestTime=time;
        note.updateLatestTime(time);
    }
    //获取当前时间
    protected String getTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return sDateFormat.format(new Date());
    }



    //parcelable必须实现的方法
    public static final Parcelable.Creator<Page> CREATOR = new Creator<Page>() {
        public Page createFromParcel(Parcel source) {
            Page page = new Page();
            page.note=source.readParcelable(Note.class.getClassLoader());
            page.createTime = source.readString();
            page.latestTime = source.readString();
            page.contentString=source.readString();
            page.pageNumber=source.readInt();
            page.tagNumber=source.readInt();
            return page;
        }
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(note,flags);
        parcel.writeString(createTime);
        parcel.writeString(latestTime);
        parcel.writeString(contentString);
        parcel.writeInt(pageNumber);
        parcel.writeInt(tagNumber);
    }

}
