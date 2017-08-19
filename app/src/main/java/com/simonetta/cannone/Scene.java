package com.simonetta.cannone;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Simonetta on 28/02/17.
 */

public interface Scene {
    public void update();
    public void draw(Canvas canvas);
    public void terminate();
    public void receiveTouch(MotionEvent event);
}
