package vision;
/*  
  * Captures the camera stream with OpenCV 
  * Search for the faces  
  * Display a circle around the faces using Java
  */  
import java.awt.*; 
import java.awt.image.BufferedImage;  
import java.awt.image.DataBufferByte;  
import javax.swing.*;  
import org.opencv.core.Core;  
import org.opencv.core.Mat;  
import org.opencv.core.MatOfRect;  
import org.opencv.core.Point;  
import org.opencv.core.Rect;  
import org.opencv.core.Scalar;  
import org.opencv.core.Size;  
import org.opencv.highgui.VideoCapture;  
import org.opencv.imgproc.Imgproc;  
import org.opencv.objdetect.CascadeClassifier;  
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
           image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);  
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
           //g.drawString("This is my custom Panel!",10,20);  
      }  
 }  
 class processor {  
      public Mat detect(Mat inputframe){ 
           Mat mRgba=new Mat();  
           Mat mHSV=new Mat();  
           inputframe.copyTo(mRgba);  
           inputframe.copyTo(mHSV);  
           Imgproc.cvtColor( mRgba, mHSV, Imgproc.COLOR_BGR2HSV);  
          // Imgproc.equalizeHist( mHSV, mHSV );  
           return mRgba;  
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