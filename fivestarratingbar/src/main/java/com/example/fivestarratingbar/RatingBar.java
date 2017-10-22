package com.example.fivestarratingbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.icu.math.BigDecimal;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Li on 2017/10/22.
 */

public class RatingBar extends LinearLayout implements View.OnClickListener {
    /**
     * 是否可点击
     */
    private boolean mClickable;
    /**
     * 星星总数
     */
    private int starCount;
    /**
     * 星星的点击事件
     */
    private OnRatingChangeListener onRatingChangeListener;
    /**
     * 每个星星的大小
     */
    private float starImageSize;
    /**
     * 每个星星的间距
     */
    private int starPadding;
    /**
     * 星星的显示数量，支持小数点
     */
    private float selectStarValue;
    /**
     * 空白的默认星星图片
     */
    private Drawable starEmptyDrawable;
    /**
     * 选中后的星星填充图片
     */
    private Drawable starFillDrawable;
    /**
     * 半颗星的图片
     */
    private Drawable starHalfDrawable;
    /**
     * 每次点击星星所增加的量是整个还是半个
     */
    private StepSize stepSize;

    /**
     * 设置半星的图片资源文件
     *
     * @param starHalfDrawable
     */
    public void setStarHalfDrawable(Drawable starHalfDrawable) {
        this.starHalfDrawable = starHalfDrawable;
    }

    /**
     * 设置满星的图片资源文件
     *
     * @param starFillDrawable
     */
    public void setStarFillDrawable(Drawable starFillDrawable) {
        this.starFillDrawable = starFillDrawable;
    }

    /**
     * 设置空白和默认的图片资源文件
     *
     * @param starEmptyDrawable
     */
    public void setStarEmptyDrawable(Drawable starEmptyDrawable) {
        this.starEmptyDrawable = starEmptyDrawable;
    }

    /**
     * 设置星星是否可以点击操作
     *
     * @param clickable
     */
    public void setClickable(boolean clickable) {
        this.mClickable = clickable;
    }

    /**
     * 设置星星点击事件
     *
     * @param onRatingChangeListener
     */
    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    /**
     * 设置星星的大小
     *
     * @param starImageSize
     */
    public void setStarImageSize(float starImageSize) {
        this.starImageSize = starImageSize;
    }

    public void setStepSize(StepSize stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * 构造函数
     * 获取xml中设置的资源文件
     *
     * @param context
     * @param attrs
     */
    public RatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);
        starImageSize = mTypedArray.getDimension(R.styleable.RatingBar_starImageSize, 20);
        starPadding = (int) mTypedArray.getDimension(R.styleable.RatingBar_starPadding, 10);
        selectStarValue = mTypedArray.getFloat(R.styleable.RatingBar_curSelectValue, 1.0f);
        stepSize = StepSize.fromStep(mTypedArray.getInt(R.styleable.RatingBar_stepSize, 1));
        starCount = mTypedArray.getInteger(R.styleable.RatingBar_allStarsCount, 5);
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starEmpty);
        starFillDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starFill);
        starHalfDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starHalf);
        mClickable = mTypedArray.getBoolean(R.styleable.RatingBar_clickable, true);

        final float scale = context.getResources().getDisplayMetrics().density;
        Log.i("leeTest-------->", "starImageSize = " + starImageSize + ",  starPadding = " + starPadding);
        Log.i("leeTest-------->", "scale = " + scale);

        mTypedArray.recycle();
        for (int i = 0; i < starCount; ++i) {
            final ImageView imageView = getStarImageView();
            imageView.setImageDrawable(starEmptyDrawable);

            addView(imageView);
        }
        setStar(selectStarValue);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View root = getRootView();
        if (root == null || !(root instanceof ViewGroup)) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("leeTest-------->", "ACTION_DOWN x = " + event.getX() + ",  y = " + event.getRawY());
                float stars = caculateStarsValue(event.getX());
                setStar(stars);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("leeTest-------->", "ACTION_MOVE x = " + event.getX() + ",  y = " + event.getRawY());
                float stars2 = caculateStarsValue(event.getX());
                setStar(stars2);
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * 根据当前点击,或者滑动的位置，计算所需要显示的星星的个数
     *
     * @param x 传入当前点击或滑动位置（单位像素，不是dp），
     *          返回值 返回0.5的倍数，返回多少个星星，比如3.5个星星，就是第3个和第4个星星中间被点击
     */
    private float caculateStarsValue(float x) {
        float fResult = 0.0f;
        float iOneStarWidth = starImageSize + starPadding;
        float value = x * 2 / iOneStarWidth;
        fResult = ((float) Math.round(value) / 2) + 0.5f;
        Log.i("leeTest-------->", "iOneStarWidth = " + iOneStarWidth + ",  fResult = " + fResult);
        return fResult;
    }

    /**
     * 设置每颗星星的参数
     *
     * @return
     */
    private ImageView getStarImageView() {
        ImageView imageView = new ImageView(getContext());

        LayoutParams layout = new LayoutParams(
                Math.round(starImageSize), Math.round(starImageSize));//设置每颗星星在线性布局的大小
        layout.setMargins(0, 0, Math.round(starPadding), 0);//设置每颗星星在线性布局的间距

        imageView.setLayoutParams(layout);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(starEmptyDrawable);
        imageView.setMinimumWidth(10);
        imageView.setMaxHeight(10);


        return imageView;

    }


    /**
     * 设置星星的个数
     *
     * @param rating 传入的值，是0.5的整数倍，比如共5个星，显示3.5个星
     */

    @TargetApi(Build.VERSION_CODES.N)
    public void setStar(float rating) {
        if (selectStarValue == rating) {
            return;
        }

        if (rating >= starCount) {
            rating = starCount;
        }

        if (rating <= 0) {
            rating = 0;
        }

        if (onRatingChangeListener != null) {
            onRatingChangeListener.onRatingChange(rating);
        }

        this.selectStarValue = rating;

        // 浮点数的整数部分
        int fint = (int) rating;
        BigDecimal b1 = new BigDecimal(Float.toString(rating));
        BigDecimal b2 = new BigDecimal(Integer.toString(fint));

        // 浮点数的小数部分
        float fPoint = b1.subtract(b2).floatValue();

        // 设置选中的星星
        for (int i = 0; i < fint; ++i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starFillDrawable);
        }

        // 设置没有选中的星星
        for (int i = fint; i < starCount; i++) {
            ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
        }

        // 小数点默认增加半颗星
        if (fPoint > 0) {
            ((ImageView) getChildAt(fint)).setImageDrawable(starHalfDrawable);
        }
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * 操作星星的点击事件
     */
    public interface OnRatingChangeListener {
        /**
         * 选中的星星的个数
         *
         * @param ratingCount
         */
        void onRatingChange(float ratingCount);

    }

    /**
     * 星星每次增加的方式整星还是半星，枚举类型
     * 类似于View.GONE
     */
    public enum StepSize {
        Half(0), Full(1);
        int step;

        StepSize(int step) {
            this.step = step;
        }

        public static StepSize fromStep(int step) {
            for (StepSize f : values()) {
                if (f.step == step) {
                    return f;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}