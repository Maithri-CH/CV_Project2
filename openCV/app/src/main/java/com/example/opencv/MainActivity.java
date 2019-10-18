package com.example.opencv;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2, AdapterView.OnItemSelectedListener {

    // will point to our View widget for our image
    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "OCVSample::Activity";

    //class variables dealing with Spinner for Image Processing algorithm selection
    Spinner spinner_menu;
    //grab array of possible menu items from strings.xml file
    String[] menu_items;
    String menu_item_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        //Load in the OpenCV dependency module code from the jni files you linked in this project
        // inside the OpenCV module
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(MainActivity.this, "Unable to load OpenCV", Toast.LENGTH_LONG).show();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //SPINNER SETUP setup menu from strings.xml file
        this.menu_items = getResources().getStringArray(R.array.spinner_menu);
        this.menu_item_selected = menu_items[0]; //initialize to first item in arry
        Log.i("SPINNER", "menu item is " + this.menu_item_selected);
        //grab a handle to spinner_menu in the XML interface
        spinner_menu = (Spinner) findViewById(R.id.spinner_menu);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_menu, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_menu.setAdapter(adapter);
        spinner_menu.setSelection(0);//initialize to first item in menu

        //set this activity to listen to the menu choice in spinner
        spinner_menu.setOnItemSelectedListener(this);

        // grab a "handle" to the OpenCV class responsible for viewing Image
        // look at the XML the id of our CameraBridgeViewBase is HelloOpenCVView
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        //set it visible, register the listener and enbale the view so connected to camera
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this); // the activity will listen to events on Camera -call onCameraFrame
        mOpenCvCameraView.enableView();
    }

    // disable JavaCameraView if app going on pause
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    //enable the JavaCameraView if app resuming
    @Override
    public void onResume() {
        super.onResume();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.enableView();
    }


    //Disable view of JavaCameraView if app is being destoryed
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    //method invoked when camera view is started
    public void onCameraViewStarted(int width, int height) {

    }


    //method invoked when camera view is stoped
    public void onCameraViewStopped() {

    }

    // THIS IS THE main method that is called each time you get a new Frame/Image
    // Implement to be a CVCameraViewListener2
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        //store in the imageMat (instance of OpenCV's Mat class, a 2D matrix) the RGB(A=alpha) image
        Mat imageMat = inputFrame.rgba();

        //Process Image as desired --note the following is commented out but gives you an idea
        // now you use the Mat class to represent the Image and you can use method calls
        // like imageMat
        // make calls like to get a pixel at i,j imageMat.get
        // double pixel[] = new double[3];
        // pixel = imageMat.get(20,10); this wil retrieve pixel and column = 20, row =10
        // similarly can set a pixel in Mat via imageMat.set(i,j,pixel);
        // read API on Mat class for OPenCV
        // A VERY USEFUL class to call image processing routines is ImagProc
        // This code in comments shows how to do the Sobel Edge Detection on our image in imageMat
        /*
            Mat gray = inputFrame.gray();
            Mat mIntermediateMat = new Mat();
            Imgproc.Sobel(gray, mIntermediateMat, CvType.CV_8U, 1, 1);
            Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);
            Imgproc.cvtColor(mIntermediateMat, imageMat, Imgproc.COLOR_GRAY2BGRA, 4);
        */

        //START IMAGE PROCESSING OPTIONS

        //Using OPenCV create the greyscale image from the current input color image
        Mat gray = inputFrame.gray();


        //SELECT IMAGE PROCESSING ALGORITHM
        // Based on spinner menu selected item stored as this.menu_item_selected perform appropriate
        // operation and return a Mat
        if (this.menu_item_selected.equals("Random")) {   //OPTION RANDOM

            //return imageMat;
            //create random number 0 to 1 and return color if < .5 and grey otherwise
            Random rand = new Random(System.currentTimeMillis());
            if (rand.nextDouble() < 0.5) {
                Log.d("SPINNER", "return color");
                return imageMat;
            } else {
                Log.d("SPINNER", "return greyscale");
                return gray;
            }
        } else if (this.menu_item_selected.equals("Greyscale")) { //OPTION GREYSCALE
            Log.d("SPINNER", "return greyscale");
            return gray;
        } else if (this.menu_item_selected.equals("Threshold")) {  //OPTION THRESHOLD urrently always threshold the greyscale image at value of 100
            /* FROM OPENCV DOCUMENTATION :   threshold(Mat src,Mat dst,double thresh, double maxval,int type)
               The function applies fixed-level thresholding to a single-channel array.
               The function is typically used to get a bi-level (binary) image out of a grayscale image
                type = THRESH_BINARY
                   if src(x,y) > thresh  then dest(x,y) = maxval; 0 otherwise
            */
            Log.i("SPINNER", "performing thresholding");
            Imgproc.threshold(gray, gray, 100.0, 255.0, Imgproc.THRESH_BINARY);
            return gray;

        } else if (this.menu_item_selected.equals("Mean Blur")) { //OPTION MeanBlur
            Imgproc.blur(imageMat,imageMat, new Size(15,15));
            Log.i("SPINNER", "performing mean blur");
            return imageMat;
        }  else if (this.menu_item_selected.equals("Gaussian Blur")) { //OPTION GaussianBlur
            Imgproc.GaussianBlur(imageMat,imageMat, new Size(15,15),0);
            Log.i("SPINNER", "performing Gaussian blur");
            return imageMat;
        } else if (this.menu_item_selected.equals("Dilation")) { //OPTION Dilate
            Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.dilate(imageMat, imageMat, kernelDilate);
            Log.i("SPINNER", "performing Dilate");
            return imageMat;
        } else if (this.menu_item_selected.equals("Erosion")) { //OPTION Erosion
            Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
            Imgproc.erode(imageMat, imageMat, kernelDilate);
            Log.i("SPINNER", "performing Erosion");
            return imageMat;
        } else if (this.menu_item_selected.equals("Adaptive Threshold")) { //OPTION AdaptiveThreshold
            Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
            Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 0);
            Log.i("SPINNER", "performing AdaptiveThreshold");
            return imageMat;
        } else if (this.menu_item_selected.equals("Difference Of Gaussian")) { //OPTION DifferenceOfGaussian
            Mat blur1 = new Mat();
            Mat blur2 = new Mat();

            Imgproc.cvtColor(imageMat,imageMat,Imgproc.COLOR_BGR2GRAY);

            //Bluring the images using two different blurring radius
            Imgproc.GaussianBlur(imageMat,blur1,new Size(15,15),5);
            Imgproc.GaussianBlur(imageMat,blur2,new Size(21,21),5);

            //Subtracting the two blurred images
            Mat DoG = new Mat();
            Core.absdiff(blur1, blur2,DoG);

            //Inverse Binary Thresholding
            Core.multiply(DoG,new Scalar(100), DoG);
            Imgproc.threshold(DoG,DoG,50,255,Imgproc.THRESH_BINARY_INV);

            return DoG;
        } else if (this.menu_item_selected.equals("Canny Edge")) { //OPTION CannyEdge
            //Mat grayMat = new Mat();
            Mat cannyEdges = new Mat();
            //Converting the image to grayscale
            Imgproc.cvtColor(imageMat,imageMat,Imgproc.COLOR_BGR2GRAY);

            Imgproc.Canny(imageMat, cannyEdges,10, 100);
            return cannyEdges;
        } else if (this.menu_item_selected.equals("Sobel Edge")) { //OPTION SobelEdge
            Mat sobel = new Mat(); //Mat to store the result

            //Mat to store gradient and absolute gradient respectively
            Mat grad_x = new Mat();
            Mat abs_grad_x = new Mat();

            Mat grad_y = new Mat();
            Mat abs_grad_y = new Mat();

            //Converting the image to grayscale
            Imgproc.cvtColor(imageMat,imageMat,Imgproc.COLOR_BGR2GRAY);

            //Calculating gradient in horizontal direction
            Imgproc.Sobel(imageMat, grad_x,CvType.CV_16S, 1,0,3,1,0);

            //Calculating gradient in vertical direction
            Imgproc.Sobel(imageMat, grad_y,CvType.CV_16S, 0,1,3,1,0);

            //Calculating absolute value of gradients in both the direction
            Core.convertScaleAbs(grad_x, abs_grad_x);
            Core.convertScaleAbs(grad_y, abs_grad_y);

            //Calculating the resultant gradient
            Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);
            return sobel;
            //Converting Mat back to Bitmap
            //Utils.matToBitmap(sobel, currentBitmap);
            //loadImageToImageView();
        } else if(this.menu_item_selected.equals("Corner Detection"))
        {
            Log.i("SPINNER", "performing Dialation");
            //Imgproc.cornerHarris(gray,gray,2,3,0.04d);


            int thresh=200;
            Mat Harris_scene = new Mat();

            Mat harris_scene_norm = new Mat(), harris_object_norm = new Mat(), harris_scene_scaled = new Mat(), harris_object_scaled = new Mat();

            int blockSize = 9;
            int apertureSize = 5;
            double k = 0.1;

            Imgproc.cornerHarris(gray, Harris_scene,blockSize, apertureSize,k);

            Core.normalize(Harris_scene, harris_scene_norm, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC3, new Mat());

            Core.convertScaleAbs(harris_scene_norm, harris_scene_scaled);

            for( int j = 0; j < harris_scene_norm.rows() ; j++){
                for( int i = 0; i < harris_scene_norm.cols(); i++){
                    if ((int) harris_scene_norm.get(j,i)[0] > thresh){
                        Imgproc.circle(harris_scene_scaled, new Point(i,j), 5 , new Scalar(0), 2 ,8 , 0);
                    }
                }
            }

            return harris_scene_scaled;
        }
        else if (this.menu_item_selected.equals("Hough Line Transform")) {

            Log.d("SPINNER", "performing  Hough Line Transform");
            Mat dst = new Mat(), cdst = new Mat();
            Imgproc.Canny(imageMat, dst, 50, 200, 3, false);


            Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);



            Mat lines = new Mat(); // will hold the results of the detection
            Imgproc.HoughLines(dst, lines, 1, Math.PI/180, 150); // runs the actual detection

            for (int x = 0; x < lines.rows(); x++) {
                double rho = lines.get(x, 0)[0],
                        theta = lines.get(x, 0)[1];

                double a = Math.cos(theta), b = Math.sin(theta);
                double x0 = a*rho, y0 = b*rho;
                Point pt1 = new Point(Math.round(x0 + 1000*(-b)), Math.round(y0 + 1000*(a)));
                Point pt2 = new Point(Math.round(x0 - 1000*(-b)), Math.round(y0 - 1000*(a)));
                Imgproc.line(cdst, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            }



            return cdst;
        }

        else if (this.menu_item_selected.equals("Edgy")){
            Log.d("SPINNER", "performing  Edgy");


            //pixel data
            byte[] d00=new byte[4];
            byte[] d01=new byte[4];
            byte[] d02=new byte[4];
            byte[] d10=new byte[4];
            byte[] d11=new byte[4];
            byte[] d12=new byte[4];
            byte[] d20=new byte[4];
            byte[] d21=new byte[4];
            byte[] d22=new byte[4];

            //laplace Filter
            double laplace[]=new double[]{
                    -1.0, -1.0, -1.0,
                    -1.0, 8.0, -1.0,
                    -1.0, -1.0, -1.0
            };


            int threshold_value = 200;
            int threshold_pixel=0;




            for(int col=1;col<gray.cols()-1;col++){
                for(int row=1;row<gray.rows()-1;row++){
                    gray.get(row-1,col-1,d00);
                    gray.get(row-1,col,d01);
                    gray.get(row-1,col+1,d02);
                    gray.get(row,col-1,d10);
                    gray.get(row,col,d11);
                    gray.get(row,col+1,d12);
                    gray.get(row+1,col-1,d20);
                    gray.get(row+1,col,d21);
                    gray.get(row+1,col+1,d22);

                    //laplacian of image
                    double r =laplace[0]*d00[0]+laplace[1]*d01[0]+ laplace[2]*d02[0]
                            +laplace[3]*d10[0]+ laplace[4]*d11[0]+laplace[5]*d12[0]+
                            laplace[6]*d20[0]+laplace[7]*d21[0]+ laplace[8]*d22[0];

                    double g =laplace[0]*d00[0]+laplace[1]*d01[0]+ laplace[2]*d02[0]
                            +laplace[3]*d10[0]+ laplace[4]*d11[0]+laplace[5]*d12[0]+
                            laplace[6]*d20[0]+laplace[7]*d21[0]+ laplace[8]*d22[0];

                    double b =laplace[0]*d00[0]+laplace[1]*d01[0]+ laplace[2]*d02[0]
                            +laplace[3]*d10[0]+ laplace[4]*d11[0]+laplace[5]*d12[0]+
                            laplace[6]*d20[0]+laplace[7]*d21[0]+ laplace[8]*d22[0];

                    double a =laplace[0]*d00[0]+laplace[1]*d01[0]+ laplace[2]*d02[0]
                            +laplace[3]*d10[0]+ laplace[4]*d11[0]+laplace[5]*d12[0]+
                            laplace[6]*d20[0]+laplace[7]*d21[0]+ laplace[8]*d22[0];

                    r=Math.min(255,Math.max(0,r));
                    g=Math.min(255,Math.max(0,g));
                    b=Math.min(255,Math.max(0,b));
                    a=Math.min(255,Math.max(0,a));

//  (binary) image out of a grayscale image
                    if(r>threshold_value)
                        threshold_pixel=255;
                    else
                        threshold_pixel=0;


                    if(threshold_pixel==255)
                    {
                        //  bitwise AND between the original color image and  the binary_edge pixel value 255
                        d00[0]=(byte) (d00[0] & 255);
                        d00[1]=(byte) (d00[1] & 0);
                        d00[2]=(byte) (d00[2] & 0);
                        d00[3]=(byte) (d00[3] & 1);
                        imageMat.put(row,col,255,0,0,1.0);
                    }
                    else {
                        imageMat.put(row,col,d00[0],d00[1],d00[2],d00[3]);
                    }
                }
            }

            return imageMat;

        }

        else if (this.menu_item_selected.equals("Best Lines")){
            Log.d("SPINNER", "performing  Best Lines");
           /*
           System.out.println("RGB image channels "+imageMat.channels() + "type " +imageMat.type() + "depth " +imageMat.depth());
           System.out.println("Grayscale image channels "+gray.channels() + "type " +gray.type() + "depth " +gray.depth());
           System.out.println("Grayscale" + gray);
           System.out.println("RGB" + imageMat);
           */

            //gray is grayscale image mat ,grayscale image mat is of type 8UC1
            Mat result= new Mat(gray.height(),gray.cols(),CvType.CV_8UC1);
            //int threshold=50;
            int threshold_pixel=0;
            int count=0;
            //pixel data
            byte[] d00=new byte[4];
            byte[] d01=new byte[4];
            byte[] d02=new byte[4];
            byte[] d10=new byte[4];
            byte[] d11=new byte[4];
            byte[] d12=new byte[4];
            byte[] d20=new byte[4];
            byte[] d21=new byte[4];
            byte[] d22=new byte[4];

            //laplace Filter
            double laplace[]=new double[]{
                    -1.0, -1.0, -1.0,
                    -1.0, 8.0, -1.0,
                    -1.0, -1.0, -1.0
            };

            for(int col=1;col<gray.cols()-1;col++) {
                for (int row = 1; row < gray.rows() - 1; row++) {
                    gray.get(row - 1, col - 1, d00);
                    gray.get(row - 1, col, d01);
                    gray.get(row - 1, col + 1, d02);
                    gray.get(row, col - 1, d10);
                    gray.get(row, col, d11);
                    gray.get(row, col + 1, d12);
                    gray.get(row + 1, col - 1, d20);
                    gray.get(row + 1, col, d21);
                    gray.get(row + 1, col + 1, d22);

                    //applying laplace transformation
                    double r = laplace[0] * d00[0] + laplace[1] * d01[0] + laplace[2] * d02[0]
                            + laplace[3] * d10[0] + laplace[4] * d11[0] + laplace[5] * d12[0] +
                            laplace[6] * d20[0] + laplace[7] * d21[0] + laplace[8] * d22[0];

                    double g = laplace[0] * d00[0] + laplace[1] * d01[0] + laplace[2] * d02[0]
                            + laplace[3] * d10[0] + laplace[4] * d11[0] + laplace[5] * d12[0] +
                            laplace[6] * d20[0] + laplace[7] * d21[0] + laplace[8] * d22[0];

                    double b = laplace[0] * d00[0] + laplace[1] * d01[0] + laplace[2] * d02[0]
                            + laplace[3] * d10[0] + laplace[4] * d11[0] + laplace[5] * d12[0] +
                            laplace[6] * d20[0] + laplace[7] * d21[0] + laplace[8] * d22[0];

                    double a = laplace[0] * d00[0] + laplace[1] * d01[0] + laplace[2] * d02[0]
                            + laplace[3] * d10[0] + laplace[4] * d11[0] + laplace[5] * d12[0] +
                            laplace[6] * d20[0] + laplace[7] * d21[0] + laplace[8] * d22[0];

                    r = Math.min(255, Math.max(0, r));
                    g = Math.min(255, Math.max(0, g));
                    b = Math.min(255, Math.max(0, b));
                    a = Math.min(255, Math.max(0, a));

                    if(r>50)
                        threshold_pixel=255;
                    else
                        threshold_pixel=0;


                    if(threshold_pixel==255)
                    {
                        //  bitwise AND between the original color image and  the binary_edge pixel value 255
                        d00[0]=(byte) (d00[0] & 255);
                        d00[1]=(byte) (d00[1] & 0);
                        d00[2]=(byte) (d00[2] & 0);
                        d00[3]=(byte) (d00[3] & 1);
                        result.put(row,col,255,0,0,1.0);
                    }
                    else {
                        result.put(row,col,d00[0],d00[1],d00[2],d00[3]);
                    }
                }
            }
            //Imgproc.threshold(result,result,10.0,255.0,Imgproc.THRESH_BINARY);

            Mat lines = new Mat(); // will hold the results of the detection

            //threshold value is be 1/4th the minimum of the width and height of the image
            int threshold_value = Math.min(gray.cols(),gray.rows())/4;
            Imgproc.HoughLines(result, lines, 1, Math.PI/180,threshold_value); // runs the actual detection


            //cycle through detected Hough Lines
            for (int x = 0; x < lines.rows(); x++) {
                double rho = lines.get(x, 0)[0],
                        theta = lines.get(x, 0)[1];

                double a = Math.cos(theta), b = Math.sin(theta);
                double x0 = a*rho, y0 = b*rho;
                double x1,y1,x2,y2;
                x1=Math.round(x0 + 1000*(-b));
                y1= Math.round(y0 + 1000*(a));
                x2=Math.round(x0 - 1000*(-b));
                y2= Math.round(y0 - 1000*(a));
                double slope=(y2-y1)/(x2-x1);
                Point pt1 = new Point(x1,y1);
                Point pt2 = new Point(x2,y2);
                Imgproc.line(imageMat, pt1, pt2, new Scalar(0, 0,255), 1);

                // choose lines that are within +/- 30 degrees  tan 30deg = 0.57
                if(slope<=0.57){
                    //choose the top 10 lines.
                    if(count<=10) {
                        //draw the selected lines on top of prev image
                        Imgproc.line(imageMat, pt1, pt2, new Scalar(255,0, 0, 1), 2);
                        count++;
                    }
                }
            }
            return imageMat;

        }

        else { // OPTION OTHERS - for now return color for all other choices
            return gray;
        }
        //Return the Mat you want to be displayed in the JavaCameraView widget which invoked this method
        //return imageMat;
    }

    //Spinner Menu Selection response method
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        this.menu_item_selected = parent.getItemAtPosition(pos).toString();
        Log.i("SPINNER", "choice is" + this.menu_item_selected);
    }


    //Spinner Menu Selected method if nothing selected --initializes to first algorithm in item list
    public void onNothingSelected(AdapterView<?> parent) {
        this.menu_item_selected = this.menu_items[0];
    }


}
