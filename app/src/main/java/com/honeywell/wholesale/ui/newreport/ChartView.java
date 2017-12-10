package com.honeywell.wholesale.ui.newreport;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.honeywell.wholesale.R;


/**
 * Created by H154326 on 16/12/29.
 * Email: yang.liu6@honeywell.com
 */

public class ChartView extends View {

    private Canvas canvas;

    private boolean isRectSelected = false;
    private String mChartKind = "";

    private static final String SALE = "SALE";
    private static final String PROFIT = "PROFIT";
    private static final String BOTH = "BOTH";

    private static final String LEFT_POSITION = "LEFT";
    private static final String MIDDLE_POSITION = "MIDDLE";
    private static final String RIGHT_POSITION = "RIGHT";

    //上移(预留)距离的比例
    private float DEFAULT_IMPROVE_HEIGHT = 0.15f;

    //上移(预留)距离
    private float mUpDefaultSize = 0f;

    //下移(预留)距离的比例
    private static final float DEFAULT_AXIS_DOWN_HEIGHT = 0.2f;

    /**
     * 默认最小宽度10dp
     */
    private static final int defaultMinWidth = 100;

    /**
     * 默认最小高度80dp
     */
    private static final int defaultMinHeight = 80;

    /**
     * 字体最小默认16dp
     */
    private int mTextSize = 16;

    /**
     * 文字颜色
     */
    private int mWeaTextColor = Color.BLACK;

    /**
     * 线条颜色
     */
    private int mLineColor = Color.parseColor("#658f2b");

    /**
     * 线的宽度
     */
    private int mWeaLineWidth = 4;

    /**
     * 圆点的宽度
     */
    private int mWeaDotRadius = 5;

    /**
     * 文字和点的间距
     */
    private int mTextDotDistance = 5;

    /**
     * 柱状图宽度24dp
     */
    private int mRecTextSize = 24;

    /**
     * 画文字的画笔
     */
    private TextPaint mTextPaint;

    /**
     * 文字的FontMetrics
     */
    private Paint.FontMetrics mTextFontMetrics;

    /**
     * 画点的画笔
     */
    private Paint mDotPaint;

    /**
     * 画线的画笔
     */
    private Paint mLinePaint;

    /**
     * 矩形画笔
     */
    private Paint recPaint;

    /**
     * 上方矩形画笔
     */
    private Paint shadowRecPaint;

    /**
     * 绘制坐标轴文本的画笔
     */
    private Paint titlePaint;

    /**
     * 数据来源
     */
    private ChartDataModel mChartDataModel;

