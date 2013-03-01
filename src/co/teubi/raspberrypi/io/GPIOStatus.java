package co.teubi.raspberrypi.io;

import java.util.ArrayList;

/**
 * Instantiate this class to store the internal state
 * of the GPIO port in the Raspberry PI.
 * @author Mario Gomez
 */
public class GPIOStatus {
	private boolean connected = false;
	public boolean UARTEnabled = false;
	public boolean SPIEnabled = false;
	public boolean I2CEnabled = false;
	
	public ArrayList<GPIOPort> ports;
	public ArrayList<GPIOPin> map;

	public GPIOStatus() {
		this.connected = false;
		this.ports = new ArrayList<GPIOPort>(GPIO.TOTAL_PORTS);
		for (int i = 0; i < 54; i++) {
			GPIOPort port = new GPIOPort();
			port.num = i;
			port.value = PORTVALUE.UNKNOWN;
			port.function = PORTFUNCTION.UNKNOWN;
			ports.add(port);
		}
		this.map = new ArrayList<GPIOPin>();
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public boolean isConnected() {
		return this.connected;
	}
	
	public void setUARTEnabled(boolean UARTEnabled) {
		this.UARTEnabled = UARTEnabled;
	}
	public boolean getUARTEnabled() {
		return this.UARTEnabled;
	}
	
	public void setSPIEnabled(boolean SPIEnabled) {
		this.SPIEnabled = SPIEnabled;
	}
	public boolean getSPIEnabled() {
		return this.SPIEnabled;
	}
	
	public void setI2CEnabled(boolean I2CEnabled) {
		this.I2CEnabled = I2CEnabled;
	}
	public boolean getI2CEnabled(boolean I2CEnabled) {
		return this.I2CEnabled;
	}
}