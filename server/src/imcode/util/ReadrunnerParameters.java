package imcode.util ;

public class ReadrunnerParameters {

    private boolean useStopChars ;
    private boolean useSepChars ;

    public ReadrunnerParameters (boolean useStopChars, boolean useSepChars) {
	this.useStopChars = useStopChars ;
	this.useSepChars  = useSepChars ;
    }

    public void setUseStopChars(boolean useStopChars) {
	this.useStopChars = useStopChars ;
    }

    public boolean getUseStopChars() {
	return this.useStopChars ;
    }

    public void setUseSepChars(boolean useSepChars) {
	this.useSepChars = useSepChars ;
    }

    public boolean getUseSepChars() {
	return this.useSepChars ;
    }

}
