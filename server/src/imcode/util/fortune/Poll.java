package imcode.util.fortune ;

import java.util.* ;

import imcode.util.* ;

/**
   This class contains a question
   with possible answers, each with an answercount.
**/
public class Poll {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private DateRange dateRange ;

    private String question ;
    private Map answers = Collections.synchronizedMap(new HashMap()) ;

    public Poll(String question, DateRange dateRange) {
	this.question = question ;
	this.dateRange = dateRange ;
    }

    // Add one answer.
    public void addAnswer(String answer) {
	Integer answerCount = (Integer)answers.get(answer) ;
	if (answerCount == null) {
	    answers.put(answer,new Integer(1)) ;
	} else {
	    answers.put(answer, new Integer(answerCount.intValue() + 1)) ;
	}
    }

    public void removeAnswer(String answer) {
	Integer answerCount = (Integer)answers.get(answer) ;
	if (answerCount != null) {
	    if (answerCount.intValue() > 1) {
		answers.put(answer, new Integer(answerCount.intValue() - 1)) ;
	    } else {
		answers.remove(answer) ;
	    }
	}
    }

    public int getTotalAnswerCount() {
	int totalAnswers = 0 ;
	Iterator answers = this.getAnswersIterator() ;
	while (answers.hasNext()) {
	    totalAnswers += this.getAnswerCount((String)answers.next()) ;
	}
	return totalAnswers ;
    }

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

    public Iterator getAnswersIterator() {
	return answers.keySet().iterator() ;
    }

    public void setQuestion(String question) {
	this.question = question ;
    }
}
