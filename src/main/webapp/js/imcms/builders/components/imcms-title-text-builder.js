/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 14.09.18
 */
const components = require('imcms-components-builder');

module.exports = class Container {
    getContainer() {
        return this.$container || (this.$container = components.texts.titleText("<span>"))
    }

    setValue(value) {
        this.getContainer().text(value)
    }

    getValue() {
        return this.getContainer().text()
    }
};
