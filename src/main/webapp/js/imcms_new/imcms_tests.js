/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 14.07.17.
 */
Imcms.define("imcms-tests", ["imcms", "jquery"], function (imcms, $) {
    return {
        checkRequired: function () {
            Imcms.require("imcms-tests", function (tests) {
                console.assert(tests, "Tests are empty! " + tests);
                return tests;
            });
            return true;
        },
        requireJquery: function () {
            Imcms.require("jquery", function ($) {
                console.assert($, "jQuery not loaded!" + $);
                return $;
            });
            return true;
        },
        requireTwoJqueries: function () {
            Imcms.require(["jquery", "jquery"], function ($1, $2) {
                console.assert($1 === $2, "Two deps not loaded!");
                return $1 === $2;
            });
            return true;
        },
        checkModules: function () {
            var isThereMoreThanOneModule = Object.keys(imcms.modules).length > 1;
            console.assert(isThereMoreThanOneModule, "There should be more modules than only one!");
            return isThereMoreThanOneModule;
        },
        checkJqueryModuleExist: function () {
            var jqueryModuleVersion = $.fn.jquery;
            console.assert(jqueryModuleVersion, "There should be jquery module!");
            return jqueryModuleVersion;
        },
        checkAnonymousDependentModuleDefinition: function () {
            var isLoadedFlag = false;

            imcms.define(["imcms"], function (imcms2) {
                isLoadedFlag = true;
                console.assert(imcms === imcms2, "Anonymous dependent module definition works wrong!");
            });

            setTimeout(function () {
                console.assert(isLoadedFlag, "Anonymous dependent module definition not working!");
            }, 500);

            return true;
        },
        checkAnonymousIndependentModuleDefinition: function () {
            var isLoadedFlag = false;

            imcms.define(["imcms"], function (imcms2) {
                isLoadedFlag = true;
                console.assert(imcms === imcms2, "Anonymous independent module definition works wrong!");
            });

            setTimeout(function () {
                console.assert(isLoadedFlag, "Anonymous independent module definition not working!");
            }, 500);

            return true;
        },
        checkJqueryRequire: function () {
            Imcms.require("jquery", function ($) {
                console.assert($.fn.jquery, "jQueries have same versions!!");
            });
            return true;
        },
        checkJqueryMaskModuleLoading: function () {
            Imcms.require(["jquery", "jquery-mask"], function ($) {
                console.assert($, "jQuery not loaded!");
                console.assert($.fn.mask, "jQuery.mask not found!");
            });
            return true;
        },
        checkIndependentDefine: function () {
            Imcms.define("imcms-independent-define", function () {
                console.assert(arguments.length === 0, "Some wrong arguments applied! " + arguments);
                return {
                    test: function () {
                        console.assert(Imcms.modules["imcms-independent-define"], "Module was not added!");
                    }
                }
            });
            setTimeout(function () {
                Imcms.require("imcms-independent-define", function (test) {
                    test.test();
                });
            }, 300);

            return true;
        },
        checkInputEventSequance: function () {
            Imcms.require(["jquery"], function ($) {

                $('body').detach();

                function keydown() {
                    console.log("keydown: " + this.value)
                }

                function keypress() {
                    console.log("keypress: " + this.value)
                }

                function keyup() {
                    console.log("keyup: " + this.value)
                }

                function input() {
                    console.log("input: " + this.value)
                }

                var $body = $('<body>')
                    .append($('<input>').keydown(keydown).keyup(keyup).keypress(keypress).on('input', input));
                $('html').append($body);
            });
            return true;
        },
        runAllTests: function () {
            var testsRun = 0;
            var totalPassed = 0;
            var totalFailed = 0;

            for (var testFunc in this) {
                if ((testFunc !== "runAllTests") && this.hasOwnProperty(testFunc)) {
                    console.log("%c Running " + testFunc + " test.", "color: blue;");
                    var passed = this[testFunc].call();
                    var logMessage = "%c " + testFunc + (passed ? " passed." : " failed");
                    var color = passed ? "color: green;" : "color: red;";
                    console.log(logMessage, color);

                    testsRun++;
                    passed ? totalPassed++ : totalFailed++;
                }
            }

            console.info("%c Tests run: " + testsRun, "color: blue;");
            console.info("%c Total passed: " + totalPassed, "color: green;");
            console.info("%c Total failed: " + totalFailed, "color: red;");
        }
    }


});
