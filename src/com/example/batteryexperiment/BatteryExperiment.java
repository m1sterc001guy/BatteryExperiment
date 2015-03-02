package com.example.batteryexperiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
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
	
	
	private ArrayList<HashMap<String, Long>> getCPUStats() throws FileNotFoundException{
		File file = new File("/proc/stat");
		Scanner scan = new Scanner(file);
		ArrayList<HashMap<String, Long>> cpuStats = new ArrayList<HashMap<String, Long>>();
		
		int numLines = 5;
		int currLine = 0;
		while(currLine < numLines){
			String stringStats = scan.nextLine();
			//Log.d("LINE", stringStats);
			HashMap<String, Long> stats = new HashMap<String, Long>();
			Scanner lineScanner = new Scanner(stringStats);
			int i = 0;
			while(lineScanner.hasNext()){
				try{
					Long value = Long.parseLong(lineScanner.next());
					switch(i){
					case 0:
						stats.put("user", value);
						break;
					case 1:
						stats.put("nice", value);
						break;
					case 2:
						stats.put("system", value);
						break;
					case 3:
						stats.put("idle", value);
						break;
					case 4:
						stats.put("iowait", value);
						break;
					case 5:
						stats.put("irq", value);
						break;
					case 6:
						stats.put("softirq", value);
						break;
					case 7:
						stats.put("steal", value);
						break;
					case 8:
						stats.put("guest", value);
						break;
					case 9:
						stats.put("guest_nice", value);
						break;
					}
					i++;
				}catch(NumberFormatException e){
					//do nothing
				}
			}
			cpuStats.add(stats);
			currLine++;
		}
		
		return cpuStats;
	}
	
	private double getCPUPercentage(ArrayList<HashMap<String, Long>> prevStats, ArrayList<HashMap<String, Long>> currStats, int cpuNumber){
		HashMap<String, Long> prevTotalStats = prevStats.get(cpuNumber);
		HashMap<String, Long> currTotalStats = currStats.get(cpuNumber);
		
		long prevIdle = prevTotalStats.get("idle") + prevTotalStats.get("iowait");
		long idle = currTotalStats.get("idle") + currTotalStats.get("iowait");
		long prevNonIdle = prevTotalStats.get("user") + prevTotalStats.get("nice") + prevTotalStats.get("system") + prevTotalStats.get("irq") + prevTotalStats.get("softirq") + prevTotalStats.get("steal");
		long nonIdle = currTotalStats.get("user") + currTotalStats.get("nice") + currTotalStats.get("system") + currTotalStats.get("irq") + currTotalStats.get("softirq") + currTotalStats.get("steal");
		long prevTotal = prevIdle + prevNonIdle;
		long total = idle + nonIdle;
		
		double cpuPercentage = ((total-prevTotal)-(idle-prevIdle))/(double)(total-prevTotal);
		return cpuPercentage;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery_experiment);
		batteryInfo = (TextView)findViewById(R.id.textViewBatteryInfo);
		imageBatteryState = (ImageView)findViewById(R.id.imageViewBatteryState);
		
		this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		
		try{
			//while(true){
				ArrayList<HashMap<String, Long>> prevStats = getCPUStats();
				
				
				//double k = 0.0;
				//while(k < 1000.5){
				//	k += .005;
				//}
				Thread.sleep(1000);
				
				ArrayList<HashMap<String, Long>> currStats = getCPUStats();
				
				double totalCpuPercentage = getCPUPercentage(prevStats, currStats, 0);
				double cpu1Percentage = getCPUPercentage(prevStats, currStats, 1);
				double cpu2Percentage = getCPUPercentage(prevStats, currStats, 2);
				double cpu3Percentage = getCPUPercentage(prevStats, currStats, 3);
				double cpu4Percentage = getCPUPercentage(prevStats, currStats, 4);
				
				Log.d("CPU PERCENTAGE", "Total Cpu %: " + totalCpuPercentage + " Cpu 1 %: " + cpu1Percentage + " Cpu 2 %: " + cpu2Percentage + " Cpu 3 %: " + cpu3Percentage + " Cpu 4 %: " + cpu4Percentage);
			//}

			
		}catch(FileNotFoundException e){
			Log.e("OPEN FILE", "FILE NOT FOUND EXCEPTION");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.battery_experiment, menu);
		return true;
	}

}
