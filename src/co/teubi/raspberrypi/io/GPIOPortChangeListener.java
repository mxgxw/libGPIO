package co.teubi.raspberrypi.io;


public interface GPIOPortChangeListener {
	public void onGPIOUpdate(GPIOStatus stat);
}