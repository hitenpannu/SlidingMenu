package com.hitenderpannu.slidingmenulibrary;

import android.app.Activity;

public class SlidingMenuBuilder {

  private int chosenDirection = SlidingMenuView.SLIDE_DIRECTION_LEFT;
  private int animationDuration = SlidingMenuView.DEFAULT_ANIMATION_TIME;
  private float scaleValue = SlidingMenuView.DEFAULT_SCALE;
  private int navigationMenuId;
  private Activity activityToSlide;
  private SlideMenuStateListener menuStateListener;

  public SlidingMenuBuilder() {
  }

  public SlidingMenuBuilder setActivityToSlide(Activity activity) {
    this.activityToSlide = activity;
    return this;
  }

  public SlidingMenuBuilder setDirectionToSlide(int direction) {
    this.chosenDirection = direction;
    return this;
  }

  public SlidingMenuBuilder setDurationForSlideToComplete(int duration) {
    this.animationDuration = duration;
    return this;
  }

  public SlidingMenuBuilder setScaleValue(float scale) {
    this.scaleValue = scale;
    return this;
  }

  public SlidingMenuBuilder setMenuId(int id) {
    this.navigationMenuId = id;
    return this;
  }

  public SlidingMenuBuilder setMenuStateListener(SlideMenuStateListener stateListener) {
    this.menuStateListener = stateListener;
    return this;
  }

  public SlidingMenuView build() {
    return new SlidingMenuView(activityToSlide, chosenDirection, animationDuration,
        navigationMenuId, scaleValue, menuStateListener, activityToSlide);
  }
}
