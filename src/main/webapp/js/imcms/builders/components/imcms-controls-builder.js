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
    move: onClick => buildControl("move", onClick),

    remove: onClick => buildControl("remove", onClick),

    edit: onClick => buildControl("edit", onClick),

    create: onClick => buildControl("create", onClick),

    copy: onClick => buildControl("copy", onClick),

    archive: onClick => buildControl("archive", onClick),

    check: onClick => buildControl("check", onClick),

    warning: onClick => buildControl("warning", onClick),

    download: onclick => buildControl("download", onclick),

    buildControlsBlock: (tag, controls) => controlsBEM.buildBlock(tag, controls, {}, "control")
};
