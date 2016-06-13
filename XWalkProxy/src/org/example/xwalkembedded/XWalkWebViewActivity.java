package org.example.xwalkembedded;

import java.io.InputStream;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.os.Build;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import android.widget.Button;
import android.widget.ProgressBar;

public class XWalkWebViewActivity extends Activity implements AudioCapabilitiesReceiver.Listener {
	private XWalkView mXWalkView;
    final static  String TAG = "fujunwei";
    private final static int LOAD_URL = 100;

    XWalkExoMediaPlayer mXWalkExoMediaPlayer;
    private SurfaceView surfaceView;
    private ProgressBar waitingBar;
    private Button replayButton;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    
    private int mSystemUiFlag;
    private View mDecorView;
    private boolean mOriginalFullscreen;
    private boolean mOriginalForceNotFullscreen;
    private boolean mIsFullscreen = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		mHandler.sendEmptyMessageDelayed(LOAD_URL, 100);
		
		audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(this, this);
        audioCapabilitiesReceiver.register();
        
        mDecorView = this.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSystemUiFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
	}
	
	private Handler mHandler = new Handler() {
		@Override  
        public void handleMessage(android.os.Message msg) {  
			if(msg.what == LOAD_URL) {
				mXWalkView = (XWalkView) findViewById(R.id.xwalkWebView);
				surfaceView = (SurfaceView) findViewById(R.id.surface_view);
				
				waitingBar = (ProgressBar) findViewById(R.id.waitingBar);
		        replayButton = (Button) findViewById(R.id.replayButton);
		        replayButton.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		                // Do something in response to button click
		                mXWalkView.evaluateJavascript("replayVideo()", null);
		                mXWalkExoMediaPlayer.replayVideo();
		            }
		        });
				
				String[] a = {"*.intel.com"};
	            mXWalkView.proxySettingsChanged("122.96.25.242", 9399, "", a);
//		        mXWalkView.setResourceClient(new MyResourceClient(mXWalkView));
	            
	            // ExoMediaPlayer
	            mXWalkExoMediaPlayer = new XWalkExoMediaPlayer(XWalkWebViewActivity.this, mXWalkView, surfaceView);
	            mXWalkView.addJavascriptInterface(mXWalkExoMediaPlayer, "xwalkExoPlayer");
	            mXWalkExoMediaPlayer.updateProxySetting("140.207.47.119", 10010);
	            mXWalkView.setExMediaPlayer(mXWalkExoMediaPlayer);
	            
				mXWalkView.load("file:///android_asset/index.html", null);
				
				mXWalkView.setResourceClient(new XWalkResourceClient(mXWalkView) {
		            @Override
		            public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
		                Log.d(TAG, "=====in onDocumentLoadedInFrame");
		                mXWalkView.evaluateJavascript(getFromAssets("video.js"), null);
		            }
		        });
			}
		}
	};
	
	// AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        mXWalkExoMediaPlayer.releasePlayer();
        mXWalkExoMediaPlayer.preparePlayer(true);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
            
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void onFullscreenToggled(boolean enterFullscreen) {
        Activity activity = this;
        if (enterFullscreen) {
            if ((activity.getWindow().getAttributes().flags &
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) != 0) {
                mOriginalForceNotFullscreen = true;
                activity.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            } else {
                mOriginalForceNotFullscreen = false;
            }
            if (!mIsFullscreen) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mSystemUiFlag = mDecorView.getSystemUiVisibility();
                    mDecorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {
                    if ((activity.getWindow().getAttributes().flags &
                            WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
                        mOriginalFullscreen = true;
                    } else {
                        mOriginalFullscreen = false;
                        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }
                }
                mIsFullscreen = true;

//                appbar.setVisibility(View.INVISIBLE);
                mXWalkView.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mOriginalForceNotFullscreen) {
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mDecorView.setSystemUiVisibility(mSystemUiFlag);
            } else {
                // Clear the activity fullscreen flag.
                if (!mOriginalFullscreen) {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }
            mIsFullscreen = false;

//            appbar.setVisibility(View.VISIBLE);
            mXWalkView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // If there's navigation happens when app is fullscreen,
            // the content will still be fullscreen after navigation.
            // In such case, the back key will exit fullscreen first.
            if (mIsFullscreen) {
                mXWalkExoMediaPlayer.onHideCustomView();
                return true;
            } else if (mXWalkView.canGoBack()) {
                mXWalkView.goBack();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        onShown();
    }

    @Override
    public void onResume() {
        super.onResume();
        onShown();
    }

    private void onShown() {
    	if (mXWalkExoMediaPlayer != null) {
    		mXWalkExoMediaPlayer.setBackgrounded(false);
    	}
    }

    @Override
    public void onPause() {
        super.onPause();
        onHidden();
    }

    @Override
    public void onStop() {
        super.onStop();
        onHidden();
    }

    private void onHidden() {
    	if (mIsFullscreen) {
            mXWalkExoMediaPlayer.onHideCustomView();
        } else {
            mXWalkExoMediaPlayer.setBackgrounded(true);
        }
    }
    
    public String getFromAssets(String fileName){
        String result = "";
        try {
            InputStream in = getResources().getAssets().open(fileName);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[]  buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
//            result = EncodingUtils.getString(buffer, ENCODING);
            result = new String(buffer);
//            Log.d(TAG, "======getFromAssets " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
    public void showWaitingBar(boolean show) {
        // Show waiting progress bar
        if (show) {
            waitingBar.setVisibility(View.VISIBLE);
            showReplayButton(false);
        } else {
            waitingBar.setVisibility(View.INVISIBLE);
        }
    }

    public void showReplayButton(boolean show) {
        // Show waiting progress bar
        if (show) {
            replayButton.setVisibility(View.VISIBLE);
            showWaitingBar(false);
        } else {
            replayButton.setVisibility(View.INVISIBLE);
        }
    }
}
