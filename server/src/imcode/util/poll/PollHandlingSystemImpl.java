package imcode.util.poll ;

import java.util.* ;
import java.text.* ;

import imcode.server.* ;
import imcode.server.db.DatabaseService;

import org.apache.log4j.* ;

public class PollHandlingSystemImpl implements PollHandlingSystem {

    private IMCServiceInterface imcref ;

    private static Logger log = Logger.getLogger( PollHandlingSystemImpl.class.getName() ) ;
    private DatabaseService databaseService;

    public PollHandlingSystemImpl(IMCServiceInterface imcref, DatabaseService databaseService ) {
    	this.imcref = imcref ;
        this.databaseService = databaseService ;
    }



	/**
		Save a poll parameter to db
	*/
	public void savePollparameter(String text_type, int meta_id, int text_no, String textstring) {
	
		// Get a poll by meta_id if it exist
		String[] poll_param = imcref.sqlProcedure( "Poll_GetOne ", new String[] { ""+meta_id } ) ; 
		
		/* Default values for new polls set by db roles
			popup_freq = "0"; 
			set_cookie = "0";
			show_result = "1";
		*/
		
		// if we not already have create a poll in db for this meta 
		// lets do it now and then get the new poll from db.		
		if ( poll_param == null || poll_param.length == 0 ) {
			imcref.sqlUpdateProcedure( "Poll_AddNew ", new String[] { ""+meta_id } ) ;
			poll_param = imcref.sqlProcedure( "Poll_GetOne ", new String[] { ""+meta_id } ) ; 
		}	
		
		int poll_id = Integer.parseInt( poll_param[0] );
		
		// if we are handle a question 
		if ( text_type.startsWith("pollquestion") ) {
		
			int question_no = Integer.parseInt( text_type.substring( text_type.indexOf("-")+1 ) );
			
			if ( question_no > 0 ) {
				String[] sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { ""+poll_id, ""+question_no } ) ; 
				
				// if we not already have create the question in db lets do it now.	
				if ( sql_data == null || sql_data.length == 0 || ("-1").equals(sql_data[3]) ){
					imcref.sqlUpdateProcedure("Poll_AddQuestion ", new String[] { ""+poll_id, ""+question_no, ""+text_no } ) ;
				}
			}
		
		// if we are handle an answer 	
		}else if ( text_type.startsWith("pollanswer") ) {
		
			int question_no = Integer.parseInt( text_type.substring( text_type.indexOf("-")+1, text_type.lastIndexOf("-")) );
			int option_no = Integer.parseInt( text_type.substring( text_type.lastIndexOf("-")+1 ) );

			if ( question_no > 0 && option_no > 0 ) {
			
				String[] sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { ""+poll_id, ""+question_no 	} );
				
				// lets create a new question in db if the user don`t have create it before he create the answer.  
				if ( sql_data == null || sql_data.length == 0){
					imcref.sqlUpdateProcedure("Poll_AddQuestion ", new String[] { ""+poll_id, ""+question_no, "-1" } ) ;
					sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { ""+poll_id, ""+question_no } );	
				}
				int question_id = Integer.parseInt(sql_data[0]);
			
