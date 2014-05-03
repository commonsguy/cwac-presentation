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

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.commonsware.cwac.layouts.AspectLockedFrameLayout;
import com.commonsware.cwac.layouts.Mirror;
import com.commonsware.cwac.layouts.MirroringFrameLayout;

abstract public class MirroringFragment extends Fragment {
  abstract protected View onCreateMirroredContent(LayoutInflater inflater,
                                                  ViewGroup container,
                                                  Bundle savedInstanceState);

  private MirroringFrameLayout source=null;
  private AspectLockedFrameLayout aspectLock=null;

  @Override
  final public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
    source=new MirroringFrameLayout(getActivity());
    source.addView(onCreateMirroredContent(inflater, source,
                                           savedInstanceState));

    aspectLock=new AspectLockedFrameLayout(getActivity());
    aspectLock.addView(source,
                       new FrameLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    Gravity.CENTER));

    return(aspectLock);
  }

  public void setMirror(Mirror mirror) {
    source.setMirror(mirror);
    aspectLock.setAspectRatioSource((View)mirror);
  }

  public void setMirror(MirrorPresentationFragment mirrorFragment) {
    Mirror mirror=mirrorFragment.getMirror();

    if (mirror == null) {
      mirrorFragment.setMirroringFragment(this);
    }
    else if (source != null) {
      setMirror(mirror);
    }
  }
  
  public void updateMirror() {
    source.invalidate();
  }
}
