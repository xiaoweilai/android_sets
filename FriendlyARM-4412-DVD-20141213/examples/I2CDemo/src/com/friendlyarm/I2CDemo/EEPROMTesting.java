package com.friendlyarm.I2CDemo;

import java.io.IOException;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import com.friendlyarm.AndroidSDK.HardwareControler;
import com.friendlyarm.AndroidSDK.FileCtlEnum;
import com.friendlyarm.I2CDemo.R;
import com.friendlyarm.Utils.CommonFuncs;
/* for thread */
import android.os.Message;
import android.os.Handler;
import android.os.Looper;

public class EEPROMTesting extends Activity implements OnClickListener {
	EditText writeEditor;
	EditText readEditor;
	TextView statusView;
	private Handler messageHandler;
	Thread writeThread = null;
	Thread readThread = null;
	int devFD = -1;

    private class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
        	String result = (String) msg.obj;
        	if (result.equals("ERROR")) {
        		statusView.setText("Status: Error");
        	} else if (result.equals("DONE")) {
        		statusView.setText("Status: Done");
        	} else if (result.startsWith("B")) {
        		readEditor.append(result.substring(1));
        	} else {
        		statusView.setText("Status: Processing");
        	}
        }
    }
    
    @Override
    public void onDestroy() {
    	if (devFD >= 0) {
    	    HardwareControler.close(devFD);
    	    devFD = -1;
    	}
    	super.onDestroy();
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.eepromtestingactivity_landscape);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.eepromtestingactivity);
		}
        
        writeEditor = (EditText) findViewById(R.id.writeEditor);
    	writeEditor.setText("Long, long ago there lived a king. He loved horses. One day he asked an artist to draw him a beautiful horse. The artist said...");
    	
    	statusView = (TextView) findViewById(R.id.statusView);
    	readEditor = (EditText) findViewById(R.id.readEditor);
    	
        ((Button)findViewById(R.id.writeEEPROMButton)).setOnClickListener(this);
        ((Button)findViewById(R.id.readEEPROMButton)).setOnClickListener(this);
        
        Looper looper = Looper.myLooper();
        messageHandler = new MessageHandler(looper);
        
        devFD = HardwareControler.open("/dev/i2c-0", FileCtlEnum.O_RDWR);
        if (devFD < 0) {
        	CommonFuncs.showAlertDialog(this, "Fail to open I2C device.");
        	finish();
        } else {
        	if (HardwareControler.setI2CSlave(devFD, 0x50) < 0) {
        		CommonFuncs.showAlertDialog(this, "Fail to set I2C slave.");
        		finish();
        	} else {
        		statusView.setText("Status: Ready");
        	}
        }
    }
    
    private void startWriteEEPROMThread() {
    	statusView.setText("Status: Processing");
    	new Thread() {
            @Override
            public void run() {
            	String result;
                byte[] writeBytes = writeEditor.getText().toString().getBytes();
                byte b;
				for (int i = 0; i < 256; i++) {
					
					if (i < writeBytes.length) {
					    b = writeBytes[i];
					} else {
						b = 0;
					}
					
					if(b != 0 && b != '\n') {
						if (b < ' ' || b > 126) {
							b = 0;
						}
					}
					if (HardwareControler.writeByteToI2C(devFD, i, b, 10) != 0) {
						result = "ERROR";
						Message message = Message.obtain();
						message.obj = result;
						messageHandler.sendMessage(message);

						break;
					} else {
						result = ""+i;
						Message message = Message.obtain();
						message.obj = result;
						messageHandler.sendMessage(message);
					}
				}
				
				result = "DONE";
				Message message = Message.obtain();
				message.obj = result;
				messageHandler.sendMessage(message);
            }

        }.start();
    }
    
    
    private void startReadEEPROMThread()
    {
    	statusView.setText("Status: Processing");
        new Thread() {
            @Override
            public void run() {
            	String result;
            	int ret;
                byte b;
                boolean bError = false;
				for (int i = 0; i < 256; i++) {
					ret = HardwareControler.readByteFromI2C(devFD, i, 10);
					if (ret < 0) {
						result = "ERROR";
						Message message = Message.obtain();
						message.obj = result;
						messageHandler.sendMessage(message);
						bError = true;
						break;
					} else {
						result = ""+i;
						{
						    Message message = Message.obtain();
						    message.obj = result;
						    messageHandler.sendMessage(message);
						}
						
						b = (byte) ret;
						if (b == 0) {
							break;
						}
						
						if (b < ' ' || b > 126) {
							if (b != '\n') {
								b = '.';
							}
						}
						
						{
							byte[] bs = {b}; 
							String str = new String(bs); 
							str = "B" + str;
							
						    Message message = Message.obtain();
						    message.obj = str;
						    messageHandler.sendMessage(message);
						}
					}
				}
				
				if (!bError) {
				    result = "DONE";
				    Message message = Message.obtain();
				    message.obj = result;
				    messageHandler.sendMessage(message);
				}
            }

        }.start();
    }
    
	public void onClick(View v) {
		switch (v.getId()) {
    	case R.id.writeEEPROMButton:
    		startWriteEEPROMThread();
    		break;
    	case R.id.readEEPROMButton:
    		readEditor.setText("");
    		startReadEEPROMThread();
    		break;
		default:
			break;
		}
	}
	
}
