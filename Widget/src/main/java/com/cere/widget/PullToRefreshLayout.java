package com.cere.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

/**
 * Created by CheRevir on 2020/10/22
 */
public class PullToRefreshLayout extends FrameLayout {
    private final int mHeaderId;
    private View mHeaderView;
    private final int mBodyId;
    private View mBodyView;
    private final int mFooterId;
    private View mFooterView;

    private int mPullToHeight = 140;

    private OnPullToChangeListener mOnPullToChangeListener;

    private boolean mRefreshing = false;
    private int mCurrentAction = -1;
    private boolean isConfirm = false;
    private boolean isFollowUp = false;
    private boolean isTrigger = false;
    private static final int INVALID = -1;
    private static final int PULL_REFRESH = 0;
    private static final int LOAD_MORE = 1;
    private float mDamping = 0.12f;

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, defStyleAttr, defStyleRes);
        mHeaderId = typedArray.getResourceId(R.styleable.PullToRefreshLayout_header, -1);
        mBodyId = typedArray.getResourceId(R.styleable.PullToRefreshLayout_body, -1);
        mFooterId = typedArray.getResourceId(R.styleable.PullToRefreshLayout_footer, -1);
        typedArray.recycle();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mRefreshing && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        if (mCurrentAction == INVALID) {
            handlerAction();
            return;
        }
        if (mCurrentAction == PULL_REFRESH && mHeaderView != null &&
                mHeaderView.getLayoutParams().height >= mPullToHeight - mPullToHeight * mDamping) {
            mRefreshing = true;
            if (isFollowUp && mOnPullToChangeListener != null) {
                isTrigger = true;
                if (!mOnPullToChangeListener.onChange(PullToLocation.HEADER, PullTaStatus.TRIGGER)) {
                    handlerAction();
                }
            } else {
                handlerAction();
            }
        } else if (mCurrentAction == LOAD_MORE && mFooterView != null &&
                mFooterView.getLayoutParams().height >= mPullToHeight - mPullToHeight * mDamping) {
            mRefreshing = true;
            if (isFollowUp && mOnPullToChangeListener != null) {
                isTrigger = true;
                if (!mOnPullToChangeListener.onChange(PullToLocation.FOOTER, PullTaStatus.TRIGGER)) {
                    handlerAction();
                }
            } else {
                handlerAction();
            }
        } else {
            handlerAction();
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
        int spinnerDy = (int) calculateDistanceY(target, dy);
        if (!isConfirm) {
            if (spinnerDy < 0 && mHeaderView != null && canBodyScrollDown()) {
                mCurrentAction = PULL_REFRESH;
                isConfirm = true;
                if (mOnPullToChangeListener != null)
                    isFollowUp = mOnPullToChangeListener.onChange(PullToLocation.HEADER, PullTaStatus.START);
            } else if (spinnerDy > 0 && mFooterView != null && canBodyScrollUp() && !mRefreshing) {
                mCurrentAction = LOAD_MORE;
                isConfirm = true;
                if (mOnPullToChangeListener != null)
                    isFollowUp = mOnPullToChangeListener.onChange(PullToLocation.FOOTER, PullTaStatus.START);
            }
        }

        if (moveSpinner(-spinnerDy)) {
            if (mHeaderView != null && canBodyScrollDown()
                    && mBodyView.getTranslationY() > 0
                    && dy > 0) {
                consumed[1] += dy;
            } else if (mFooterView != null && canBodyScrollUp()
                    && mBodyView.getTranslationY() < 0
                    && dy < 0) {
                consumed[1] += dy;
            } else {
                consumed[1] += spinnerDy;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeaderId != -1) {
            mHeaderView = findViewById(mHeaderId);
            mHeaderView.getLayoutParams().height = 0;
        }
        if (mBodyId != -1) {
            mBodyView = findViewById(mBodyId);
        } else {
            throw new IllegalArgumentException("没有设置主体Id");
        }
        if (mFooterId != -1) {
            mFooterView = findViewById(mFooterId);
            mFooterView.getLayoutParams().height = 0;
        }
    }

    private double calculateDistanceY(View target, int dy) {
        int viewHeight = target.getMeasuredHeight();
        double ratio = (viewHeight - Math.abs(target.getY())) / 1.0d / viewHeight * mDamping;
        if (ratio <= 0.01d) {
            ratio = 0.01d;
        }
        return ratio * dy;
    }

    private boolean moveSpinner(float distanceY) {
        if (mRefreshing) {
            return false;
        }
        if (mHeaderView != null && canBodyScrollDown() && mCurrentAction == PULL_REFRESH) {
            LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
            lp.height += distanceY;
            if (lp.height < 0) {
                lp.height = 0;
            }
            if (lp.height == 0) {
                isConfirm = false;
                mCurrentAction = INVALID;
            }
            mHeaderView.setLayoutParams(lp);
            mBodyView.setTranslationY(lp.height);
            return true;
        } else if (mFooterView != null && canBodyScrollUp() && mCurrentAction == LOAD_MORE) {
            LayoutParams lp = (LayoutParams) mFooterView.getLayoutParams();
            lp.height -= distanceY;
            if (lp.height < 0) {
                lp.height = 0;
            }
            if (lp.height == 0) {
                isConfirm = false;
                mCurrentAction = INVALID;
            }
            mFooterView.setLayoutParams(lp);
            mBodyView.setTranslationY(-lp.height);
            return true;
        }
        return false;
    }

    private void handlerAction() {
        if (mHeaderView != null && mCurrentAction == PULL_REFRESH) {
            int height = mHeaderView.getLayoutParams().height;
            if (height > 0) {
                resetHeaderView(height);
            }
        } else if (mFooterView != null && mCurrentAction == LOAD_MORE) {
            int height = mFooterView.getLayoutParams().height;
            if (height > 0) {
                resetFooterView(height);
            }
        }
    }

    private void resetHeaderView(int headerViewHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(headerViewHeight, 0);
        animator.addUpdateListener(animation -> {
            LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
            lp.height = (int) animation.getAnimatedValue();
            mHeaderView.setLayoutParams(lp);
            mBodyView.setTranslationY(lp.height);
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isFollowUp && isTrigger && mOnPullToChangeListener != null)
                    mOnPullToChangeListener.onChange(PullToLocation.HEADER, PullTaStatus.DONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRefreshing = false;
                if (isFollowUp && isTrigger && mOnPullToChangeListener != null)
                    mOnPullToChangeListener.onChange(PullToLocation.HEADER, PullTaStatus.END);
                isTrigger = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void resetFooterView(int footerViewHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(footerViewHeight, 0);
        animator.addUpdateListener(animation -> {
            LayoutParams lp = (LayoutParams) mFooterView.getLayoutParams();
            lp.height = (int) animation.getAnimatedValue();
            mFooterView.setLayoutParams(lp);
            mBodyView.setTranslationY(-lp.height);
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isFollowUp && isTrigger && mOnPullToChangeListener != null)
                    mOnPullToChangeListener.onChange(PullToLocation.FOOTER, PullTaStatus.DONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRefreshing = false;
                if (isFollowUp && isTrigger && mOnPullToChangeListener != null)
                    mOnPullToChangeListener.onChange(PullToLocation.FOOTER, PullTaStatus.END);
                isTrigger = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private boolean canBodyScrollDown() {
        if (mBodyView == null) return false;
        return mBodyView.canScrollVertically(1);
    }

    private boolean canBodyScrollUp() {
        if (mBodyView == null) return false;
        return mBodyView.canScrollVertically(-1);
    }

    /**
     * 设置最大拖动 - 配合setPullToHeight
     *
     * @param damping default 0.12f
     */
    public void setDamping(float damping) {
        mDamping = damping;
    }

    /**
     * 设置拖动距离 - 触发PullToState.DONE
     *
     * @param pullToHeight default 140
     */
    public void setPullToHeight(int pullToHeight) {
        mPullToHeight = pullToHeight;
    }

    /**
     * @return 刷新状态
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    /**
     * 设置刷新状态
     *
     * @param refreshing 刷新状态
     */
    public void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;
        if (!refreshing) {
            handlerAction();
        }
    }

    /**
     * 设置拖动状态监听器
     *
     * @param onPullToChangeListener 拖动状态监听器
     */
    public void setOnPullToChangeListener(@NonNull OnPullToChangeListener onPullToChangeListener) {
        mOnPullToChangeListener = onPullToChangeListener;
    }

    /**
     * 拖动状态监听器
     */
    public interface OnPullToChangeListener {
        /**
         * 拖动发生变化
         *
         * @param location 哪个方向拖动
         * @param status   拖动状态
         * @return 是否监听后续状态变化
         */
        boolean onChange(@NonNull PullToLocation location, @NonNull PullTaStatus status);
    }

    public enum PullToLocation {
        /**
         * 顶部
         */
        HEADER,
        /**
         * 底部
         */
        FOOTER
    }

    public enum PullTaStatus {
        /**
         * 拖动开始
         */
        START,
        /**
         * 拖动到指定位置
         */
        TRIGGER,
        /**
         * 拖动完成
         */
        DONE,
        /**
         * 拖动结束
         */
        END
    }
}

