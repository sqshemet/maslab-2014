package controls;

import comm.MapleComm;

import devices.actuators.Cytron;
import devices.sensors.Encoder;
import devices.sensors.Gyroscope;
import devices.sensors.Ultrasonic;

public class PID{
	MapleComm comm;
	Cytron leftMotor;
	Cytron rightMotor;
	Encoder leftEnc;
	Encoder rightEnc;
	Ultrasonic frontSonar;
	//Gyroscope gyro;
	//double distP = .8;` 
	static final double ANGLE_P = .2;
	static final double BIAS = .13;
	static final double TURN_BIAS = .1;
	static double P = .4;
	static double I = 1;

	public PID(MapleComm mComm, Cytron leftM, Cytron rightM, 
			Encoder leftE, Encoder rightE, Ultrasonic ultra /*Gyrotscope scope*/){
		comm = mComm;
		leftMotor = leftM;
		rightMotor = rightM;
		leftEnc = leftE;
		rightEnc = rightE;
		frontSonar = ultra;
		//gyro = scope;angle
	}

	public void driveForward(Double distance){
		/**
		 * drive forward given distance in inches
		 */
		//80ish, 100ish, 150ish
		//60ish, 70ish, 50ish
		//it is a map to the treasure
		double wall;
		comm.updateSensorData();
		double travelled = 0.0;
		double remaining = distance;
		double totalError = 0.0;
		while (remaining > 1){
			wall = frontSonar.getDistance();
			System.out.println("wall :" + wall);
			if (wall < 12 && wall != 0){
				System.out.println("Sonar if");
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				comm.transmit();
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			double rightDist = rightEnc.getDeltaAngularDistance()*2.5;
			double leftDist = leftEnc.getDeltaAngularDistance()*2.5;
			double angleDiff = (rightDist + leftDist);
			double power = .7*(P*angleDiff + I*totalError);
			System.out.println("Power:");
			System.out.println(power);
			System.out.println("leftDist :" + leftDist);
			System.out.println("rightDist :" + rightDist);
			leftMotor.setSpeed(-BIAS - power);
			rightMotor.setSpeed(BIAS-power);
			/*if (remaining > 1.0){
				leftMotor.setSpeed(BIAS + power);
				rightMotor.setSpeed(BIAS - power);
			} else {
				leftMotor.setSpeed(BIAS*remaining + power);
				rightMotor.setSpeed(BIAS*remaining - power);
			} */
			comm.transmit();
			comm.updateSensorData();
			travelled = (rightDist-leftDist)/2.0;
			remaining -= travelled;
			System.out.println("Travelled: " + travelled);
			System.out.println("Remaining:" + remaining);
			totalError += angleDiff;
			/*try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		comm.transmit();
	}
	
	public void turnToPoint(double angle){
		/**
		 * turn given angle in radians
		 */
		System.out.println("TurntoPoint");
		double currentAngle = 0;
		angle = angle-Math.PI;
		double diff = angle-currentAngle;
		double totalError = diff;
		while (Math.abs(diff)>.1){
			
			if (Math.abs(diff) > .5){
				leftMotor.setSpeed(-1*Math.signum(diff)*TURN_BIAS);
				rightMotor.setSpeed(Math.signum(diff)*TURN_BIAS);
			} else {
				leftMotor.setSpeed(-1*diff*TURN_BIAS);
				rightMotor.setSpeed(diff*TURN_BIAS);
			} 
			comm.updateSensorData();
			//currentAngle += gyro.getDeltaAngle();
			diff = angle-currentAngle;
		}
	}

	public void driveToward(double distance, int orientation) {
		if (orientation == -3){
			turnToPoint(-2.356);
		}
		else if (orientation == -2){
			turnToPoint(-1.57);
			
		}
		else if (orientation == -1){
			turnToPoint(-.785);
			
		}
		else if (orientation == 0){
			
		}
		else if (orientation == 1){
			turnToPoint(.785);
			
		}
		else if (orientation == 2){
			turnToPoint(1.57);
		}
		else if (orientation == 3){
			turnToPoint(2.356);
			
		}
		else{
			
		}
		driveForward(distance);
	}
}
