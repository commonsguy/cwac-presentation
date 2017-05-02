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

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import com.commonsware.cwac.preso.PresentationFragment;

public class NestedPresentationFragment extends PresentationFragment {
  public static NestedPresentationFragment newInstance(Context ctxt,
                                                       Display display) {
    NestedPresentationFragment frag=new NestedPresentationFragment();

    frag.setDisplay(ctxt, display);

    return(frag);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.webfrag, container, false);

    final WebViewFragment wvf=new WebViewFragment();

    getChildFragmentManager().beginTransaction()
                             .add(R.id.nested_web, wvf).commit();

    result.post(new Runnable() {
      @Override
      public void run() {
        wvf.getWebView().loadUrl("https://commonsware.com");
      }
    });

    return(result);
  }
}
