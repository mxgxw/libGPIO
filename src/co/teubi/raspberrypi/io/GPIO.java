package co.teubi.raspberrypi.io;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.androidhive.jsonparsing.JSONParser;

/**
 * 
 * @author Mario Gomez <mario.gomez_at_teubi.co>
 *
 */
public class GPIO implements Runnable {

	public static interface PortUpdateListener {
		public void onPortUpdated(GPIOStatus stat);
	}
	public static interface ConnectionEventListener {
		public void onConnectionFailed(String message);
	}

	public static class ConnectionInfo {
		public String host;
		public int port;
		public String username;
		public String password;

		public ConnectionInfo(String host) {
			this.host = host;
			this.port = 8000;
			this.username = "webiopi";
			this.password = "raspberry";
		}

		public ConnectionInfo(String host, int port, String user, String pass) {
			this.host = host;
			this.port = port;
			this.username = user;
			this.password = pass;
		}
	}

	public static final int TOTAL_PORTS = 54;
	public static final int TOTAL_PINS = 26;

	private GPIOStatus status;
	private ArrayList<PortUpdateListener> portUpdateListeners;
	private ArrayList<ConnectionEventListener> connectionListeners;
	private ConnectionInfo conn;
	private boolean connected;
	private ArrayList<GPIOPin> map;

	/**
	 * Initializes the GPIO object that allows you to
	 * connect to WebIOPi. This object provides a series
	 * of utility functions that help you to control WebIOPi
	 * using REST calls.
	 * @param info Connection information for the host runing WebIOPi
	 */
	public GPIO(ConnectionInfo info) {
		conn = info;
		status = new GPIOStatus();
		portUpdateListeners = new java.util.ArrayList<PortUpdateListener>();
		connectionListeners = new java.util.ArrayList<ConnectionEventListener>();
		connected = false;
		
		// TODO: Can we make this look beautiful?
		// I never liked the idea of having a lot of static
		// code here
		int i = 0;
		map = new ArrayList<GPIOPin>(26);
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.V33,PORTVALUE.V33,"Power 3.3V, 50 mA max (pin 1 & 17)"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.V50,PORTVALUE.V50,"Power 5.0V, TypeA 500 mA | TypeB 700 mA Supply through input poly fuse"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"Rev1 GPIO 0 | Rev2 GPIO 2, ALT0 = I2C_SDA"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.V50,PORTVALUE.V50,"Power 5.0V, TypeA 500 mA | TypeB 700 mA Supply through input poly fuse"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"Rev1 GPIO 1 | Rev2 GPIO 2, ALT0 = I2C_SCL"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.GND,PORTVALUE.GND,"Ground"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 4"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 14, ALT0 = UART0_TXD"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.GND,PORTVALUE.GND,"Ground"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 15, ALT0 = UART0_RXD"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 17, ALT3 = UART0_RTS, ALT5 = UART1_RTS"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 18, ALT4 = SPI1_CE0_N, ALT5 = PWM0"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"Rev1 GPIO 21 | Rev2 GPIO 27, ALT0 = PCM_DIN, ALT5 = GPCLK1"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.GND,PORTVALUE.GND,"Ground"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 22, ALT3 = SD1_CLK, ALT4 = ARM_TRST"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 23, ALT3 = SD1_CMD, ALT4 = ARM_RTCK"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.V33,PORTVALUE.V33,"Power 3.3V, 50 mA max (pin 1 & 17)"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 24, ALT3 = SD1_DATA0, ALT4 = ARM_TDO"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 10, ALT0 = SPI0_MOSI"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.GND,PORTVALUE.GND,"Ground"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 09, ALT0 = SPI0_MISO"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 25, ALT4 = ARM_RTCK"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 11, ALT0 = SPI0_SCLK"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 08, ALT0 = SPI0_CE0_N"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.GND,PORTVALUE.GND,"Ground"));
		map.add(new GPIOPin(i++,-1,PORTFUNCTION.UNKNOWN,PORTVALUE.UNKNOWN,"GPIO 07, ALT0 = SPI0_CE1_NN"));
	}
	
	/**
	 * getPinMap returns the current GPIO pin mapping
	 * for the raspberry PI.
	 * @return Current ping mapping as an ArrayList
	 */
	public ArrayList<GPIOPin> getPinmap() {
		return this.map;
	}

