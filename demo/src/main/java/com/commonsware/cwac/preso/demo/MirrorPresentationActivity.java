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

package com.commonsware.cwac.preso.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import com.commonsware.cwac.preso.MirrorPresentationFragment;
import com.commonsware.cwac.preso.MirroringWebViewFragment;
import com.commonsware.cwac.preso.PresentationFragment;
import com.commonsware.cwac.preso.PresentationHelper;

public class MirrorPresentationActivity extends Activity implements
    PresentationHelper.Listener {
  PresentationFragment preso=null;
  PresentationHelper helper=null;
  MirroringWebViewFragment source=null;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.mirror_presentation);

    helper=new PresentationHelper(this, this);
    source=
        (MirroringWebViewFragment)getFragmentManager().findFragmentById(R.id.source);
    source.getWebView().getSettings().setJavaScriptEnabled(true);
    source.getWebView().loadUrl("http://commonsware.com");
  }

  @Override
  public void onResume() {
    super.onResume();
    helper.onResume();
  }

  @Override
  public void onPause() {
    helper.onPause();
    super.onPause();
  }

  @Override
  public void clearPreso(boolean switchToInline) {
    if (preso != null) {
      preso.dismiss();
      preso=null;
    }
  }

  @Override
  public void showPreso(Display display) {
    preso=buildPreso(display);
    preso.show(getFragmentManager(), "preso");
  }

  private PresentationFragment buildPreso(Display display) {
    MirrorPresentationFragment result=
        MirrorPresentationFragment.newInstance(this, display);

    source.setMirror(result);

    return(result);
  }
}
