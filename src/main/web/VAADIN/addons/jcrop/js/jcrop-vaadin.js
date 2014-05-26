
var jcropJquery = jQuery.noConflict(true);

(function($) {

    /**
     * Checks if a selection is valid. Selection is valid if it has an area (width * height) greater than 0.
     *
     * @param selArea {{x1: number, y1: number, x2: number, y2: number}} the selection area to test
     *
     * @returns {boolean}   true if a valid selection, otherwise false
     */
    function isValidArea(selArea) {
        var area = (Math.max(selArea.x1, selArea.x2) - Math.min(selArea.x1, selArea.x2)) *
                   (Math.max(selArea.y1, selArea.y2) - Math.min(selArea.y1, selArea.y2));

        return area > 0;
    }

    /** Vaadin connector initialisation function for Jcrop widget. */
    window.com_imcode_imcms_jcrop_Jcrop = function() {

        /** The widget DOM container element. */
        var cont = $(this.getElement());
        /** The DOM image element that will be cropped. */
        var img = $("<img/>");

        cont.append(img);

        /**
         * Reference to jcrop api object that has been initialised on the image.
         * @type {$.Jcrop}
         */
        var
            jcrop,
            /**
             *
             * @type {com_imcode_imcms_jcrop_Jcrop}
             */
            self = this,
            /**
             * Did we just receive a release RPC method call from server.
             * If this flag is set then we ignore the "onRelease" method invocation by jcrop and reset this flag.
             * @type {boolean}
             */
            releaseFromServer = false,
            /**
             * Image source URL of the displayed image.
             * @type {string}
             */
            currentImageUrl,
            /**
             * The true with of the displayed image.
             * @type {number}
             */
            currentImageWidth,
            /**
             * The true height of the displayed image.
             * @type {number}
             */
            currentImageHeight,
            /**
             * The aspect ratio that the selection area must have.
             * @type {number}
             */
            currentAspectRatio,
            /**
             * The width of the image as it should be displayed. Can be larger/smaller than true width.
             * @type {number}
             */
            currentDisplayWidth,
            /**
             * The height of the image as it should be displayed. Can be larger/smaller than true height.
             * @type {number}
             */
            currentDisplayHeight,
            /**
             * The maximum width/height of the jcrop viewport area, that can be shown without visible scrollbars.
             * @type {{width: number, height: number}}
             */
            currentViewportDimensions,
            /**
             * ID returned from invocation of setTimeout.
             */
            updateViewportDimensionsTimer;

        /**
         * Invokes function updateViewportDimensions at a later time. Cancels any outstanding invocations
         * of updateViewportDimensionsLater that haven't completed.
         */
        function updateViewportDimensionsLater() {
            if (updateViewportDimensionsTimer != null) {
                clearTimeout(updateViewportDimensionsTimer);
            }

            updateViewportDimensionsTimer = setTimeout(function() {
                updateViewportDimensionsTimer = null;

                updateViewportDimensions();
            }, 1000);
        }

        /**
         * Calculates the available jcrop viewport dimensions (width / height) and if they have changed from
         * previous value, notifies the server side listeners.
         */
        function updateViewportDimensions() {
            var viewportDimensions = getViewportDimensions();

            if (currentViewportDimensions == null ||
                currentViewportDimensions.width != viewportDimensions.width ||
                currentViewportDimensions.height != viewportDimensions.height) {

                currentViewportDimensions = viewportDimensions;

                if (self.getState().reportViewportChange) {
                    // server rpc method
                    self.onViewportChanged(viewportDimensions.width, viewportDimensions.height);
                }
            }
        }

        /**
         * Initialiases the Jcrop javascript component and attaches it to the image.
         */
        function initJcrop() {
            // remove previous jcrop instance if we have one
            if (jcrop) {
                jcrop.destroy();
                jcrop = null;
            }

            var params = {
                /**
                 * Invoked when an area has been selected in the cropper. Jcrop handler.
                 *
                 * @param c {{x: number, y: number, x2: number, y2: number}}    The selection area.
                 */
                onSelect: function(c) {

                    if (isNaN(c.x)) {
                        // ignore invalid selection, this happens sometimes when we do the initial init of jcrop
                        // and the image doesn't yet have a display width/height
                        return;
                    }

                    /*
                    A little adjustment, it seems that at some scales the
                    coordinates can't reach maximum (true image width/height).
                    */
                    if ((currentImageWidth - c.x2) < 0.6) {
                        c.x2 = currentImageWidth;
                    }
                    if ((currentImageHeight - c.y2) < 0.6) {
                        c.y2 = currentImageHeight;
                    }

                    var area = {
                        x1: c.x,
                        y1: c.y,
                        x2: c.x2,
                        y2: c.y2
                    };

                    // server rpc method
                    // notify the server side listeners that the cropping selection has changed
                    self.onSelectionChanged(area);
                },
                /**
                 * Invoked when an area selection has been cleared (no selection) in the cropper. Jcrop handler.
                 */
                onRelease: function() {
                    if (releaseFromServer) {
                        releaseFromServer = false;
                        return;
                    }

                    // server rpc method
                    self.onSelectionChanged({ x1: 0, y1: 0, x2: 0, y2: 0 });
                },
                /** The real width / height of the image without any scaling. */
                trueSize: [self.getState().imageWidth, self.getState().imageHeight],
                /** Background color for jcrop when there's an active selection area. */
                bgColor: "#fff"
            };

            var initArea = self.getState().selectionArea;

            if (isValidArea(initArea)) {
                // set the initial crop selection area
                params.setSelect = [initArea.x1, initArea.y1, initArea.x2, initArea.y2];
            }

            if (self.getState().aspectRatio != null) {
                // the crop selection area should have a specific aspect ratio
                params.aspectRatio = self.getState().aspectRatio;
            }

            var displayWidth = self.getState().displayWidth,
                displayHeight = self.getState().displayHeight;

            img.css({
                width: (displayWidth > 0 ? displayWidth + "px" : ""),
                height: (displayHeight > 0 ? displayHeight + "px" : "")
            });

            img.attr({
                width: (displayWidth > 0 ? displayWidth : ""),
                height: (displayHeight > 0 ? displayHeight : "")
            });

            // We use a deprecated initialisation method. The correct init method uses a timer
            // which creates problems for us.
            jcrop = $.Jcrop(img[0], params);
        }

        // Vaadin component resize handler
        this.addResizeListener(cont.closest(".v-verticallayout")[0], function(e) {

            // When this function is called the element might still not have its full size.
            // Use a timer for viewport dimension calculation.
            updateViewportDimensionsLater();
        });

        /**
         * Executed by vaadin when server side state changes.
         */
        this.onStateChange = function() {
            var changeImageUrl = self.getState().resources.image.uRL,
                changeImageWidth = self.getState().imageWidth,
                changeImageHeight = self.getState().imageHeight,
                changeDisplayWidth = self.getState().displayWidth,
                changeDisplayHeight = self.getState().displayHeight,
                changeAspectRatio = self.getState().aspectRatio;


            if (currentImageUrl !== changeImageUrl ||
                currentImageWidth !== changeImageWidth ||
                currentImageHeight !== changeImageHeight ||
                currentDisplayWidth !== changeDisplayWidth ||
                currentDisplayHeight !== changeDisplayHeight ||
                currentAspectRatio !== changeAspectRatio) {

                if (currentImageUrl !== changeImageUrl) {
                    img.attr("src", changeImageUrl);
                }

                currentImageUrl = changeImageUrl;
                currentImageWidth = changeImageWidth;
                currentImageHeight = changeImageHeight;
                currentDisplayWidth = changeDisplayWidth;
                currentDisplayHeight = changeDisplayHeight;
                currentAspectRatio = changeAspectRatio;

                initJcrop();
            }
        };

        /**
         * Resets the cropper selection area.
         *
         * This is a client RPC method (invoked from server).
         */
        this.reset = function() {
            if (!jcrop) {
                return;
            }

            // set an one time flag, otherwise we'll end up notifying the server side about selection change
            releaseFromServer = true;
            jcrop.release();
        };

        /**
         * Selects an area in the cropper.
         *
         * This is a client RPC method (invoked from server).
         *
         * @param x1 {number}   The top left x-coordinate of the area.
         * @param y1 {number}   The top left y-coordinate of the area.
         * @param x2 {number}   The bottom right x-coordinate of the area.
         * @param y2 {number}   The bottom right y-coordinate of the area.
         */
        this.setSelection = function(x1, y1, x2, y2) {
            if (!jcrop) {
                return;
            }

            var area = {
                x1: x1,
                y1: y1,
                x2: x2,
                y2: y2
            };

            if (isValidArea(area)) {
                jcrop.setSelect([area.x1, area.y1, area.x2, area.y2]);
            } else {
                jcrop.release();
            }
        };


        /**
         * @returns {{width: number, height: number}} the maximum width and height that can be used for the viewport
         * without scrollbars appearing
         */
        function getViewportDimensions() {
            var verticalLayout = cont.closest(".v-verticallayout"),
                scrollableHeight = verticalLayout.closest(".v-scrollable").height(),
                verticalLayoutHeight = verticalLayout.outerHeight(),
                widgetHeight = cont.parent().height(),
                viewportWidth = verticalLayout.width(),
                viewportHeight = scrollableHeight - (verticalLayoutHeight - widgetHeight);

            return {
                width: viewportWidth,
                height: viewportHeight
            };
        }
    };

})(jcropJquery);
