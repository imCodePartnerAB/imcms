/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 19.08.17.
 */
const BEM = require('imcms-bem-builder');

const controlsBEM = new BEM({
    block: "imcms-controls",
    elements: {"control": "imcms-control"}
});

function buildControl(modifier, onClick) {
    return controlsBEM.buildElement("control", "<div>", {click: onClick}, [modifier]);
}

module.exports = {
    plus: onClick => buildControl("plus", onClick),

    move: onClick => buildControl("move", onClick),

    remove: onClick => buildControl("remove", onClick),

    edit: onClick => buildControl("edit", onClick),

    create: onClick => buildControl("create", onClick),

    copy: onClick => buildControl("copy", onClick),

    archive: onClick => buildControl("archive", onClick),

    check: onClick => buildControl("check", onClick),

    warning: onClick => buildControl("warning", onClick),

    download: onclick => buildControl("download", onclick),

    add: onclick => buildControl("add", onclick),

    upload: onclick => buildControl("upload", onclick),

    left: onclick => buildControl("left", onclick),

    right: onclick => buildControl("right", onclick),

    vertical_move: onclick => buildControl('vertical_move', onclick),

    sortDesc: onclick => buildControl('sort-desc', onclick),

    sortAsc: onclick => buildControl('sort-asc', onclick),

    images: onclick => buildControl('images', onclick),

    star: onclick => buildControl('star', onclick),

    trash: onclick => buildControl('trash', onclick),

    permalink: onClick => buildControl('permalink', onClick),

    buildControlsBlock: (tag, controls, attributes) => controlsBEM.buildBlock(tag, controls, attributes || {}, "control")
};
