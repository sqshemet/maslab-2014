package controls;

import comm.MapleComm;

import devices.actuators.Cytron;
import devices.sensors.Encoder;

public class Drive{
	MapleComm comm;
	Cytron leftMotor;
	Cytron rightMotor;
	Encoder leftEnc;
	Encoder rightEnc;
	//double distP = .8;
	double angleP = .5;
	double bias = .4;

	public Drive(MapleComm mComm, Cytron leftM, Cytron rightM, Encoder leftE, Encoder rightE){
		comm = mComm;
		leftMotor = leftM;
		rightMotor = rightM;
		leftEnc = leftE;
		rightEnc = rightE;
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
			double power = angleP*angleDiff;
			if (remaining > 1.0){
				leftMotor.setSpeed(bias + power);
				rightMotor.setSpeed(bias - power);
			} else {
				leftMotor.setSpeed(bias*remaining + power);
				rightMotor.setSpeed(bias*remaining - power);
			} totalDist += (rightDist+leftDist)/2.0;
			comm.updateSensorData();
		}
	}
	
	public void turnToPoint(double angle){
		/**
		 * turn given angle in radians
		 */
		throw new UnsupportedOperationException();  
	}
}
