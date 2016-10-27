/**
 * Created by Shadowgun on 04.06.2015.
 *
 * Upgraded by Serhii Maksymchuk in 2016
 */
Imcms.BackgroundWorker = {
    registeredTasks: [],
    completedTasksOptions: [],
    processWindow: undefined,
    contentChangedListeners: [],

    /**
     * Options for every task, all are optional
     */
    Options: {
        /**
         * Set true if you want to reload whole page
         */
        reloadWholePage: false,
        /**
         * Set true if you want to reload only <body> content.
         * $(document).ready() will not run again
         */
        refreshPage: false,
        /**
         * Process window with grey background and gif while loading
         */
        showProcessWindow: false,
        /**
         * If you want to redirect somewhere, just add URL
         */
        redirectURL: "",
        /**
         * If you do not want to reload whole page or <body>, you can specify
         * element to be reloaded and callback if needs
         */
        reloadContent: {
            /**
             * jQuery selected element
             */
            element: $(document),
            callback: function () {
            }
        }
    },

    /**
     * Creates process window with gif logo while BackgroundWorker works
     */
    createProcessWindow: function () {
        var $this = Imcms.BackgroundWorker,
            $window = $(window),
            $spinner = $("<div>").addClass("spinner");

        $("body").css({overflow: "hidden"});
        $this.processWindow = $("<div>")
            .addClass("process-window")
            .appendTo("body")
            .append($("<img>").addClass("logo").attr("src", Imcms.Linker.get("imcmsLogo")))
            .append($spinner)
            .css({width: $window.width(), height: $window.height(), padding: ($window.height() - 400) / 2 + "px 0"})
            .fadeIn(1000);
        for (var i = 0; i < 3; i++) {
            $("<div>").addClass("bounce" + i).appendTo($spinner);
        }
    },

    /**
     * Create Background work and show process window while work undone
     * @param opt - see {@link Imcms.BackgroundWorker.Options}
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

    _shouldRefreshPage: function (element) {
        return element.refreshPage;
    },

    _shouldReloadWholePage: function (element) {
        return element.reloadWholePage;
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
            var redirectOption = $this.completedTasksOptions
                .find(function (option) {
                    return option.redirectURL; // find first option with redirecting, other will be ignored!
                });

            var reloadElementOptions = $this.completedTasksOptions
                .filter(function (option) {
                    return option.reloadContent;
                });

            if (redirectOption) {
                location.href = redirectOption.redirectURL;

            } else if (reloadElementOptions.length) {
                reloadElementOptions.forEach(function (option) {
                    option.reloadContent.element.reload(option.reloadContent.callback);
                });

            } else if ($this.completedTasksOptions.some($this._shouldReloadWholePage)) {
                $this.reloadWholePage();

            } else if ($this.completedTasksOptions.some($this._shouldRefreshPage)) {
                $this.reloadPage()
            }

            $this.completedTasksOptions = [];
            $this.closeProcessWindow();
        }
    },

    /**
     * Reload Page and replace <body> with new
     */
    reloadPage: function () {
        Imcms.BackgroundWorker.loadPage(location.href);
    },

    /**
     * Loads Page by url and replaces <body> with new
     * @param url - url of result page
     */
    loadPage: function (url) {
        $.ajax({
            url: url,
            success: Imcms.BackgroundWorker.refreshPageContent
        });
    },

    /**
     * Reloads whole page, not only <body> as {@link reloadPage}
     */
    reloadWholePage: function () {
        location.reload();
    },

    /**
     * Replace page <body> with new
     * @param content
     */
    refreshPageContent: function (content) {
        var $this = Imcms.BackgroundWorker;
        var pattern = /<body[^>]*>((.|[\n\r])*)<\/body>/im;

        content = pattern.exec(content)[0];
        content = $($.parseHTML($.trim(content)));

        $("body>*").remove();
        var $body = $("body");
        $body.append(content).append($this.processWindow);

        if (!$.cookie("userLoggedIn")) {
            Imcms.isEditMode = false;
        }

        new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);

        $this.contentChangedListeners.forEach(function (item) {
            item.call($this);
        });
    },

    /**
     * Close Process Window
     * Usually it happens when all tasks have been done
     */
    closeProcessWindow: function () {
        var $this = Imcms.BackgroundWorker;

        if ($this.processWindow) {
            setTimeout(function () {
                $this.processWindow.fadeOut(1200, function () {
                    $("body").css({overflow: "auto"});
                    $this.processWindow.remove();
                    delete $this.processWindow;
                });
            }, 300);
        }
    },

    /**
     * Add page content changed listener
     * @param {Function} listener
     */
    addOnContentChangedListener: function (listener) {
        Imcms.BackgroundWorker.contentChangedListeners.push(listener);
    }
};
