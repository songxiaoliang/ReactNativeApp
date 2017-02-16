package com.example.song.reactnativeappdemo.constants;

import android.os.Environment;

import com.example.song.reactnativeappdemo.MainApplication;

import java.io.File;

/**
 * Created by Song on 2017/2/15.
 */
public class FileConstant {

    /**
     * zip的文件名
     */
    public static final String ZIP_NAME = "patch";

    /**
     * bundle文件名
     */
    public static final String JS_BUNDLE_LOCAL_FILE = "index.android.bundle";

    /**
     * zip文件
     */
    public static final String JS_PATCH_LOCAL_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + MainApplication.getInstance().getAppPackageName() +"/"+ZIP_NAME+".zip";

    /**
     * 解压zip后的文件目录
     */
    public static final String JS_PATCH_LOCAL_FOLDER = Environment.getExternalStorageDirectory().toString()
            + File.separator + MainApplication.getInstance().getAppPackageName();

    /**
     * 合并后的bundle文件保存路径
     */
    public static final String JS_BUNDLE_LOCAL_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + MainApplication.getInstance().getAppPackageName()+"/patch/"+ JS_BUNDLE_LOCAL_FILE;

    /**
     * .pat文件
     */
    public static final String JS_PATCH_LOCAL_FILE = Environment.getExternalStorageDirectory().toString()
            + File.separator + MainApplication.getInstance().getAppPackageName() +"/patch/bundle.pat";

    /**
     * 下载URL
     */
    public static final String JS_BUNDLE_REMOTE_URL = "http://oleeed73x.bkt.clouddn.com/"+ZIP_NAME+".zip";

}
