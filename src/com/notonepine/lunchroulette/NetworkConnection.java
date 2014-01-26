package com.notonepine.lunchroulette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class NetworkConnection {
	private static NetworkService sBoundService;
	private static Context sContext;
	
	private NetworkConnection() {};
	
	private static ServiceConnection sConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			sBoundService = ((NetworkService.NetworkBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			sBoundService = null;
		}
	};
	
	public static void initialize(Context context) {
		sContext = context;
	}
	
	public static NetworkService getService() {
		if (sBoundService == null) {
			bindService();
		}
		return sBoundService;
	}
	
	public static void unBindService() {
		sContext.unbindService(sConnection);
	}

	public static void bindService() {
		sContext.bindService(new Intent(sContext, NetworkService.class), sConnection, Context.BIND_AUTO_CREATE);
	}
}
