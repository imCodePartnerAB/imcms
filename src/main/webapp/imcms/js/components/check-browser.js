define(
    'check-browser',
    function () {
        function isFirefox() {
            return checkerBrowser('Firefox');
        }

        function isChrome() {
            return checkerBrowser('Chrome');
        }

        function isSafari() {
            return checkerBrowser('Safari');
        }

        function isInternetExplorer() {
            return checkerBrowser('MSIE'); //not sure
        }

        function isOpera() {
            return checkerBrowser('Opera');
        }

        function checkerBrowser(checkName) {
            return window.navigator.userAgent.indexOf(checkName) > -1;
        }

        return {
            isChrome,
            isFirefox,
            isOpera,
            isSafari,
            isInternetExplorer
        }
    }
);