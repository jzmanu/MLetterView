package com.manu.letter.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.manu.letter.R;


/**
 * Powered by jzman.
 * Created on 2018/7/5 0005.
 */
public class LetterView extends View {

    private String[] letters = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    private OnLetterChangeListener mOnLetterChangeListener;

    private Paint mLetterPaint;
    private Paint mLetterIndicatorPaint;
    private int mWidth;
    private int mItemHeight;
    private int mTouchIndex = -1;
    private Context mContext;
    private boolean isTouch;
    private boolean enableIndicator;

    public LetterView(Context context) {
        super(context);
    }

    public LetterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LetterView);
        int letterTextColor = array.getColor(R.styleable.LetterView_letterTextColor, Color.RED);
        int letterTextBackgroundColor = array.getColor(R.styleable.LetterView_letterTextBackgroundColor, Color.WHITE);
        int letterIndicatorColor = array.getColor(R.styleable.LetterView_letterIndicatorColor, Color.parseColor("#333333"));
        float letterTextSize = array.getDimension(R.styleable.LetterView_letterTextSize, 12);
        enableIndicator = array.getBoolean(R.styleable.LetterView_letterEnableIndicator, true);

        //默认设置
        mContext = context;
        mLetterPaint = new Paint();
        mLetterPaint.setTextSize(letterTextSize);
        mLetterPaint.setColor(letterTextColor);
        mLetterPaint.setAntiAlias(true);

        mLetterIndicatorPaint = new Paint();
        mLetterIndicatorPaint.setStyle(Paint.Style.FILL);
        mLetterIndicatorPaint.setColor(letterIndicatorColor);
        mLetterIndicatorPaint.setAntiAlias(true);
        setBackgroundColor(letterTextBackgroundColor);
        array.recycle();
    }

    public LetterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnLetterChangeListener(OnLetterChangeListener mOnLetterChangeListener) {
        this.mOnLetterChangeListener = mOnLetterChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽高的尺寸大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //wrap_content默认宽高
        @SuppressLint("DrawAllocation") Rect mRect = new Rect();
        mLetterPaint.getTextBounds("A", 0, 1, mRect);
        mWidth = mRect.width() + dpToPx(mContext, 12);
        int mHeight = (mRect.height() + dpToPx(mContext, 5)) * letters.length;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }

        mWidth = getMeasuredWidth();
        int averageItemHeight = getMeasuredHeight() / 28;
        int mOffset = averageItemHeight / 30; //界面调整
        mItemHeight = averageItemHeight + mOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取字母宽高
        @SuppressLint("DrawAllocation") Rect rect = new Rect();
        mLetterPaint.getTextBounds("A", 0, 1, rect);
        int letterWidth = rect.width();
        int letterHeight = rect.height();

        //绘制指示器
        if (enableIndicator) {
            for (int i = 1; i < letters.length + 1; i++) {
                if (mTouchIndex == i) {
                    canvas.drawCircle(0.5f * mWidth, i * mItemHeight - 0.5f * mItemHeight, 0.5f * mItemHeight, mLetterIndicatorPaint);
                }
            }
        }
        //绘制字母
        for (int i = 1; i < letters.length + 1; i++) {
            canvas.drawText(letters[i - 1], (mWidth - letterWidth) / 2, mItemHeight * i - 0.5f * mItemHeight + letterHeight / 2, mLetterPaint);
        }
    }

    //If a View that overrides onTouchEvent or uses an OnTouchListener does not also implement performClick
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouch = true;
                int y = (int) event.getY();
                Log.i("onTouchEvent", "--y->" + y + "-y-dp-->" + pxToDp(getContext(), y));
                int index = y / mItemHeight;
                if (index != mTouchIndex && index < 28 && index > 0) {
                    mTouchIndex = index;
                    Log.i("onTouchEvent", "--mTouchIndex->" + mTouchIndex + "--position->" + mTouchIndex);
                }

                if (mOnLetterChangeListener != null && mTouchIndex > 0) {
                    mOnLetterChangeListener.onLetterListener(letters[mTouchIndex - 1]);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (mOnLetterChangeListener != null && mTouchIndex > 0) {
                    mOnLetterChangeListener.onLetterDismissListener();
                }
                break;
        }
        return true;
    }

    public void setTouchIndex(String word) {
        if (!isTouch) {
            for (int i = 0; i < letters.length; i++) {
                if (letters[i].equals(word)) {
                    mTouchIndex = i + 1;
                    invalidate();
                    return;
                }
            }
        }
    }

    /**
     * dp to pix
     */
    public int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * pix to dp
     */
    public int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public interface OnLetterChangeListener {
        void onLetterListener(String touchIndex);
        void onLetterDismissListener();
    }
}
