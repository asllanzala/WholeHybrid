package com.honeywell.wholesale.ui.newreport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/1/6.
 * Email: yang.liu6@honeywell.com
 */

public class AxisView extends View {


    //纵坐标上移(预留)距离的比例
    private float DEFAULT_AXIS_IMPROVE_HEIGHT = 0.15f;
    //纵坐标上移(预留)距离
    private float mUpAxisDefaultSize = 0f;

    //纵坐标下移距离的比例
    private static final float DEFAULT_AXIS_DOWN_HEIGHT = 0.2f;
    //纵坐标下移距离
    private float mDownAxisDefaultSize = 0f;



    private Canvas canvas;

    //坐标轴的画笔
    private Paint axisLinePaint;

    //画文字的画笔
    private TextPaint mTextPaint;

    //文字的FontMetrics
    private Paint.FontMetrics mTextFontMetrics;

    //文字颜色
    private int mWeaTextColor = Color.BLACK;

    //字体最小默认8dp
    private int mTemperTextSize = 8;

    //默认最小宽度100dp
    private int defaultMinWidth = 50;

    //默认最小高度80dp
    private int defaultMinHeight = 150;

    //判断是左坐标轴 OR 右坐标轴
    private boolean isLeftAxis = true;

    //坐标轴的最大最小数据
    private ChartAssistDataModel mChartAssistDataModel =new ChartAssistDataModel();

    //左坐标轴的刻度值
    private ArrayList<String> mLeftAxisArrayList;

    //右坐标轴的刻度值
    private ArrayList<String> mRightAxisArrayList;

    //坐标轴刻度数目
    private int mAxisNum  = 4;


    public AxisView(Context context) {
        this(context, null);
    }

    public AxisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AxisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void setmChartAssistDataModel(ChartAssistDataModel mChartAssistDataModel) {
        this.mChartAssistDataModel = mChartAssistDataModel;
        invalidate();
    }

    public void setIsLeftAxis(boolean mJudge){
        this.isLeftAxis = mJudge;
        invalidate();
    }

    private void initData(){
        float mHightestIncome = mChartAssistDataModel.getmHighestIncome();
        float mLowestIncome = mChartAssistDataModel.getmLowestIncome();
        mLeftAxisArrayList = new ArrayList<>(mAxisNum + 1);
        for (int i = mAxisNum; i >= 0; i--){
            mLeftAxisArrayList.add(String.valueOf((int) ((i / (float) mAxisNum) * (mHightestIncome - mLowestIncome) + mLowestIncome)));
        }

        float mHightestProfit = mChartAssistDataModel.getmHighestProfit();
        float mLowestProfit = mChartAssistDataModel.getmLowestProfit();
        mRightAxisArrayList = new ArrayList<>(mAxisNum + 1);
        for (int i = mAxisNum; i >= 0; i--){
            mRightAxisArrayList.add(String.valueOf((int) ((i / (float) mAxisNum) * (mHightestProfit - mLowestProfit) + mLowestProfit)));
        }

        if (mLowestIncome == 0 || mLowestProfit == 0 ){
            DEFAULT_AXIS_IMPROVE_HEIGHT = 0f;
        } else {
            DEFAULT_AXIS_IMPROVE_HEIGHT = 0.15f;
        }

        mUpAxisDefaultSize = (float) (DEFAULT_AXIS_IMPROVE_HEIGHT * 0.8 *getHeight());
        mDownAxisDefaultSize = DEFAULT_AXIS_DOWN_HEIGHT * getHeight();

    }

    private void initPaint(){
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTemperTextSize * getChartDentity());
        mTextPaint.setColor(mWeaTextColor);
        mTextFontMetrics = mTextPaint.getFontMetrics();
    }

    private int getSize(int mode, int size, int type) {
        // 默认
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            if (type == 0) {
                // 最小不能低于最小的宽度
                result = dp2px(getContext(), defaultMinWidth) + getPaddingLeft() + getPaddingRight();
            } else {
                result = dp2px(getContext(), defaultMinHeight) + getPaddingTop() + getPaddingBottom();
            }

            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = getSize(widthMode, widthSize, 0);
        int height = getSize(heightMode, heightSize, 1);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(this.canvas);

        initData();

        if (mChartAssistDataModel == null) {
            return;
        }

        // 绘制 Y 周坐标
        if (isLeftAxis){
            mWeaTextColor = Color.parseColor("#f46e17");
            mTextPaint.setColor(mWeaTextColor);
            float leftHeight = getHeight() - mDownAxisDefaultSize;// 左侧外周的 需要划分的高度：
            float hPerHeight = leftHeight/4;
            mTextPaint.setTextAlign(Align.CENTER);
            for(int i=0;i<mLeftAxisArrayList.size();i++)
            {
                canvas.drawText(mLeftAxisArrayList.get(i), getWidth() / 2, i*hPerHeight + mDownAxisDefaultSize - mUpAxisDefaultSize, mTextPaint);
            }
        } else {
            mWeaTextColor = Color.parseColor("#417505");
            mTextPaint.setColor(mWeaTextColor);
            float leftHeight = getHeight() - mDownAxisDefaultSize;// 左侧外周的 需要划分的高度：
            float hPerHeight = leftHeight/4;
            mTextPaint.setTextAlign(Align.CENTER);
            for(int i=0;i<mRightAxisArrayList.size();i++)
            {
                canvas.drawText(mRightAxisArrayList.get(i), getWidth() / 2, i*hPerHeight + mDownAxisDefaultSize - mUpAxisDefaultSize, mTextPaint);
            }
        }
    }

    private float getChartDentity(){
        return getContext().getResources().getDisplayMetrics().density;
    }
}
