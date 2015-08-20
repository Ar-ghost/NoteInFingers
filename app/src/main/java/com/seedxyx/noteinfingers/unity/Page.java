package com.seedxyx.noteinfingers.unity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ar_ghost on 2015/8/10.
 */
public class Page implements Parcelable {
    private String noteName;
    private String createTime;
    private String latestTime;
    private int pageNumber;
    private String contentString;

    public Page(){

    }
    public Page(int pageNumber,String noteName,String createTime,String latestTime,String contentString){
        this.createTime=createTime;
        this.noteName=noteName;
        this.latestTime=latestTime;
        this.pageNumber=pageNumber;
        this.contentString=contentString;
    }

    public void setNoteName(String noteName){
        this.noteName=noteName;
    }
    public void setCreateTime(String createTime){
        this.createTime=createTime;
    }
    public void setLatestTime(String latestTime){
        this.latestTime=latestTime;
    }
    public void setPageNumber(int pageNumber){
        this.pageNumber=pageNumber;
    }
    public void setContentString(String contentString){
        this.contentString=contentString;
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
    public int getPageNumber(){
        return pageNumber;
    }
    public String getContentString(){
        return contentString;
    }

    public static final Parcelable.Creator<Page> CREATOR = new Creator<Page>() {
        public Page createFromParcel(Parcel source) {
            Page page = new Page();
            page.noteName = source.readString();
            page.createTime = source.readString();
            page.latestTime = source.readString();
            page.contentString=source.readString();
            page.pageNumber=source.readInt();
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
        parcel.writeString(noteName);
        parcel.writeString(createTime);
        parcel.writeString(latestTime);
        parcel.writeString(contentString);
        parcel.writeInt(pageNumber);
    }

}
