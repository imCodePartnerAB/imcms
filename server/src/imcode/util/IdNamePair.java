/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-apr-29
 * Time: 19:03:41
 */
package imcode.util;

public class IdNamePair implements Comparable {

    private int id ;
    private String name ;

    public IdNamePair( int id, String name ) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int compareTo( Object o ) {
        return id - ((IdNamePair)o).id ;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof IdNamePair ) ) {
            return false;
        }

        final IdNamePair idNamePair = (IdNamePair)o;

        if ( id != idNamePair.id ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id;
    }

}