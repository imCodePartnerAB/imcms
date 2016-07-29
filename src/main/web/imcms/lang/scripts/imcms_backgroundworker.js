/**
 * Created by Shadowgun on 04.06.2015.
 */
Imcms.BackgroundWorker = {
    registeredTasks: [],
    completedTasksOptions: [],
    processWindow: undefined,
    contentChangedListeners: [],

    /**
     *
     */
    createProcessWindow: function () {
        var $this = Imcms.BackgroundWorker,
            $window = $(window),
            $spinner = $("<div>").addClass("spinner");
        $("body").css({overflow: "hidden"});
        $this.processWindow = $("<div>")
            .addClass("process-window")
            .appendTo("body")
            .append($("<img>").addClass("logo").attr("src", Imcms.contextPath + "/images/logo-imcms.png"))
            .append($spinner)
            .css({width: $window.width(), height: $window.height(), padding: ($window.height() - 400) / 2 + "px 0"})
            .fadeIn(1000);
        for (var i = 0; i < 3; i++) {
            $("<div>").addClass("bounce" + i).appendTo($spinner);
        }
    },

    /**
     * Create Background work and show process window while work undone
     * @param opt
     * @return {Function|*}
     */
    createTask: function (opt) {
        var $this = Imcms.BackgroundWorker,
            ticket, task;

        task = function () {
            $this.onComplete(ticket, opt);
        };

        ticket = $this.registerTask(task);

        if (!$this.processWindow && (opt.showProcessWindow || opt.refreshPage)) {
            $this.createProcessWindow();
        }

        return task;
    },

    /**
     * @return {String} GUID
     */
    registerTask: function (task) {
        var $this = Imcms.BackgroundWorker,
            guid = $this.guid();

        $this.registeredTasks.push({task: task, guid: guid});

        return guid;
    },

    /**
     * @return {string}
     */
    guid: function () {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    },

    /**
     *
     * @param guid
     * @return {*}
     */
    findTaskByGUID: function (guid) {
        var $this = Imcms.BackgroundWorker,
            foundElements = $this.registeredTasks.filter(function (obj) {
                return obj.guid && obj.task && obj.guid === guid;
            });

        return foundElements.length ? foundElements[0] : undefined;
    },

    /**
     *
     * @param ticket
     * @param tskOpt
     */
    onComplete: function (ticket, tskOpt) {
        var $this = Imcms.BackgroundWorker,
            task = $this.findTaskByGUID(ticket);

        if (!task) {
            if ($this.processWindow) {
                $this.processWindow.remove();
                delete $this.processWindow;
            }
            throw new Error("Task with ticket " + ticket + "has been removed yet." +
                " Be sure that this task used once in your code");
        }

        $this.registeredTasks.remove(task);
        $this.completedTasksOptions.push(tskOpt);

        if (!$this.registeredTasks.length) {
            if ($this.completedTasksOptions.some(function (element) {
                    return element.refreshPage;
                })) {
                $this.reloadPage()
            }

            $this.closeProcessWindow();
        }
    },

    /**
     * Reload Page and replace <body> with new
     */
    reloadPage: function () {
        $.ajax({
            url: location.href,
            success: Imcms.BackgroundWorker.refreshPageContent
        });
    },

    /**
     * Replace page <body> with new
     * @param content
     */
    refreshPageContent: function (content) {
        var $this = Imcms.BackgroundWorker;
        var pattern = /<body[^>]*>((.|[\n\r])*)<\/body>/im;

        //$('html[manifest=saveappoffline.appcache]').attr('content', '');
        content = pattern.exec(content)[0];
        content = $($.parseHTML($.trim(content)));

        $("body>*").remove();
        $("body").append(content).append($this.processWindow);

        new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);

        $this.contentChangedListeners.forEach(function (item) {
            item.call($this);
        });
        if( !$.cookie("userLoggedIn")) {
            if ($("body").css('paddingLeft').length > 0) {
                $("body").removeAttr('style');
            }
        }
    },
    /**
     * Close Process Window
     * Usually it happens when all tasks have been done
     */
    closeProcessWindow: function () {
        var $this = Imcms.BackgroundWorker;

        setTimeout(function () {
            $this.processWindow.fadeOut(1200, function () {
                $("body").css({overflow: "auto"});
                $this.processWindow.remove();
                delete $this.processWindow;
            });
        }, 300);
    },

    /**
     * Add page content changed listener
     * @param {Function} listener
     */
    addOnContentChangedListener: function (listener) {
        var $this = Imcms.BackgroundWorker;

        $this.contentChangedListeners.push(listener);
    }
};
Imcms.BackgroundWorker.Options = {refreshPage: false, showProcessWindow: false};
