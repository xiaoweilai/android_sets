package com.friendlyarm.LEDDemo;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.friendlyarm.AndroidSDK.HardwareControler;

public class LEDDemoMainActivity extends Activity {

	MyCustomAdapter dataAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leddemo_main);

		// Generate list View from ArrayList
		displayListView();
		
		// turn off all led
		HardwareControler.setLedState(0,0);
		HardwareControler.setLedState(1,0);
		HardwareControler.setLedState(2,0);
		HardwareControler.setLedState(3,0);
	}

	private void displayListView() {

		// Array list of countries
		ArrayList<LED> ledList = new ArrayList<LED>();
		LED led = new LED(0, "LED 1", false);
		ledList.add(led);
		led = new LED(1, "LED 2", false);
		ledList.add(led);
		led = new LED(2, "LED 3", false);
		ledList.add(led);
		led = new LED(3, "LED 4", false);
		ledList.add(led);

		// create an ArrayAdaptar from the String Array
		dataAdapter = new MyCustomAdapter(this, R.layout.checkbox_listview_item,
				ledList);
		ListView listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LED led = (LED) parent.getItemAtPosition(position);
				HardwareControler.setLedState(led.code,led.isSelected()?1:0);
			}
		});

	}

	private class MyCustomAdapter extends ArrayAdapter<LED> {

		private ArrayList<LED> ledList;

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<LED> ledList) {
			super(context, textViewResourceId, ledList);
			this.ledList = new ArrayList<LED>();
			this.ledList.addAll(ledList);
		}

		private class ViewHolder {
			CheckBox name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.checkbox_listview_item, null);

				holder = new ViewHolder();
				holder.name = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				convertView.setTag(holder);

				holder.name.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						LED led = (LED) cb.getTag();
						led.setSelected(cb.isChecked());
						HardwareControler.setLedState(led.code,led.isSelected()?1:0);
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			LED led = ledList.get(position);
			holder.name.setText(led.getName());
			holder.name.setChecked(led.isSelected());
			holder.name.setTag(led);

			return convertView;

		}

	}
}