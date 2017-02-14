package com.example.swipedeletedemo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/2/10.
 */

public class Tool {
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
