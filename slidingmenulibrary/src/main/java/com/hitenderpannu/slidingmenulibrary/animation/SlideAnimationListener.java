package com.hitenderpannu.slidingmenulibrary.animation;

import android.animation.Animator;

public class SlideAnimationListener implements Animator.AnimatorListener {

  AnimationCallback animationCallback;

  public SlideAnimationListener(AnimationCallback callback) {
    animationCallback = callback;
  }

  @Override public void onAnimationStart(Animator animation) {

  }

  @Override public void onAnimationEnd(Animator animation) {
    animationCallback.onMenuSlidingAnimationEnd();
  }

  @Override public void onAnimationCancel(Animator animation) {

  }

  @Override public void onAnimationRepeat(Animator animation) {

  }

  public interface AnimationCallback {
    void onMenuSlidingAnimationEnd();
  }
}
