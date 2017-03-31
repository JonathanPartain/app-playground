package com.playground.playground;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // paint object
    private Paint paintRects;
    private Paint paintBall;
    private Paint paintScore;
    private Paint paintCenterLine;

    SurfaceHolder holder;
    Canvas canvas;
    PongClass pongClassView;

    int height;
    int width;

    int paddleWidth;
    int paddleHeight;

    // bottom player
    // top right
    float botPaddlex1;
    float botPaddley1;

    // bottom right
    float botPaddlex2;
    float botPaddley2;

    // top player

    float topPaddlex1;
    float topPaddley1;
    float topPaddlex2;
    float topPaddley2;

    // ballpositions
    int ballX;
    int ballY;

    float ballspeedx;
    float ballspeedy;

    // score for top and bottom
    int scoreTop = 0;
    int scoreBot = 0;

    // fps stuff
    long lastFrameTime;
    int fps;

    // state boolean
    volatile boolean playing;

    // screen things
    Display display;
    Point size;

    // ball radius
    int ballr = 30;
    // ball movements
    boolean goLeft;
    boolean goRight;
    boolean goUp;
    boolean goDown;

    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pongClassView = new PongClass(this);
        setContentView(pongClassView);

        display = getWindowManager().getDefaultDisplay();

        size = new Point();

        display.getSize(size);
        width = size.x;
        height = size.y;

        // bottom paddle dimenstions
        paddleWidth = 300;
        paddleHeight = 75;
        // top player dimensions
        topPaddlex1 = (width/2)-(paddleWidth/2);
        topPaddley1 = (paddleHeight);

        topPaddlex2 = (width/2)+(paddleWidth/2);
        topPaddley2 = 2*paddleHeight;

        // bottom player dimensions
        botPaddlex1 = width/2-paddleWidth/2;
        botPaddley1 = height-2*paddleHeight;


        botPaddlex2 = width/2+paddleWidth/2;
        botPaddley2 = height-3*paddleHeight;

        // ball initial start pos

        ballX = width / 2;
        ballY = (height / 2) - paddleHeight;


        // Toast for testing
        Context context = getApplicationContext();
        CharSequence text = "Bounce!";
        int duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, text, duration);



    }

    class PongClass extends SurfaceView implements Runnable {

        Thread mythread = null;


        public PongClass(Context context) {
            super(context);

            holder = getHolder();
            paintRects = new Paint();
            paintBall  = new Paint();
            paintScore = new Paint();
            paintCenterLine = new Paint();
            // create Paint and set color

            paintRects.setColor(Color.GRAY);
            paintBall.setColor(Color.RED);
            paintScore.setColor(Color.GREEN);
            paintScore.setTextSize(50);
            paintCenterLine.setColor(Color.GRAY);
            paintCenterLine.setStyle(Paint.Style.STROKE);


        }

        @Override
        public void run() {
            setBall();
            while (playing) {
                updateThings();
                drawThings();
                controlFPS();

            }


        }


        public void updateThings() {

            // TODO: Add collision with paddles
            // collide with bottom paddle

            if (ballX+(ballr) > botPaddlex1 &&
                ballX+(ballr) < botPaddlex2   ) {

                if (ballY >= botPaddley2-ballr &&
                    ballY <= botPaddley1    ) {
                    goDown = false;
                    goUp = true;
                    ballspeedx += 0.1;
                    ballspeedy += 0.1;
                }
            }

            // collide with top paddle
            if (ballX+(ballr) > topPaddlex1 &&
                ballX+(ballr) < topPaddlex2    ) {
                if (ballY < topPaddley2 + ballr &&
                    ballY > topPaddley2 - (paddleHeight+ballr)    ) {
                    goUp = false;
                    goDown = true;
                    ballspeedx += 0.1;
                    ballspeedy += 0.1;
                }


            }


            // check bounds

            // hit top
            if (ballY <= ballr) {
                scoreBot += 1;
                setBall();

            }
            // hit bottom
            if (ballY >= height - paddleHeight) {
                scoreTop += 1;
                setBall();


            }

            // hit right
            if (ballX +ballr >= width) {
                goRight = false;
                goLeft = true;
            }

            // hit left
            if (ballX -ballr <= 0) {
                goRight = true;
                goLeft = false;
            }



            if (goLeft) {
                ballX -= ballspeedx;
            }
            if (goRight) {
                ballX += ballspeedx;
            }
            if (goDown) {
                ballY += ballspeedy;
            }
            if (goUp) {
                ballY -= ballspeedy;
            }


        }

        public void drawThings() {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.BLUE);
                // draw center line
                canvas.drawLine(0, height/2-(3*ballr), width, height/2-(3*ballr), paintCenterLine);
                canvas.drawCircle(width/2, height/2-(3*ballr), 64, paintCenterLine);

                // top and bottom rects
                // bottom
                canvas.drawRect(botPaddlex1, botPaddley1, botPaddlex2, botPaddley2, paintRects);

                // top
                canvas.drawRect(topPaddlex1, topPaddley1, topPaddlex2, topPaddley2, paintRects);
                // draw ball
                canvas.drawCircle(ballX, ballY, ballr, paintBall);

                // draw score
                canvas.drawText("Score: "+scoreBot, 40, height/2 + 40,  paintScore);
                // scale to rotate
                canvas.scale(-1f, -1f);
                canvas.drawText("Score: "+scoreTop, -(width)+40, -((height/2)-260), paintScore);
                canvas.scale(1f, 1f);

                // unlock and post
                holder.unlockCanvasAndPost(canvas);

            }

        }

        public void setBall() {
            Random random = new Random();
            do {
                ballspeedx = random.nextFloat();
            } while (ballspeedx < 0.5);

            do {
                ballspeedy = random.nextFloat();
            } while (ballspeedy < 0.5);


            int up = random.nextInt(2);
            int right =  random.nextInt(2);

            if (up == 0) {
                goDown = true;
                goUp = false;
            } else {
                goDown = false;
                goUp = true;
            }

            if (right == 0) {
                goRight = false;
                goLeft = true;
            } else {
                goRight = true;
                goLeft = false;
            }


            ballspeedy *= 13;
            ballspeedx *= 13;


            ballX = width / 2;
            ballY = height / 2 - paddleHeight;
        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis()-lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;

            if (timeThisFrame > 0) {
                fps = ( int ) (1000/timeThisFrame);
            }

            if (timeToSleep > 0) {
                try {
                    mythread.sleep(timeToSleep);
                } catch (InterruptedException e) { /*Nothing*/}
            }
            lastFrameTime = System.currentTimeMillis();
        }

        public void pause() {
            playing = false;
            try {
                mythread.join();
            }
            catch (InterruptedException e) {}
        }
        public void resume() {
            playing =  true;
            mythread = new Thread(this);
            mythread.start();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = 0;
        int y = 0;
        int num = event.getPointerCount();

        // for every touch event

        for(int a = 0; a<num;a++) {
            x = ( int ) event.getX(event.getPointerId(a));
            y = ( int ) event.getY(event.getPointerId(a));

            // bottom half
            if (y > height / 2) {
                botPaddlex1 = x - paddleWidth/2;
                botPaddlex2 = x + paddleWidth/2;
            }
            // top half
            if (y < height / 2) {
                topPaddlex1 = x - paddleWidth/2;
                topPaddlex2 = x + paddleWidth/2;
            }
        }



        return true;


    }

    @Override
    protected void onStop() {
        super.onStop();

        while (true) {
            pongClassView.pause();
            break;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pongClassView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pongClassView.resume();

    }


}
