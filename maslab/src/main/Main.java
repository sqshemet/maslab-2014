package main;

import java.util.Map;
import java.util.Random;

import comm.MapleComm;
import vision.BallFinder;
import vision.BlobDetect;
import comm.MapleIO;

import devices.actuators.Cytron;
import devices.sensors.Encoder;
import devices.sensors.Gyroscope;
import devices.sensors.Ultrasonic;
import org.opencv.core.*;

import controls.PID;

public class Main {

	public static void main(String[] args) {
		new Main();
		System.exit(0);
	}

	public Main() {
		
		/*
		 * Create your Maple communication framework by specifying what kind of 
		 * serial port you would like to try to autoconnect to.
		 */
		// MapleComm comm = new MapleComm(MapleIO.SerialPortType.SIMULATION);
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		/*
		 * Create an object for each device. The constructor arguments specify
		 * their pins (or, in the case of the gyroscope, the index of a fixed
		 * combination of pins).
		 * Devices are generally either Sensors or Actuators. For example, a
		 * motor controller is an actuator, and an encoder is a sensor.
		 */
		Cytron motor1 = new Cytron(3, 2); //left motor  (blue dir, yellow pwm)
		Cytron motor2 = new Cytron(1, 0); //right motor (blue dir, yellow pwm)
		Ultrasonic ultra = new Ultrasonic(6, 7);
//		Ultrasonic ultra2 = new Ultrasonic(36, 34);
//		Gyroscope gyro = new Gyroscope(1, 9);
		Encoder enc1 = new Encoder(27, 28); //left encoder  (pinA, pinB)
		Encoder enc2 = new Encoder(31, 32); //right encoder (pinA, pinB)

		/*
		 * Build up a list of devices that will be sent to the Maple for the
		 * initialization step.
		 */
		comm.registerDevice(motor1);
		comm.registerDevice(motor2);
		comm.registerDevice(ultra);
//		comm.registerDevice(ultra2);
//		comm.registerDevice(gyro);
		comm.registerDevice(enc1);
		comm.registerDevice(enc2);


		// Send information about connected devices to the Maple
		comm.initialize();
		System.out.println("comm initialized");
		
		//Initialize motor control class
		PID driver = new PID(comm, motor1, motor2, enc1, enc2, ultra);
		System.out.println("driver initialized");
		// driver.driveForward(18.0); //drive forward 2 feet
		//driver.turnToPoint(-Math.PI/2.0);
		//driver.driveToward(24, -2);
		motor1.setSpeed(0);
		motor2.setSpeed(0);
		comm.transmit();
		System.out.println("lolol main bitchez");
		
	//	BlobDetect ballHandler = new BlobDetect();
		//BallFinder finder = new BallFinder();
		//Begins looking for balls, updating global variable ball with ball
		//finder.start();
		//Map.Entry<Point, Float> closestBall = finder.ball;
		//System.out.println(closestBall); 
		Map.Entry<Point, Float> closestBall = null;
			
		while (true) {
			
			// Request sensor data from the Maple and update sensor objects accordingly
			comm.updateSensorData();
			
			// All sensor classes have getters.
			//System.out.println(gyro.getOmega() + " " + ultra1.getDistance());
			//System.out.println(ultra1.getDistance() + " " + ultra2.getDistance());
			//System.out.println(enc.getTotalAngularDistance() + " " + enc.getAngularSpeed());
			
			// All actuator classes have setters.
			//motor1.setSpeed(0.2);
			//motor2.setSpeed(-0.3);

			// Request that the Maple write updated values to the actuators
			//comm.transmit();
			
			// Just for console-reading purposes; don't worry abtout timing
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
			/*if (closestBall != null){
				//Drive toward closest ball
				double distance = ballHandler.distanceToBall(closestBall);
				int orientation  = ballHandler.orientation2(closestBall);
				//This is stupid as shit and will lead to a drunk robot.
				System.out.println("Distance: " + distance);
				System.out.println("Orientation: " + orientation);
				//driver.driveToward(distance, orientation);
			}  */
			if (closestBall == null){
				Random rand = new Random();
				int randomInt = rand.nextInt(3);
				int sign = rand.nextInt(2);
				if (sign == 0){
					randomInt *= -1;
				}
				//Randomly drive without hitting walls
				double randomAng = randomInt + rand.nextFloat();
				driver.turnToPoint(randomAng);
				driver.driveForward(36.0);
			}
		}
	}
}

