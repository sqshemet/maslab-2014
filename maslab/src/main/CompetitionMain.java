package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
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
	    //Code here
		botclient.send("a", "State", "Ball Search");
		botclient.send("b", "Gyro", "3.14159");
		
		// Smiley
		ArrayList<Mat> mats = new ArrayList<Mat>();
		ArrayList<BufferedImage> bufs = new ArrayList<BufferedImage>();
		Mat image1 = Highgui.imread("/home/sqshemet/ariel1.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
		mats.add(image1);
		for(Mat m : mats)
			{
			bufs.add(MatToBufferedImage(m));
		}
		
		botclient.sendImage(bufs.get(1));
	    
		botclient.close();
}

	public static BufferedImage MatToBufferedImage(Mat matBGR){  
		BufferedImage image;
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
	   // System.out.println(String.format("Elapsed time: %.2f ms", (float)(endTime - startTime)/1000000));  
	    return image;  
	}  
}

