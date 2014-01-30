package Test;

import comm.MapleComm;
import comm.MapleIO;

import devices.sensors.Ultrasonic;
import devices.sensors.Encoder;

public class TestSonar {
	
	public static void main(String[] args) {
		new TestSonar();
		System.exit(0);
	}

	public TestSonar() {
		
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		Ultrasonic hedgehog = new Ultrasonic(6, 7);
		Encoder enc1 = new Encoder(27, 28);
		Encoder enc2 = new Encoder(31, 32);

		comm.registerDevice(hedgehog);
		comm.registerDevice(enc1);
		comm.registerDevice(enc2);
		comm.initialize();
		System.out.println("comm initialized");

		while (true) {
			comm.updateSensorData();
			//System.out.println(hedgehog.getDistance());
			System.out.println("left: " + enc1.getDeltaAngularDistance());
			System.out.println("right: " + enc2.getDeltaAngularDistance());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
		}
	}
}
