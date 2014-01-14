package vision;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.*;
import org.opencv.core.Point;


public class ImageProcessor {
        
        private static final int numBuffers = 3;
        private List<Mat> buffers = null;
        
        static {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
        
        public ImageProcessor() {
                // Fill "buffers" with however many intermediate images that you will need
                // to get from "rawImage" to "processedImage" in the "process" method
                buffers = new ArrayList<Mat>();
                for (int i = 0; i < numBuffers; i++) {
                        buffers.add(new Mat(new Size(), CvType.CV_8UC3));
                        buffers.add(new Mat(new Size(), CvType.CV_8UC1));
                        buffers.add(new Mat(new Size(), CvType.CV_8UC1));
                        buffers.add(new Mat(new Size(), CvType.CV_8UC1));
                }
        }

        // Input: an image from the camera
        // Output: the OpenCV-processed image
        
        // (In practice it's a little different:
        //  the output image will be for your visual reference,
        //  but you will mainly want to output a list of the locations of detected objects.)
        public void process(Mat rawImage, Mat processedImage) {
        		Mat circles = new Mat();
                Imgproc.cvtColor(rawImage, buffers.get(0), Imgproc.COLOR_BGR2HSV);
                Core.split(buffers.get(0), buffers.subList(1,  4));
                Core.inRange(buffers.get(0), new Scalar(38, 50, 0), new Scalar(198, 255, 255), buffers.get(5)); //Green
                Core.inRange(buffers.get(0), new Scalar (0, 50, 50), new Scalar(6, 255, 255), buffers.get(6)); //Vaguely shitty red 1
                Core.inRange(buffers.get(0), new Scalar(175, 50, 50, 0), new Scalar(179, 255, 255), buffers.get(7)); //Vaguely shitty red 2
                Core.bitwise_or(buffers.get(6), buffers.get(7), buffers.get(6)); // Red
                Mat array255=new Mat(buffers.get(0).height(),buffers.get(0).width(),CvType.CV_8UC1);  
                array255.setTo(new Scalar(255)); 
                Mat S = buffers.get(2);
                Mat V = buffers.get(3);  
                Core.subtract(array255, S, S);  
                Core.subtract(array255, V, V);  
                S.convertTo(S, CvType.CV_32F);  
                V.convertTo(V, CvType.CV_32F);  
                Core.magnitude(S, V, buffers.get(10));  
                Core.inRange(buffers.get(10),new Scalar(0.0), new Scalar(200.0), buffers.get(9));  
                Core.bitwise_and(buffers.get(7), buffers.get(9), buffers.get(7));  
                Core.bitwise_or(buffers.get(5), buffers.get(6), buffers.get(7)); //Red or green
                Imgproc.blur(buffers.get(7), buffers.get(7), new Size(9,9));
                Imgproc.HoughCircles(buffers.get(7), circles, Imgproc.CV_HOUGH_GRADIENT, 2, buffers.get(6).height()/4, 500, 50, 0, 0);
                Imgproc.blur(circles, processedImage, new Size(9,9));
                int rows = circles.rows();
                System.out.println(rows);
                int elemSize = (int)circles.elemSize();
                System.out.println(elemSize);
                float[] data = new float[rows * elemSize/4];
                if (data.length>0){
                	System.out.println("Found circles.");
                	circles.get(0, 0, data);
                	for(int i=0; i<data.length; i=i+3){
                		Point center = new Point(data[i], data[i+1]);
                		Core.ellipse(processedImage,  center,  new Size((double)data[i+2], (double)data[i+2]), 0, 0, 360, new Scalar(255, 0, 255), 4, 8, 0);
                	}
                }
        }
        
}