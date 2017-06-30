package com.example.song.reactnativeappdemo.preloadreact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.ReactActivity;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import javax.annotation.Nullable;

/**
 * 预加载Activity基类
 * Created by Song on 2017/5/10.
 */
public class PreLoadReactActivity extends ReactActivity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    private PreLoadReactDelegate mPreLoadReactDelegate;

    protected PreLoadReactActivity() {
        mPreLoadReactDelegate = createPreLoadReactDelegate();
    }

    private PreLoadReactDelegate createPreLoadReactDelegate() {
        return new PreLoadReactDelegate(this,getMainComponentName());
    }

    /**
     * 子类重写，返回RN对应的界面组件名称
     * @return
     */
    protected @Nullable String getMainComponentName() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreLoadReactDelegate.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreLoadReactDelegate.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreLoadReactDelegate.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreLoadReactDelegate.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if(!mPreLoadReactDelegate.onNewIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPreLoadReactDelegate.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mPreLoadReactDelegate.onRNKeyUp(keyCode) || super.onKeyUp(keyCode, event);
    }

    /**
     * 处理权限授权
     * @param permissions
     * @param requestCode
     * @param listener
     */
    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPreLoadReactDelegate.requestPermissions(permissions,requestCode,listener);
    }

    /**
     * 授权结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
       mPreLoadReactDelegate.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

}