	/**
	 * setPinmap forces the change of current pin mapping.
	 * GPIO tries to acquire the current pin mapping from
	 * WebIOPi. Sometimes, like when we cannot connect to
	 * the remote host we want to be able to establish a
	 * default port mapping for reference purposes.
	 * @param map
	 */
	public void setPinmap(ArrayList<GPIOPin> map) {
		this.map = map;
	}

	/**
	 * outputPulse sends a single pulse to the specified
	 * GPIO port. This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host
	 * @param port Port number to output the pulse
	 */
	public void outputPulse(final int port) {
		final GPIO self = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONParser parser = new JSONParser(conn.username,
							conn.password, conn.host);
					JSONObject obj = parser.postJSONFromURL("http://"
							+ conn.host + ":" + conn.port + "/GPIO/"+
							port+"/pulse/", conn.port);
					if (obj != null) {
					}
				} catch (JSONException e) {
					
				} catch (Exception e) {
					self.connected = false;
					self.dispatchConnectionEvent(e.getMessage());
				}
			}
		});
		t.start();
	}
	

	/**
	 * outputSequence sends a sequence of bits to the specified
	 * GPIO port with the delay specified. The sequence is specified
	 * as a string of 0 and 1 characters. It doesn't validate the
	 * string sent. This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host.
	 * @param port Port number to output the sequence
	 * @param delay Delay (in milliseconds) between pulses
	 * @param seq A string composed of 0 and 1 characters to output.
	 */
	public void outputSequence(final int port, final int delay, final String seq) {
		final GPIO self = this;
		//TODO: Validate seq
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONParser parser = new JSONParser(conn.username,
							conn.password, conn.host);
					JSONObject obj = parser.postJSONFromURL("http://"
							+ conn.host + ":" + conn.port + "/GPIO/"+
							port+"/sequence/"+delay+","+seq, conn.port);
					if (obj != null) {
					}
				} catch (JSONException e) {
					
				} catch (Exception e) {
					self.connected = false;
					self.dispatchConnectionEvent(e.getMessage());
				}
			}
		});
		t.start();
	}
	

	/**
	 * outputPWMDutyCicle outputs a PWM signal to the specified
	 * GPIO port using the specified duty cycle ratio. 0 equals
	 * always off (0%) and 1 equals (100%) always on.
	 * This method only works on ports configured as PWM.
	 * This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host. 
	 * @param port Target port number
	 * @param ratio Pulse width ratio
	 */
	public void outputPWMDutyCycle(final int port, final float ratio) {
		final GPIO self = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONParser parser = new JSONParser(conn.username,
							conn.password, conn.host);
					JSONObject obj = parser.postJSONFromURL("http://"
							+ conn.host + ":" + conn.port + "/GPIO/"+
							port+"/pulseRatio/"+ratio, conn.port);
					if (obj != null) {
					}
				} catch (JSONException e) {
					
				} catch (Exception e) {
					self.connected = false;
					self.dispatchConnectionEvent(e.getMessage());
				}
			}
		});
		t.start();
	}
	

	/**
	 * outputPWMServoAngle outputs a PWM signal to the specified
	 * GPIO port using the specified servo motor angle. The range
	 * for "angle" could be from 0 to 359.
	 * This method only works on ports configured as PWM.
	 * This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host. 
	 * @param port Target port number
	 * @param angle Target angle in degrees
	 */
	public void outputPWMServoAngle(final int port, int angle) {
		final int correctedAngle = (angle>359) ? angle%359 : angle;
		final GPIO self = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONParser parser = new JSONParser(conn.username,
							conn.password, conn.host);
					JSONObject obj = parser.postJSONFromURL("http://"
							+ conn.host + ":" + conn.port + "/GPIO/"+
							port+"/pulseRatio/"+correctedAngle, conn.port);
					if (obj != null) {
					}
				} catch (JSONException e) {
					
				} catch (Exception e) {
					self.connected = false;
					self.dispatchConnectionEvent(e.getMessage());
				}
			}
		});
		t.start();
	}

	/**
	 * setValue changes the current value on the GPIO port. 
	 * You can call this method at any time but it only works
	 * on ports configured as output.
	 * This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host. 
	 * @param port Target port number
	 * @param value Target port value
	 */
	public void setValue(final int port, final int value) {
		final GPIO self = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONParser parser = new JSONParser(conn.username,
							conn.password, conn.host);
					JSONObject obj = parser.postJSONFromURL("http://"
							+ conn.host + ":" + conn.port + "/GPIO/"+
							port+"/value/"+value, conn.port);
					if (obj != null) {
					}
				} catch (JSONException e) {
					
				} catch (Exception e) {
					self.connected = false;
					self.dispatchConnectionEvent(e.getMessage());
				}
			}
		});
		t.start();
	}

	/**
	 * getValue returns the current port value.
	 * It returns an enumeration representing the current
	 * port value.
	 * This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host. 
	 * @param port Target port
	 * @return current port value
	 */
	public PORTVALUE getValue(int port) {
		return this.status.ports.get(port).value;
	}

	/**
	 * setFunction changes the specified port function.
	 * This method works in an asynchronous way
	 * starting a new Thread to make the request and ends.
	 * <p>
	 * If there is any connection exceptions it generates
	 * a "ConnectionEvent", this is useful to detect if
	 * GPIO is disconnected from the target host. 
	 * @param port Target port
	 * @param funct New port function
	 */
	public void setFunction(final int port, final PORTFUNCTION funct) {
		final GPIO self = this;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String function = String.valueOf(funct).toLowerCase(
							Locale.US);
					JSONParser parser = new JSONParser(conn.username,
							conn.password, conn.host);
					JSONObject obj = parser.postJSONFromURL("http://"
							+ conn.host + ":" + conn.port + "/GPIO/" + port
							+ "/function/" + function, conn.port);
					if (obj != null) {
					}
				} catch (JSONException e) {
					
				} catch (Exception e) {
					self.connected = false;
					self.dispatchConnectionEvent(e.getMessage());
				}
			}
		});
		t.start();
	}

	/**
	 * getFunction returns the current port function.
	 * It doesn't generates a REST request because it's supposed
	 * that a background thread is updating the internal state
	 * of GPIO ports
	 * @param port Target port
	 * @return Current port function
	 */
	public PORTFUNCTION getFunction(int port) {
		return this.status.ports.get(port).function;
	}
	
	/**
	 * isConnected returns if GPIO is currently connected
	 * to WebIOPi.
	 * @return true on connected false otherwise.
	 */
	public boolean isConnected() {
		return this.connected;
	}


	/**
	 * addPortUpdateListener adds update listeners to the
	 * current GPIO connection. The listeners are called
	 * each time that GPIO pools WebIOPi.
	 * @param listener
	 */
	public void addPortUpdateListener(PortUpdateListener listener) {
		portUpdateListeners.add(listener);
	}

	/**
	 * removePortUpdateListener removes an update listener
	 * from the current list of update listeners.
	 * @param listener
	 */
	public void removePortUpdateListener(PortUpdateListener listener) {
		portUpdateListeners.remove(listener);
	}

	/**
	 * dispatchGPIOStatus sends the current status of the port
	 * to all PortUpdateListeners registered in the instance.
	 * @param Current GPIO status
	 */
	private void dispatchGPIOStatus(GPIOStatus status) {
		for (int i = 0; i < portUpdateListeners.size(); i++) {
			portUpdateListeners.get(i).onPortUpdated(status);
		}
	}
	

	/**
	 * addPortConnectionListener adds a new collection listener.
	 * This listeners are used to identify when GPIO disconnects
	 * from WebIOPi.
	 * @param listener to be added.
	 */
	public void addPortConnectionListener(ConnectionEventListener listener) {
		connectionListeners.add(listener);
	}

	/**
	 * removePortConnectionListener removes a connection listener
	 * from the current list of connection listeners.
	 * @param listener
	 */
	public void removePortConnectionListener(ConnectionEventListener listener) {
		connectionListeners.remove(listener);
	}

	/**
	 * dispatchConnectionEvent sends a failed connection message to
	 * all connection listeners of this instance.
	 * @param Error message
	 */
	private void dispatchConnectionEvent(String message) {
		for (int i = 0; i < connectionListeners.size(); i++) {
			connectionListeners.get(i).onConnectionFailed(message);
		}
	}

	/**
	 * isInteger verifies if the text specified is a number.
	 * @param Text to verify
	 * @return true if the text contains an integer value
	 */
	private boolean isInteger(String text) {
		try {
			Integer.parseInt(text); // 
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * getPinNum returns the physical pin number less one.
	 * If the GPIO port doesn't have a pin assigned it returns
	 * the value -1.
	 * @param gpioNum GPIO number
	 * @return Pin number
	 */
	public int getPinNum(int gpioNum) {
		for(int i=0;i<map.size();i++ ) {
			if(map.get(i).gpio==gpioNum) {
				return i;
			}
		}
		return -1; // -1 is used for not mapped pins.
	}

	/**
	 * Main background task
	 * GPIO is designed to work in background. It can be
	 * started creating a new Thread. The main loop is
	 * going to continue while it can connect to WebIOPi
	 * and parse the resulting data.
	 * <p>
	 * After each successful request and parse of JSON
	 * data it calls onPortUpdate for the PortUpdateListener
	 * registered.
	 * <p>
	 * Each loop is treated as a "transaction", if any
	 * exceptions are found it breaks the main loops and
	 * ends the thread. onConnectionFailed is called on
	 * all ConnnectionListeners if there any registered.
	 */
	public void run() {
		GPIOStatus status = new GPIOStatus();
		JSONParser parser = new JSONParser(conn.username, conn.password,
				conn.host);
		
		boolean firstRun = true;
		while (true) {
			// Handle pause requested by user
			if(Thread.interrupted()) {
				this.connected = false;
				break;
			}
			try {
				// Treat this whole operation as a transaction
				// any exception caught here means a failed
				// connection.
				JSONObject obj = parser.getJSONFromUrl("http://" + conn.host
						+ ":" + conn.port + "/*", conn.port);

				if (obj != null) {
					status.UARTEnabled = (obj.getInt("UART0") == 0) ? false
							: true;
					status.SPIEnabled = (obj.getInt("SPI0") == 0) ? false
							: true;
					status.I2CEnabled = ((obj.getInt("I2C0") == 0) ? false
							: true)
							| ((obj.getInt("I2C1") == 0) ? false : true);

				}
				
				if(firstRun) {
					firstRun = false;
					JSONArray arr = parser.getJSONArrayFromUrl("http://" + conn.host
							+ ":" + conn.port + "/map", conn.port);
					
					if(arr !=null) {
						for (int i = 0; i < arr.length(); i++) {
							String val = arr.getString(i);
							GPIOPin pin = map.get(i);
							pin.gpio = (isInteger(val) ? Integer.parseInt(val) : -1 );
							map.add(pin);
						}
					} else {
						throw new Exception("Impossible to get pin mapping.");
					}
				}

				JSONObject values = obj.getJSONObject("GPIO");

				for (int i = 0; i < GPIO.TOTAL_PORTS; i++) {
					JSONObject JSONPort = values.getJSONObject(String
							.valueOf(i));
					GPIOPort p = new GPIOPort();
					String function = JSONPort.getString("function");
					if (function.equalsIgnoreCase("in")) {
						p.function = PORTFUNCTION.INPUT;
					} else if (function.equalsIgnoreCase("out")) {
						p.function = PORTFUNCTION.OUTPUT;
					} else if (function.equalsIgnoreCase("pwm")) {
						p.function = PORTFUNCTION.PWM;
					} else if (function.equalsIgnoreCase("alt0")) {
						p.function = PORTFUNCTION.ALT0;
					} else if (function.equalsIgnoreCase("alt1")) {
						p.function = PORTFUNCTION.ALT1;
					} else if (function.equalsIgnoreCase("alt2")) {
						p.function = PORTFUNCTION.ALT2;
					} else if (function.equalsIgnoreCase("alt3")) {
						p.function = PORTFUNCTION.ALT3;
					} else if (function.equalsIgnoreCase("alt4")) {
						p.function = PORTFUNCTION.ALT4;
					} else if (function.equalsIgnoreCase("alt5")) {
						p.function = PORTFUNCTION.ALT5;
					} else {
						p.function = PORTFUNCTION.UNKNOWN;
					}
					if (p.function == PORTFUNCTION.INPUT
							|| p.function == PORTFUNCTION.OUTPUT) {
						String value = JSONPort.getString("value");
						if (value.equalsIgnoreCase("1")) {
							p.value = PORTVALUE.LOGIC_1;
						} else if (value.equalsIgnoreCase("0")) {
							p.value = PORTVALUE.LOGIC_0;
						} else {
							p.value = PORTVALUE.UNKNOWN;
						}
					}
					p.num = i;
					p.pin = getPinNum(i);
					if(p.pin>-1) {
						map.get(p.pin).function = p.function;
						map.get(p.pin).value = p.value;
					}
					status.ports.set(i, p);
				}
				
				// If we can get here everything is OK now.
				status.map = this.map;
				status.setConnected(true);
				this.connected = true;
				
				this.dispatchGPIOStatus(status);
			} catch (Exception e) {
				e.printStackTrace();
				this.connected = false;
				this.dispatchConnectionEvent(e.getMessage());
				break;
			}
		}
	}

}