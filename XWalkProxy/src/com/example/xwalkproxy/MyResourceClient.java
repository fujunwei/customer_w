package com.example.xwalkproxy;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.socketproxy.HttpGetProxy;
import com.example.socketproxy.Utils;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkViewInternal;
import android.media.MediaPlayer.OnPreparedListener;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by junweifu on 4/13/2016.
 */
public class MyResourceClient extends XWalkResourceClient {
    String TAG = "MyResourceClient";
    private HttpGetProxy proxy;
    static private final int PREBUFFER_SIZE= 4*1024*1024;
    private long startTimeMills;
    String id = "";
    private long waittime=8000;//ç­‰å¾…ç¼“å†²æ—¶é—´

    public MyResourceClient(XWalkView xWalkView) {
        super(xWalkView);
    }

    @Override
    public boolean shouldOverrideResourceLoading(XWalkView view,
            MediaPlayer mediaPlayer, Context context, Uri uri, Map<String, String> headers) {

        //åˆ›å»ºé¢„åŠ è½½è§†é¢‘æ–‡ä»¶å­˜æ”¾æ–‡ä»¶å¤¹
        new File(getBufferDir()).mkdirs();

        // åˆ�å§‹åŒ–ä»£ç�†æœ�åŠ¡å™¨
        proxy = new HttpGetProxy(getBufferDir(),// é¢„åŠ è½½è§†é¢‘æ–‡ä»¶å­˜æ”¾è·¯å¾„
                PREBUFFER_SIZE,// é¢„åŠ è½½ä½“ç§¯
                10);// é¢„åŠ è½½æ–‡ä»¶ä¸Šé™�

        id = System.currentTimeMillis() + "";
        try {
            proxy.startDownload(id, uri.toString(), true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        delayToStartPlay.sendEmptyMessageDelayed(0, waittime);
//
//        mediaPlayer.setOnPreparedListener(mOnPreparedListener);

        String proxyUrl = proxy.getLocalURL(id);
        Uri localUri = Uri.parse(proxyUrl);
        try {
            mediaPlayer.setDataSource(context, localUri, headers);
        } catch (IOException e) {
            Log.e(TAG, "Media player set data source failed : " + e);
        }

        return true;
    }

//    private OnPreparedListener mOnPreparedListener=new OnPreparedListener(){
//        @Override
//        public void onPrepared(MediaPlayer mp) {
//            mVideoView.start();
//            long duration=System.currentTimeMillis() - startTimeMills;
//            Log.e(TAG,"ç­‰å¾…ç¼“å†²æ—¶é—´:"+waittime+",é¦–æ¬¡ç¼“å†²æ—¶é—´:"+duration);
//        }
//    };
//
//    private Handler delayToStartPlay = new Handler() {
//        public void handleMessage(Message msg) {
//            startTimeMills=System.currentTimeMillis();
//            String proxyUrl = proxy.getLocalURL(id);
//            mVideoView.setVideoPath(proxyUrl);
//        }
//    };
//
    static public String getBufferDir(){
        String bufferDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/ProxyBuffer/files";
        return bufferDir;
    }
}
