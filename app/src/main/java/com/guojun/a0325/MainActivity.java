/*
*

*
#                       _oo0oo_
#                      o8888888o
#                      88" . "88
#                      (| -_- |)
#                      0\  =  /0
#                    ___/`---'\___
#                  .' \\|     |// '.
#                 / \\|||  :  |||// \
#                / _||||| -:- |||||- \
#               |   | \\\  -  /// |   |
#               | \_|  ''\---/''  |_/ |
#               \  .-\__  '-'  ___/-. /
#             ___'. .'  /--.--\  `. .'___
#          ."" '<  `.___\_<|>_/___.' >' "".
#         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
#         \  \ `_.   \_ __\ /__ _/   .-` /  /
#     =====`-.____`.___ \_____/___.-`___.-'=====
#                       `=---='
#
#
#     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#                  佛祖保佑  永无bug
#
*
* */

package com.guojun.a0325;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.guojun.a0325.utils.UiUtil;

import java.util.ArrayList;

import static com.guojun.a0325.utils.UiUtil.initialize;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private ArrayList<Square> mSquareList ;
    private Square mSquare;
    private BallView mBallView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_main);
        mSquare = new Square(this);
        mSquareList = new ArrayList<>();
        mSquare.setBackgroundResource(R.drawable.square);
        mBallView = new BallView(getBaseContext());
        initialize(this);
        addContentView(mBallView,new FrameLayout.LayoutParams(UiUtil.getScreenWidth(),UiUtil.getScreenHeight()));
      //  addContentView(mSquare,new ActionBar.LayoutParams(Gravity.CENTER));
      //  Init();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    public void Init(){
        mSquareList = new ArrayList<>();
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
        //        addSquare();
                return false;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

    public void addSquare(){
        TranslateAnimation mTranslationAnimator = new TranslateAnimation(0,1000,0, UiUtil.getScreenWidth());
        mTranslationAnimator.setDuration(3000);
        mSquare.startAnimation(mTranslationAnimator);
        mSquareList.add(mSquare);

    }

    public class Square extends FrameLayout{

        public Square(Context context) {
            super(context);
        }

    }
}
