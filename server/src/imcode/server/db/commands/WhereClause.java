package imcode.server.db.commands;

public abstract class WhereClause {

    abstract Object[] getValues() ;
    abstract String toSql() ;

    public String toString() {
        return toSql() ;
    }
}
