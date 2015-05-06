package com.friendlyarm.WatchDogDemo;

import com.friendlyarm.AndroidSDK.HardwareControler;
import android.os.Handler;
import android.os.Message;
import com.friendlyarm.WatchDogDemo.R;
import com.friendlyarm.Utils.CommonFuncs;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import java.util.Timer;
import java.util.TimerTask;

import com.friendlyarm.AndroidSDK.HardwareControler;
import com.friendlyarm.AndroidSDK.SPIEnum;
import com.friendlyarm.AndroidSDK.GPIOEnum;
import com.friendlyarm.AndroidSDK.FileCtlEnum;

public class WatchDogDemoActivity extends Activity implements OnClickListener {
	HardwareControler hw;
	
	TextView textViewTempLabel;
	TextView textViewTempValue;
	Button btnStartWatchDog;
	Button btnFeedWatchDog;
	int fd = -1;
	
	private Timer timer = new Timer();
	 private int countDown = 1; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        
        textViewTempLabel = ((TextView)findViewById(R.id.textViewTempLabel));
        textViewTempValue = ((TextView)findViewById(R.id.textViewTempValue));
        
        btnStartWatchDog = ((Button)findViewById(R.id.btnStartWatchDog));
        btnStartWatchDog.setOnClickListener(this);
        
        btnFeedWatchDog = ((Button)findViewById(R.id.btnFeedWatchDog));
        btnFeedWatchDog.setOnClickListener(this);
    }
    
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				textViewTempValue.setText(String.valueOf(countDown));
				break;
			}
			super.handleMessage(msg);
		}
	};
    
	private TimerTask watchDogTimerTask = new TimerTask() {
		public void run() {
			if (fd >= 0) {
				countDown ++;
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		}
	}; 
	

    public void onClick(View v)
    {
        switch (v.getId()) {
        case R.id.btnStartWatchDog:
			if (fd == -1) {
				fd = HardwareControler.open( "/dev/watchdog0", FileCtlEnum.O_WRONLY );
			}
			if (fd >= 0) {
	        	countDown = 1;
	        	textViewTempValue.setText(String.valueOf(countDown));
	        	timer.schedule(watchDogTimerTask, 1000, 1000); 
			}
            break;
        case R.id.btnFeedWatchDog:
        	final String str = "W";
        	byte[] writeBytes = str.getBytes();
			HardwareControler.write(fd, writeBytes);
			countDown = 1;
			textViewTempValue.setText(String.valueOf(countDown));
        	break;
        }
    }
	
	@Override
    public void onStart() {
		super.onStart();
	}

	@Override
    public void onRestart() {
		super.onRestart();
	}

	@Override
    public void onResume() {
		super.onResume();
	}

	@Override
    public void onPause() {
		super.onPause();
	}

	@Override
    public void onStop() {
		super.onStop();
	}

	@Override
    public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
}
