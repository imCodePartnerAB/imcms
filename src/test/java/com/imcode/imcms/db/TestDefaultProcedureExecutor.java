package com.imcode.imcms.db;

import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

public class TestDefaultProcedureExecutor extends TestCase {

    DefaultProcedureExecutor procedureExecutor = new DefaultProcedureExecutor(null, null);

    public void testPrepareProcedure() throws Exception {
        String procedure = "CREATE PROCEDURE test @a INT, @b INT AS @b @a" ;
        DefaultProcedureExecutor.Procedure preparedProcedure = procedureExecutor.prepareProcedure( procedure, "test" );
        assertEquals( "? ?", preparedProcedure.getBody()) ;
        assertTrue( ArrayUtils.isEquals( preparedProcedure.getParameterIndices(), new int[] { 1, 0 }));
        procedure += " @c" ;
        try {
            procedureExecutor.prepareProcedure( procedure, "test" );
            fail() ;
        } catch( IllegalArgumentException iae ) {}
    }
}