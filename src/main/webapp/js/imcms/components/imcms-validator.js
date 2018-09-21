module.exports = class Validator {
    constructor(element, isValid) {
        this.isValid = isValid.bind(element);
    }
};
