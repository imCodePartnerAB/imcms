package imcode.server ;

public class UserFlag {

    private String name ;
    private String description ;
    private int    type ;

    /**
       get-method for name

       @return the value of name
    **/
    public String getName()  {
	return this.name;
    }

    /**
       set-method for name

       @param name Value for name
    **/
    public void setName(String name) {
	this.name = name;
    }

    /**
       get-method for description

       @return the value of description
    **/
    public String getDescription()  {
	return this.description;
    }

    /**
       set-method for description

       @param description Value for description
    **/
    public void setDescription(String description) {
	this.description = description;
    }

    /**
       get-method for type

       @return the value of type
    **/
    public int getType()  {
	return this.type;
    }

    /**
       set-method for type

       @param type Value for type
    **/
    public void setType(int type) {
	this.type = type;
    }

}
