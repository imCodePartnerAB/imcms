package imcode.util.fortune ;

import java.util.* ;

import imcode.util.* ;

/**
   This class contains a question
   with possible answers, each with an answercount.
**/
public class Poll {

    private DateRange dateRange ;

    private String question ;
    private Map answers = Collections.synchronizedMap(new HashMap()) ;

    public Poll(String question) {
	this.question = question ;
    }

    // Add one answer.
    public void addAnswer(String answer) {
	Integer answerCount = (Integer)answers.get(answer) ;
	if (answerCount == null) {
	    answers.put(answer,new Integer(1)) ;
	} else {
	    answers.put(answer, new Integer(answerCount.intValue() + 1)) ; 
	}
    } ;

    public void removeAnswer(String answer) {
	Integer answerCount = (Integer)answers.get(answer) ;
	if (answerCount != null) {
	    if (answerCount.intValue() > 0) {
		answers.put(answer, new Integer(answerCount.intValue() - 1)) ; 
	    } else {
		answers.remove(answer) ;
	    }
	}
    } ;

    public int getAnswerCount(String answer) {
	Integer answerCount = (Integer)answers.get(answer) ;
	return answerCount == null ? 0 : answerCount.intValue() ;
    }

    public void setAnswerCount(String answer, int count) {
	answers.put(answer,new Integer(count)) ;
    }

    public DateRange getDateRange() {
	return dateRange ;
    }

    public void setDateRange(DateRange dateRange) {
	this.dateRange = dateRange ;
    }

    public String getQuestion() {
	return question ;
    }

    public void setQuestion(String question) {
	this.question = question ;
    }

}
