/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 14.09.18
 */
const Container = require('imcms-title-text-builder');

const widthContainer = new Container();

module.exports = {
    getOriginalWidthContainer: () => widthContainer.getContainer(),
    setOriginalWidth: (newWidth) => widthContainer.setValue(newWidth),
};
