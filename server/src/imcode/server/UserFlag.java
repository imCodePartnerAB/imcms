package imcode.server;

public class UserFlag {

    private String name;
    private String description;
    private int type;

    public boolean equals( Object o ) {

        return o instanceof UserFlag &&
               type == ( (UserFlag)o ).type &&
               name.equals( ( (UserFlag)o ).name ) ;
    }

    /**
     * get-method for name
     *
     * @return the value of name
     */
    public String getName() {
        return this.name;
    }

    /**
     * set-method for name
     *
     * @param name Value for name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * get-method for description
     *
     * @return the value of description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * set-method for description
     *
     * @param description Value for description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * set-method for type
     *
     * @param type Value for type
     */
    public void setType( int type ) {
        this.type = type;
    }

}
