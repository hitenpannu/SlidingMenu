package com.hitenderpannu.slidingmenudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.hitenderpannu.slidingmenulibrary.SlideMenuStateListener;
import com.hitenderpannu.slidingmenulibrary.SlidingMenuBuilder;
import com.hitenderpannu.slidingmenulibrary.SlidingMenuView;

public class MainActivity extends AppCompatActivity implements SlideMenuStateListener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    SlidingMenuView menuView = new SlidingMenuBuilder().setActivityToSlide(this)
        .setMenuId(R.layout.drawer)
        .setDirectionToSlide(SlidingMenuView.SLIDE_DIRECTION_LEFT)
        .setDurationForSlideToComplete(250)
        .setScaleValue(0.75f)
        .setMenuStateListener(this)
        .build();

    menuView.initializeView();
    menuView.setBackgroundResource(R.drawable.background);
  }

  @Override public void menuIsOpened() {

  }

  @Override public void menuIsClosed() {

  }
}
