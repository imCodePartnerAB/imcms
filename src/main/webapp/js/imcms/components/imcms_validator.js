define("imcms-validator", [], function () {

    var Validator = function (element, isValid) {
        this.isValid = isValid.bind(element);
    };
    return Validator;

});
