/**
 * List with fixed max length and displacing first element on oversize.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.05.18
 */

module.exports = class DisplacingList {
    constructor(size) {
        if (typeof size !== 'number') throw new Error("Size should be integer!");
        if (size < 1) throw new Error("Size should be >= 1");

        this.size = size;
        this.elements = [];
        this.length = 0;
    }

    push(content) {
        if (this.elements.length > this.size) {
            this.elements.splice(0, 1); // removes first element
        }

        this.elements.push(content);
        this.length = this.elements.length;
    }

    forEach(doForEach) {
        this.elements.forEach(doForEach);
    }

    get(index) {
        return this.elements[index];
    }
};
