
import imcode.external.diverse.VariableManager;
import imcode.server.IMCServiceInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class UserHandler extends Administrator {

    /**
     * Executes the sproc xxxx which will add the users values in the db
     */

    public static void addUserInfoDB(IMCServiceInterface imcref, Properties userParams) {

        // Lets build the users information into a string and add it to db
        imcref.sqlUpdateProcedure("AddNewUser", UserHandler.createUserInfoSprocParameterArray(userParams));
    }

    /**
     * Creates hea sql string string used to run sproc updateUser
     */

    public static String[] createUserInfoSprocParameterArray(Properties params) {

        return new String[]{
            params.getProperty("user_id"),
            params.getProperty("login_name").trim(),
            params.getProperty("password1").trim(),
            params.getProperty("first_name").trim(),
            params.getProperty("last_name").trim(),
            params.getProperty("title").trim(),
            params.getProperty("company").trim(),
            params.getProperty("address").trim(),
            params.getProperty("city").trim(),
            params.getProperty("zip").trim(),
            params.getProperty("country").trim(),
            params.getProperty("country_council").trim(),
            params.getProperty("email").trim(),
            "0",
            "1001",
            "0",
            params.getProperty("lang_id"),
            params.getProperty("user_type"),
            params.getProperty("active"),
        };
    } // End of createUserInfoString

    /**
     * Collects all userparameters from the users table in the db
     * Returns null if something goes wrong
     */
    public static Properties getUserInfoDB(IMCServiceInterface imcref, String userId) {

        // Get default props
        Properties p = doDefaultUser();
        Hashtable h = imcref.sqlProcedureHash("GetUserInfo", new String[]{userId});
        Enumeration keys = h.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String[] values = (String[]) h.get(key);
            String aValue = values[0];
            p.setProperty(key.toString(), aValue);
        }

        return p;
    }

    /**
     * Creates a properties with all the users properties from the
     * users table. All keys are here, but not the values
     */
    private static Properties doDefaultUser() {

        Properties p = new Properties();

        p.setProperty("user_id", "");
        p.setProperty("login_name", "");
        p.setProperty("login_password", "");
        p.setProperty("first_name", "");
        p.setProperty("last_name", "");
        p.setProperty("title", "");
        p.setProperty("company", "");
        p.setProperty("address", "");
        p.setProperty("city", "");
        p.setProperty("zip", "");
        p.setProperty("country", "");
        p.setProperty("country_council", "");
        p.setProperty("email", "");

        p.setProperty("admin_mode", "");
        p.setProperty("last_page", "");
        p.setProperty("archive_mode", "");
        p.setProperty("lang_id", "");

        p.setProperty("user_type", "");
        p.setProperty("active", "");
        p.setProperty("create_date", "");
        return p;

    }  // End of

    public void log(String str) {
        super.log(str);
        System.out.println("UserChangePrefs: " + str);
    }

    /**
     * Validates the password. Password must contain at least 4 characters
     * Generates an errorpage and returns false if something goes wrong
     */

    public static boolean verifyPassword(Properties prop, HttpServletRequest req, HttpServletResponse res) throws IOException {

        String pwd1 = prop.getProperty("password1");
        String pwd2 = prop.getProperty("password2");
        String header = "Verify password error";

        if (!pwd1.equals(pwd2)) {
            header = req.getServletPath();
            new AdminError2(req, res, header, 52);
            return false;
        }

        if (pwd1.length() < 4) {
            header = req.getServletPath();
            new AdminError2(req, res, header, 53);
            return false;
        }

        return true;

    } // End verifyPassword

    /**
     * Validates the phonenumber. Password must contain at least 4 characters
     * Generates an errorpage and returns false if something goes wrong
     */

    public static boolean verifyPhoneNumber(Properties prop, HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String[] arr = {prop.getProperty("country_code"), prop.getProperty("area_code"), prop.getProperty("local_code")};

            for (int i = 0; i < arr.length; i++) {
                Integer.parseInt(arr[i]);
            }

        } catch (NumberFormatException e) {
            new AdminError2(req, res, "", 63);
            return false;
        } catch (NullPointerException e) {
            new AdminError2(req, res, "", 63);
            return false;
        }
        return true;

    } // End phonenumber

    /**
     * Validates the username. Returns true if the login_name doesnt exists.
     * Returns false if the username exists
     */
    public static boolean checkExistingUserName(IMCServiceInterface imcref, Properties prop) {
        String userName = prop.getProperty("login_name");
        String userNameExists[] = imcref.sqlProcedure("FindUserName", new String[]{userName});
        if (userNameExists != null) {
            if (userNameExists.length > 0) {
                return false;
            }
        }
        return true;
    } // CheckExistingUserName

} // End of class
