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

public class PresentationHelper implements
    DisplayManager.DisplayListener {
  public interface Listener {
    void showPreso(Display display);

    void clearPreso(boolean switchToInline);
  }

  private Listener listener=null;
  private DisplayManager mgr=null;
  private Display current=null;
  private boolean isFirstRun=true;
  private boolean isEnabled=true;

  public PresentationHelper(Context ctxt, Listener listener) {
    this.listener=listener;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      mgr=
          (DisplayManager)ctxt.getSystemService(Context.DISPLAY_SERVICE);
    }
  }

  public void onResume() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      handleRoute();
      mgr.registerDisplayListener(this, null);
    }
  }

  public void onPause() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      listener.clearPreso(false);
      current=null;

      mgr.unregisterDisplayListener(this);
    }
  }

  public void enable() {
    isEnabled=true;
    handleRoute();
  }

  public void disable() {
    isEnabled=false;

    if (current != null) {
      listener.clearPreso(true);
      current=null;
    }
  }

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

  @Override
  public void onDisplayAdded(int displayId) {
    handleRoute();
  }

  @Override
  public void onDisplayChanged(int displayId) {
    handleRoute();
  }

  @Override
  public void onDisplayRemoved(int displayId) {
    handleRoute();
  }
}