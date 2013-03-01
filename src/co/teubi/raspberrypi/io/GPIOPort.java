package co.teubi.raspberrypi.io;

/**
 * 
 * @author Mario Gomez <mario.gomez_at_teubi.co>
 *
 */
public class GPIOPort {
	public int num;
	public int pin;
	public PORTFUNCTION function;
	public PORTVALUE value;

	public GPIOPort() {
		this.num = 0;
		this.pin = -1;
		this.function = PORTFUNCTION.UNKNOWN;
		this.value = PORTVALUE.LOGIC_0;
	}
}