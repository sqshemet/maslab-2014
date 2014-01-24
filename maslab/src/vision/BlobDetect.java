package vision;
/*  
  * Captures the camera stream with OpenCV 
  * Search for the faces  
  * Display a circle around the faces using Java
  */  
import java.awt.*; 
import java.awt.image.BufferedImage;  
import java.awt.image.DataBufferByte;  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;  
import javax.swing.text.html.HTMLDocument.Iterator;

import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;  
import org.opencv.imgproc.Imgproc;  

 class My_Panel extends JPanel{  
      private static final long serialVersionUID = 1L;  
      private BufferedImage image;  
      // Create a constructor method  
      public My_Panel(){  
           super();   
      }  
      /**  
       * Converts/writes a Mat into a BufferedImage.  
       *   
       * @param matrix Mat of type CV_8UC3 or CV_8UC1  
       * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
       */  
      public boolean MatToBufferedImage(Mat matBGR){  
           long startTime = System.nanoTime();  
           int width = matBGR.width(), height = matBGR.height(), channels = matBGR.channels() ;  
           byte[] sourcePixels = new byte[width * height * channels];  
           matBGR.get(0, 0, sourcePixels);  
           // create new image and get reference to backing data 
           if(matBGR.type() == 16){
        	   image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);  
           }
           else {
        	   image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
           }
           final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
           System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);  
           long endTime = System.nanoTime();  
          // System.out.println(String.format("Elapsed time: %.2f ms", (float)(endTime - startTime)/1000000));  
           return true;  
      }  
      public void paintComponent(Graphics g){  
           super.paintComponent(g);   
           if (this.image==null) return;  
            g.drawImage(this.image,10,10,this.image.getWidth(),this.image.getHeight(), null);   
      }  
 }  
 class processor {  
      public Mat detect(Mat inputframe){ 
    	  // What do you want to see in the stream?
    	  // Initialize things yaaay!
           Mat mRgba=new Mat(); 
           inputframe.copyTo(mRgba);  
           List<MatOfPoint> contours = getContours(inputframe);
           HashMap<Point, Float> objects = getBlobs(contours);
           for(int i=0; i<objects.size(); i++){
        	   if (Imgproc.contourArea(contours.get(i)) > 50){
        		   Rect rect = Imgproc.boundingRect(contours.get(i));
           		   Core.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x+rect.width, rect.y+rect.height), new Scalar(0, 0, 255));
        	   }
        	  for (Map.Entry<Point, Float> entry : objects.entrySet())
        	  {
        		Point center = entry.getKey();
        		float radius = entry.getValue();
        		Core.circle(mRgba, center, (int)radius, new Scalar(0,0,255), 0);
        		   }
           }
           return mRgba; 
      }
      
      public Mat cropBelowBlue(Mat inputframe){
    	  return null;
      }
      public List<MatOfPoint> getContours(Mat inputframe){
    	  // Initialize things yaaay!
    	  inputframe = cropBlue(inputframe);
          Mat mRgba=new Mat();  
          Mat mHSV=new Mat();  
          Mat green = new Mat();
          Mat red = new Mat();
          Mat red1 = new Mat();
          Mat thresholded = new Mat();
          List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
          inputframe.copyTo(mRgba);  
          inputframe.copyTo(mHSV);
          inputframe.copyTo(red);
          // Convert to HSV
          Imgproc.cvtColor( mRgba, mHSV, Imgproc.COLOR_BGR2HSV); 
          //List<Mat> lhsv = new ArrayList<Mat>(3);  
          // Split into channels
          //Core.split(mHSV, lhsv);
          // Threshold over green and red (red wraps around)
          Core.inRange(mHSV, new Scalar(50, 60, 40), new Scalar(90, 110, 160), green); 
          Core.inRange(mHSV, new Scalar(0, 140, 95), new Scalar(10, 250, 210), thresholded);
          //Core.inRange(mHSV, new Scalar(171, 50, 50), new Scalar(180, 255, 255), red1);
          // Compound thresholded image
         // Core.bitwise_or(red, red1, thresholded);
          //Core.bitwise_or(red, green, thresholded);
          //Blur to reduce noise
          Mat blurred = new Mat();
          Imgproc.blur(thresholded, blurred, new Size(9,9));
          //Imgproc.drawContours(thresholded, contours, 1, new Scalar(0,0,255));
          //Filter small blobs
          Imgproc.erode(blurred, blurred, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,10)));       
          Imgproc.dilate(blurred, blurred, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)));
          //Find contours
          Imgproc.findContours(blurred, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
          return contours;
      }
      
      public HashMap<Point, Float> getBlobs(List<MatOfPoint> contours){
    	  //Takes list of contours, returns hashtable of centers and radii of objects
    	  HashMap<Point, Float> objects = new HashMap<Point, Float>();
    	  List<MatOfPoint2f> curves = new ArrayList<MatOfPoint2f>(contours.size());
          float[] radius = new float[contours.size()];
          for(int i=0; i<contours.size(); i++){
       	   MatOfPoint2f mMOP2f1 = new MatOfPoint2f();
       	   MatOfPoint2f mMOP2f2 = new MatOfPoint2f();
       	   contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
       	   Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, 3.0, true);
       	   curves.add(i, mMOP2f2);
       	   Point center = new Point();
       	   //I don't understand why the * radii is a float[], but it is. All we care about
       	   // is the first element.
       	   Imgproc.minEnclosingCircle(curves.get(i), center, radius);
       	   System.out.print(center);
       	   System.out.println(radius[0]);
       	   objects.put(center, radius[0]);
       	   	}
          return objects;
          }
      
      public Mat cropBlue(Mat inputframe){
    	  Mat mRgba = new Mat();
    	  Mat mHSV = new Mat();
    	  Mat noBlue = new Mat();
    	  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    	  Mat thresholded = new Mat();
    	  inputframe.copyTo(mRgba);
    	  inputframe.copyTo(mHSV);
    	  inputframe.copyTo(noBlue);
    	  Imgproc.cvtColor(mRgba, mHSV, Imgproc.COLOR_BGR2HSV);
    	  Core.inRange(mHSV,new Scalar(100, 160, 100), new Scalar(110, 220, 130), thresholded);
          //Core.inRange(mHSV, new Scalar(0, 140, 95), new Scalar(10, 250, 210), thresholded);
          Mat blurred = new Mat();
          Imgproc.blur(thresholded, blurred, new Size(9,9));
          //Filter small blobs
          Imgproc.erode(blurred, blurred, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,10)));       
          Imgproc.dilate(blurred, blurred, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)));
          Imgproc.findContours(blurred, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
         // Imgproc.drawContours(blurred, contours, 1, new Scalar(0,0,255));
          double maxContourArea = 0;
          int maxAreaIdx = 0;
          MatOfPoint maxContour = new MatOfPoint();
          for (int idx = 0; idx < contours.size(); idx++) 
          {
                MatOfPoint contour = contours.get(idx);
                double contourarea = Imgproc.contourArea(contour);
                if (contourarea > maxContourArea) 
                {
                         maxContour = contour;
                         maxContourArea = contourarea;
                         maxAreaIdx = idx;
                }
          }

          Point[] points_contour = maxContour.toArray();
          int nbPoints = points_contour.length; 
          Point minPoint = new Point(0, mRgba.height());
          for(int i=0; i< nbPoints;i++)
          {
                  if (points_contour[i].y < minPoint.y){
                	  minPoint = points_contour[i];
                  }
                  
          }
          noBlue = mRgba.submat(new Rect(0, (int)minPoint.y, 640,(int)(mRgba.height()-minPoint.y)));
          return noBlue;
    	  
          
    	  
      }

 }  

 public class BlobDetect { 
	  public static void viewStream(){
		// Mat image = Highgui.imread("/home/sqshemet/blue_small.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
		String window_name = "Capture - Blob detection";  
	    JFrame frame = new JFrame(window_name);  
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	    frame.setSize(400,400);  
	    processor my_processor=new processor();  
	    My_Panel my_panel = new My_Panel();  
	    frame.setContentPane(my_panel);       
	    frame.setVisible(true);        
	       //-- 2. Read the video stream  
	        Mat webcam_image=new Mat();  
	        VideoCapture capture =new VideoCapture(1);  
	    //frame.setSize(image.width()+40, image.height()+60);
	    //Mat no_blue = my_processor.cropBlue(image);
	    //my_panel.MatToBufferedImage(no_blue);
	    //my_panel.repaint();
	    if( capture.isOpened())  
	           {  
	            while( true )  
	            {  
	                 capture.read(webcam_image);  
	              if( !webcam_image.empty() )  
	               {   
	                    frame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
	                    //-- 3. Detect blobs
	                    webcam_image=my_processor.detect(webcam_image);  
	                   //-- 4. Display the image  
	                    my_panel.MatToBufferedImage(webcam_image); // We could look at the error...  
	                    my_panel.repaint();   
	               }  
	               else  
	               {   
	                    System.out.println(" --(!) No captured frame -- Break!");   
	                    break;   
	               }  
	              }  
	             }  
	             return;  
	  }
      public static void main(String arg[]){  
       // Load the native library.    
    	  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	  viewStream(); //Comment this out if you don't want the stream
    	  //Mat frame = getFrame();
    	  Mat frame = Highgui.imread("/home/sqshemet/blue_small.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
    	  Map.Entry<Point, Float> ball = getClosestBall(frame);
    	  double distance = distanceToBall(ball.getValue());
    	  Point center = ball.getKey();
    	  int orientation = orientation(center);
    
      } 
      
      public static Mat getFrame(){
    	  //Get next camera frame
    	  Mat webcam_image = new Mat();
    	  VideoCapture capture = new VideoCapture(1);
    	  if(capture.isOpened()){
    		  capture.read(webcam_image);
    		  return webcam_image;
    		  }
    	  else{
    		  System.out.println("No frame -- Break");
    		  return null;
    	  }
      }
      
      public static Map.Entry<Point, Float> getClosestBall(Mat frame){
    	  //Takes in frame, returns a point with the center and the radius of the largest object
    	  processor processor = new processor();
    	  Mat cropped = processor.cropBlue(frame);
    	  List<MatOfPoint> contours = processor.getContours(cropped);
    	  HashMap<Point, Float> objects = processor.getBlobs(contours);
    	  float maxRadius = 0;
    	  Map.Entry<Point, Float> closeCircle = null;
    	  for (Map.Entry<Point, Float> entry : objects.entrySet())
    	  {
    		float radius = entry.getValue();
    		if (radius > maxRadius){
    			maxRadius = radius;
    			closeCircle = entry;
    		}
    	  }
    	  return closeCircle;  
      }

      public static double distanceToBall(float radius){
    	  //Takes radius in px, returns distance in inches
    	  float focalLength = 350; //This is approximate. Range I got from calculations was like 310-390
    	  double ballRadius = .875;
    	  return ballRadius*focalLength/radius;
    	  
      }
      
      public static int orientation(Point center){
    	  //Takes in center of ball, returns 0 if approximately the center of view
    	  // -1 if left, 1 if right, 2 if error.
    	  if (center.x > 240 && center.x < 400){
    		  return 0;
    	  }
    	  else if (center.x < 240){
    		  return -1;
    	  }
    	  else if (center.x > 400){
    		  return 1;
    	  }
    	  return -2;
      }
 }  