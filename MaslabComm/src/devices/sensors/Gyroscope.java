package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Gyroscope extends Sensor {
	private static final double CONVERSION_FACTOR = (Math.PI / 180) / 80;
	private static final int ERROR_CODE = -32767;
	
	byte spiPort;
	byte ssPin;
	double omega;
	
	long lastUpdateTime;
	long deltaTime;
	
	double angle;
	double deltaAngle;
	
	/*
	 * The first argument is the index of an "SPI port". There are two on the Maple.
	 * One is pins 10-13, and the other is pins 31-34. (Read the bottom of the Maple.)
	 * The SS pin (pin 10 or pin 31) needs to be controlled manually, so you need to
	 * provide a fifth (digital) pin.
	 */
	public Gyroscope(int spiPort, int ssPin) {
		this.spiPort = (byte) spiPort;
		this.ssPin = (byte) ssPin;
		lastUpdateTime = System.nanoTime();
		deltaTime = 0;
		angle = 0;
		deltaAngle=0;
	}

	@Override
	public byte getDeviceCode() {
		return 'Y';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {spiPort, ssPin};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		long currentTime = System.nanoTime();
		byte msb = buff.get();
		byte lsb = buff.get();
		double new_omega = (msb * 256) + ((int) lsb & 0xff);
		deltaTime = currentTime - lastUpdateTime;
		if (new_omega != ERROR_CODE) {
			new_omega =  new_omega * CONVERSION_FACTOR;
			deltaAngle = (((new_omega - omega)/2.0)*deltaTime)*1000000000.0;
			angle = (angle+deltaAngle)%(2.0*Math.PI);
			omega = new_omega;
		lastUpdateTime = currentTime;
		}
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}
	
	// in radians per second
	public double getOmega() {
		return omega;
	}
	
	// in radians
	public double getAngleChangeSinceLastUpdate() {
		return deltaAngle;
	}
	
	// in radians
	public double getCurrentAngle() {
		return angle;
	}

}
