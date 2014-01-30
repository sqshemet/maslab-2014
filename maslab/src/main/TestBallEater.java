package main;

import comm.MapleComm;
import comm.MapleIO;
import controls.Drive;

import devices.actuators.Cytron;
import devices.sensors.Encoder;

public class TestBallEater {
	
	public static void main(String[] args) {
		new Main();
		System.exit(0);
	}

	public TestBallEater() {
		
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		Cytron balleater = new Cytron(9, 8); //balleater motor (green dir, blue pwm)

		comm.registerDevice(balleater);

		comm.initialize();
		System.out.println("comm initialized");

		while (true) {
			balleater.setSpeed(2.0);
			comm.transmit();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
	}
}
