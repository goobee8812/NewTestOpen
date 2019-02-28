package com.example.locate.newtestopen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Test";
    private Button gray_btn;
    private Button junzhi_btn;
    private Button gaosi_btn;
    private Button ruihua_btn;
    private ImageView img;

    private Bitmap srcBitmap;
    private Bitmap grayBitmap;
    private static boolean flag = true;
    private static boolean isFirst = true;

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView)findViewById(R.id.img);
        gray_btn = (Button)findViewById(R.id.gray_btn);
        junzhi_btn = (Button)findViewById(R.id.junzhi_btn);
        gaosi_btn = (Button)findViewById(R.id.gaosi_btn);
        ruihua_btn = (Button)findViewById(R.id.ruihua_btn);
        gray_btn.setOnClickListener(this);
        junzhi_btn.setOnClickListener(this);
        gaosi_btn.setOnClickListener(this);
        ruihua_btn.setOnClickListener(this);

        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hh);
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i("TAG", "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i("TAG", "加载失败");
                    break;
            }
        }
    };



    public void procSrc2Gray(){
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i("TAG", "procSrc2Gray sucess...");
    }

    /**
     * Sobel算计计算边缘
     * @return
     */
    public Bitmap getSobel(){
        Mat src = new Mat(srcBitmap.getHeight(),srcBitmap.getWidth(), CvType.CV_8UC4);
        Mat grayMat = new Mat();
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();
        Mat sobel = new Mat();
        Utils.bitmapToMat(srcBitmap,src);
        Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

        Bitmap processedImage = Bitmap.createBitmap(sobel.cols(), sobel.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(sobel, processedImage);
        return processedImage;
    }

    public Bitmap getJunzhi(){
        Mat src = new Mat(srcBitmap.getHeight(),srcBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(srcBitmap,src);
        //均值模糊
        Imgproc.blur(src, src, new Size(12,12));
        Log.d(TAG, "getJunzhi: ");
        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, processedImage);
        return processedImage;
    }

    public Bitmap getGaosi(){
        Mat src = new Mat(srcBitmap.getHeight(),srcBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(srcBitmap,src);
        //高斯模糊
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 0);
        Log.d(TAG, "getGaosi: ");
        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, processedImage);
        return processedImage;
    }

    public Bitmap getRuihua(){
        Mat src = new Mat(srcBitmap.getHeight(),srcBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(srcBitmap,src);
        //锐化
        Mat kenrl = new Mat(3, 3, CvType.CV_16SC1);
        kenrl.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Imgproc.filter2D(src, src, src.depth(), kenrl);
        Log.d(TAG, "getRuihua: ");
        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, processedImage);
        return processedImage;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gray_btn:
                if(flag){
                    procSrc2Gray();
                    img.setImageBitmap(grayBitmap);
                    gray_btn.setText("原图");
                    flag = false;
                }else{
                    img.setImageBitmap(srcBitmap);
                    gray_btn.setText("灰度化");
                    flag = true;
                }
                break;
            case R.id.junzhi_btn:
                img.setImageBitmap(getJunzhi());
                break;
            case R.id.gaosi_btn:
                img.setImageBitmap(getGaosi());
                break;
            case R.id.ruihua_btn:
                img.setImageBitmap(getSobel());
                break;
            default:
                break;
        }
    }


    public class ProcessClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(flag){
                procSrc2Gray();
                img.setImageBitmap(grayBitmap);
                gray_btn.setText("原图");
                flag = false;
            }else{
                img.setImageBitmap(srcBitmap);
                gray_btn.setText("灰度化");
                flag = true;
            }
        }
    }
}
