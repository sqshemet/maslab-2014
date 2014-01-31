package vision;

import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class BallFinder extends Thread {
	public Map.Entry<Point, Float> ball;
	@Override
	public void run() {
		BlobDetect detector = new BlobDetect();
		while (true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Mat frame = detector.getFrame();
			ball = detector.getClosestBall(frame);
			
		}
	}

}
