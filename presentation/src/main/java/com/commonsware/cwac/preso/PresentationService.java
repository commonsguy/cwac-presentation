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
import android.os.Build;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * A service that drives a presentation from the background. Use this when
 * presentations need to span activities or when they should be occurring even
 * if the app's UI has moved to the background.
 *
 * This is an abstract class -- create a subclass and override getThemeId() and
 * buildPresoView().
 */
public abstract class PresentationService extends Service implements
    PresentationHelper.Listener {
  /**
   * @return the theme to use for driving the resources used by
   * this presentation
   */
  protected abstract int getThemeId();

  /**
   * Override this to provide the UI that goes into the presentation.
   * This works somewhat like a fragment's onCreateView().
   *
   * @param ctxt a Context, in case you need one, but note that it will
   *            <i>not</i> be an activity
   * @param inflater a LayoutInflater, in case you need one for creating
   *                 the UI
   * @return the View that should be shown on the external display
   */
  protected abstract View buildPresoView(Context ctxt,
                                         LayoutInflater inflater);

  private WindowManager wm=null;
  private View presoView=null;
  private PresentationHelper helper=null;

  /**
   * {@inheritDoc}
   */
  @Override
  public IBinder onBind(Intent intent) {
    return(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onCreate() {
    super.onCreate();

    helper=new PresentationHelper(this, this);
    helper.onResume();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDestroy() {
    helper.onPause();

    super.onDestroy();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void showPreso(Display display) {
    Context presoContext=createPresoContext(display);
    LayoutInflater inflater=LayoutInflater.from(presoContext);

    wm=
        (WindowManager)presoContext.getSystemService(Context.WINDOW_SERVICE);

    presoView=buildPresoView(presoContext, inflater);
    wm.addView(presoView, buildLayoutParams());
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * Returns the window type to use. The default implementation will use
   * TYPE_TOAST on Android 7.0 and lower and TYPE_SYSTEM_ALERT on Android
   * 7.1+ and TYPE_APPLICATION_OVERLAY on Android 8.0 and higher. If you are using Cast Remote Display, override this and return
   * TYPE_PRIVATE_PRESENTATION (note: untested).
   *
   * @return a window type (e.g., TYPE_TOAST)
   */
  protected int getWindowType() {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
      return WindowManager.LayoutParams.TYPE_TOAST;
    }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    } else  {
      return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

  }

  protected WindowManager.LayoutParams buildLayoutParams() {
    return(new WindowManager.LayoutParams(
                                          WindowManager.LayoutParams.MATCH_PARENT,
                                          WindowManager.LayoutParams.MATCH_PARENT,
                                          0,
                                          0,
                                          getWindowType(),
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
