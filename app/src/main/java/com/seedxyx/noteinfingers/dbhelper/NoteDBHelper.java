package com.seedxyx.noteinfingers.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ar_ghost on 2015/8/10.
 */
public class NoteDBHelper extends SQLiteOpenHelper {

    public NoteDBHelper(Context context,String name,int Version){
        super(context,name,null,Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }


}
