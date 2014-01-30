package main;

import comm.MapleComm;
import comm.MapleIO;

import devices.actuators.DigitalOutput;

public class TestBallEater {
	
	public static void main(String[] args) {
		new TestBallEater();
		System.exit(0);
	}

	public TestBallEater() {
		
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		 DigitalOutput ballEater = new DigitalOutput(35); //ballEater gate pin (green; pin 35)
		 DigitalOutput pullyThings = new DigitalOutput(37);

		comm.registerDevice(ballEater);
		comm.registerDevice(pullyThings);
		comm.initialize();
		System.out.println("comm initialized");
		
		int iters = 0;

		while (iters < 3) {
			ballEater.setValue(true);
			comm.transmit();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ballEater.setValue(false);
				comm.transmit();
			}
			iters++;
		}
		ballEater.setValue(false);
		comm.transmit();
		System.out.println("done, bitch.");
	}
}
