package com.hitenderpannu.slidingmenulibrary.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.hitenderpannu.slidingmenulibrary.TouchDisableView;

public class SlideAnimationHelper {
  public static float getTargetScale(TouchDisableView viewActivity, float currentRawX, float lastRawX,
      float screenWidth) {
    float scaleFloatX = ((currentRawX - lastRawX) / screenWidth) * 0.5f;

    float targetScale = viewActivity.getScaleX() - scaleFloatX;
    targetScale = targetScale > 1.0f ? 1.0f : targetScale;
    targetScale = targetScale < 0.5f ? 0.5f : targetScale;
    return targetScale;
  }

  private static AnimatorSet buildScaleUpAnimation(View target, float targetScaleX,
      float targetScaleY, int animationDuration) {

    AnimatorSet scaleUp = new AnimatorSet();
    scaleUp.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
        ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));
    scaleUp.setDuration(animationDuration);
    return scaleUp;
  }

  private static AnimatorSet buildScaleDownAnimation(View target, float targetScaleX,
      float targetScaleY, int animationDuration) {

    AnimatorSet scaleDown = new AnimatorSet();
    scaleDown.playTogether(ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
        ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

    scaleDown.setInterpolator(new DecelerateInterpolator());
    scaleDown.setDuration(animationDuration);
    return scaleDown;
  }

  private static AnimatorSet buildMenuAnimation(View target, float alpha, int animationDuration) {

    AnimatorSet alphaAnimation = new AnimatorSet();
    alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha", alpha));

    alphaAnimation.setDuration(animationDuration);
    return alphaAnimation;
  }

  public static AnimatorSet buildMenuOpenAnimation(View activityView, View navigationView,
      float scaleValue, int animationDuration) {
    AnimatorSet scaleDown_activity =
        buildScaleDownAnimation(activityView, scaleValue, scaleValue, animationDuration);
    AnimatorSet scaleDownMenu =
        buildScaleDownAnimation(navigationView, 1.0f, 1.0f, animationDuration);

    AnimatorSet menuAlpha = buildMenuAnimation(navigationView, 1f, animationDuration);
    scaleDown_activity.playTogether(scaleDownMenu, menuAlpha);
    return scaleDown_activity;
  }

  public static AnimatorSet buildMenuCloseAnimation(View activityView, View navigationView,
      float scaleValue, int animationDuration) {
    AnimatorSet scaleUpActivity =
        buildScaleUpAnimation(activityView, 1.0f, 1.0f, animationDuration);
    AnimatorSet scaleUpMenu =
        buildScaleUpAnimation(navigationView, 2 - scaleValue, 2 - scaleValue, animationDuration);

    AnimatorSet menuAlpha = buildMenuAnimation(navigationView, 0f, animationDuration);
    scaleUpActivity.playTogether(scaleUpMenu, menuAlpha);
    return scaleUpActivity;
  }
}
