package com.guojun.a0325;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;
import java.util.Vector;

/**
 * Created by topwise on 16-3-28.
 */
public class BallView extends View {

    private final Random mRandom;
    public static int SPEED_DIRECT_ONE = 1;
    public static int SPEED_DIRECT_TWO = 2;
    public static int SPEED_DIRECT_THREE = 3;
    public static int SPEED_DIRECT_FOUR = 4;

    static class Ball {
        int radius; // 半径
        static float cx;   // 圆心
        static float cy;   // 圆心
        static double vx; // X轴速度
        static double vy; // Y轴速度
        Paint paint;

        // 移动
        void move() {
            //向角度的方向移动，偏移圆心
            cx += vx;
            cy += vy;
        }

        int left() {
            return (int) (cx - radius);
        }

        int right() {
            return (int) (cx +radius);
        }

        int bottom() {
            return (int) (cy + radius);
        }

        int top() {
            return (int) (cy - radius);
        }

    }

    private int mCount = 4;   // 小球个数
    private int maxRadius;  // 小球最大半径
    private int minRadius; // 小球最小半径
    private int minSpeed = 5; // 小球最小移动速度
    private int maxSpeed = 200; // 小球最大移动速度

    private int mWidth = 200;
    private int mHeight = 200;


    public Ball[] mBalls;   // 用来保存所有小球的数组
    public Vector mRadius = new Vector(0,1);

    public BallView(Context context) {
        super(context);

        // 初始化所有球(设置颜色和画笔, 初始化移动的角度)
        mRandom = new Random();
        //   RandomColor randomColor = new RandomColor(); // 随机生成好看的颜色，github开源库。
        mBalls = new Ball[mCount];

        for(int i=0; i< mCount; i++) {
            mBalls[i] = new Ball();
            // 设置画笔
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(180);
            paint.setStrokeWidth(0);

            // 设置速度
            float speedX = (mRandom.nextInt(maxSpeed -minSpeed +1)+5)/10f;
            float speedY = (mRandom.nextInt(maxSpeed -minSpeed +1)+5)/10f;
            mBalls[i].paint = paint;
            mBalls[i].vx = mRandom.nextBoolean() ? speedX : -speedX;
            mBalls[i].vy = mRandom.nextBoolean() ? speedY : -speedY;
        }

        // 圆心和半径测量的时候才设置
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = resolveSize(mWidth, widthMeasureSpec);
        mHeight = resolveSize(mHeight, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        maxRadius = mWidth/12;
        minRadius = maxRadius/2;

        // 初始化圆的半径和圆心
        for (int i=0; i<mBalls.length; i++) {
            mBalls[i].radius = mRandom.nextInt(maxRadius+1 - minRadius) +minRadius;
//            mBalls[i].mass = (int) (Math.PI * mBalls[i].radius * mBalls[i].radius);
            // 初始化圆心的位置， x最小为 radius， 最大为mwidth- radius
            mBalls[i].cx = mRandom.nextInt(mWidth - mBalls[i].radius) + mBalls[i].radius;
            mBalls[i].cy = mRandom.nextInt(mHeight - mBalls[i].radius) + mBalls[i].radius;
            mRadius.addElement(mBalls[i]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        // 先画出所有圆
        for (int i = 0; i < mCount; i++) {
            Ball ball = mBalls[i];
            canvas.drawCircle(ball.cx, ball.cy, ball.radius, ball.paint);
        }

        // 球碰撞边界
        for (int i = 0; i < mCount; i++) {
            Ball ball = mBalls[i];
            for(Object a:mRadius){

            }
            collisionDetectingAndChangeSpeed(ball); // 碰撞边界的计算
          //  collisionDetectingBallToBall();
            ball.move(); // 移动
        }

        long stopTime = System.currentTimeMillis();
        long runTime = stopTime - startTime;
        // 16毫秒执行一次
        postInvalidateDelayed(Math.abs(runTime -16));
    }

    // 判断球是否碰撞碰撞边界
    public void collisionDetectingAndChangeSpeed(Ball ball) {
        int left = getLeft();
        int top = getTop();
        int right = getRight();
        int bottom = getBottom();

        double speedX = ball.vx;
        double speedY = ball.vy;

        // 碰撞左右，X的速度取反。 speed的判断是防止重复检测碰撞，然后黏在墙上了=。=
        if(ball.left() <= left && speedX < 0) {
            ball.vx = -ball.vx;
        } else if(ball.top() <= top && speedY < 0) {
            ball.vy = -ball.vy;
        } else if(ball.right() >= right && speedX >0) {
            ball.vx = -ball.vx;
        } else if(ball.bottom() >= bottom && speedY >0) {
            ball.vy = -ball.vy;
        }
    }

    //判断球的速度矢量在第几象限
    public int direct(Ball ball){
        double vx = ball.vx;
        double vy = ball.vy;
        if( vx >= 0 && vy >= 0) {
            return SPEED_DIRECT_ONE;
        }else if(vx >= 0 && vy <= 0){
            return SPEED_DIRECT_TWO;
        }else if (vx <= 0 && vy <= 0){
            return SPEED_DIRECT_THREE;
        }else if (vx <= 0 && vy >= 0){
            return SPEED_DIRECT_FOUR;
        }
        return 0;
    }

    /*
    计算碰撞后的速度分量，用到动量守恒和动能守恒公式，为此还去翻了一边高中物理书-.-||
    根据： m1v10+m2v20 = m1v1+m2v2
          1/2 m1v10^2 + 1/2 m2v20^2 = 1/2 m1v1^2+ 1/2m2v2^2
      得： v1 = [(m1-m2)v10 + 2m2v20] / (m1+m2)
           v2 = [(m2-m1)v20 + 2m1v10] / (m1+m2)
    * */
    public void collisionDetectingBallToBall(Ball a,Ball b){
        double M1 = Math.pow(a.radius,2);
        double M2 = Math.pow(b.radius,2);
        double vx1AfterCollision,vx2AfterCollision,vy1AfterCollision,vy2AfterCollision;
        vx1AfterCollision = ((M1 - M2) * a.vx + 2 * M2 * (b.vx))/(M1 + M2);
        vy1AfterCollision = ((M1 - M2) * a.vy + 2 * M2 * (b.vy))/(M1 + M2);
        vx2AfterCollision = ((M2 - M1) * b.vx + 2 * M1 * (b.vx))/(M1 + M2);
        vy2AfterCollision = ((M2 - M1) * b.vy + 2 * M1 * (b.vy))/(M1 + M2);
        a.vx = vx1AfterCollision;
        a.vy = vy1AfterCollision;
        b.vx = vx2AfterCollision;
        b.vy = vy2AfterCollision;
    }

    public void doMath(double M1,double M2,double v1,double v2){

    }
}