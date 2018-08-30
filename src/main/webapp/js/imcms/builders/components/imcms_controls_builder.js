/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 19.08.17.
 */
define("imcms-controls-builder", ["imcms-bem-builder"], function (BEM) {
    var controlsBEM = new BEM({
        block: "imcms-controls",
        elements: {"control": "imcms-control"}
    });

    function buildControl(modifier, onClick) {
        return controlsBEM.buildElement("control", "<div>", {click: onClick}, [modifier]);
    }

    return {
        move: function (onClick) {
            return buildControl("move", onClick);
        },
        remove: function (onClick) {
            return buildControl("remove", onClick);
        },
        edit: function (onClick) {
            return buildControl("edit", onClick);
        },
        create: function (onClick) {
            return buildControl("create", onClick);
        },
        copy: function (onClick) {
            return buildControl("copy", onClick)
        },
        archive: function (onClick) {
            return buildControl("archive", onClick)
        },
        buildControlsBlock: function (tag, controls) {
            return controlsBEM.buildBlock(tag, controls, {}, "control");
        }
    };
});
