
package imcode.external.chat;
/**
*A standard counter class
*/

class Counter{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private int _countValue;
	
	void Counter(){
		_countValue = 0;
	}
	
	void Counter(int startValue){
		_countValue = startValue;
	}
	void setStartValue(int value) {
			_countValue = value;
	}	

	//*** methods ****
	public int getValue(){
		return _countValue;
	}

	public void increment(){
		_countValue++;
	}
}//end class
