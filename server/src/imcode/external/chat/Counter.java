
package imcode.external.chat;
/**
*A standard counter class
*/

class Counter{

    private int _countValue;

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
