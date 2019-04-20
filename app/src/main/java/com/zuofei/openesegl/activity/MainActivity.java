package com.zuofei.openesegl.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import rx.functions.Action1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaRecorder;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


import com.tbruyelle.rxpermissions.RxPermissions;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.zuofei.openesegl.utils.EglHelper;
import com.zuofei.openesegl.R;
import com.zuofei.openesegl.utils.UpdateAppHttpUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;


    /**
     * {
     *   "update": "Yes",
     *   "new_version": "0.8.3",
     *   "apk_file_url": "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/sample-debug.apk",
     *   "update_log": "1，添加删除信用卡接口。\r\n2，添加vip认证。\r\n3，区分自定义消费，一个小时不限制。\r\n4，添加放弃任务接口，小时内不生成。\r\n5，消费任务手动生成。",
     *   "target_size": "5M",
     *   "new_md5":"b97bea014531123f94c3ba7b7afbaad2",
     *   "constraint": false
     * }
     */
    private String mUpdateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
    private File mAudioFile;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recorderFront();
        getPermission();
//        Intent intent = new Intent(MainActivity.this, RecorderService.class);
//        intent.putExtra(RecorderService.INTENT_VIDEO_PATH, "/folder-path/"); //eg: "/video/camera/"
//        startService(intent);

//        updateApp();
//        renderSurfaceView();
    }

    private void recorderFront() {
        String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
        int color = getResources().getColor(R.color.colorPrimaryDark);
        int requestCode = 0;
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(filePath)
                .setColor(color)
                .setRequestCode(requestCode)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(true)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }
//    private void renderSurfaceView() {
//        surfaceView = findViewById(R.id.surfaceview);
//        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//
//            }
//            @Override
//            public void surfaceChanged(final SurfaceHolder holder, int format, final int width, final int height) {
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        render(holder, width, height);
//                        return;
//                    }
//                }.start();
//            }
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }

    /**
     * 更新app
     */
    private void updateApp() {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUpdateUrl)
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .update();
    }

    /**
     * 获取权限
     */
    public void getPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(WRITE_EXTERNAL_STORAGE,RECORD_AUDIO)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(MainActivity.this, "已授权", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "未授权", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 渲染surfaceview
     * @param holder
     * @param width
     * @param height
     */
    private void render(SurfaceHolder holder, int width, int height) {
        EglHelper eglHelper = new EglHelper();
        eglHelper.initEgl(holder.getSurface(),null);
        while (true){
            GLES20.glViewport(0,0,width,height);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glClearColor(1.0f,0.0f,0.0f,1.0f);
            eglHelper.swapBuffers();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始录音
     */
    protected void start() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            String mp3_name = "/sdcard/"+simpleDateFormat.format(date)+".mp3";
            File file = new File(mp3_name);
            if (file.exists()) {
                // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
                file.delete();
            }
            mediaRecorder = new MediaRecorder();
            // 设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制音频的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置录制音频文件输出文件路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording=false;
                    Toast.makeText(MainActivity.this, "录音发生错误",Toast.LENGTH_LONG).show();
                }
            });

            // 准备、开始
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording=true;
            Toast.makeText(MainActivity.this, "开始录音", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音结束
     */
    protected void stop() {
        if (isRecording) {
            // 如果正在录音，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording=false;
            Toast.makeText(MainActivity.this, "录音结束", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            // 如果正在录音，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }


    public void startRecord(View view) {
        start();
    }

    public void stopRecord(View view) {
        stop();
    }
}
