package com.example.song.reactnativeappdemo.preloadreact;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.KeyEvent;

import com.example.song.reactnativeappdemo.MainApplication;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Callback;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionListener;

import javax.annotation.Nullable;

/**
 * 处理预加载代理类
 * Created by Song on 2017/5/10.
 */

public class PreLoadReactDelegate {

    private final Activity mActivity;
    private ReactRootView mReactRootView;
    private Callback mPermissionsCallback;
    private final String mMainComponentName;
    private PermissionListener mPermissionListener;
    private final int REQUEST_OVERLAY_PERMISSION_CODE = 1111;
    private DoubleTapReloadRecognizer mDoubleTapReloadRecognizer;

    public PreLoadReactDelegate(Activity activity, @Nullable String mainComponentName) {
        this.mActivity = activity;
        this.mMainComponentName = mainComponentName;
    }

    public void onCreate() {
        boolean needsOverlayPermission = false;
        if (getReactNativeHost().getUseDeveloperSupport() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get permission to show redbox in dev builds.
            if (!Settings.canDrawOverlays(mActivity)) {
                needsOverlayPermission = true;
                Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
                mActivity.startActivityForResult(serviceIntent, REQUEST_OVERLAY_PERMISSION_CODE);
            }
        }

        if (mMainComponentName != null && !needsOverlayPermission) {
            // 1.从缓存中获取RootView
            mReactRootView = ReactNativePreLoader.getReactRootView(mMainComponentName);

            if(mReactRootView == null) {

                // 2.缓存中不存在RootView,直接创建
                mReactRootView = new ReactRootView(mActivity);
                mReactRootView.startReactApplication(
                        getReactInstanceManager(),
                        mMainComponentName,
                        null);
            }
            // 3.将RootView设置到Activity布局
            mActivity.setContentView(mReactRootView);
        }

        mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
    }

    public void onResume() {
        if (getReactNativeHost().hasInstance()) {
            getReactInstanceManager().onHostResume(mActivity, (DefaultHardwareBackBtnHandler)mActivity);
        }
        if (mPermissionsCallback != null) {
            mPermissionsCallback.invoke();
            mPermissionsCallback = null;
        }
    }

    public void onPause() {
        if (getReactNativeHost().hasInstance()) {
            getReactInstanceManager().onHostPause(mActivity);
        }
    }

    public void onDestroy() {

        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
            mReactRootView = null;
        }
        if (getReactNativeHost().hasInstance()) {
            getReactInstanceManager().onHostDestroy(mActivity);
        }

        // 清除View
        ReactNativePreLoader.deatchView(mMainComponentName);
    }

    public boolean onNewIntent(Intent intent) {
        if (getReactNativeHost().hasInstance()) {
            getReactInstanceManager().onNewIntent(intent);
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getReactNativeHost().hasInstance()) {
            getReactInstanceManager().onActivityResult(mActivity, requestCode, resultCode, data);
        } else {
            // Did we request overlay permissions?
            if (requestCode == REQUEST_OVERLAY_PERMISSION_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(mActivity)) {
                    if (mMainComponentName != null) {
                        if (mReactRootView != null) {
                            throw new IllegalStateException("Cannot loadApp while app is already running.");
                        }
                        mReactRootView = new ReactRootView(mActivity);
                        mReactRootView.startReactApplication(
                                getReactInstanceManager(),
                                mMainComponentName,
                                null);
                        mActivity.setContentView(mReactRootView);
                    }
                }
            }
        }
    }

    public boolean onBackPressed() {
        if (getReactNativeHost().hasInstance()) {
            getReactInstanceManager().onBackPressed();
            return true;
        }
        return false;
    }

    public boolean onRNKeyUp(int keyCode) {
        if (getReactNativeHost().hasInstance() && getReactNativeHost().getUseDeveloperSupport()) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                getReactInstanceManager().showDevOptionsDialog();
                return true;
            }
            boolean didDoubleTapR = Assertions.assertNotNull(mDoubleTapReloadRecognizer)
                    .didDoubleTapR(keyCode, mActivity.getCurrentFocus());
            if (didDoubleTapR) {
                getReactInstanceManager().getDevSupportManager().handleReloadJS();
                return true;
            }
        }
        return false;
    }

    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        mActivity.requestPermissions(permissions, requestCode);
    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        mPermissionsCallback = new Callback() {
            @Override
            public void invoke(Object... args) {
                if (mPermissionListener != null && mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    mPermissionListener = null;
                }
            }
        };
    }

    /**
     * 获取 Application中 ReactNativeHost
     * @return
     */
    private ReactNativeHost getReactNativeHost() {
        return MainApplication.getInstance().getReactNativeHost();
    }

    /**
     * 获取 ReactInstanceManager
     * @return
     */
    private ReactInstanceManager getReactInstanceManager() {
        return getReactNativeHost().getReactInstanceManager();
    }
}
