package com.example.song.reactnativeappdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

/**
 * Created by Song on 2017/2/13.
 */

public class MyReactActivity extends Activity implements DefaultHardwareBackBtnHandler{

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    private FrameLayout mRnContaner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_rn);
        mRnContaner = (FrameLayout) findViewById(R.id.rn_contaner);
        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                /**
                 * http://stackoverflow.com/questions/37951246/react-native-cannot-find-development-server-integrating-existing-android-app
                 * 调试模式下,建议直接写成 true 吧,我就因为这个错误,调了两天原因
                 */
//                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setUseDeveloperSupport(true)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        mReactRootView.startReactApplication(mReactInstanceManager, "HotRN", null);
        mRnContaner.addView(mReactRootView);// 添加RN界面到当前容器布局
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
