package imcode.util.poll ;

import java.util.* ;


public interface PollHandlingSystem {


	/**
		Save a poll parameter to db
		called from IMCServer.sqlUpdateSaveTextAttribute
	 */
	public void savePollparameter(String text_type, int meta_id, int text_no, String textstring);

	/**
		Increase a answer options parameter 'answer_count' in db
		Called from sevlet PollHandler
	*/
	public void increasePollAnswer(String meta_id, String question_no, String option_no);


	/** 
		Get all parameters for a poll
		returns parameters from db as String:
				@poll_id int, 
				@meta_id int,
				@popup_freq int
				@set_cookie bit ,
				@hide_result bit ,
				@confirmation_text int ,
				@email_recipients int ,
				@email_from int ,
				@email_subject int ,
				@result_template int
	*/
	public imcode.server.db.DatabaseService.Table_polls getPollParameters(int meta_id);

	/**
		Get all questions for a poll
		returns parameter from db as String
			@id
			@poll_id
			@question_number
			@text_id
	*/
	public String[][] getAllPollQuestions(String poll_id);


	/**
		Get all answers for a poll-question
		returns parameter from db as String
			@id
			@question_id
			@question_number
			@text_id
			@option_number
			@answer_count
			@option_point
			
	*/
	public String[][] getAllPollAnswers(String question_id);


	/**
		Get one questions for a poll
		returns parameter from db as String
			@id
			@poll_id
			@question_number
			@text_id
	*/
	public String[] getPollQuestion( String poll_id, String question_no);	

	/**
		Get all polls from db
		return parameters
			@id int
			@name varchar (100) (store only text_id)
			@description varchar (100) (store only text_id)
			@meta_id int
			@popup_freq int
			@set_cookie bit
			@hide_result bit
			@confirmation_text int 
			@email_recipients int
			@email_from int
			@email_subject int
			@result_template int 
	*/
	public imcode.server.db.DatabaseService.Table_polls[] getAllPolls();

}