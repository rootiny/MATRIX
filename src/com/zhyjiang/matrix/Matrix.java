package com.zhyjiang.matrix;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by zhyjiang on 11/30/15.
 */
public class Matrix extends WallpaperService {

    private final Handler mHandler = new Handler();
    private Movie mMovie;
    private volatile boolean mPaused = false;
    private long mMovieStart;
    private int mCurrentAnimationTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mMovie = Movie.decodeStream(getResources().openRawResource(
                R.raw.number_rain));

//        TypedValue value = new TypedValue();
//        getResources().getValue(R.raw.number_rain, value, true);
//        File file = new File(value.string.toString());
//        FileInputStream fileInputStream ;
//        try {
//            fileInputStream = new FileInputStream(file);
//            mMovie = Movie.decodeStream(fileInputStream);
//
//        } catch (FileNotFoundException e){
//            e.printStackTrace();
//            Toast.makeText(Matrix.this, value.string.toString() + " not found !!", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMovie = null;
    }

    @Override
    public Engine onCreateEngine() {
        return new MatrixEngine();
    }

    class MatrixEngine extends Engine{

        /**
         * 默认为1秒
         */
        private static final int DEFAULT_MOVIE_DURATION = 1000;
        private float mOffset;
        private float mTouchX = -1;
        private float mTouchY = -1;
        private long mStartTime;
        private float mCenterX;
        private float mCenterY;
        private float mWidth;
        private float mHight;
        private float mScale;
        private boolean mVisible;
        private final Runnable mDrawMatrix = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        public MatrixEngine() {
            mStartTime = SystemClock.elapsedRealtime();

        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawMatrix);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawMatrix);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchX = event.getX();
                mTouchY = event.getY();
            } else {
                mTouchX = -1;
                mTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            mOffset = xOffset;
            drawFrame();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mWidth = width/1.0f;
            mHight = height/1.0f;
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawMatrix);
        }

        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // draw something
                    if (mMovie != null) {
                        if (!mPaused) {
                            updateAnimationTime();
                            drawMovieFrame(canvas);
                        } else {
                            drawMovieFrame(canvas);
                        }
                    }
                }
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }

//             Reschedule the next redraw
            mHandler.removeCallbacks(mDrawMatrix);
            if (mVisible) {
                mHandler.postDelayed(mDrawMatrix, 10);
            }
        }

        private void updateAnimationTime() {
            long now = android.os.SystemClock.uptimeMillis();
            // 如果第一帧，记录起始时间
            if (mMovieStart == 0) {
                mMovieStart = now;
            }
            // 取出动画的时长
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = DEFAULT_MOVIE_DURATION;
            }
            // 算出需要显示第几帧
            mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
        }

        private void drawMovieFrame(Canvas canvas) {
            // 设置要显示的帧，绘制即可
            mMovie.setTime(mCurrentAnimationTime);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
//            canvas.scale(mScale, mScale);
//            mMovie.draw(canvas, mLeft / mScale, mTop / mScale);
            canvas.scale(1, 1);
            mMovie.draw(canvas, 0, 0);
            canvas.restore();
        }

    }


}
