package Test;

import comm.MapleComm;
import comm.MapleIO;

import devices.sensors.Ultrasonic;

public class TestSonar {
	
	public static void main(String[] args) {
		new TestSonar();
		System.exit(0);
	}

	public TestSonar() {
		
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		 Ultrasonic hedgehog = new Ultrasonic(6, 7);

		comm.registerDevice(hedgehog);
		comm.initialize();
		System.out.println("comm initialized");

		while (true) {
			comm.updateSensorData();
			System.out.println(hedgehog.getDistance());
			comm.transmit();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
}
