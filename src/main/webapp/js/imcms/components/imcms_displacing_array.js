/**
 * Array with fixed max length and displacing first element on oversize.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.05.18
 */
Imcms.define('imcms-displacing-array', [], function () {

    var DisplacingArray = function (size) {
        if (typeof size !== 'number') throw new Error("Size should be integer!");
        if (size < 1) throw new Error("Size should be >= 1");

        this.size = size;
        this.elements = [];
    };

    DisplacingArray.prototype = {
        push: function (content) {
            if (this.elements.length > this.size) {
                this.elements.splice(0, 1); // removes first element
            }

            this.elements.push(content);
        },
        forEach: function (doForEach) {
            this.elements.forEach(doForEach);
        }
    };

    return DisplacingArray;
});
