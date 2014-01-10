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


public class ImageProcessor {
        
        private static final int numBuffers = 2;
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
                Core.inRange(buffers.get(0), new Scalar(38, 50, 0), new Scalar(198, 255, 255), buffers.get(3)); //Green
                Core.inRange(buffers.get(0), new Scalar (0, 50, 50), new Scalar(6, 255, 255), buffers.get(1)); //Vagely shitty red 1
                Core.inRange(buffers.get(0), new Scalar(175, 50, 50, 0), new Scalar(179, 255, 255), buffers.get(2)); //Vaguely shitty red 2
                Core.bitwise_or(buffers.get(1), buffers.get(2), buffers.get(1)); // Red
                Core.bitwise_or(buffers.get(1), buffers.get(3), buffers.get(1)); //Red or green
                Imgproc.blur(buffers.get(1), buffers.get(6), new Size(9,9));
                Imgproc.HoughCircles(buffers.get(6), circles, Imgproc.CV_HOUGH_GRADIENT, 2, buffers.get(6).height()/4, 500, 50, 0, 0);
                Imgproc.blur(buffers.get(6), processedImage, new Size(9,9));
                System.out.println(circles);
        }
        
}