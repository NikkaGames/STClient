package ge.nikka.stclient.ui.home;

import android.animation.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.*;

public class EditTextCursorWatcher extends EditText {
    public EditTextCursorWatcher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ValueAnimator animator = ValueAnimator.ofFloat(1.f, 0.95f);
                        animator.setDuration(150);
                        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            EditTextCursorWatcher.this.setScaleX(val);
                            EditTextCursorWatcher.this.setScaleY(val);
                        });
                        animator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ValueAnimator animator2 = ValueAnimator.ofFloat(0.95f, 1.f);
                        animator2.setDuration(150);
                        animator2.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator2.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            EditTextCursorWatcher.this.setScaleX(val);
                            EditTextCursorWatcher.this.setScaleY(val);
                        });
                        animator2.start();
                        break;
                }
                return false;
            }
        });
    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ValueAnimator animator = ValueAnimator.ofFloat(1.f, 0.95f);
                        animator.setDuration(150);
                        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            EditTextCursorWatcher.this.setScaleX(val);
                            EditTextCursorWatcher.this.setScaleY(val);
                        });
                        animator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ValueAnimator animator2 = ValueAnimator.ofFloat(0.95f, 1.f);
                        animator2.setDuration(150);
                        animator2.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator2.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            EditTextCursorWatcher.this.setScaleX(val);
                            EditTextCursorWatcher.this.setScaleY(val);
                        });
                        animator2.start();
                        break;
                }
                return false;
            }
        });
    }

    public EditTextCursorWatcher(Context context) {
        super(context);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ValueAnimator animator = ValueAnimator.ofFloat(1.f, 0.95f);
                        animator.setDuration(150);
                        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            EditTextCursorWatcher.this.setScaleX(val);
                            EditTextCursorWatcher.this.setScaleY(val);
                        });
                        animator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ValueAnimator animator2 = ValueAnimator.ofFloat(0.95f, 1.f);
                        animator2.setDuration(150);
                        animator2.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator2.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            EditTextCursorWatcher.this.setScaleX(val);
                            EditTextCursorWatcher.this.setScaleY(val);
                        });
                        animator2.start();
                        break;
                }
                return false;
            }
        });
    }

    @Override   
    protected void onSelectionChanged(int selStart, int selEnd) {
        ValueAnimator anim2 = ValueAnimator.ofFloat(1f, 1.01f);
        anim2.setDuration(50);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                EditTextCursorWatcher.this.setScaleX((Float)animation.getAnimatedValue());
                EditTextCursorWatcher.this.setScaleY((Float)animation.getAnimatedValue());
            }
        });
        anim2.setRepeatCount(1);
        anim2.setRepeatMode(ValueAnimator.REVERSE);
        anim2.start();
    }
}