package com.example.batteryexperiment;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class BatteryExperiment extends Activity {

	private TextView batteryInfo;
	private ImageView imageBatteryState;
	
	private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver(){
	
		@Override
		public void onReceive(Context context, Intent intent) {
			int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
			int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			boolean present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
			String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
			int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
			int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
			
			batteryInfo.setText(
					"Health: "+health+"\n"+
					"Icon Small:"+icon_small+"\n"+
					"Level: "+level+"\n"+
					"Plugged: "+plugged+"\n"+
					"Present: "+present+"\n"+
					"Scale: "+scale+"\n"+
					"Status: "+status+"\n"+
					"Technology: "+technology+"\n"+
					"Temperature: "+temperature+"\n"+
					"Voltage: "+voltage+"\n");
				imageBatteryState.setImageResource(icon_small);
			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery_experiment);
		batteryInfo = (TextView)findViewById(R.id.textViewBatteryInfo);
		imageBatteryState = (ImageView)findViewById(R.id.imageViewBatteryState);
		
		this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.battery_experiment, menu);
		return true;
	}

}
