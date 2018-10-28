/***
  Copyright (c) 2013-2014 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.cwac.preso.demo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.commonsware.cwac.preso.PresentationService;
import androidx.core.app.NotificationCompat;

public class SlideshowService extends PresentationService implements
    Runnable {
  private static final String CHANNEL_WHATEVER="channel_whatever";
  private static final String ACTION_STOP="stop";
  private static final int[] SLIDES= { R.drawable.img0,
      R.drawable.img1, R.drawable.img2, R.drawable.img3,
      R.drawable.img4, R.drawable.img5, R.drawable.img6,
      R.drawable.img7, R.drawable.img8, R.drawable.img9,
      R.drawable.img10, R.drawable.img11, R.drawable.img12,
      R.drawable.img13, R.drawable.img14, R.drawable.img15,
      R.drawable.img16, R.drawable.img17, R.drawable.img18,
      R.drawable.img19 };
  private ImageView iv=null;
  private int iteration=0;
  private Handler handler=null;

  @Override
  public void onCreate() {
    handler=new Handler(Looper.getMainLooper());
    super.onCreate();

    NotificationManager mgr=
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      mgr.getNotificationChannel(CHANNEL_WHATEVER)==null) {
      mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER,
        "Whatever", NotificationManager.IMPORTANCE_DEFAULT));
    }

    startForeground(1338, buildForegroundNotification());
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (ACTION_STOP.equals(intent.getAction())) {
      stopSelf();
    }

    return(super.onStartCommand(intent, flags, startId));
  }

  @Override
  protected int getThemeId() {
    return(R.style.AppTheme);
  }

  @Override
  protected View buildPresoView(Context ctxt, LayoutInflater inflater) {
    iv=new ImageView(ctxt);
    run();

    return(iv);
  }

  @Override
  public void run() {
    iv.setImageResource(SLIDES[iteration % SLIDES.length]);
    iteration+=1;

    handler.postDelayed(this, 5000);
  }

  @Override
  public void onDestroy() {
    handler.removeCallbacks(this);

    super.onDestroy();
  }

  private Notification buildForegroundNotification() {
    NotificationCompat.Builder b=
      new NotificationCompat.Builder(this, CHANNEL_WHATEVER);

    b.setOngoing(true)
      .setContentTitle(getString(R.string.msg_foreground))
      .setSmallIcon(R.drawable.ic_stat_screen)
      .addAction(android.R.drawable.ic_media_pause, getString(R.string.msg_stop),
        buildStopPendingIntent());

    return(b.build());
  }

  private PendingIntent buildStopPendingIntent() {
    Intent i=new Intent(this, getClass()).setAction(ACTION_STOP);

    return(PendingIntent.getService(this, 0, i, 0));
  }
}
