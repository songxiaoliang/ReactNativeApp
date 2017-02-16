package com.example.song.reactnativeappdemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.song.reactnativeappdemo.constants.FileConstant;
import com.example.song.reactnativeappdemo.utils.FileUtils;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private CompleteReceiver localReceiver;
    private long mDownLoadId;
    private File zipfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registeReceiver();
    }

    public void skip(View v) {
        startActivity(new Intent(this,MyReactActivity.class));
    }

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
        zipfile = new File(FileConstant.JS_PATCH_LOCAL_PATH);

        if(zipfile != null && zipfile.exists()) {
            zipfile.delete();
        }
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
                FileUtils.decompression();
                zipfile.delete();
                startActivity(new Intent(MainActivity.this,MyReactActivity.class));
            }
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
