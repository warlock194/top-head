package com.guojun.a0325;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Created by topwise on 16-3-28.
 */
public class BallView extends View {

    private String TAG = "BallView";
    private final Random mRandom;
    public static int SPEED_DIRECT_SAME = 1;
    public static int SPEED_DIRECT_DIFFERENT = 2;
    public static int SPEED_DIRECT_ZERO = 0;
    static class Ball {
        int radius; // 半径
        float cx;   // 圆心
        float cy;   // 圆心
        double vx; // X轴速度
        double vy; // Y轴速度
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
    private boolean COLLINSION = false;
    private int mCount = 3;   // 小球个数
    private int maxRadius;  // 小球最大半径
    private int minRadius; // 小球最小半径
    private int minSpeed = 5; // 小球最小移动速度
    private int maxSpeed = 50; // 小球最大移动速度
    private int mWidth = 200;
    private int mHeight = 200;
    private boolean FLAG = true;


    public Ball[] mBalls;   // 用来保存所有小球的数组
   // public ArrayList<Ball> mRadius = new ArrayList<>();

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
            Log.d(TAG,"draw ----------ball" + i);
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
            mBalls[i].cx = mRandom.nextInt(mWidth - mBalls[i].radius) + mBalls[i].radius;
            mBalls[i].cy = mRandom.nextInt(mHeight - mBalls[i].radius) + mBalls[i].radius;
      //      /*
            if ( i > 0 ) {
                FLAG = true;
                judge:while (FLAG){
                   // Log.d(TAG,"collinisioned with -- " + i);
                    //判断是否会跟先画出的圆有重合，有的话就重画一边，直到与之前画的所有圆都不重合为止
                    for (int j=0;j<i;j++ ){
                        if (collinsion(mBalls[i],mBalls[j])){
                            FLAG = true;
                            Log.d(TAG,"collinisioned with -- " + i);
                            mBalls[i].radius = mRandom.nextInt(maxRadius+1 - minRadius) +minRadius;
                            mBalls[i].cx = mRandom.nextInt(mWidth - mBalls[i].radius) + mBalls[i].radius;
                            mBalls[i].cy = mRandom.nextInt(mHeight - mBalls[i].radius) + mBalls[i].radius;
                            continue judge;
                        }
                    }
                    FLAG = false;
                }
            }
        //    */
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        // 先画出所有圆
        for (int i = 0; i < mCount; i++) {
            Ball ball = mBalls[i];
            Log.d(TAG,"draw Ball " + i + "   --R = " + ball.radius + "--");
            canvas.drawCircle(ball.cx, ball.cy, ball.radius, ball.paint);
        }

        // 球碰撞边界
        for (int i = 0; i < mCount; i++) {
            Ball ball = mBalls[i];

            collisionDetectingAndChangeSpeed(ball); // 碰撞边界的计算
            //collisionDetectingBallToBall();
            ball.move(); // 移动
        }

        collinsionBalltoBall(mBalls,mCount);
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
    public int direct(double v1,double v2){
        if( v1 * v2 > 0) {
            return SPEED_DIRECT_SAME;
        }else if (v1 * v2 < 0){
            return SPEED_DIRECT_DIFFERENT;
        }else {
            return SPEED_DIRECT_ZERO;
        }
    }

    /*
    计算碰撞后的速度分量，用到动量守恒和动能守恒公式，为此还去翻了一边高中物理书-.-||
    根据： m1v10+m2v20 = m1v1+m2v2
          1/2 m1v10^2 + 1/2 m2v20^2 = 1/2 m1v1^2+ 1/2m2v2^2
      得： v1 = [(m1-m2)v10 + 2m2v20] / (m1+m2)
           v2 = [(m2-m1)v20 + 2m1v10] / (m1+m2)
    * */

    //非对心碰撞后的速度变化，写起来才发现比想象中稍微复杂了些
    public void speedChangedwhencollinsion(Ball a,Ball b){
        double M1 = Math.pow(a.radius,2);
        double M2 = Math.pow(b.radius,2);
        double sin = Math.abs(a.cy - b.cy)/Math.sqrt(Math.pow((a.cx - b.cx),2) + Math.pow((a.cy - b.cy),2));
        double cos = Math.abs(a.cx - b.cx)/Math.sqrt(Math.pow((a.cx - b.cx),2) + Math.pow((a.cy - b.cy),2));

        double vx1AfterCollision,vx2AfterCollision,vy1AfterCollision,vy2AfterCollision;
        vx1AfterCollision = ((M1 - M2) * a.vx * cos + 2 * M2 * (b.vx * cos))/(M1 + M2);
        vx2AfterCollision = ((M2 - M1) * b.vx * cos + 2 * M1 * (a.vx * cos))/(M1 + M2);
        vy1AfterCollision = ((M1 - M2) * a.vy * sin + 2 * M2 * (b.vy * sin))/(M1 + M2);
        vy2AfterCollision = ((M2 - M1) * b.vy * sin + 2 * M1 * (a.vy * sin))/(M1 + M2);
        a.vx = a.vx - a.vx * cos + vx1AfterCollision ;
        a.vy = a.vy - a.vy * sin + vy1AfterCollision ;
        b.vx = b.vx - b.vx * cos + vx2AfterCollision ;
        b.vy = b.vy - b.vy * sin + vy2AfterCollision ;
    }

