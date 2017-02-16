package com.example.song.reactnativeappdemo.utils;

import android.content.Context;
import android.util.Log;

import com.example.song.reactnativeappdemo.constants.FileConstant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 热更新工具类
 * Created by Song on 2017/2/15.
 */
public class RefreshUpdateUtils {

    /**
     * 解压
     * @return
     */
    public static void decompression() {
        try {

            ZipInputStream inZip = new ZipInputStream(new FileInputStream(FileConstant.JS_PATCH_LOCAL_PATH));
            ZipEntry zipEntry;
            String szName;
            try {

                while((zipEntry = inZip.getNextEntry()) != null) {

                    szName = zipEntry.getName();
                    if(zipEntry.isDirectory()) {
                        szName = szName.substring(0,szName.length()-1);
                        File folder = new File(FileConstant.JS_PATCH_LOCAL_FOLDER + File.separator + szName);
                        folder.mkdirs();
                    }else{

                        File file1 = new File(FileConstant.JS_PATCH_LOCAL_FOLDER + File.separator + szName);
                        boolean s = file1.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file1);
                        int len;
                        byte[] buffer = new byte[1024];
                        while((len = inZip.read(buffer)) != -1) {
                            fos.write(buffer, 0 , len);
                            fos.flush();
                        }
                        fos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            inZip.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Assets目录下的bundle文件
     * @return
     */
    public static String getJsBundleFromAssets(Context context) {

        String result = "";
        try {

            InputStream is = context.getAssets().open(FileConstant.JS_BUNDLE_LOCAL_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将.pat文件转换为String
     * @param patPath 下载的.pat文件所在目录
     * @return
     */
    public static String getStringFromPat(String patPath) {

        FileReader reader = null;
        String result = "";
        try {

            reader = new FileReader(patPath);
            int ch = reader.read();
            StringBuilder sb = new StringBuilder();
            while (ch != -1) {
                sb.append((char)ch);
                ch  = reader.read();
            }
            reader.close();
            result = sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