				if ( question_id > 0 ) {
				
					sql_data = imcref.sqlProcedure( "Poll_GetAnswer ", new String[] { ""+question_id, ""+option_no } ) ;
					
					// if we not already have create the answer in db lets do it now.
					if ( sql_data == null || sql_data.length == 0 ){
						imcref.sqlUpdateProcedure("Poll_AddAnswer ", new String[] { ""+question_id, ""+text_no, ""+option_no } ) ;	
					}
				}			
			}
			
		// if we are handle an pollanswer-option point-value	
		}else if ( text_type.startsWith("pollpointanswer") ) {
			
			int question_no = Integer.parseInt( text_type.substring( text_type.indexOf("-")+1, text_type.lastIndexOf("-")) );
			int option_no = Integer.parseInt( text_type.substring( text_type.lastIndexOf("-")+1 ) );
			
			if ( question_no > 0 && option_no > 0 ) {
			
				String[] sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { ""+poll_id, ""+question_no 	} );
				
				// lets create a new question in db if the user don`t have create it before he create the answer-option.  
				if ( sql_data == null || sql_data.length == 0){
					imcref.sqlUpdateProcedure("Poll_AddQuestion ", new String[] { ""+poll_id, ""+question_no, "-1" } ) ;
					sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { ""+poll_id, ""+question_no } );	
				}
				int question_id = Integer.parseInt(sql_data[0]);
				
				if ( question_id > 0 ) {
			
					sql_data = imcref.sqlProcedure( "Poll_GetAnswer ", new String[] { ""+question_id, ""+option_no } ) ;	
					
					// if we have an answer item in db lets set it´s answerpoint value
					if ( sql_data != null && sql_data.length > 0 ){
						imcref.sqlUpdateProcedure("Poll_SetAnswerPoint ", new String[] { sql_data[0], textstring.trim() } ) ;
					}
				}
			}
	
		
		// if we are handle an poll parameter
		}else if ( text_type.startsWith("pollparameter") ) {
		
			String[] sql_param = new String[] { ""+poll_id, "", textstring.trim() };
			
			if ( text_type.endsWith("popup_frequency") ){
				sql_param[1] =  "popup_freq" ;
				sql_param[2] = 	textstring.trim().equals("") ? "0" : ( ""+ Integer.parseInt(textstring.trim()));
			
			}else if ( text_type.endsWith("cookie") ){
				sql_param[1] =  "set_cookie" ;
				sql_param[2] = 	textstring.trim().equals("") ? "0" : "1" ; // we accept anything that is not an empty string and take it for a '1'	
			
			}else if ( text_type.endsWith("hideresults") ){
				sql_param[1] = "hide_result" ;
				sql_param[2] = 	textstring.trim().equals("") ? "0" : "1" ; // we accept anything that is not an empty string and take it for a '1'		
			
			}else if ( text_type.endsWith("confirmation_text") ){
				sql_param[1] = "confirmation_text" ;
				sql_param[2] = ""+text_no ; 	
				
			}else if ( text_type.endsWith("email_recipients") ){
				sql_param[1] = "email_recipients" ;
				sql_param[2] = ""+text_no ;
				
			}else if ( text_type.endsWith("email_from") ){
				sql_param[1] = "email_from" ;
				sql_param[2] = ""+text_no ;	
				
			}else if ( text_type.endsWith("email_subject") ){
				sql_param[1] = "email_subject" ;
				sql_param[2] = ""+text_no ;
			
			}else if ( text_type.endsWith("name") ){
				sql_param[1] = "name" ;
				sql_param[2] = ""+text_no ;
				
			}else if ( text_type.endsWith("description") ){
				sql_param[1] = "description" ;
				sql_param[2] = ""+text_no ;
										
			}else if ( text_type.endsWith("result_template") ){
				sql_param[1] = "result_template" ;
				sql_param[2] = textstring.trim().equals("") ? "0" : ""+text_no ;
			}
			

			if ( sql_param != null ){			
				imcref.sqlUpdateProcedure( "Poll_SetParameter ", sql_param ) ;	
			}
		}
	
	} // end of savePollparameter
	
	
	/**
		Increase a answer options parameter 'answer_count' in db
		Called from sevlet PollHandler
	*/
	
	public void increasePollAnswer(String meta_id, String question_no, String option_no){
		
		// Get poll by meta_id if it exist
		String[] sql_data = imcref.sqlProcedure( "Poll_GetOne ", new String[] { meta_id } ) ; 
		String poll_id;
		
		if ( sql_data != null && sql_data.length != 0 ) {
			poll_id = sql_data[0] ;
			
			//Get question
			sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { poll_id, question_no 	} );
			
			if ( sql_data != null && sql_data.length != 0 ) {
			
				String[] sql_param = new String[] { sql_data[0], option_no }; // question_id, option_no
 
				imcref.sqlUpdateProcedure( "Poll_IncreaseAnswerOption ", sql_param ) ;
				log.info("Increase answer option (meta_id , question_no, option_no ): " + meta_id + ", " + question_no + ", " + option_no );
			}
		}	
	}
	
	
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
	public DatabaseService.Table_polls getPollParameters(int meta_id){
		return databaseService.sproc_Poll_GetOne( meta_id ) ;
	}
	
	/**
		Get all questions for a poll
		returns parameter from db as String arry
			@id
			@poll_id
			@question_number
			@text_id
	*/
	public String[][] getAllPollQuestions(String poll_id){
		String[][] sql_data = imcref.sqlProcedureMulti( "Poll_GetAllQuestions ", new String[] {poll_id} );
		return sql_data;
	}
	
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
	public String[][] getAllPollAnswers(String question_id){
		String[][] sql_data = imcref.sqlProcedureMulti( "Poll_GetAllAnswers ", new String[] {question_id} );
		return sql_data;
	}
	
	
	/**
		Get one questions for a poll
		returns parameter from db as String
			@id
			@poll_id
			@question_number
			@text_id
	*/
	public String[] getPollQuestion( String poll_id, String question_no){
		
		//Get question
		String[] sql_data =  imcref.sqlProcedure( "Poll_GetQuestion ", new String[] { poll_id, question_no	} );
		return sql_data;
	}
	
	
	/**
		Get all polls from db
		return parameter
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
	public DatabaseService.Table_polls[] getAllPolls(){
		DatabaseService.Table_polls[] polls = databaseService.sproc_Poll_GetAll() ;
        return polls ;
	}

}
	