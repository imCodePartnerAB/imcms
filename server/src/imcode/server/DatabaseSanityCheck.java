package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.imcms.db.DdlUtilsPlatformCommand;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.alteration.*;
import org.apache.ddlutils.model.ForeignKey;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;
import org.apache.ddlutils.model.Reference;

import java.util.*;

public class DatabaseSanityCheck implements SanityCheck {

    private Database database;
    private org.apache.ddlutils.model.Database wantedModel;

    public DatabaseSanityCheck(Database database, org.apache.ddlutils.model.Database wantedModel) {
        this.database = database;
        this.wantedModel = wantedModel;
    }

    public Collection<Problem> execute() {
        DdlUtilsPlatformCommand databaseCommand = new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                platform.setDelimitedIdentifierModeOn(true);
                org.apache.ddlutils.model.Database actualModel = platform.readModelFromDatabase(null);
                ModelComparator modelComparator = new ModelComparator(platform.getPlatformInfo(), false);
                List<ModelChange> changes = modelComparator.compare(actualModel, wantedModel);
                Set<Class> errorChanges = new HashSet<Class>(Arrays.asList(new Class[] {
                        AddColumnChange.class,
                        RemoveColumnChange.class,
                        AddTableChange.class,
                        ColumnAutoIncrementChange.class,
                        AddPrimaryKeyChange.class,
                        ColumnRequiredChange.class,
                        PrimaryKeyChange.class,
                        AddIndexChange.class,
                        ColumnDataTypeChange.class,
                }));
                Set<Class> warningChanges = new HashSet<Class>(Arrays.asList(new Class[] {
                        ColumnSizeChange.class,
                        AddForeignKeyChange.class,
                        ColumnOrderChange.class,
                }));
                Set<Class> ignoredChanges = new HashSet<Class>(Arrays.asList(new Class[] {
                        RemoveForeignKeyChange.class,
                        RemoveIndexChange.class,
                        RemoveTableChange.class,
                        RemovePrimaryKeyChange.class,
                        ColumnDefaultValueChange.class
                }));
                List<Problem> problems = new ArrayList<Problem>();
                for ( ModelChange change : changes ) {
                    Class<? extends ModelChange> changeClass = change.getClass();
                    Problem problem = null;
                    if (errorChanges.contains(changeClass)) {
                        problem = getProblem(Problem.Severity.ERROR, change, platform);
                    } else if (warningChanges.contains(changeClass)) {
                        problem = getProblem(Problem.Severity.WARNING, change, platform);
                    } else if (!ignoredChanges.contains(changeClass)){
                        problem = getProblem(Problem.Severity.UNKNOWN, change, platform);
                    }
                    if (null != problem) {
                        problems.add(problem);
                    }
                }
                return problems;
            }
        };
        return (Collection<Problem>) database.execute(databaseCommand);
    }

    private static Problem getProblem(final Problem.Severity severity, ModelChange change, Platform platform) {
        String changeString;
        if (change instanceof RemoveIndexChange ) {
            RemoveIndexChange removeIndexChange = (RemoveIndexChange) change;
            Index index = removeIndexChange.getIndex();
            changeString = "Unexpected index "+index.getName() + " on column(s) "+ stringifyIndexColumns(index);
        } else if (change instanceof ColumnDefaultValueChange ) {
            ColumnDefaultValueChange columnDefaultValueChange = (ColumnDefaultValueChange) change;
            changeString = "Unexpected default value "+columnDefaultValueChange.getChangedColumn().getDefaultValue()+", expected "+columnDefaultValueChange.getNewDefaultValue();
        } else if (change instanceof ColumnDataTypeChange ) {
            ColumnDataTypeChange columnDataTypeChange = (ColumnDataTypeChange) change;
            changeString = "Expected data type "+platform.getPlatformInfo().getNativeType(columnDataTypeChange.getNewTypeCode()) ;
        } else if (change instanceof AddTableChange ) {
            AddTableChange addTableChange = (AddTableChange) change;
            changeString = "Missing table "+addTableChange.getNewTable();
        } else if (change instanceof RemoveForeignKeyChange) {
            RemoveForeignKeyChange removeForeignKeyChange = (RemoveForeignKeyChange) change;
            ForeignKey foreignKey = removeForeignKeyChange.getForeignKey();
            changeString = "Unexpected foreign key to "+foreignKey.getForeignTableName();
            changeString+=" ("+stringifyReferences(foreignKey)+")";
        } else if (change instanceof AddForeignKeyChange) {
            AddForeignKeyChange removeForeignKeyChange = (AddForeignKeyChange) change;
            ForeignKey newForeignKey = removeForeignKeyChange.getNewForeignKey();
            changeString = "Missing foreign key to "+newForeignKey.getForeignTableName();
            changeString+=" ("+stringifyReferences(newForeignKey)+")";
        } else if (change instanceof AddIndexChange) {
            AddIndexChange addIndexChange = (AddIndexChange) change;
            Index index = addIndexChange.getNewIndex();
            changeString = "Missing index "+index.getName()+ " on column(s) "+stringifyIndexColumns(index);
        } else {
            changeString = change.toString();
        }
        if (change instanceof ColumnChange ) {
            ColumnChange columnChange = (ColumnChange) change;
            changeString = columnChange.getChangedTable().getName()+"."+columnChange.getChangedColumn().getName()+": "+changeString ;
        } else if (change instanceof TableChange ) {
            changeString = ((TableChange)change).getChangedTable().getName()+": "+changeString ;
        }

        return new SimpleProblem(severity, changeString);
    }

    private static String stringifyIndexColumns(Index index) {
        return commafy(index.getColumns(), new Transformer() {
            public Object transform(Object input) {
                IndexColumn indexColumn = (IndexColumn) input ;
                return indexColumn.getName();
            }
        });
    }

    private static String stringifyReferences(ForeignKey newForeignKey) {
        return commafy(newForeignKey.getReferences(), new Transformer() {
            public Object transform(Object input) {
                Reference reference = (Reference) input;
                return reference.getLocalColumnName() + " -> " + reference.getForeignColumnName();
            }
        });
    }

    private static String commafy(Object[] array, Transformer transformer) {
        return StringUtils.join(CollectionUtils.collect(Arrays.asList(array), transformer).iterator(), ", ");
    }

}
