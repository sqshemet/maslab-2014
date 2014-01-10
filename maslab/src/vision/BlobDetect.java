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
import java.util.List;

import javax.swing.*;  
import org.opencv.core.Core;  
import org.opencv.core.CvType;
import org.opencv.core.Mat;   
import org.opencv.core.Point;   
import org.opencv.core.Scalar;  
import org.opencv.core.Size;  
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
           if(matBGR.type() == BufferedImage.TYPE_3BYTE_BGR){
        	   image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);  
           }
           else {
        	   image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
           }
           final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
           System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);  
           long endTime = System.nanoTime();  
           System.out.println(String.format("Elapsed time: %.2f ms", (float)(endTime - startTime)/1000000));  
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
           Mat mRgba=new Mat();  
           Mat mHSV=new Mat();  
           Mat green = new Mat();
           Mat red1 = new Mat();
           Mat thresholded = new Mat();
           Mat circles = new Mat();
           inputframe.copyTo(mRgba);  
           inputframe.copyTo(mHSV);
           Mat red = new Mat(mHSV.height(), mHSV.width(), CvType.CV_8UC1);
           inputframe.copyTo(red);
           Imgproc.cvtColor( mRgba, mHSV, Imgproc.COLOR_BGR2HSV); 
           List<Mat> lhsv = new ArrayList<Mat>(3);  
           Core.split(mHSV, lhsv);
           Core.inRange(mHSV, new Scalar(38, 50, 0), new Scalar(198, 255, 255), green);
           Core.inRange(mHSV, new Scalar(0, 50, 50), new Scalar(6, 255, 255), red);
           Core.inRange(mHSV, new Scalar(175, 50, 50), new Scalar(179, 255, 255), red1);
           Core.bitwise_or(red, red1, thresholded);
          // Core.bitwise_or(red, green, thresholded);
           Imgproc.blur(thresholded, thresholded, new Size(9,9));
           Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height()/4, 500, 50, 0, 0);
           int rows = circles.rows();
           int elemSize = (int)circles.elemSize();
           float[] data = new float[rows * elemSize/4];
           if (data.length>0){
        	   System.out.println("Detected circles");
        	   circles.get(0, 0, data);
        	   for(int i=0; i<data.length; i=i+3){
        		   Point center = new Point(data[i], data[i+1]);
        		   Core.ellipse(thresholded, center, new Size((double)data[i+2], (double)data[i+2]), 0, 0, 260, new Scalar(255, 0, 255), 4, 8, 0);
        	   }
           }
           return thresholded;  
      }  
 }  
 public class BlobDetect {  
      public static void main(String arg[]){  
       // Load the native library.    
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
 }  