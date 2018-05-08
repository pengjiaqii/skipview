package com.jade.skipview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jade on 2018/5/8.
 * splash界面右上角的跳过按钮
 */
public class SkipView extends View {
    private Paint mTextPaint;
    private Paint mInnerCirclePaint;
    private Paint mOutArcPaint;

    //外部圆弧的笔画宽度
    public static final int ARC_WIDTH = 10;
    //内部字体大小
    public static final int TEXT_SIZE = 72;
    //文字距离内部灰色圆边框的间距
    public static final int INNER_PADDING = 20;
    private float mOutArcRadius;
    private float mMeasureTextWidth;
    private RectF mRectF;
    private float mInnerCircleRadius;
    private Handler mHandler;

    public SkipView(Context context) {
        super(context);
    }

    public SkipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //准备三只画笔
        mTextPaint = new Paint();
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setColor(Color.WHITE);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(Color.GRAY);
        mInnerCirclePaint.setAntiAlias(true);

        mOutArcPaint = new Paint();
        mOutArcPaint.setColor(Color.RED);
        mOutArcPaint.setAntiAlias(true);
        mOutArcPaint.setStrokeWidth(ARC_WIDTH);
        mOutArcPaint.setStyle(Paint.Style.STROKE);

        //通过paint来算出文字宽度
        mMeasureTextWidth = mTextPaint.measureText("跳过");
        //内部圆的半径
        mInnerCircleRadius = (2 * INNER_PADDING + mMeasureTextWidth)/2;
        //外部圆弧的半径   = (内部圆的直径+2*外部圆弧的笔画宽度)/2
        mOutArcRadius = (mInnerCircleRadius * 2 + 2 * ARC_WIDTH)/2;

        //绘制圆弧时用到的矩形
        mRectF = new RectF(0+ARC_WIDTH/2, 0+ARC_WIDTH/2,
                mOutArcRadius * 2-ARC_WIDTH/2, mOutArcRadius * 2-ARC_WIDTH/2);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //修改当前时间,改变角度
                mCurrentTime+=100;
                float progress = (mCurrentTime * 1.0f) / (mTotalTime * 1.0f);

                Log.d("jade","progress--->" + progress);

                int i = (int) (progress * 100);

                if(null != mOnSkipListener){
                    mOnSkipListener.setProgress(i);
                }
                //开始重绘
                invalidate();
                if(mCurrentTime>=mTotalTime){
                    //该停止了
                    //跳转到某个具体页面,具体逻辑咱们这里不写,
                    // 交给使用了这个控件得Activity去写

                    //3 在合适的时机下调用接口实例的方法
                    if(mOnSkipListener!=null){
                        mOnSkipListener.onSkip();
                    }
                    return;
                }
                mHandler.sendEmptyMessageDelayed(0,100);
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.5f);
                break;
            case MotionEvent.ACTION_UP:
                //触摸点到相对于控件的x，y距离
                float x = event.getX();
                float y = event.getY();
                //触摸点到相对于屏幕的x，y距离
                float rawX = event.getRawX();
                float rawY = event.getRawY();

                Log.d("jade","x--->" + x);
                Log.d("jade","y--->" + y);
                Log.d("jade","rawX--->" + rawX);
                Log.d("jade","rawY--->" + rawY);
                //防止点击跳过后不松开,然后滑动到其他地方再松开,所以当up的时候判断手指是否在控件区域内
                if(mRectF.contains(x,y)){
                    //让handler中的逻辑循环停下来
                    mHandler.removeCallbacksAndMessages(null);
                    setAlpha(1f);
                    //要跳转页面
                    //3 在合适的时机下调用接口实例的方法
                    if(mOnSkipListener!=null){
                        mOnSkipListener.onSkip();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
//                mHandler.removeCallbacksAndMessages(null);
                setAlpha(1f);
                break;
        }
        return true;
    }

    //1 定义一个接口和里面的方法
    public interface OnSkipListener{
        void onSkip();

        void setProgress(int progress);
    }

    //2 在控件中准备一个成员变量  作为该接口的实例
    private OnSkipListener mOnSkipListener;

    //4 准备一个set方法去给这个成员变量赋值
    public void setOnSkipListener(OnSkipListener skipListener){
        mOnSkipListener = skipListener;
    }


    public void start(){
        //开始进行不断的重绘
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode==MeasureSpec.AT_MOST){
            widthSize = (int) (mOutArcRadius*2);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = (int) (mOutArcRadius*2);
        }
        setMeasuredDimension(widthSize,heightSize);
    }


    private int mCurrentTime = 0;//当前时间
    private int mTotalTime = 2000;//总的展示时间



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //绘制外部圆弧 内部灰色圆 中间的文字
        canvas.save();
        canvas.rotate(-90,getMeasuredWidth()/2,getMeasuredHeight()/2);
        canvas.drawArc(mRectF,0,mCurrentTime*1f/mTotalTime*360,false,mOutArcPaint);
        canvas.restore();

        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,mInnerCircleRadius,mInnerCirclePaint);
        //获得文字的高度上的基线
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        float ascent = fontMetrics.ascent;
        float descent = fontMetrics.descent;
        Log.e(getClass().getSimpleName(), "onDraw: "+"top "+top
                +"  ascent "+ascent+"  descent "+descent+"  bottom "+bottom);
        int baseLine = getMeasuredHeight()/2;

        //公式 :用来计算出baseLine在Y轴居中的公式(推导过程不再赘述)
        baseLine = (int) (baseLine-(top+bottom)/2);

        canvas.drawText("跳过",getMeasuredWidth()/2-mMeasureTextWidth/2,
                baseLine,mTextPaint);

    }
}
