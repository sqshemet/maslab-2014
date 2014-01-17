package controls;

import comm.MapleComm;

import devices.actuators.Cytron;
import devices.sensors.Encoder;
import devices.sensors.Gyroscope;

public class Drive{
	MapleComm comm;
	Cytron leftMotor;
	Cytron rightMotor;
	Encoder leftEnc;
	Encoder rightEnc;
	Gyroscope gyro;
	//double distP = .8;
	static final double ANGLE_P = .5;
	static final double BIAS = .4;
	static final double TURN_BIAS = .3;

	public Drive(MapleComm mComm, Cytron leftM, Cytron rightM, 
			Encoder leftE, Encoder rightE, Gyroscope scope){
		comm = mComm;
		leftMotor = leftM;
		rightMotor = rightM;
		leftEnc = leftE;
		rightEnc = rightE;
		gyro = scope;
	}

	public void driveForward(Double distance){
		/**
		 * drive forward given distance in inches
		 */
		//80ish, 100ish, 150ish
		//60ish, 70ish, 50ish
		//it is a map to the treasure
		double totalDist = 0.0;
		double remaining = distance - totalDist;
		while (remaining > 0.83){
			double rightDist = rightEnc.getDeltaAngularDistance()*.323;
			double leftDist = leftEnc.getDeltaAngularDistance()*.323;
			double angleDiff = (rightDist - leftDist);
			double power = ANGLE_P*angleDiff;
			if (remaining > 1.0){
				leftMotor.setSpeed(BIAS + power);
				rightMotor.setSpeed(BIAS - power);
			} else {
				leftMotor.setSpeed(BIAS*remaining + power);
				rightMotor.setSpeed(BIAS*remaining - power);
			} 
			totalDist += (rightDist+leftDist)/2.0;
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
		while (Math.abs(diff)>.1){
			if (Math.abs(diff) > .5){
				leftMotor.setSpeed(-1*Math.signum(diff)*TURN_BIAS);
				rightMotor.setSpeed(Math.signum(diff)*TURN_BIAS);
			} else {
				leftMotor.setSpeed(-1*diff*TURN_BIAS);
				rightMotor.setSpeed(diff*TURN_BIAS);
			} 
			comm.updateSensorData();
			currentAngle += gyro.getDeltaAngle();
			diff = angle-currentAngle;
		}
	}
}
