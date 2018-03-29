package org.opencv.samples.facedetect;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import android.graphics.Color;
import android.support.v7.widget.*;
import org.opencv.core.Point;
import org.opencv.samples.facedetect.R;

import java.util.List;
import java.util.ArrayList;
public class Keerthy extends Activity implements View.OnTouchListener,CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    private Mat mRgba1;
    private Size mSize0;
    TextView touch_coordinates;
    TextView touch_color;
    double x = -1;
    double y = -1;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private  Mat mIntermediateMat;
    int ks;
    private Mat mSepiaKernel;
    private MatOfInt mHistSize;
    private int                  mHistSizeNum = 25;
    private MatOfFloat mRanges;
    private MatOfInt             mChannels[];
    private Scalar               mColorsRGB[];
    private Scalar               mColorsHue[];
    private Point                mP1;
    private Point                mP2;
    private float                mBuff[];
    private Mat                  mMat0;

    private List<Rect> ListOfRect = new ArrayList<Rect>();
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(Keerthy.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keerthy);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        touch_coordinates=(TextView) findViewById(R.id.touch_coordinates);
        touch_color=(TextView) findViewById(R.id.touch_color);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        ks = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }



    @Override
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat();
        mRgba1= new Mat();
        mBlobColorRgba=new Scalar(255);
        mBlobColorHsv=new Scalar(255);
        mIntermediateMat = new Mat();
        mSize0 = new Size();

        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mColorsHue = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        };

        mP1 = new Point();
        mP2 = new Point();
        mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);
        mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);
        mSepiaKernel.put(2, 0, /* B */0.131f, 0.534f, 0.272f, 0f);
        mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        switch(ks)
        {
            case 0:

                break;
            case 1: {
                Size sizeRgba = mRgba.size();
                Mat hist = new Mat();
                int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
                if (thikness > 5) thikness = 5;
                int offset = (int) ((sizeRgba.width - (5 * mHistSizeNum + 4 * 10) * thikness) / 2);
                // Value and Hue
                Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV_FULL);

                // Hue
                Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[0], mMat0, hist, mHistSize, mRanges);
                Core.normalize(hist, hist, sizeRgba.height / 2, 0, Core.NORM_INF);
                hist.get(0, 0, mBuff);
                for (int h = 0; h < mHistSizeNum; h++) {
                    mP1.x = mP2.x = offset + (4 * (mHistSizeNum + 10) + h) * thikness;
                    mP1.y = sizeRgba.height - 1;
                    mP2.y = mP1.y - 2 - (int) mBuff[h];
                    Imgproc.line(mRgba, mP1, mP2, mColorsHue[h], thikness);
                }
                break;

            }
            case 2:
            {
                Mat gray = inputFrame.gray();
                Imgproc.Sobel(gray, mIntermediateMat, CvType.CV_8U, 1, 1);
                Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);

                break;
            }
            case 3:
            {
                org.opencv.core.Size s = new Size(11,11);
                Imgproc.GaussianBlur(mRgba, mRgba,s,2 );
                Imgproc.Canny(mRgba, mIntermediateMat, 80, 90);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);

                break;
            }
            case 4:
            {

                Size sizeRgba = mRgba.size();

                int rows = (int) sizeRgba.height;
                int cols = (int) sizeRgba.width;

                Mat zoomCorner = mRgba.submat(0, 200, 0, 266);
                Mat mZoomWindow = mRgba.submat(190, 280, 260,370);
                Imgproc.resize(mZoomWindow, zoomCorner, zoomCorner.size());
                Size wsize = mZoomWindow.size();
                Imgproc.rectangle(mZoomWindow, new Point(1, 1), new Point(wsize.width - 2, wsize.height - 2), new Scalar(255, 0, 0, 255), 2);
                break;
            }
            case 5:
            {
                Size sizeRgba = mRgba.size();


                Mat hist = new Mat();
                int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
                if(thikness > 5) thikness = 5;
                int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);
                // RGB
                for(int c=0; c<3; c++) {
                    Imgproc.calcHist(Arrays.asList(mRgba), mChannels[c], mMat0, hist, mHistSize, mRanges);
                    Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
                    hist.get(0, 0, mBuff);
                    for(int h=0; h<mHistSizeNum; h++) {
                        mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
                        mP1.y = sizeRgba.height-1;
                        mP2.y = mP1.y - 2 - (int)mBuff[h];
                        Imgproc.line(mRgba, mP1, mP2, mColorsRGB[c], thikness);
                    }
                }

                break;
            }
            case 6:
            {
                Mat rgbaInnerWindow;
                if( (x-70<0) || (y-70) < 0 || (x+70) >= mRgba.cols() || (y+70) >= mRgba.rows())
                {
                    return mRgba;
                }
                int rows = (int)x;
                int cols = (int)y;

                rgbaInnerWindow = mRgba.submat(cols-70,cols+70,rows-70,rows+70);
                Imgproc.resize(rgbaInnerWindow, mIntermediateMat, mSize0, 0.1, 0.1, Imgproc.INTER_NEAREST);
                Imgproc.resize(mIntermediateMat, rgbaInnerWindow, rgbaInnerWindow.size(), 0., 0., Imgproc.INTER_NEAREST);
                break;
            }
            case  7:
            {
                Mat rgbaInnerWindow;
                Size sizeRgba = mRgba.size();

                int rows = (int) sizeRgba.height;
                int cols = (int) sizeRgba.width;

                int left = cols / 8;
                int top = rows / 8;

                int width = cols * 3 / 4;
                int height = rows * 3 / 4;

                rgbaInnerWindow = mRgba.submat(top, top + height, left, left + width);
                Core.transform(rgbaInnerWindow, rgbaInnerWindow, mSepiaKernel);

            }

        }


        return mRgba;
    }

    public void applyCanny(View v)
    {

        if(ks!=3)
        ks = 3;
        else
            ks=0;


    }
    public void drawHue(View v)
    {


        if(ks!=1)
            ks = 1;
        else
            ks=0;


    }
    public void applySobel(View v)
    {
        if(ks!=2)
            ks = 2;
        else
            ks=0;


    }
    public void applySepia(View v)
    {

        if(ks!=7)
            ks = 7;
        else
            ks=0;


    }
    public void applyNormal(View v)
    {


        ks = 0;


    }
    public void applyZoomIn(View v)
    {

        if(ks!=4)
            ks = 4;
        else
            ks=0;


    }
    public void applyHist(View v)
    {

        if(ks!=5)
            ks = 5;
        else
            ks=0;

    }
    public void applyCensor(View v)
    {


        if(ks!=6)
            ks = 6;
        else
            ks=0;


    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int cols = mRgba.cols();
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


