package com.gdurmaz.myapplication.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.gdurmaz.myapplication.R;
import com.gdurmaz.myapplication.ui.helper.ViewDisplay;
import com.gdurmaz.myapplication.ui.listener.ColorGenerator;
import com.gdurmaz.myapplication.ui.listener.ViewRectProviderHelper;

import java.util.Random;
import java.util.logging.Handler;

public class BasicScrollerView extends FrameLayout {
    private static final String TAG = BasicScrollerView.class.getSimpleName();

    private float oldX = 0f;

    private float oldY = 0f;

    private static int CHILD_COUNT = 0;

    private final int LAYOUT_FRACTION = 4;

    private int limit;

    private LinearLayout childrenView;

    private final Random random = new Random();

    private final ColorGenerator colorGenerator = () -> Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
    );

    private final ViewRectProviderHelper viewRectProviderHelper = (rad) -> new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            int cornerRadius = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, rad, ViewDisplay.getDisplayMetrics(getContext()));
            outline.setRoundRect(0, 0, childrenView.getWidth(), childrenView.getHeight(), cornerRadius);
        }
    };

    public BasicScrollerView(Context context) {
        super(context);
        initView(context);
    }

    public BasicScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BasicScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setViewOrientationByCenter();
    }

    private void initView(Context context) {
        Log.i(TAG, "initView() -- Enter.");
        childrenView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.scroller_view_children, this, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        childrenView.setLayoutParams(layoutParams);
        childrenView.setOutlineProvider(viewRectProviderHelper.changeViewRect(50));
        childrenView.setBackgroundColor(colorGenerator.generateColor());
        childrenView.setClipToOutline(true);

        childrenView.setOnLongClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getContext().startActivity(intent);
            return true;
        });

        addView(childrenView);

        int isChildVisible = childrenView.getVisibility();
        if (View.VISIBLE != isChildVisible) {
            childrenView.setVisibility(View.VISIBLE);
        }

        setViewOrientationByCenter();
    }

    private void  setViewOrientationByCenter() {
        int centerX = ((getWidth() - childrenView.getWidth()) / 2);
        int centerY = ((getHeight() - childrenView.getHeight()) / 2);
        scrollTo(0, 0);
        scrollBy(-centerX, -centerY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DisplayMetrics displayMetrics = ViewDisplay.getDisplayMetrics(getContext());
        if (displayMetrics != null) {
            limit = (displayMetrics.heightPixels / LAYOUT_FRACTION) * 3;
        }
        CHILD_COUNT = getChildCount();
        Log.i(TAG, "onAttachedToWindow() -- limit = " + limit);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        limit = 0;
        CHILD_COUNT = 0;
        scrollTo(0, 0);
        removeAllViews();
        invalidate();
        Log.i(TAG, "onDetachedFromWindow() -- limit = " + limit);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent() -- Enter.");
        int action = ev.getAction();
        float x = ev.getX();
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent() -- Enter ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent() -- Enter ACTION_MOVE");
                float deltaX = Math.abs(x - oldX);
                float deltaY = Math.abs(y - oldY);
                if (deltaY > 0 && deltaY > getTop()) {
                    Log.d(TAG, "onInterceptTouchEvent() -- Enter to if statement -- = " + ((int) deltaX) + ":" + ((int) deltaY));
                    oldX = deltaX;
                    oldY = deltaY;
                    scrollTo((int) (getX() - ev.getRawX()), (int) (getY() - ev.getRawY()));
                    invalidate();
                }
                if (limit <= 0) {
                    Log.i(TAG, "onInterceptTouchEvent() -- limit <= 0");
                    return true;
                }
                if (ev.getRawY() > limit) {
                    Log.i(TAG, "onInterceptTouchEvent() -- ev.getRawY() > limit = " + ev.getRawY() + " > " + limit);
                    for (int i = 0; i < CHILD_COUNT; i++) {
                        View childView = getChildAt(i);
                        removeView(childView);
                    }
                    CHILD_COUNT = 0;
                    return true;
                }
                if (CHILD_COUNT < 1) {
                    Log.i(TAG, "onInterceptTouchEvent() -- CHILD_COUNT = " + CHILD_COUNT);
                    addView(childrenView);
                    childrenView.setBackgroundColor(colorGenerator.generateColor());
                    CHILD_COUNT = getChildCount();
                }
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent() -- Enter ACTION_UP");
                setViewOrientationByCenter();
                return true;
        }
        return super.onTouchEvent(ev);
    }
}
