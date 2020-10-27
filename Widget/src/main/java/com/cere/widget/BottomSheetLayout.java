package com.cere.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * Created by CheRevir on 2020/10/26
 */
public class BottomSheetLayout extends FrameLayout {
    private View mBottomSheetView;
    /**
     * 展开高度
     */
    private int height = 400;
    /**
     * 状态
     */
    private int state = -1;
    /**
     * 折叠状态 {@value}
     */
    public static final int STATE_COLLAPSED = -1;
    /**
     * 展开状态 {@value}
     */
    public static final int STATE_EXPANSION = 1;

    private OnBottomSheetStateChangeListener mChangeListener;

    public BottomSheetLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int bottomCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.gravity == Gravity.BOTTOM) {
                bottomCount++;
                mBottomSheetView = child;
                if (lp.height > 0) {
                    height = lp.height;
                    lp.height = 0;
                }
            }
        }
        if (bottomCount == 0) {
            throw new IllegalArgumentException("child view no layout_gravity bottom");
        } else if (bottomCount > 1) {
            throw new IllegalArgumentException("child view layout_gravity bottom count > 1");
        }
    }

    private void collapsed() {
        state = STATE_COLLAPSED;
        ValueAnimator animator = ValueAnimator.ofInt(height, 0);
        animator.addUpdateListener(animation -> {
            int height = (int) animation.getAnimatedValue();
            LayoutParams lp = (LayoutParams) mBottomSheetView.getLayoutParams();
            lp.height = height;
            mBottomSheetView.setLayoutParams(lp);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                LayoutParams child_lp = (LayoutParams) child.getLayoutParams();
                if (child_lp.gravity != Gravity.BOTTOM) {
                    getChildAt(i).setTranslationY(-height);
                }
            }
        });
        animator.setDuration(500);
        animator.start();
        if (mChangeListener != null) mChangeListener.onBottomSheetStateChange(state);
    }

    private void expansion() {
        state = STATE_EXPANSION;
        ValueAnimator animator = ValueAnimator.ofInt(0, height);
        animator.addUpdateListener(animation -> {
            int height = (int) animation.getAnimatedValue();
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.gravity == Gravity.BOTTOM) {
                    lp.height = height;
                    child.setLayoutParams(lp);
                } else {
                    child.setTranslationY(-height);
                }
            }
        });
        animator.setDuration(500);
        animator.start();
        if (mChangeListener != null) mChangeListener.onBottomSheetStateChange(state);
    }

    /**
     * 获取当前状态
     *
     * @return {@link #STATE_COLLAPSED}, {@link #STATE_EXPANSION}
     */
    public int getState() {
        return state;
    }

    /**
     * 设置BottomSheet高度
     *
     * @param height Default 400
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 设置折叠/展开
     *
     * @param state {@link #STATE_COLLAPSED}, {@link #STATE_EXPANSION}
     */
    public void setState(int state) {
        if (state == STATE_COLLAPSED) {
            collapsed();
        } else if (state == STATE_EXPANSION) {
            expansion();
        } else {
            throw new IllegalArgumentException("BottomSheet state unknown " + state);
        }
    }

    /**
     * 设置BottomSheet状态监听器
     *
     * @param listener {@link OnBottomSheetStateChangeListener}
     */
    public void setOnBottomSheetStateChangeListener(@NonNull OnBottomSheetStateChangeListener listener) {
        this.mChangeListener = listener;
    }

    /**
     * BottomSheet状态监听器
     */
    public interface OnBottomSheetStateChangeListener {
        /**
         * 当BottomSheet状态发生改变
         *
         * @param state {@link #STATE_COLLAPSED}, {@link #STATE_EXPANSION}
         */
        void onBottomSheetStateChange(int state);
    }
}
