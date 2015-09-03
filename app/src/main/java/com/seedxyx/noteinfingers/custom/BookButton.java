package com.seedxyx.noteinfingers.custom;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Ar_ghost on 2015/9/3.
 */
public class BookButton extends Button {
    public BookButton(Context context){
        super(context);

        LayoutInflater.from(context).inflate(R.layout.title, this);
        titleText = (TextView) findViewById(R.id.title_text);
        leftButton = (Button) findViewById(R.id.button_left);
    }

}
