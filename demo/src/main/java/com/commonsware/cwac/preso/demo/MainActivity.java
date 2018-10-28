/***
  Copyright (c) 2013-2018 CommonsWare, LLC
  
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

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.commonsware.cwac.preso.PresentationFragment;
import com.commonsware.cwac.preso.PresentationHelper;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements
    PresentationHelper.Listener {
  PresentationFragment preso=null;
  View inline=null;
  TextView prose=null;
  PresentationHelper helper=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    inline=findViewById(R.id.preso);
    prose=findViewById(R.id.prose);

    helper=new PresentationHelper(this, this);
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
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.mirror_fragment:
        startActivity(new Intent(this, MirrorFragmentActivity.class));
        return(true);

      case R.id.mirror_preso:
        startActivity(new Intent(this, MirrorPresentationActivity.class));
        return(true);

      case R.id.nested:
        startActivity(new Intent(this, NestedFragmentActivity.class));
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void clearPreso(boolean switchToInline) {
    if (switchToInline) {
      inline.setVisibility(View.VISIBLE);
      prose.setText(R.string.primary);
      getSupportFragmentManager().beginTransaction()
                          .add(R.id.preso, buildPreso(null)).commit();
    }

    if (preso != null) {
      preso.dismiss();
      preso=null;
    }
  }

  @Override
  public void showPreso(Display display) {
    if (inline.getVisibility() == View.VISIBLE) {
      inline.setVisibility(View.GONE);
      prose.setText(R.string.secondary);

      Fragment f=getFragmentManager().findFragmentById(R.id.preso);

      getFragmentManager().beginTransaction().remove(f).commit();
    }

    preso=buildPreso(display);
    preso.show(getSupportFragmentManager(), "preso");
  }

  private PresentationFragment buildPreso(Display display) {
    return(SamplePresentationFragment.newInstance(this, display,
                                                  "https://commonsware.com"));
  }
}
