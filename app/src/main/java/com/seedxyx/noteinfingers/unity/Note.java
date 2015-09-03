package com.seedxyx.noteinfingers.unity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ar_ghost on 2015/8/10.
 */
public class Note implements Parcelable{
    private String noteName;
    private String createTime;
    private String latestTime;
    private int pagesNumber;


    public Note(){
        noteName="";
        createTime="";
        latestTime="";
        pagesNumber=0;
    }
    public Note(String noteName,String createTime,String latestTime,int pagesNumber){
        this.noteName=noteName;
        this.createTime=createTime;
        this.latestTime=latestTime;
        this.pagesNumber=pagesNumber;
    }
    public void setNoteName(String noteName){
        this.noteName=noteName;
    }
    public void setLatestTime(String latestTime){
        this.latestTime=latestTime;
    }
    public void setPagesNumber(int pagesNumber){
        this.pagesNumber=pagesNumber;
    }
    public String getNoteName(){
        return noteName;
    }
    public String getCreateTime(){
        return createTime;
    }
    public String getLatestTime(){
        return latestTime;
    }
    public int getPagesNumber(){
        return pagesNumber;
    }


    //更新最后更改时间，时间由page传递过来
    public void updateLatestTime(String time){
        latestTime=time;
    }

    //parcelable接口必须实现的方法
    public static final Parcelable.Creator<Note> CREATOR = new Creator<Note>() {
        public Note createFromParcel(Parcel source) {
            Note note = new Note();
            note.noteName = source.readString();
            note.createTime = source.readString();
            note.latestTime = source.readString();
            note.pagesNumber=source.readInt();
            return note;
        }
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(noteName);
        parcel.writeString(createTime);
        parcel.writeString(latestTime);
        parcel.writeInt(pagesNumber);
    }

}
