/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18
 */
define("imcms-texts-validation-rest-api", ["imcms-rest-api", "imcms"], function (rest, imcms) {
    return {
        validate: rest.ajax.bind({url: `${imcms.contextPath}/texts/validate`, type: "POST", json: false})
    }
});
