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
import android.widget.VideoView;
import com.commonsware.cwac.preso.PresentationFragment;

public class VideoPresentationFragment extends PresentationFragment {
  private static final String ARG_PATH="path";
  private static final String STATE_POSITION="pos";

  public static VideoPresentationFragment newInstance(Context ctxt,
                                                      Display display,
                                                      String path) {
    VideoPresentationFragment frag=new VideoPresentationFragment();

    frag.setDisplay(ctxt, display);

    Bundle b=new Bundle();

    b.putString(ARG_PATH, path);
    frag.setArguments(b);

    return(frag);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    VideoView result=new VideoView(getActivity());

    result.setVideoPath(getArguments().getString(ARG_PATH));

    if (savedInstanceState != null) {
      result.seekTo(savedInstanceState.getInt(STATE_POSITION));
    }

    return(result);
  }

  @Override
  public void onResume() {
    super.onResume();

    ((VideoView)getView()).start();
  }

  @Override
  public void onPause() {
    ((VideoView)getView()).pause();

    super.onPause();
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    state.putInt(STATE_POSITION,
                 ((VideoView)getView()).getCurrentPosition());
  }
}
