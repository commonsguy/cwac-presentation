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
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.commonsware.cwac.layouts.Mirror;

public class MirrorPresentationFragment extends PresentationFragment {
  private Mirror mirror=null;
  private MirroringFragment source=null;
  
  public static MirrorPresentationFragment newInstance(Context ctxt,
                                                       Display display) {
    MirrorPresentationFragment frag=new MirrorPresentationFragment();

    frag.setDisplay(ctxt, display);

    return(frag);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    mirror=new Mirror(getActivity());
    
    if (source!=null) {
      source.setMirror(mirror);
    }
    
    return(mirror);
  }
  
  Mirror getMirror() {
    return(mirror);
  }
  
  void setMirroringFragment(MirroringFragment source) {
    this.source=source;
  }
}
