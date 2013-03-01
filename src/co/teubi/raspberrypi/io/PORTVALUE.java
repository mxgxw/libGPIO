package co.teubi.raspberrypi.io;

/**
 * 
 * @author Mario Gomez <mario.gomez_at_teubi.co>
 *
 */
public enum PORTVALUE {
	LOGIC_0("0"),
	LOGIC_1("1"),
	V33("V33"),
	V50("V50"),
	GND("GND"),
	UNKNOWN("UNKNOWN");

    /**
     * @param text
     */
    private PORTVALUE(final String text) {
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
    
    public boolean toBool() {
    	if(this.text.equals(PORTVALUE.LOGIC_0.toString()))
    		return false;
    	else
    		return true;
    }
    
    public PORTVALUE parseString(String s) {
    	for(int i=0;i<PORTVALUE.values().length;i++) {
    		if(s.equals(PORTVALUE.values()[i])) {
    			return PORTVALUE.values()[i];
    		}
    	}
    	return PORTVALUE.UNKNOWN;
    }
};