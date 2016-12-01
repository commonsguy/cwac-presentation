/***
  Copyright (c) 2013 CommonsWare, LLC
  
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

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.view.Display;

/**
 * A class to assist in managing a Presentation.
 *
 * To work properly, you need to forward the onResume() and onPause() events
 * from your activity to the helper.
 *
 * The helper starts in an enabled state. Call disable() to disable it (and
 * stop showing presentations) and enable() to re-enable it.
 */
public class PresentationHelper implements DisplayManager.DisplayListener {
  /**
   * A callback to let you know when it is time to show a Presentation
   * or stop showing that Presentation.
   */
  public interface Listener {
    void showPreso(Display display);

    void clearPreso(boolean switchToInline);
  }

  private Listener listener=null;
  private DisplayManager mgr=null;
  private Display current=null;
  private boolean isFirstRun=true;
  private boolean isEnabled=true;

  /**
   * Basic constructor.
   *
   * @param ctxt a Context, typically the activity that is planning on showing
   *             the Presentation
   * @param listener the callback for show/hide events
   */
  public PresentationHelper(Context ctxt, Listener listener) {
    this.listener=listener;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      mgr=
          (DisplayManager)ctxt.getSystemService(Context.DISPLAY_SERVICE);
    }
  }

  /**
   * Call this from onResume() of your activity, so we can determine what
   * changes need to be made to the Presentation, if any
   */
  public void onResume() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      handleRoute();
      mgr.registerDisplayListener(this, null);
    }
  }

  /**
   * Call this from onPause() of your activity, so we can determine what
   * changes need to be made to the Presentation, if any
   */
  public void onPause() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      listener.clearPreso(false);
      current=null;

      mgr.unregisterDisplayListener(this);
    }
  }

  /**
   * Call this to re-enable the helper after having previously called disable()
   */
  public void enable() {
    isEnabled=true;
    handleRoute();
  }

  /**
   * Call this to stop showing presentations, even if there is an associated
   * Display for displaying them
   */
  public void disable() {
    isEnabled=false;

    if (current != null) {
      listener.clearPreso(true);
      current=null;
    }
  }

  /**
   * @return whether or not this helper is enabled, based on enable() and
   * disable() calls
   */
  public boolean isEnabled() {
    return(isEnabled);
  }

  private void handleRoute() {
    if (isEnabled()) {
      Display[] displays=
          mgr.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

      if (displays.length == 0) {
        if (current != null || isFirstRun) {
          listener.clearPreso(true);
          current=null;
        }
      }
      else {
        Display display=displays[0];

        if (display != null && display.isValid()) {
          if (current == null) {
            listener.showPreso(display);
            current=display;
          }
          else if (current.getDisplayId() != display.getDisplayId()) {
            listener.clearPreso(true);
            listener.showPreso(display);
            current=display;
          }
          else {
            // no-op: should already be set
          }
        }
        else if (current != null) {
          listener.clearPreso(true);
          current=null;
        }
      }

      isFirstRun=false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDisplayAdded(int displayId) {
    handleRoute();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDisplayChanged(int displayId) {
    handleRoute();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDisplayRemoved(int displayId) {
    handleRoute();
  }
}