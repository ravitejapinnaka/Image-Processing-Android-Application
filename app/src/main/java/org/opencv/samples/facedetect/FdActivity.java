package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import static org.opencv.samples.facedetect.R.id.Keerthy;
import static org.opencv.samples.facedetect.R.id.image;

public class FdActivity extends Activity implements View.OnTouchListener,CvCameraViewListener2, View.OnClickListener {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private Mat                    mRgba;
    private Mat                    mGray;
    double x = -1;
    double y = -1;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    TextView touch_coordinates,touch_color;




    private CameraBridgeViewBase   mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(FdActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);
        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        touch_color = (TextView) findViewById(R.id.touch_color);
        Button Ravi = (Button) findViewById(R.id.Ravi);
        Ravi.setOnClickListener(this);
        Ravi.setEnabled(true);
        Button Keerthy = (Button) findViewById(R.id.Keerthy);
        Keerthy.setOnClickListener(this);
        Keerthy.setEnabled(true);


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        //mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Ravi:       getRavi();break;
            case Keerthy:       getKeerthy();break;
            default:break;
        }
    }

    public void getRavi(){
        Intent intent_h = new Intent(this,Ravi.class);
        startActivity(intent_h);
    }


    public void getKeerthy(){
        Intent intent_h = new Intent(this,Keerthy.class);
        startActivity(intent_h);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        //mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgba = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

      public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        return mRgba;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int cols = mRgba.rows();
        int rows = mRgba.rows();

        double yLow = (double)mOpenCvCameraView.getHeight()*0.2401961;
        double yHigh = (double)mOpenCvCameraView.getHeight()*0.7696078;

        double xScale = (double)cols / (double)mOpenCvCameraView.getWidth();
        double yScale = (double)rows / (yHigh - yLow);

        x = event.getX();
        y = event.getY();

        y = y- yLow;
        x = x * xScale;
        y = y * yScale;

        if((x<0) || (y<0) || (x>cols) || (y>rows)) return false;

        touch_coordinates.setText("X:" +Double.valueOf(x)+",Y:" +Double.valueOf(y) );

        Rect touchedRect=new Rect();

        touchedRect.x=(int)x;
        touchedRect.y=(int)y;

        touchedRect.width=8;
        touchedRect.height=8;

        Mat touchedRegionRgba=mRgba.submat(touchedRect);

        Mat touchedRegionHSV=new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHSV, Imgproc.COLOR_RGB2HLS_FULL);

        mBlobColorHsv= Core.sumElems(touchedRegionHSV);
        int PointCount= touchedRect.width*touchedRect.height;
        for (int i=0; i< mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= PointCount;
        mBlobColorRgba= convertScalarHsv2Rgba(mBlobColorHsv);
        touch_color.setText("Color: #"+String.format("%02X",(int)mBlobColorRgba.val[0])
                +String.format("%02X",(int)mBlobColorRgba.val[1])
                +String.format("%02X",(int)mBlobColorRgba.val[2]));

        touch_color.setTextColor(Color.rgb((int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1] ,
                (int) mBlobColorRgba.val[2]));

        touch_coordinates.setTextColor(Color.rgb((int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1] ,
                (int) mBlobColorRgba.val[2]));


        return false;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba=new Mat();
        Mat pointMatHsv=new Mat(1,1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv,pointMatRgba,Imgproc.COLOR_HSV2RGB_FULL,4);

        return new Scalar(pointMatRgba.get(0,0));
    }
}
