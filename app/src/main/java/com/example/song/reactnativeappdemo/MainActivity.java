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
import com.example.song.reactnativeappdemo.constants.FileConstant;
import com.example.song.reactnativeappdemo.utils.RefreshUpdateUtils;
import com.example.song.reactnativeappdemo.utils.java.name.fraser.neil.plaintext.diff_match_patch;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private File zipfile;
    private long mDownLoadId;
    private CompleteReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registeReceiver();
    }

    /**
     * 向RN发送消息
     * @param v
     */
    public void sendMsgToRN(View v) {
        Log.e("---","sendMsgToRN");
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

        // 1.检查是否存在pat压缩包,存在则删除
        zipfile = new File(FileConstant.JS_PATCH_LOCAL_PATH);
        if(zipfile != null && zipfile.exists()) {
            zipfile.delete();
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
                // 1.解压
                RefreshUpdateUtils.decompression();
                zipfile.delete();
                // 2.将下载好的patches文件与assets目录下的原index.android.bundle合并，得到新的
                // bundle文件
                mergePatAndAsset();
                startActivity(new Intent(MainActivity.this,MyReactActivity.class));
            }
        }
    }

    /**
     * 合并patches文件
     */
    private void mergePatAndAsset() {

        // 1.获取Assets目录下的bunlde
        String assetsBundle = RefreshUpdateUtils.getJsBundleFromAssets(getApplicationContext());
        // 2.获取.pat文件字符串
        String patcheStr = RefreshUpdateUtils.getStringFromPat(FileConstant.JS_PATCH_LOCAL_FILE);
        // 3.初始化 dmp
        diff_match_patch dmp = new diff_match_patch();
        // 4.转换pat
        LinkedList<diff_match_patch.Patch> pathes = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(patcheStr);
        // 5.与assets目录下的bundle合并，生成新的bundle
        Object[] bundleArray = dmp.patch_apply(pathes,assetsBundle);
        // 6.保存新的bundle
        try {
            Writer writer = new FileWriter(FileConstant.JS_BUNDLE_LOCAL_PATH);
            String newBundle = (String) bundleArray[0];
            writer.write(newBundle);
            writer.close();
            // 7.删除.pat文件
            File patFile = new File(FileConstant.JS_PATCH_LOCAL_FILE);
            patFile.delete();
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
