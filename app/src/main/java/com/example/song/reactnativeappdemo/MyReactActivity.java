package com.example.song.reactnativeappdemo;

import android.os.Bundle;
import com.facebook.react.ReactActivity;
import javax.annotation.Nullable;

/**
 * Created by Song on 2017/2/13.
 */

public class MyReactActivity extends ReactActivity {

    @Nullable
    @Override
    protected String getMainComponentName() {
        return "HotRN";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
