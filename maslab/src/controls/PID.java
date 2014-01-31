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
	//double distP = .8;
	static final double ANGLE_P = .3;
	static final double BIAS = .1;
	static final double TURN_BIAS = .1;
	static double P = 2;
	static double I = 4;

	public PID(MapleComm mComm, Cytron leftM, Cytron rightM, 
			Encoder leftE, Encoder rightE, Ultrasonic ultra /*Gyroscope scope*/){
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
		double travelled = 0.0;
		double remaining = distance - travelled;
		double totalError = remaining;
		double wall = frontSonar.getDistance();
		if (wall < remaining){
			driveForward(wall-.5);
			turnToPoint(.785);
		}
		while (remaining > 0.83){
			double rightDist = rightEnc.getDeltaAngularDistance()*.323;
			double leftDist = leftEnc.getDeltaAngularDistance()*.323;
			double angleDiff = (rightDist - leftDist);
			double power = P*remaining + I*totalError;
			leftMotor.setSpeed(BIAS + power);
			rightMotor.setSpeed(BIAS-power);
			//double power = ANGLE_P*angleDiff;
			/*if (remaining > 1.0){
				leftMotor.setSpeed(BIAS + power);
				rightMotor.setSpeed(BIAS - power);
			} else {
				leftMotor.setSpeed(BIAS*remaining + power);
				rightMotor.setSpeed(BIAS*remaining - power);
			} */
			comm.transmit();
			travelled += (rightDist+leftDist)/2.0;
			remaining = distance - travelled;
			totalError += remaining;
			comm.updateSensorData();
		}
	}
	
	public void turnToPoint(double angle){
		/**
		 * turn given angle in radians
		 */
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
