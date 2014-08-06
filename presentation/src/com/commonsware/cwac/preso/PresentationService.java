/***
  Copyright (c) 2014 CommonsWare, LLC
  
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

package com.commonsware.cwac.preso;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public abstract class PresentationService extends Service implements
    PresentationHelper.Listener {
  protected abstract int getThemeId();

  protected abstract View buildPresoView(Context ctxt,
                                         LayoutInflater inflater);

  private WindowManager wm=null;
  private View presoView=null;
  private PresentationHelper helper=null;

  @Override
  public IBinder onBind(Intent intent) {
    return(null);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    helper=new PresentationHelper(this, this);
    helper.onResume();
  }

  @Override
  public void onDestroy() {
    helper.onPause();

    super.onDestroy();
  }

  @Override
  public void showPreso(Display display) {
    Context presoContext=createPresoContext(display);
    LayoutInflater inflater=LayoutInflater.from(presoContext);

    wm=
        (WindowManager)presoContext.getSystemService(Context.WINDOW_SERVICE);

    presoView=buildPresoView(presoContext, inflater);
    wm.addView(presoView, buildLayoutParams());
  }

  @Override
  public void clearPreso(boolean switchToInline) {
    if (presoView != null) {
      try {
        wm.removeView(presoView);
      }
      catch (Exception e) {
        // probably the window is gone, don't worry, be
        // happy
      }
    }

    presoView=null;
  }

  protected WindowManager.LayoutParams buildLayoutParams() {
    return(new WindowManager.LayoutParams(
                                          WindowManager.LayoutParams.MATCH_PARENT,
                                          WindowManager.LayoutParams.MATCH_PARENT,
                                          0,
                                          0,
                                          0,
                                          0, PixelFormat.OPAQUE));
  }

  private Context createPresoContext(Display display) {
    Context displayContext=createDisplayContext(display);
    final WindowManager wm=
        (WindowManager)displayContext.getSystemService(WINDOW_SERVICE);

    return(new ContextThemeWrapper(displayContext, getThemeId()) {
      @Override
      public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
          return(wm);
        }

        return(super.getSystemService(name));
      }
    });
  }
}
