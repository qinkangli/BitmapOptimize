package com.example.administrator.bitmapoptimizedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:

                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:

                    break;
                default:
                    break;
            }
        }
    };

    public int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){     //API 19
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight(); //earlier version
        }
    }


    public void getBitmapSize(View view) {
        File file = Environment.getExternalStorageDirectory();
        File originFile = new File(file,"dog.jpg");
        Bitmap originBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath());
        int bitmapSize = getBitmapSize(originBitmap);
        Log.i("测试","图片所占内存大小->" + bitmapSize);//结果：5760000
    }

    public void getQualityBitmapSize(View view) {
        qualityCompress(Bitmap.CompressFormat.JPEG,10);//quality:10=22.57,50=98.12kb,70=122.97
    }

    public void getSampleBitmapSize(View view) {
        sampleRateCompress(2);
    }

    public void getSizeBitmapSize(View view) {
        sizeCompress(2);
    }

    public void qualityCompress(Bitmap.CompressFormat format,int quality){
        File file = Environment.getExternalStorageDirectory();
        File originFile = new File(file,"dog.jpg");
        Bitmap originBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        originBitmap.compress(format,quality,bos);
        int bitmapSize = getBitmapSize(originBitmap);
        Log.i("测试","质量压缩后图片所占内存大小->" + bitmapSize);//结果：5760000
        try {
            FileOutputStream fos = new FileOutputStream(new File(file,"resultQualityImg.jpg"));
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sampleRateCompress(int inSampleSize) {
        File file = Environment.getExternalStorageDirectory();
        File originFile = new File(file, "dog.jpg");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap resultBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath(), options);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//quality:100=215.38kb,80=50.41kb
        int bitmapSize = getBitmapSize(resultBitmap);
        Log.i("测试","采样率压缩后图片所占内存大小->" + bitmapSize);//结果：1440000
        try {
            FileOutputStream fos = new FileOutputStream(new File(file, "resultSampleRateImg.jpg"));
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sizeCompress(int radio) {//radio:5=47.85kb,2=221.39kb
        File sdFile = Environment.getExternalStorageDirectory();
        File originFile = new File(sdFile, "dog.jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath());
        //设置缩放比
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / radio, bitmap.getHeight() / radio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF rectF = new RectF(0, 0, bitmap.getWidth() / radio, bitmap.getHeight() / radio);
        //将原图画在缩放之后的矩形上
        canvas.drawBitmap(bitmap, null, rectF, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 100, bos);//
        int bitmapSize = getBitmapSize(result);
        Log.i("测试","尺寸压缩后图片所占内存大小->" + bitmapSize);//结果：230400
        try {
            FileOutputStream fos = new FileOutputStream(new File(sdFile, "resultSizeRateImg.jpg"));
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
