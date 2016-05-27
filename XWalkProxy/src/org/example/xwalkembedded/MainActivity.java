package org.example.xwalkembedded;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.os.Build;

import org.xwalk.core.XWalkView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;

public class MainActivity extends Activity implements AudioCapabilitiesReceiver.Listener {
	private XWalkView mXWalkView;
    final static  String TAG = "fujunwei";
    private final static int LOAD_URL = 100;

    XWalkExoMediaPlayer mXWalkExoMediaPlayer;
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
				mXWalkView.load("file:///android_asset/index.html", null);
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
			String[] a = {"*.intel.com"};
            mXWalkView.proxySettingsChanged("122.96.25.242", 9399, "", a);
//	        mXWalkView.setResourceClient(new MyResourceClient(mXWalkView));
            
            // ExoMediaPlayer
            mXWalkExoMediaPlayer = new XWalkExoMediaPlayer(this, mXWalkView);
            mXWalkExoMediaPlayer.updateProxySetting("140.207.47.119", 10010);
            mXWalkView.setExMediaPlayer(mXWalkExoMediaPlayer);
            
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
        mXWalkExoMediaPlayer.setBackgrounded(false);
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
        mXWalkExoMediaPlayer.onHideCustomView();
    }
	
}
