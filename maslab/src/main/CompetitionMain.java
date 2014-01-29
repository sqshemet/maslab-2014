package main;

import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;

import BotClient.BotClient;
import controls.*;
import vision.*;


public class CompetitionMain {
	public static void main( String[] args ) {
		
		BotClient botclient = new BotClient("18.150.7.174:6667","1221",false);
		
		while( !botclient.gameStarted() ) {
		}
		System.out.println("***GAME STARTED***");
		System.out.println("MAP --> " + botclient.getMap());
   //  Load the native library.    
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    vision.BlobDetect.viewStream(); //Comment this out if you don't want the stream
	    Mat frame = vision.BlobDetect.getFrame();
	    //Mat frame = Highgui.imread("/home/sqshemet/blue_small.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
	    Map.Entry<Point, Float> ball = vision.BlobDetect.getClosestBall(frame);
	    double distance = vision.BlobDetect.distanceToBall(ball.getValue());
	    Point center = ball.getKey();
	    int orientation = vision.BlobDetect.orientation(center);
	
		botclient.close();
	}
}