    /**
     * 数据范围
     */
    private ChartAssistDataModel mChartAssistDataModel;


    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        initPaint();
    }

    public String getmChartKind() {
        return mChartKind;
    }

    public void setmChartKind(String mChartKind) {
        this.mChartKind = mChartKind;
        invalidate();
    }

    public boolean isRectSelected() {
        return isRectSelected;
    }

    //设置该条柱状图是否选中状态
    public void setRectSelected(boolean rectSelected) {
        isRectSelected = rectSelected;
    }

    /**
     * 改变柱状图颜色
     */
    public void changeRectColor(){
        recPaint.setColor(getResources().getColor(R.color.chat_rect_selected_color));
        drawRect(canvas);

    }

    /**
     * 设置报表数据
     * @param chartDataModel
     */
    public void setChatData(ChartDataModel chartDataModel){
        this.mChartDataModel = chartDataModel;
        invalidate();
    }

    /**
     * 设置报表辅助数据
     * @param chartAssistDataModel
     */
    public void setChatAssistData(ChartAssistDataModel chartAssistDataModel){
        this.mChartAssistDataModel = chartAssistDataModel;
        invalidate();
    }

    /**
     * 设置画笔信息
     */
    private void initPaint() {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mWeaTextColor);
        mTextFontMetrics = mTextPaint.getFontMetrics();

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setColor(mWeaTextColor);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mWeaLineWidth);
        mLinePaint.setColor(mLineColor);

        recPaint = new Paint();

        shadowRecPaint = new Paint();

        titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
    }

    /**
     * 获取自定义属性并赋初始值
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartView,
                defStyleAttr, 0);
        mTextSize = (int) a.getDimension(R.styleable.ChartView_temperTextSize,
                dp2px(context, mTextSize));
        mWeaTextColor = a.getColor(R.styleable.ChartView_weatextColor, mLineColor);
        mWeaLineWidth = (int) a.getDimension(R.styleable.ChartView_chatLineWidth,
                dp2px(context, mWeaLineWidth));
        mWeaDotRadius = (int) a.getDimension(R.styleable.ChartView_chatDotRadius,
                dp2px(context, mWeaDotRadius));
        mTextDotDistance = (int) a.getDimension(R.styleable.ChartView_textDotDistance,
                dp2px(context, mTextDotDistance));
        a.recycle();
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

    /**
     * @param mode Mode
     * @param size Size
     * @param type 0表示宽度，1表示高度
     * @return 宽度或者高度
     */
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
                // 最小不能小于最小的宽度加上一些数据
                int textHeight = (int) (mTextFontMetrics.bottom - mTextFontMetrics.top);
                // 加上2个文字的高度
                result = dp2px(getContext(), defaultMinHeight) + 2 * textHeight +
                        // 需要加上两个文字和圆点的间距
                        getPaddingTop() + getPaddingBottom() + 2 * mTextDotDistance;
            }

            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(this.canvas);

        if (mChartDataModel == null) {
            return;
        }

        if (isRectSelected){
            recPaint.setColor(getResources().getColor(R.color.chat_rect_selected_color));
            mWeaDotRadius = (int) (7 * getChartDentity());
        } else {
            recPaint.setColor(getResources().getColor(R.color.rectColor));
            mWeaDotRadius = (int) (5 * getChartDentity());
        }

        float mHightestIncome = mChartAssistDataModel.getmHighestIncome();
        float mLowestIncome = mChartAssistDataModel.getmLowestIncome();
        float mHightestProfit = mChartAssistDataModel.getmHighestProfit();
        float mLowestProfit = mChartAssistDataModel.getmLowestProfit();

        if (mLowestIncome == 0 || mLowestProfit == 0 ){
            DEFAULT_IMPROVE_HEIGHT = 0f;
        } else {
            DEFAULT_IMPROVE_HEIGHT = 0.15f;
        }

        shadowRecPaint.setColor(Color.parseColor("#f6f6f6"));

        mUpDefaultSize = (float) (DEFAULT_IMPROVE_HEIGHT * 0.8 *getHeight());

        canvas.drawColor(Color.WHITE);

        if (mChartKind.equals(SALE)){
            drawRect(this.canvas);
        } else if (mChartKind.equals(PROFIT)){
            drawShadowRect(this.canvas);
            drawLine(this.canvas);
        } else if (mChartKind.equals(BOTH)){
            drawRect(this.canvas);
            drawLine(this.canvas);
        }
    }



    /**
     * 画折线图
     * @param canvas
     */
    private void drawLine(Canvas canvas){

        float baseHeight = (float)getHeight();
        // 中间
        float calowMiddle = baseHeight - (cacLineHeight(mChartDataModel.getmProfit()));
        canvas.drawCircle(((float) getWidth()) / 2.0f, calowMiddle - mUpDefaultSize, mWeaDotRadius, mDotPaint);

        float calowLeftX;

        calowLeftX = 0f;

        float calowLeftY;

        calowLeftY = baseHeight - (cacLineHeight(mChartDataModel.getmPreAvgProfit()));

        float calowRightX;

        calowRightX = getWidth();

        float calowRightY;

        calowRightY = baseHeight - cacLineHeight(mChartDataModel.getmBehAvgProfit());

        if (calowLeftY != calowMiddle){
            float mLineLeftSlope = getSlope(calowLeftX, calowLeftY, getWidth() / 2.0f, calowMiddle);
            calowLeftX -= mWeaLineWidth * getChartDentity();
            calowLeftY += mLineLeftSlope * (-mWeaLineWidth * getChartDentity());
        }

        if (calowMiddle != calowRightY){
            float mLineRightSlope = getSlope(getWidth() / 2.0f, calowMiddle, calowRightX, calowRightY);
            calowRightX += mWeaLineWidth * getChartDentity();
            calowRightY += mLineRightSlope * mWeaLineWidth * getChartDentity();
        }

        if ( ! mChartDataModel.getmPosition().equals(LEFT_POSITION)) {
            // 左边
            canvas.drawLine(calowLeftX, calowLeftY - mUpDefaultSize, getWidth() / 2.0f, calowMiddle - mUpDefaultSize, mLinePaint);
        }

        if ( ! mChartDataModel.getmPosition().equals(RIGHT_POSITION)) {
            // 右边
            canvas.drawLine(getWidth() / 2.0f, calowMiddle - mUpDefaultSize, calowRightX, calowRightY - mUpDefaultSize, mLinePaint);
        }
    }

    /**
     * 计算线条的斜率
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private float getSlope(float x1, float y1, float x2, float y2){
        return ((y2-y1) / (x2 - x1));
    }

    /**
     * 画阴影柱状图
     * @param canvas
     */
    private void drawShadowRect(Canvas canvas) {


        float shadowLeft = getWidth() /2 - getChartDentity() * mRecTextSize / 2;
        float shadowRight = getWidth() /2 + getChartDentity() * mRecTextSize / 2;
        float shadowTop = 0;
        float shadowBottom = getBottom();

        canvas.drawRect(shadowLeft,shadowBottom - mUpDefaultSize, shadowRight,shadowBottom, shadowRecPaint);
        canvas.drawRect(shadowLeft,shadowTop,shadowRight,shadowBottom - mUpDefaultSize, shadowRecPaint);
        //canvas.drawRect(shadowRect, shadowRecPaint);

    }

    /**
     * 画柱状图
     * @param canvas
     */
    private void drawRect(Canvas canvas) {

        float baseHeight = (float) getHeight();
        float left = getWidth() /2 - getChartDentity() * mRecTextSize / 2;
        float right = getWidth() /2 + getChartDentity() * mRecTextSize / 2;

//      当前的相对高度：

        float calowMiddle = baseHeight - cacRectHeight(mChartDataModel.getmIncome()) - mUpDefaultSize;

        float top = calowMiddle;
        float bottom = getBottom();

        canvas.drawRect(left,bottom - mUpDefaultSize,right,bottom,recPaint);
        canvas.drawRect(left,top,right,bottom - mUpDefaultSize,recPaint);

        float shadowLeft = getWidth() /2 - getChartDentity() * mRecTextSize / 2;
        float shadowRight = getWidth() /2 + getChartDentity() * mRecTextSize / 2;
        float shadowTop = 0;
        float shadowBottom = calowMiddle;

        canvas.drawRect(shadowLeft,shadowTop,shadowRight,shadowBottom,shadowRecPaint);
    }

    /**
     * 计算折线图的height
     * @param tem
     * @return
     */
    private float cacLineHeight(float tem) {
        // 最低，最高之差
        float temDistance = mChartAssistDataModel.getmHighestProfit() - mChartAssistDataModel.getmLowestProfit();
        if (temDistance == 0f){
            return 0f;
        } else {
            float viewDistance = (1 - DEFAULT_AXIS_DOWN_HEIGHT) * getHeight();
            // 今天的和最低之间的差别
            float currTemDistance = tem - mChartAssistDataModel.getmLowestProfit();
            return currTemDistance * viewDistance / temDistance;
        }
    }

    /**
     * 计算柱状图的height
     * @param tem
     * @return
     */
    private float cacRectHeight(float tem) {
        // 最低，最高之差
        float temDistance = mChartAssistDataModel.getmHighestIncome() - mChartAssistDataModel.getmLowestIncome();
        if (temDistance == 0f){
            return 0f;
        } else {
            float viewDistance = (1 - DEFAULT_AXIS_DOWN_HEIGHT) * getHeight();
            // 今天的和最低之间的差别
            float currTemDistance = tem - mChartAssistDataModel.getmLowestIncome();
            return viewDistance * currTemDistance / temDistance;
        }
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    private float getChartDentity(){
        return getContext().getResources().getDisplayMetrics().density;
    }
}
