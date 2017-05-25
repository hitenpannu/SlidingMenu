package com.hitenderpannu.slidingmenulibrary;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.hitenderpannu.slidingmenulibrary.animation.SlideAnimationHelper;
import com.hitenderpannu.slidingmenulibrary.animation.SlideAnimationListener;
import com.hitenderpannu.slidingmenulibrary.databinding.SlidingMenuBinding;

public class SlidingMenuView extends FrameLayout
    implements TouchEventHandler.TouchEventCallback, SlideAnimationListener.AnimationCallback {
  public static final int SLIDE_DIRECTION_RIGHT = 1;
  public static final int SLIDE_DIRECTION_LEFT = 0;
  public static final int DEFAULT_ANIMATION_TIME = 250;
  public static final float DEFAULT_SCALE = 0.75f;
  private static final int THRESOLD_PERCENTAGE = 30;

  private int chosenDirection;
  private int animationDuration;
  private float activityScaleRatio;
  private int screenWidth;
  private int screenHeight;
  private boolean isMenuOpen;
  private boolean isInSlidingState;
  private SlideMenuStateListener menuStateListener;
  private Activity activityToSlide;
  private TouchDisableView activityToSlideView;
  private SlidingMenuBinding parentBinding;
  private View navigationView;
  private SlideAnimationListener animationListener;
  private TouchEventHandler touchEventHandler;

  public SlidingMenuView(Context context, int chosenDirection, int animationDuration,
      int navigationMenuId, float activityScaleRatio, SlideMenuStateListener listener,
      Activity activityToSlide) {
    super(context);
    this.chosenDirection = chosenDirection;
    this.animationDuration = animationDuration;
    this.activityScaleRatio = activityScaleRatio;
    this.menuStateListener = listener;
    this.activityToSlide = activityToSlide;

    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    parentBinding = DataBindingUtil.inflate(inflater, R.layout.sliding_menu, this, true);
    navigationView = LayoutInflater.from(context).inflate(navigationMenuId, this, false);
    parentBinding.svMenuHolder.addView(navigationView);
  }

  public void initializeView() {
    getScreenDimensions();
    rearrangeActivityView();
    //provideAppropriateMarginToNavigationMenu();
    setScaleDirection();

    animationListener = new SlideAnimationListener(this);
    touchEventHandler = new TouchEventHandler(chosenDirection, screenWidth, this);
  }

  private void getScreenDimensions() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activityToSlide.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    screenHeight = displayMetrics.heightPixels;
    screenWidth = displayMetrics.widthPixels;
  }

  /**
   * This is to expand your navigation view to fill the screen but to use it
   * you need to have your navigation view in linearLayout
   *
   * or you can override to suite your needs as always
   */

  //todo make margin dynamic
  protected void provideAppropriateMarginToNavigationMenu() {
    int[] location = new int[2];
    activityToSlideView.getLocationOnScreen(location);
    int x = (int) Math.ceil(screenWidth * 0.12);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
    params.weight = 1;
    params.setMarginEnd(x);
    navigationView.setLayoutParams(params);
  }

  private void rearrangeActivityView() {
    ViewGroup viewDecor = (ViewGroup) activityToSlide.getWindow().getDecorView();
    activityToSlideView = new TouchDisableView(activityToSlide);
    View content = viewDecor.getChildAt(0);
    viewDecor.removeViewAt(0);
    activityToSlideView.setContent(content);
    if (activityToSlideView.getBackground() == null) {
      activityToSlideView.setBackgroundColor(Color.WHITE);
    }
    addView(activityToSlideView);
    viewDecor.addView(this, 0);
  }

  public void setBackgroundResource(int resourceId) {
    parentBinding.ivBackground.setImageResource(resourceId);
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    Log.e("MotionEvent", String.valueOf(ev.getAction()));
    boolean touchResponse = touchEventHandler.updateMotionEvent(ev);
    return touchResponse || super.dispatchTouchEvent(ev);
  }

  @Override public void slideActivity(float to, float from) {
    int percentage = (int) ((from / screenWidth) * 100);
    float targetScale;
    if (chosenDirection == SLIDE_DIRECTION_LEFT) {
      targetScale = slideToLeft(to, from, percentage);
    } else {
      targetScale = slideToRight(to, from, percentage);
    }

    if (targetScale == -1f) return;

    activityToSlideView.setScaleX(targetScale);
    activityToSlideView.setScaleY(targetScale);
    navigationView.setScaleX(1 - activityScaleRatio + targetScale);
    navigationView.setScaleY(1 - activityScaleRatio + targetScale);
    navigationView.setAlpha((1 - targetScale) * 5.0f);
  }

  private float slideToRight(float to, float from, int percentage) {
    if (percentage <= 100 - THRESOLD_PERCENTAGE && !isMenuOpen && !isInSlidingState) return -1f;
    isInSlidingState = true;
    float targetScale =
        SlideAnimationHelper.getTargetScale(activityToSlideView, from, to, screenWidth);

    if (targetScale <= activityScaleRatio) {
      targetScale = activityScaleRatio;
    }

    return targetScale;
  }

  private float slideToLeft(float to, float from, int percentage) {
    if (percentage >= THRESOLD_PERCENTAGE && !isMenuOpen && !isInSlidingState) return -1f;
    isInSlidingState = true;
    float targetScale =
        SlideAnimationHelper.getTargetScale(activityToSlideView, to, from, screenWidth);

    if (targetScale <= activityScaleRatio) {
      targetScale = activityScaleRatio;
    }

    return targetScale;
  }

  @Override public void toggleMenu() {
    float currentActivityScaleX = activityToSlideView.getScaleX();
    if (currentActivityScaleX == 1f) setScaleDirection();
    isInSlidingState = false;
    if (isMenuOpen) {
      if (currentActivityScaleX > 0.56f) {
        closeMenu();
      } else {
        openMenu();
      }
    } else {
      if (currentActivityScaleX < 0.94f) {
        openMenu();
      } else {
        closeMenu();
      }
    }
  }

  public void openMenu() {
    setScaleDirection();
    AnimatorSet menuOpenAnimation =
        SlideAnimationHelper.buildMenuOpenAnimation(activityToSlideView, navigationView,
            activityScaleRatio, animationDuration);
    menuOpenAnimation.addListener(animationListener);
    menuOpenAnimation.start();
  }

  public void closeMenu() {
    AnimatorSet menuCloseAnimation =
        SlideAnimationHelper.buildMenuCloseAnimation(activityToSlideView, navigationView,
            activityScaleRatio, animationDuration);
    menuCloseAnimation.addListener(animationListener);
    menuCloseAnimation.start();
  }

  private void setScaleDirection() {
    float pivotX = screenWidth * (chosenDirection == SLIDE_DIRECTION_RIGHT ? -2.5f : 3.5f);
    float pivotY = screenHeight * 0.5f;
    activityToSlideView.setPivotX(pivotX);
    activityToSlideView.setPivotY(pivotY);
  }

  @Override public void onMenuSlidingAnimationEnd() {
    if (isMenuOpen) {
      activityToSlideView.setTouchDisable(true);
      activityToSlideView.setOnClickListener((view) -> closeMenu());
      menuStateListener.menuIsOpened();
      isMenuOpen = true;
    } else {
      activityToSlideView.setTouchDisable(false);
      activityToSlideView.setOnClickListener(null);
      menuStateListener.menuIsClosed();
      isMenuOpen = false;
    }
  }

  /**
   * Returns right menu view so you can findViews and do whatever you want with
   */

  @Override protected boolean fitSystemWindows(Rect insets) {
    // Applies the content insets to the view's padding, consuming that
    // content (modifying the insets to be 0),
    // and returning true. This behavior is off by default and can be
    // enabled through setFitsSystemWindows(boolean)
    // in api14+ devices.

    // This is added to fix soft navigationBar's overlapping to content above LOLLIPOP
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int bottomPadding = activityToSlideView.getPaddingBottom() + insets.bottom;
      bottomPadding += getNavigationBarHeight();
      this.setPadding(activityToSlideView.getPaddingLeft() + insets.left,
          activityToSlideView.getPaddingTop() + insets.top,
          activityToSlideView.getPaddingRight() + insets.right, bottomPadding);
      insets.left = insets.top = insets.right = insets.bottom = 0;
      return true;
    } else {
      return false;
    }
  }

  private int getNavigationBarHeight() {
    Resources resources = getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return resources.getDimensionPixelSize(resourceId);
    }
    return 0;
  }
}
