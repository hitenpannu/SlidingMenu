package com.hitenderpannu.slidingmenulibrary;

import android.view.MotionEvent;

public class TouchEventHandler {

  private static final int STATE_PRESSED_DOWN = 0;
  private static final int STATE_PRESSED_UP = 1;
  private static final int STATE_MOVE_HORIZONTAL = 2;
  private static final int STATE_MOVE_VERTICAL = 3;
  private static final int THRESOLD_PERCENTAGE = 30;

  private float lastX;
  private float lastY;
  private float lastRawX;
  private int pressedState;

  private int chosenDirection;
  private float screenWidth;
  private TouchEventCallback callback;

  public TouchEventHandler(int chosenDirection, float screenWidth, TouchEventCallback callback) {
    this.chosenDirection = chosenDirection;
    this.screenWidth = screenWidth;
    this.callback = callback;
  }

  public boolean updateMotionEvent(MotionEvent ev) {

    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastX = ev.getX();
        lastY = ev.getY();
        pressedState = STATE_PRESSED_DOWN;
        break;
      case MotionEvent.ACTION_MOVE:
        if (pressedState != STATE_PRESSED_DOWN && pressedState != STATE_MOVE_HORIZONTAL) break;

        int xOffset = (int) (ev.getX() - lastX);
        int yOffset = (int) (ev.getY() - lastY);

        if (pressedState == STATE_PRESSED_DOWN) {
          if (yOffset > 25 || yOffset < -25) {
            pressedState = STATE_MOVE_VERTICAL;
            break;
          }
          if (xOffset < -50 || xOffset > 50) {
            pressedState = STATE_MOVE_HORIZONTAL;
            ev.setAction(MotionEvent.ACTION_CANCEL);
          }
        } else if (pressedState == STATE_MOVE_HORIZONTAL) {
          callback.slideActivity(ev.getRawX(), lastRawX);
          lastRawX = ev.getRawX();
        }
        break;
      case MotionEvent.ACTION_UP:
        if (pressedState != STATE_MOVE_HORIZONTAL) break;
        callback.toggleMenu();
        break;

      default:
        ev.setAction(MotionEvent.ACTION_CANCEL);
    }
    return true;
  }

  interface TouchEventCallback {
    void slideActivity(float to, float from);

    void toggleMenu();
  }
}
