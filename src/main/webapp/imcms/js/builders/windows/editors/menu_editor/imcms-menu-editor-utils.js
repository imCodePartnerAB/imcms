let isStandalone = false;

module.exports = {
    changeDataLevelTheTopDoc:function ($element, functionUsages, diff) {
        let menuDocLvl = parseInt($element.attr("data-menu-items-lvl"));
        let difference;
        if (functionUsages === 0) {
            difference = menuDocLvl - 1;
            menuDocLvl = 1;
        } else {
            menuDocLvl = menuDocLvl - diff;
            difference = diff;
        }
        functionUsages++;

        $element.attr("data-menu-items-lvl", menuDocLvl);
        $element.children().each(function () {
            if ($(this).attr("data-menu-items-lvl")) {
                this.changeDataLevelTheTopDoc($(this), functionUsages, difference);
            }
        });
    },
    isStandaloneEditor: function () {
        return isStandalone;
    },
    setStandaloneEditor: function () {
        isStandalone = true;
    }
}
