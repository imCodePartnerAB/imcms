console.time("imCMS JS loaded");

Imcms.loadedDependencies = {};
Imcms.dependencyTree = {
    imcms: []
};
Imcms.requiresQueue = [];
Imcms.browserInfo = {
    isIE10: window.navigator.userAgent.indexOf("Mozilla/5.0 (compatible; MSIE 10.0;") === 0
};
Imcms.config = {
    basePath: Imcms.contextPath + "/js/imcms_new",
    dependencies: {
        "jquery": {
            path: "//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js",
            onLoad: function ($) {
                return $.noConflict(true);
            }
        },
        "tinyMCE": {
            path: "//cdn.tinymce.com/4/tinymce.min.js",
            moduleName: "tinyMCE",
            onLoad: function () {
                var tinyMCE = window.tinyMCE;

                // TinyMCE version for IE 10 plugins require "tinymce" in global scope
                if (!Imcms.browserInfo.isIE10) {
                    delete window.tinyMCE;
                    delete window.tinymce;
                }

                return tinyMCE;
            }
        },
        "imcms-tests": "imcms_tests.js",
        // components
        "imcms-calendar": "components/imcms_calendar.js",
        "imcms-date-picker": "components/imcms_date_picker.js",
        "imcms-time-picker": "components/imcms_time_picker.js",
        "imcms-uuid-generator": "components/imcms_uuid_generator.js",
        "imcms-image-cropper": "components/imcms_image_cropper.js",
        "imcms-validator": "components/imcms_validator.js",
        // editors initializer
        "imcms-text-editor-initializer": "editor_initializer/imcms_text_editor_initializer.js",
        "imcms-editors-initializer": "editor_initializer/imcms_editors_initializer.js",
        "imcms-image-editor-initializer": "editor_initializer/imcms_image_editor_initializer.js",
        "imcms-loop-editor-initializer": "editor_initializer/imcms_loop_editor_initializer.js",
        "imcms-menu-editor-initializer": "editor_initializer/imcms_menu_editor_initializer.js",
        // editors init data modules
        "imcms-image-editor-init-data": "editor_initializer/imcms_editors_init_data/imcms_image_editor_init_data.js",
        "imcms-menu-editor-init-data": "editor_initializer/imcms_editors_init_data/imcms_menu_editor_init_data.js",
        "imcms-loop-editor-init-data": "editor_initializer/imcms_editors_init_data/imcms_loop_editor_init_data.js",
        // init strategy
        "imcms-editor-init-strategy": "editor_initializer/imcms_editor_init_strategy.js",
        // <builders>
        // basic components builders
        "imcms-buttons-builder": "builders/components/imcms_buttons_builder.js",
        "imcms-flags-builder": "builders/components/imcms_flags_builder.js",
        "imcms-checkboxes-builder": "builders/components/imcms_checkboxes_builder.js",
        "imcms-radio-buttons-builder": "builders/components/imcms_radio_buttons_builder.js",
        "imcms-selects-builder": "builders/components/imcms_selects_builder.js",
        "imcms-texts-builder": "builders/components/imcms_texts_builder.js",
        "imcms-switch-builder": "builders/components/imcms_switch_builder.js",
        "imcms-choose-image-builder": "builders/components/imcms_choose_image_builder.js",
        "imcms-keywords-builder": "builders/components/imcms_keywords_builder.js",
        "imcms-date-time-builder": "builders/components/imcms_date_time_builder.js",
        "imcms-controls-builder": "builders/components/imcms_controls_builder.js",
        "imcms-primitives-builder": "builders/imcms_primitives_builder.js",
        "imcms-components-builder": "builders/imcms_components_builder.js",
        "imcms-window-components-builder": "builders/imcms_window_components_builder.js",
        // <windows>
        "imcms-window-builder": "builders/windows/imcms_window_builder.js",
        "imcms-modal-window-builder": "builders/windows/imcms_modal_window_builder.js",
        "imcms-content-manager-builder": "builders/windows/imcms_content_manager_builder.js",
        "imcms-page-info-builder": "builders/windows/imcms_page_info_builder.js",
        // <page_info_tabs>
        "imcms-page-info-tabs-builder": "builders/windows/page_info_tabs/imcms_page_info_tabs_builder.js",
        "imcms-page-info-tabs-linker": "builders/windows/page_info_tabs/imcms_page_info_tabs_linker.js",
        "imcms-appearance-tab-builder": "builders/windows/page_info_tabs/imcms_appearance_tab_builder.js",
        "imcms-life-cycle-tab-builder": "builders/windows/page_info_tabs/imcms_life_cycle_tab_builder.js",
        "imcms-keywords-tab-builder": "builders/windows/page_info_tabs/imcms_keywords_tab_builder.js",
        "imcms-categories-tab-builder": "builders/windows/page_info_tabs/imcms_categories_tab_builder.js",
        "imcms-access-tab-builder": "builders/windows/page_info_tabs/imcms_access_tab_builder.js",
        "imcms-permissions-tab-builder": "builders/windows/page_info_tabs/imcms_permissions_tab_builder.js",
        "imcms-templates-tab-builder": "builders/windows/page_info_tabs/imcms_templates_tab_builder.js",
        "imcms-status-tab-builder": "builders/windows/page_info_tabs/imcms_status_tab_builder.js",
        // </page_info_tabs>
        // editors
        "imcms-menu-editor-builder": "builders/windows/editors/imcms_menu_editor_builder.js",
        "imcms-document-editor-builder": "builders/windows/editors/imcms_document_editor_builder.js",
        "imcms-image-editor-builder": "builders/windows/editors/imcms_image_editor_builder.js",
        "imcms-loop-editor-builder": "builders/windows/editors/imcms_loop_editor_builder.js",
        // </windows>
        // other builders
        "imcms-bem-builder": "builders/imcms_bem_builder.js",
        "imcms-image-content-builder": "builders/imcms_image_content_builder.js",
        "imcms-admin-panel-builder": "builders/imcms_admin_panel_builder.js",
        // </builders>
        // rest api
        "imcms-rest-api": "rest/imcms_rest_api.js",
        "imcms-files-rest-api": "rest/imcms_files_rest_api.js",
        "imcms-documents-rest-api": "rest/imcms_documents_rest_api.js",
        "imcms-users-rest-api": "rest/imcms_users_rest_api.js",
        "imcms-categories-rest-api": "rest/imcms_categories_rest_api.js",
        "imcms-roles-rest-api": "rest/imcms_roles_rest_api.js",
        "imcms-templates-rest-api": "rest/imcms_templates_rest_api.js",
        "imcms-category-types-rest-api": "rest/imcms_category_types_rest_api.js",
        "imcms-loop-rest-api": "rest/imcms_loop_rest_api.js",
        "imcms-menu-rest-api": "rest/imcms_menu_rest_api.js",
        "imcms-image-rest-api": "rest/imcms_image_rest_api.js"
    }
};
if (Imcms.browserInfo.isIE10) {
    Imcms.config.dependencies.tinyMCE.path = "//cdnjs.cloudflare.com/ajax/libs/tinymce/4.5.7/tinymce.min.js";
}
Imcms.modules = {
    imcms: Imcms // default module
};
Function.prototype.bindArgs = function () {
    return this.bind.apply(this, [null].concat(Array.prototype.slice.call(arguments)));
};
Function.prototype.applyAsync = function (args, context) {
    setTimeout(this.apply.bind(this, context, args));
};
(function () {
    function registerModule(id, module) {
        console.log("Registering module " + id);
        if (Imcms.modules[id]) {
            console.error("Module already registered! " + id);
            return;
        }

        if (Imcms.config.dependencies[id] && Imcms.config.dependencies[id].onLoad) {
            module = Imcms.config.dependencies[id].onLoad(module);
        }

        Imcms.modules[id] = module;
    }

    function getModule(id) {
        return Imcms.modules[id];
    }

    function getDependency(id) {
        return Imcms.config.dependencies[id];
    }

    function loadScript(dependency) {
        var registerFunction;

        if (dependency.moduleName) {
            registerFunction = registerModule.bindArgs(dependency.moduleName, true);
        }

        var onLoad = function () {
            registerFunction && registerFunction.call();
            setTimeout(runModuleLoader);
        };

        getScript(dependency.path, onLoad);
    }

    function loadDependencyById(id) {
        var dependency = getDependency(id);

        if (!dependency) {
            console.error("No dependency found with id " + id);
            return;
        }

        if (Imcms.loadedDependencies[id]) {
            console.error("Dependency is already loaded!!!! " + id);
            return;
        }

        Imcms.loadedDependencies[id] = true;

        var loader;

        switch (typeof dependency) {
            case "string":
                if (dependency.indexOf(".") !== 0) {
                    dependency = Imcms.config.basePath + "/" + dependency;
                }
                loader = appendScript;
                break;
            case "object":
                loader = loadScript;
        }

        loader(dependency);
    }

    function createXMLHttpRequest() {
        if (typeof XMLHttpRequest !== 'undefined') {
            return new XMLHttpRequest();
        }
        // old browsers support
        var versions = [
            "MSXML2.XmlHttp.6.0",
            "MSXML2.XmlHttp.5.0",
            "MSXML2.XmlHttp.4.0",
            "MSXML2.XmlHttp.3.0",
            "MSXML2.XmlHttp.2.0",
            "Microsoft.XmlHttp"
        ];

        var xhr;
        for (var i = 0; i < versions.length; i++) {
            try {
                xhr = new ActiveXObject(versions[i]);
                break;
            } catch (e) {
            }
        }
        return xhr;
    }

    function getScript(url, callback, async) {
        if (async === undefined) {
            async = true;
        }
        var ajaxRequest = createXMLHttpRequest();
        ajaxRequest.open("GET", url, async);
        ajaxRequest.overrideMimeType && ajaxRequest.overrideMimeType('application/javascript');
        ajaxRequest.onreadystatechange = function () {
            if (ajaxRequest.readyState !== XMLHttpRequest.DONE) {
                return;
            }

            if (ajaxRequest.status === 200) {
                console.info('script ' + url + " loaded successfully.");
                var response = eval(ajaxRequest.responseText);
                callback && callback(response);

            } else {
                console.error('Script get request error: ' + ajaxRequest.status + ' for url: ' + url);
            }
        };
        ajaxRequest.send(null);
    }

    function appendScript(url, callback) {
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.async = true;
        script.setAttribute("data-loader", "imcms");

        var onLoad = function () {
            console.info('module ' + url + " loaded successfully.");
            callback && callback.call();
        };

        if (script.readyState) {  // IE support
            script.onreadystatechange = function () {
                if (script.readyState === "loaded" || script.readyState === "complete") {
                    script.onreadystatechange = null;
                    onLoad();
                }
            };
        } else {  // Other normal browsers
            script.onload = onLoad;
        }

        script.src = url;
        document.getElementsByTagName("head")[0].appendChild(script);
    }

    function resolveDefineArgs() {
        var anonymousModuleId = undefined,
            depsForIndependentModule = [],
            unresolvedArgs = Array.prototype.slice.call(arguments),
            resolvedArgs;

        try {
            switch (arguments[0] && arguments[0].constructor) {
                case undefined : // anonymous module with undefined id, nothing to modify
                case String : // means first arg is id
                    resolvedArgs = unresolvedArgs;
                    break;

                case Array : // in this case we have an anonymous module with dependencies array
                    resolvedArgs = [anonymousModuleId].concat(unresolvedArgs);
                    break;

                case Function : // anonymous independent module
                    resolvedArgs = [anonymousModuleId, depsForIndependentModule].concat(unresolvedArgs);
                    break;

                default :
                    console.error("Something wrong!");
                    console.error(arguments);
            }
            switch (resolvedArgs[1] && resolvedArgs[1].constructor) {
                case Array : // dependencies are presented, nothing to change
                    break;

                case Function : // independent module and dependencies are not presented
                    var factory = resolvedArgs[1];
                    resolvedArgs[1] = []; // empty dependencies array
                    resolvedArgs[2] = factory;
                    break;

                default :
                    console.error("Something wrong!");
                    console.error(arguments);
            }
        } catch (e) {
            console.error(e);
            console.error(arguments);
        }

        return resolvedArgs;
    }

    /**
     * AMD interface function to catch only imcms-configured modules
     */
    function define() {
        var resolvedArgs = resolveDefineArgs.apply(null, arguments);
        defineModule.apply(null, resolvedArgs);
    }

    define.amd = {};

    /**
     * AMD define function.
     * Defines module by it's (optional) id with (optional) dependencies
     * by calling factory function after dependencies load.
     *
     * @param {string?} id defined module id
     * @param {[]?} dependencies array as module ids
     * @param {function} factory which return is this defined module in result
     */
    Imcms.define = function (id, dependencies, factory) {
        define.apply(null, arguments);
    };
    /**
     * AMD require function.
     *
     * @param {string|[string]} id required module id(s)
     * @param {function} onLoad function that will be called with loaded required modules
     */
    Imcms.require = function (id, onLoad) {
        registerRequires(id, onLoad);
        setTimeout(runModuleLoader);
    };

    function addToDependencyTree(id, dependencies) {
        if (Imcms.dependencyTree[id]) {
            console.error("Dependency " + id + " already registered! Redundant define function calling!");
        }
        Imcms.dependencyTree[id] = dependencies;
    }

    function defineModule(id, dependencies, factory) {
        if (id) {
            addToDependencyTree(id, dependencies);
        }

        var onDependenciesLoad = function () {
            var module = factory.apply(null, arguments);
            if (id) { // register only not anonymous modules
                registerModule(id, module);
            }
            setTimeout(runModuleLoader);
        };

        Imcms.require(dependencies, onDependenciesLoad);
    }

    function registerRequires(id, onLoad) {
        var requires;

        switch (id.constructor) {
            case String :
                requires = [id];
                break;
            case Array :
                requires = id;
                break;
            default :
                console.error("Wrong type: ");
                console.error(id);
        }

        Imcms.requiresQueue.push({
            requires: requires,
            onLoad: onLoad
        });
    }

    var failsCount = 0;

    function runModuleLoader() {
        var notSuccessRequiresBuffer = [];

        while (Imcms.requiresQueue.length) {
            var require = Imcms.requiresQueue.shift();
            var undefinedRequires = require.requires.filter(function (dependency) {
                return !Imcms.modules[dependency];
            });

            if (undefinedRequires.length) {
                undefinedRequires = undefinedRequires.filter(function (dependency) {
                    return !Imcms.loadedDependencies[dependency];
                });

                if (undefinedRequires.length) {
                    undefinedRequires.forEach(loadDependencyById);
                    notSuccessRequiresBuffer.push(require);

                } else if (failsCount < 1000) {// dummy fail limit
                    // means dependency is still loading script, we should add it to queue after the while cycle
                    failsCount++;
                    notSuccessRequiresBuffer.push(require);

                } else {
                    console.error("Failed to load dependency:");
                    console.error(require);
                }
            } else {
                failsCount = 0;
                var dependencies = require.requires.map(getModule);
                require.onLoad && require.onLoad.applyAsync(dependencies);
            }
        }

        setTimeout(function () {
            notSuccessRequiresBuffer.forEach(function (require) {
                Imcms.requiresQueue.push(require);
            });
        });
    }

    function getMainScriptPath() {
        var imcmsMainScripts = Array.prototype.slice.call(document.scripts).filter(function (script) {
            return script.attributes["data-name"] && script.attributes["data-name"].value === "imcms";
        });

        if (!imcmsMainScripts || imcmsMainScripts.length === 0) {
            console.error("Not founded entry point for imCMS JS engine.\n" +
                "Should be script tag with attribute data-name=\"imcms\" and attribute data-main with path to script " +
                "to load as entry point.");
            return;
        }

        if (imcmsMainScripts.length !== 1) {
            console.error("Founded more than one entry points.");
            console.error(imcmsMainScripts);
            return;
        }

        var mainScriptPath = imcmsMainScripts[0].attributes["data-main"].value;
        console.info("Founded entry point " + mainScriptPath);
        return mainScriptPath;
    }

    var mainScriptPath = getMainScriptPath();
    appendScript(mainScriptPath);
})();
