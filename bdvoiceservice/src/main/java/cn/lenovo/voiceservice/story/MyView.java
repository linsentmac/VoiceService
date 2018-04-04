package cn.lenovo.voiceservice.story;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by yunduo on 18-4-3.
 */

@SuppressLint("AppCompatCustomView")
public class MyView extends TextView{
    private static final String TAG = MyView.class.getSimpleName();

    private String[] hanzi = new String[]{};
    private String[] pinyin = new String[]{};
    private TextPaint myTextPaint = new TextPaint();
    private int viewWidth;
    private int viewHeight;
    private float currProgress = 0;
    private int length = 0;
    private ValueAnimator animator;


    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private float lineHeight = 0;
    private void initPaint() {
        myTextPaint.setAntiAlias(true);
        myTextPaint.setTextSize(10 * getResources().getDisplayMetrics().density);
        myTextPaint.setColor(0xFF000000);
        Paint.FontMetricsInt fontMetrics = myTextPaint.getFontMetricsInt();
        lineHeight = fontMetrics.bottom - fontMetrics.top;
    }

    /**
     * 设置字体所占的高度
     * @param lineHeight
     */
    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        viewHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setHanzi(String[] hanzi) {
        this.hanzi = hanzi;
    }

    public void setPinyin(String[] pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        myTextPaint.setColor(Color.RED);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int initX = getPaddingLeft();
        int initY = getPaddingTop() + 30;
        int startX = initX; // 开始绘制的x起点
        int startY = initY; // 开始绘制的y起点
        int widthPadding = 5;

        Rect hanZiRect, pinYinRect;
        String hanZi, pinYin;
        int combineWidth, hanZiWidth, pinYinWidth;
        int hanZiCenterPadding, pinYinCenterPadding;
        for (int i = 0; i < hanzi.length; i++) {
            // 拿到hanzi，与宽高
            hanZi = this.hanzi[i];
            hanZiRect = new Rect();
            myTextPaint.getTextBounds(hanZi, 0, hanZi.length(), hanZiRect);
            hanZiWidth = hanZiRect.right - hanZiRect.left;
            // 拿到拼音，与宽高
            pinYin = this.pinyin[i];
            pinYinRect = new Rect();
            myTextPaint.getTextBounds(pinYin, 0, pinYin.length(), pinYinRect);
            pinYinWidth = pinYinRect.right - pinYinRect.left;
            // 比较 拼音 - 汉字 的宽度，取最宽的为标准
            combineWidth = Math.max(pinYinWidth, hanZiWidth);

            // 居中显示
            hanZiCenterPadding = (combineWidth - hanZiWidth) / 2;
            pinYinCenterPadding = (combineWidth - pinYinWidth) / 2;
            // 渲染颜色
            if (i < currProgress) {
                myTextPaint.setColor(Color.GREEN);
            } else {
                myTextPaint.setColor(Color.GRAY);
            }

            // startY 是 baseLine 不是字体的起点 https://stackoverflow.com/questions/27631736/meaning-of-top-ascent-baseline-descent-bottom-and-leading-in-androids-font
            canvas.drawText(pinYin, startX + pinYinCenterPadding, startY, myTextPaint);
            canvas.drawText(hanZi, startX + hanZiCenterPadding, startY + lineHeight, myTextPaint);
            // 下一个绘制的x
            startX = startX + combineWidth + widthPadding;

            //新的一行
            if (startX + 50 >= viewWidth) {
                startX = initX;
                startY = (int) (startY + 2 * lineHeight);
            }


        }
    }

    public void setCurrProgress(float currProgress) {
        this.currProgress = currProgress;
    }




    @SuppressLint("NewApi")
    public void startRenderColor(int duration) {
        clearAnimation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            animate().cancel();
        }

        length = hanzi.length;
        animator = ValueAnimator.ofInt(new int[]{0, length});
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setCurrProgress(value);
                invalidate();
            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @SuppressLint("NewApi")
    public void pauseRenderColor(){
        animator.addPauseListener(new Animator.AnimatorPauseListener() {
            @Override
            public void onAnimationPause(Animator animation) {

            }

            @Override
            public void onAnimationResume(Animator animation) {

            }
        });
        animator.pause();
    }

    @SuppressLint("NewApi")
    public void resumeRenderColor(){
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setCurrProgress(value);
                invalidate();
            }
        });
        animator.resume();
    }
}
