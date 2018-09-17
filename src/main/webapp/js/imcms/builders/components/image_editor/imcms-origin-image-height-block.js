/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 14.09.18
 */
const Container = require('imcms-title-text-builder');

const heightContainer = new Container();

module.exports = {
    getOriginalHeightContainer: () => heightContainer.getContainer(),
    setOriginalHeight: (newHeight) => heightContainer.setValue(newHeight),
};
