console.time("imCMS JS loaded");

Function.prototype.bindArgs = function () {
    return this.bind.apply(this, [null].concat(Array.prototype.slice.call(arguments)));
};
Function.prototype.applyAsync = function (args, context) {
    setTimeout(this.apply.bind(this, context, args));
};
if (!String.prototype.endsWith) {
    Object.defineProperty(String.prototype, 'endsWith', {
        value: function (searchString, position) {
            var subjectString = this.toString();
            if (position === undefined || position > subjectString.length) {
                position = subjectString.length;
            }
            position -= searchString.length;
            var lastIndex = subjectString.indexOf(searchString, position);
            return lastIndex !== -1 && lastIndex === position;
        }
    });
}
if (!String.prototype.startsWith) {
    Object.defineProperty(String.prototype, 'startsWith', {
        enumerable: false,
        configurable: false,
        writable: false,
        value: function (searchString, position) {
            position = position || 0;
            return this.indexOf(searchString, position) === position;
        }
    });
}
(function (imcms) {
    function registerModule(id, module) {
        if (imcms.modules[id]) {
            console.error("Module already registered! " + id);
            return;
        }

        if (imcms.config.dependencies[id] && imcms.config.dependencies[id].onLoad) {
            module = imcms.config.dependencies[id].onLoad(module);
        }

        imcms.modules[id] = module;
        console.log("%c Module " + id + " registered.", "color: blue;");
        runModuleLoader.applyAsync();
    }

    function getModule(id) {
        return imcms.modules[id];
    }

    function getDependency(id) {
        return imcms.config.dependencies[id];
    }

    function loadScript(dependency, id) {
        var registerFunction;
        dependency.id = id;

        if (dependency.moduleName) {
            registerFunction = registerModule.bindArgs(dependency.moduleName, true);
        }

        var onLoad = function () {
            registerFunction && registerFunction.applyAsync();
            runModuleLoader.applyAsync();
        };

        getScript(dependency, onLoad);
    }

    function loadDependencyById(id) {
        var dependency = getDependency(id);

        if (!dependency) {
            throw "No dependency found with id " + id;
        }

        if (imcms.loadedDependencies[id]) {
            console.error("Dependency is already loaded!!!!1 " + id);
            return;
        }

        imcms.loadedDependencies[id] = true;

        var loader;

        switch (typeof dependency) {
            case "string":
                if (dependency.indexOf(".") !== 0) {
                    dependency = imcms.config.basePath + "/" + dependency;
                }
                loader = appendScript;
                break;
            case "object":
                loader = loadScript;
                break;
            default:
                throw "Dependency type not resolved: " + dependency;
        }

        loader(dependency, id);
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

    function getScript(dependency, callback, async) {
        if (async === undefined) {
            async = true;
        }

        var url = dependency.path;
        var ajaxRequest = createXMLHttpRequest();
        ajaxRequest.open("GET", url, async);
        ajaxRequest.overrideMimeType && ajaxRequest.overrideMimeType('application/javascript');
        ajaxRequest.onreadystatechange = function () {
            if (ajaxRequest.readyState !== XMLHttpRequest.DONE) {
                return;
            }

            if (ajaxRequest.status === 200) {
                console.info('%c Script ' + dependency.id + " loaded.", "color: green;");
                var response = eval(ajaxRequest.responseText);
                callback && callback.applyAsync([response]);

            } else {
                console.error('Script get request error: ' + ajaxRequest.status + ' for url: ' + url);
            }
        };
        ajaxRequest.send(null);
    }

    function appendScript(url, id) {
        var script = document.createElement("script");
        script.async = true;
        script.setAttribute("data-loader", "imcms");

        var onLoad = function () {
            console.info('%c Module ' + id + " loaded.", "color: green;");
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

        switch (arguments[0] && arguments[0].constructor) {
            case undefined : // anonymous module with undefined id, nothing to modify
            case String : // means first arg is id, nothing to modify too
                resolvedArgs = unresolvedArgs;
                break;

            case Array : // in this case we have an anonymous module with dependencies array
                resolvedArgs = [anonymousModuleId].concat(unresolvedArgs);
                break;

            case Function : // anonymous independent module
                resolvedArgs = [anonymousModuleId, depsForIndependentModule].concat(unresolvedArgs);
                break;

            default :
                console.error("Can't resolve first argument for 'define'!");
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
                console.error("Can't resolve second argument for 'define'!");
                console.error(arguments);
        }

        return resolvedArgs;
    }

    /**
     * AMD interface function to catch only imcms-configured modules
     */
    this["define"] = function () {
        var resolvedArgs = resolveDefineArgs.apply(null, arguments);
        defineModule.applyAsync(resolvedArgs);
    };

    this["define"].amd = {};

    /**
     * AMD define function.
     * Defines module by it's (optional) id with (optional) dependencies
     * by calling factory function after dependencies load.
     *
     * @param {string?} id defined module id
     * @param {[]?} dependencies array as module ids
     * @param {function} factory which return is this defined module in result
     */
    imcms.define = function (id, dependencies, factory) {
        define.applyAsync(arguments);
    };
    /**
     * AMD require function.
     *
     * @param {string|[string]} id required module id(s)
     * @param {function} onLoad function that will be called with loaded required modules
     */
    imcms.require = function (id, onLoad) {
        registerRequires(id, onLoad);
        runModuleLoader.applyAsync();
    };

    function addToDependencyTree(id, dependencies) {
        if (imcms.dependencyTree[id]) {
            console.error("Dependency " + id + " already registered! Redundant define function calling!");
        }
        imcms.dependencyTree[id] = dependencies;
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
            runModuleLoader.applyAsync();
        };

        imcms.require(dependencies, onDependenciesLoad);
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

        imcms.requiresQueue.push({
            requires: requires,
            onLoad: onLoad
        });
    }

    var failsCount = 0;

    function runModuleLoader() {
        var notSuccessRequiresBuffer = [];

        while (imcms.requiresQueue.length) {
            var require = imcms.requiresQueue.shift();
            var undefinedRequires = require.requires.filter(function (dependency) {
                return !imcms.modules[dependency];
            });

            if (undefinedRequires.length) {
                undefinedRequires = undefinedRequires.filter(function (dependency) {
                    return !imcms.loadedDependencies[dependency];
                });

                if (undefinedRequires.length) {
                    undefinedRequires.forEach(loadDependencyById);
                    notSuccessRequiresBuffer.push(require);

                } else if (failsCount < 5000) {// dummy fail limit
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

        (function () {
            notSuccessRequiresBuffer.forEach(function (require) {
                imcms.requiresQueue.push(require);
            });
            setTimeout(runModuleLoader, 200);
        }).applyAsync();
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
        console.info("%c Found entry point " + mainScriptPath, "color: blue;");
        return mainScriptPath;
    }

    var mainScriptPath = getMainScriptPath();
    appendScript(mainScriptPath, "imcms-main");
})(Imcms);
