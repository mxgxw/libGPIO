package co.teubi.raspberrypi.io;

/**
 * 
 * @author Mario Gomez <mario.gomez_at_teubi.co>
 *
 */
public enum PORTFUNCTION {
	INPUT("IN"),
	OUTPUT("OUT"),
	ALT0("ALT0"),
	ALT1("ALT1"),
	ALT2("ALT2"),
	ALT3("ALT3"),
	ALT4("ALT4"),
	ALT5("ALT5"),
	V33("V33"),
	V50("V50"),
	GND("GND"),
	PWM("PWM"),
	UNKNOWN("UNKNOWN");

    /**
     * @param text
     */
    private PORTFUNCTION(final String text) {
        this.text = text;
    }

    private final String text;

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
    

    public PORTFUNCTION parseString(String s) {
    	for(int i=0;i<PORTFUNCTION.values().length;i++) {
    		if(s.equals(PORTFUNCTION.values()[i])) {
    			return PORTFUNCTION.values()[i];
    		}
    	}
    	return PORTFUNCTION.UNKNOWN;
    }
};
