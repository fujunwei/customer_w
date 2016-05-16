package com.example.xwalkproxy;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import org.xwalk.core.XWalkView;

public class MainActivity extends Activity {
	private XWalkView mXWalkView;
    final static  String TAG = "fujunwei";
    private final static int LOAD_URL = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		mHandler.sendEmptyMessageDelayed(LOAD_URL, 100);
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
            XWalkExoMediaPlayer mXWalkExoMediaPlayer = new XWalkExoMediaPlayer(this, mXWalkView);
            mXWalkExoMediaPlayer.updateProxySetting("122.96.25.242", 9399);
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
	
}
