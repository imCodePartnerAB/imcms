/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.01.18
 */
define("imcms-texts-validation-rest-api", ["imcms-rest-api"], function (rest) {
    return {
        validate: rest.ajax.bind({url: "/texts/validate", type: "POST", json: false})
    }
});