    public void collinsionBalltoBall(Ball[] mball,int t){
        for (int i = 0; i < t-1 ; i++){
                //判断两球球心距离是否小于半径之和，即是否相撞
            if (collinsion(mball[t-1],mball[i])){
                COLLINSION = true;
                speedChanged(mball[t-1],mball[i]);
                //speedChangedwhencollinsion(mball[t-1],mball[i]);
                // Log.d(TAG, Rr +"---" + Dd);
                Log.d(TAG,"collinsionBalltoBall -----------------" + (t-1) + "---" + i);
                }
            }
        if ( t > 2){
            collinsionBalltoBall(mball,t-1);
        }
        int Energy = 0;
        for (int j = 0;j < mCount ; j++){
            Energy +=Math.pow(mball[j].radius,2) * (Math.pow(mball[j].vx,2) + Math.pow(mball[j].vy,2));
        }
        Log.d(TAG,"energy = " + Energy);
    }

    public void speedChanged(Ball a,Ball b){
        double Vx1 = a.vx;
        double Vy1 = a.vy;
        double Px1 = a.cx;
        double Py1 = a.cy;
        double Vx2 = b.vx;
        double Vy2 = b.vy;
        double Px2 = b.cx;
        double Py2 = b.cy;
        double M1 = Math.pow(a.radius,2);
        double M2 = Math.pow(b.radius,2);
        /*
        //圆心连线的角度正余弦值
        double cos = Math.abs(Px1 - Px2)/(Math.sqrt(Math.pow(Px1-Px2,2) + Math.pow(Py1-Py2,2)));
        double sin = Math.abs(Py1 - Py2)/(Math.sqrt(Math.pow(Px1-Px2,2) + Math.pow(Py1-Py2,2)));

        //球a速度方向上的正余弦值
        double cos1 = Vx1/Math.sqrt(Math.pow(Vx1,2)+Math.pow(Vy1,2));
        double sin1 = Vy1/Math.sqrt(Math.pow(Vx1,2)+Math.pow(Vy1,2));
        //球b速度方向上的正余弦值
        double cos2 = Vx2/Math.sqrt(Math.pow(Vx2,2)+Math.pow(Vy2,2));
        double sin2 = Vy2/Math.sqrt(Math.pow(Vx2,2)+Math.pow(Vy2,2));
        */
        //根据向量来解出圆心连线方向上的速度分量沿x，y轴的分量。。。
        //有点绕口。。。就是将小球分解成沿圆心连线方向和垂直圆心连线方向两个分量，再将沿着
        //连线方向的分量分解为沿着x，y轴的两个分量a和b，然后对其进行对心碰撞的速度转换
        /*
        公式如下：
        Vx_=(Vx1*(Px2-Px1)^2 - Vy1*(Py2-Py1)*(Px2-Px1))/((Px2-Px1)^2 - (Py2-Py1)^2)
        Vy_=(Vx1*(Py2-Py1)*(Px2-Px1) - Vy1*(Py2-Py1)^2)/((Px2-Px1)^2 - (Py2-Py1)^2)

           v1 = [(m1-m2)v10 + 2m2v20] / (m1+m2)
           v2 = [(m2-m1)v20 + 2m1v10] / (m1+m2)
        */
        double Vx_1 = (Vx1*Math.pow(Px2-Px1,2) - Vy1*(Py2-Py1)*(Px2-Px1))/Math.pow(Px2-Px1,2) - Math.pow(Py2-Py1,2);
        double Vy_1 = Vy1*(Py2-Py1)*(Px2-Px1) - (Vx1*Math.pow(Py2-Py1,2))/Math.pow(Px2-Px1,2) - Math.pow(Py2-Py1,2);
        double Vx_2 = (Vx2*Math.pow(Px2-Px1,2) - Vy2*(Py2-Py1)*(Px2-Px1))/Math.pow(Px2-Px1,2) - Math.pow(Py2-Py1,2);
        double Vy_2 = Vy2*(Py2-Py1)*(Px2-Px1) - (Vx2*Math.pow(Py2-Py1,2))/Math.pow(Px2-Px1,2) - Math.pow(Py2-Py1,2);


        double Vx_1_after = ((M1-M2)*Vx_1 + 2*M2*Vx_2)/(M1+M2);
        double Vx_2_after = ((M2-M1)*Vx_2 + 2*M1*Vx_1)/(M1+M2);
        double Vy_1_after = ((M1-M2)*Vy_1 + 2*M2*Vy_2)/(M1+M2);
        double Vy_2_after = ((M2-M1)*Vy_2 + 2*M1*Vy_1)/(M1+M2);

        a.vx = a.vx - Vx_1 + Vx_1_after;
        a.vy = a.vy - Vy_1 + Vy_1_after;
        b.vx = b.vx - Vx_2 + Vx_2_after;
        b.vy = b.vy - Vy_2 + Vy_2_after;
    }

    public boolean collinsion(Ball ball_1, Ball ball_2){
        float R1 = ball_1.radius;
        float R2 = ball_2.radius;
        float absX = Math.abs(ball_1.cx - ball_2.cx);
        float absY = Math.abs(ball_1.cy - ball_2.cy);
        double Rr = Math.pow(R1 + R2,2);
        double Dd = Math.pow(absX,2)+Math.pow(absY,2);
        if (Rr >= Dd){
            return true;
        }
        return false;
    }

    public void doMath(double M1,double M2,double v1,double v2){

    }
}