package com.example.song.reactnativeappdemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.song.reactnativeappdemo.constants.AppConstant;
import com.example.song.reactnativeappdemo.constants.FileConstant;
import com.example.song.reactnativeappdemo.preloadreact.ReactNativePreLoader;
import com.example.song.reactnativeappdemo.utils.ACache;
import com.example.song.reactnativeappdemo.utils.FileUtils;
import com.example.song.reactnativeappdemo.utils.java.name.fraser.neil.plaintext.diff_match_patch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private File bundleFile;
    private long mDownLoadId;
    private CompleteReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registeReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            ReactNativePreLoader.preLoad(MainActivity.this,"HotRN");
        }
    }

    /**
     * 向RN发送消息
     * @param v
     */
    public void sendMsgToRN(View v) {
        MainApplication.getReactPackage().mModule.nativeCallRn("hello");
    }

    /**
     * 跳转到RN界面
     * @param v
     */
    public void skip(View v) {
        startActivity(new Intent(this,MyReactActivity.class));
    }

    /**
     * 下载更新包
     * @param v
     */
    public void load(View v) {
        checkVersion();
    }

    /**
     * 检查版本号
     */
    private void checkVersion() {

        if(true) {
            // 有最新版本
            Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
            downLoadBundle();
        }
    }

    /**
     * 下载最新Bundle
     */
    private void downLoadBundle() {

        // 1.下载前检查SD卡是否存在更新包文件夹,FIRST_UPDATE来标识是否为第一次下发更新包
        bundleFile = new File(FileConstant.LOCAL_FOLDER);
        if(bundleFile != null && bundleFile.exists()) {
            ACache.get(getApplicationContext()).put(AppConstant.FIRST_UPDATE,false);
        } else {
            ACache.get(getApplicationContext()).put(AppConstant.FIRST_UPDATE,true);
        }

        // 2.下载
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager
                .Request(Uri.parse(FileConstant.JS_BUNDLE_REMOTE_URL));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.parse("file://"+ FileConstant.JS_PATCH_LOCAL_PATH));
        mDownLoadId = downloadManager.enqueue(request);
    }

    /**
     * 注册
     */
    private void registeReceiver() {
        localReceiver = new CompleteReceiver();
        registerReceiver(localReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 下载完成后收到广播
     */
    public class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(completeId == mDownLoadId) {
                handleZIP();
            }
        }
    }

    /**
     * 下载完成后，处理ZIP压缩包
     */
    private void handleZIP() {

        // 开启单独线程，解压，合并。
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean result = (Boolean) ACache.get(getApplicationContext()).getAsObject(AppConstant.FIRST_UPDATE);
                if (result) {
                    // 解压到根目录
                    FileUtils.decompression(FileConstant.JS_PATCH_LOCAL_FOLDER);
                    // 合并
                    mergePatAndAsset();
                } else {
                    // 解压到future目录
                    FileUtils.decompression(FileConstant.FUTURE_JS_PATCH_LOCAL_FOLDER);
                    // 合并
                    mergePatAndBundle();
                }
                // 删除ZIP压缩包
                FileUtils.deleteFile(FileConstant.JS_PATCH_LOCAL_PATH);
            }
        }).start();
    }

    /**
     * 与Asset资源目录下的bundle进行合并
     */
    private void mergePatAndAsset() {

        // 1.解析Asset目录下的bundle文件
        String assetsBundle = FileUtils.getJsBundleFromAssets(getApplicationContext());
        // 2.解析bundle当前目录下.pat文件字符串
        String patcheStr = FileUtils.getStringFromPat(FileConstant.JS_PATCH_LOCAL_FILE);
        // 3.合并
        merge(patcheStr,assetsBundle);
        // 4.删除pat
       FileUtils.deleteFile(FileConstant.JS_PATCH_LOCAL_FILE);
    }

    /**
     * 与SD卡下的bundle进行合并
     */
    private void mergePatAndBundle() {

        // 1.解析sd卡目录下的bunlde
        String assetsBundle = FileUtils.getJsBundleFromSDCard(FileConstant.JS_BUNDLE_LOCAL_PATH);
        // 2.解析最新下发的.pat文件字符串
        String patcheStr = FileUtils.getStringFromPat(FileConstant.FUTURE_PAT_PATH);
        // 3.合并
        merge(patcheStr,assetsBundle);
        // 4.添加图片
        FileUtils.copyPatchImgs(FileConstant.FUTURE_DRAWABLE_PATH,FileConstant.DRAWABLE_PATH);
        // 5.删除本次下发的更新文件
        FileUtils.traversalFile(FileConstant.FUTURE_JS_PATCH_LOCAL_FOLDER);
    }

    /**
     * 合并,生成新的bundle文件
     */
    private void merge(String patcheStr, String bundle) {

        // 3.初始化 dmp
        diff_match_patch dmp = new diff_match_patch();
        // 4.转换pat
        LinkedList<diff_match_patch.Patch> pathes = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(patcheStr);
        // 5.pat与bundle合并，生成新的bundle
        Object[] bundleArray = dmp.patch_apply(pathes,bundle);
        // 6.保存新的bundle文件
        try {
            Writer writer = new FileWriter(FileConstant.JS_BUNDLE_LOCAL_PATH);
            String newBundle = (String) bundleArray[0];
            writer.write(newBundle);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                System.exit(0);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
