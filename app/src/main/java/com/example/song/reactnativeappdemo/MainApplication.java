package com.example.song.reactnativeappdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.song.reactnativeappdemo.communication.CommPackage;
import com.example.song.reactnativeappdemo.constants.FileConstant;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Song on 2017/2/13.
 */

public class MainApplication extends Application implements ReactApplication {

    public static Context appContext;
    private static MainApplication instance;
    private static final CommPackage mCommPackage = new CommPackage();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();
        SoLoader.init(this,false);
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

        @Nullable
        @Override
        protected String getJSBundleFile() {
            File file = new File (FileConstant.JS_BUNDLE_LOCAL_PATH);
            if(file != null && file.exists()) {
                return FileConstant.JS_BUNDLE_LOCAL_PATH;
            } else {
                return super.getJSBundleFile();
            }
        }

        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    mCommPackage
            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    /**
     *包名
     */
    public String getAppPackageName() {
        return this.getPackageName();
    }

    /**
     * 获取Application实例
     */
    public static MainApplication getInstance() {
        return instance;
    }

    /**
     * 获取 reactPackage
     * @return
     */
    public static CommPackage getReactPackage() {
        return mCommPackage;
    }



}
