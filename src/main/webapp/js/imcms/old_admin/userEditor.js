/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 18.06.18
 */
Imcms.require("jquery", function ($) {

    function activateUserAdminRoles() {
        if (document.forms[0].user_admin_role_ids) {
            var list = document.forms[0].role_ids;
            document.forms[0].user_admin_role_ids.disabled = true;
            for (i = 0; i < list.length; i++) {
                if (list.options[i].text === "Useradmin" && list.options[i].selected) {
                    document.forms[0].user_admin_role_ids.disabled = false;
                }
            }
        }
    }

    $(document).ready(function () {
        $("input[name=login_name]").focus();
        activateUserAdminRoles();
    });
});
