package com.alisa.lswitch;

import android.content.Context;
import android.util.Log;

import com.alisa.lswitch.service.DeviceListService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DeviceListRefresher {

  private final long intervalSec;
  private final Context context;
  private final ScheduledExecutorService scheduler;
  private ScheduledFuture<?> task;
  private static final String TAG = DeviceListRefresher.class.getSimpleName();


  public DeviceListRefresher(final long intervalSec, final Context context) {
    this.intervalSec = intervalSec;
    this.context = context;
    this.scheduler = Executors.newScheduledThreadPool(1);
  }

  private class Refresher implements Runnable {
    private final String passCode;
    public Refresher(String passCode) {
      this.passCode = passCode;
    }
    @Override
    public void run() {
      DeviceListService.refreshListOfDevices(context, passCode);
      Log.d(TAG, "List refreshed");
    }
  }

  public void start(String passCode) {
    task = scheduler.scheduleAtFixedRate(
        new Refresher(passCode),
        0, //initial delay
        intervalSec,
        TimeUnit.SECONDS
    );
    Log.d(TAG, "Started");
  }

  public void stop() {
    if (task != null) { task.cancel(true); }
    Log.d(TAG, "Stopped");
  }
}
