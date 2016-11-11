/**
 * Created by Shadowgun on 26.03.2015.
 * Updated by Serhii and 3emluk in 2016
 */
Imcms.Image = {};
Imcms.Image.API = function () {
};
Imcms.Image.API.prototype = {
    read: function (request, response) {
        Imcms.Logger.log("Image.API::read :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("content.image", request.object) + this._parseURL(),
                type: "GET",
                success: response
            }), request);
    },
    update: function (request, response) {
        Imcms.Logger.log("Image.API::update :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("content.image", request.object) + this._parseURL(),
                type: "POST",
                data: request,
                success: response
            }), request);
    },
    remove: function (request, response) {
        Imcms.Logger.log("Image.API::remove :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("content.image", request.object) + this._parseURL(),
                type: "DELETE",
                success: response
            }), request);
    },
    _parseURL: function () {
        var searchURL = window.location.search;
        return (searchURL && searchURL.charAt(0) === "?")
            ? "&" + searchURL.slice(1)
            : "";
    }
};

Imcms.Image.Loader = function () {
    this.init();
};
Imcms.Image.Loader.prototype = {
    _api: new Imcms.Image.API(),
    _editorsList: [],
    init: function () {
        $(".editor-image").each(this.initEditor.bind(this));
    },
    initEditor: function (position, element) {
        this._editorsList[position] = new Imcms.Image.Editor(element, this);
    },
    getById: function (id, meta, langCode, callback) {
        this._api.read({object: ((meta || Imcms.document.meta) + "/" + id + "?langCode=" + langCode)},
            Imcms.Logger.log.bind(this, "Image::getById : ", callback));
    },
    getByLoopItemRef: function (id, loopId, entryId, meta, langCode, callback) {
        this._api.read({object: ((meta || Imcms.document.meta) + "/" + id + "?loopId=" + loopId + "&entryId=" + entryId + "&langCode=" + langCode)},
            Imcms.Logger.log.bind(this, "Image::getByLoopItemRef : ", callback));
    },
    getByPath: function (path, callback) {
        this._api.read({object: path}, callback);
    },
    getPreview: function (folder, name, width, height, extension, callback) {
        this._api.read({object: folder + name + "-" + width + "-" + height + "." + extension},
            callback);
    },
    save: function (id, meta, isShared, langCode, data, callback) {
        this._api.update({
            sharedMode: isShared,
            imageDomainObject: JSON.stringify(data),
            object: ((meta || Imcms.document.meta) + "/" + id + "?langCode=" + langCode)
        }, Imcms.Logger.log.bind(this, "Image::save : ", callback));
    },
    saveLoopItem: function (id, meta, isShared, loopId, entryId, langCode, data, callback) {
        this._api.update({
            sharedMode: isShared,
            imageDomainObject: JSON.stringify(data),
            object: ((meta || Imcms.document.meta) + "/" + id + "?loopId=" + loopId + "&entryId=" + entryId + "&langCode=" + langCode)
        }, Imcms.Logger.log.bind(this, "Image::saveLoopItem : ", callback));
    },
    remove: function (id, meta, langCode, callback) {
        this._api.remove({object: ((meta || Imcms.document.meta) + "/" + id + "?langCode=" + langCode)},
            Imcms.Logger.log.bind(this, "Image::remove : ", callback));
    },
    removeLoopItem: function (id, loopId, entryId, meta, langCode, callback) {
        this._api.remove({object: ((meta || Imcms.document.meta) + "/" + id + "?loopId=" + loopId + "&entryId=" + entryId + "&langCode=" + langCode)},
            Imcms.Logger.log.bind(this, "Image::removeLoopItem : ", callback));
    }
};

Imcms.Image.Editor = function (element, loader) {
    this._element = element;
    this._loader = loader;
    this.init();
};
Imcms.Image.Editor.prototype = {
    _element: {},
    _frame: {},
    _id: {},
    _meta: {},
    _loopId: undefined,
    _entryId: undefined,
    _loader: {},
    _builder: {},
    _imageViewAdapter: {},
    _infoViewAdapter: {},
    _imageCropper: {},
    _isShowed: false,
    _isLoaded: false,
    _source: {},
    _primarySource: {},
    _language: '',

    init: function () {
        var data = $(this._element).data();
        this._id = data.no;
        this._meta = data.meta;
        this._loopId = data.loop;
        this._entryId = data.entry;
        this._language = Imcms.language.code;
        this.buildView().buildExtra();
        if (data.loop && data.entry) {
            this._loader.getByLoopItemRef(this._id, data.loop, data.entry, this._meta, this._language, this.initSource.bind(this));
        }
        else {
            this._loader.getById(this._id, this._meta, this._language, this.initSource.bind(this));
        }
    },
    initSource: function (data) {
        this._primarySource = data;
        this._getSource(data);
    },
    buildView: function () {
        this._builder = new JSFormBuilder("<div>")
            .form()
            .div()
            .setClass("imcms-header")
            .div()
            .html("Image Editor")
            .setClass("imcms-title")
            .end()
            .button()
            .reference("closeButton")
            .setClass("imcms-close-button")
            .on("click", $.proxy(this.close, this))
            .end()
            .end()
            .div()
            .setClass("imcms-content")
            .div()
            .setClass("image")
            .reference("imageView")
            .end()
            .div()
            .setClass("info")
            .reference("infoView")
            .end()
            .end()
            .div()
            .setClass("imcms-footer")
            .button()
            .html("Save and close")
            .setClass("imcms-positive imcms-save-and-close")
            .on("click", this.confirm.bind(this, "You have not added an alt - text to the image", "Do you want to continue?", (function () {
                //Getting input value
                var altText = $(this._infoViewAdapter._infoRef.getHTMLElement()).find("input[name=alternateText]").val();
                return altText === "" || altText === " ";
            }).bind(this), this.save.bind(this)))
            .end()
            .button()
            .html("Remove image and close")
            .setClass("imcms-neutral remove-image")
            .on("click", $.proxy(this._onRemoveImage, this))
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo("body").addClass("editor-form editor-image reset");
        return this;
    },
    buildCropper: function (data) {
        if (this._imageCropper instanceof Imcms.Image.ImageCropper) {
            this._imageCropper.close();
        }
        var isFreeTransformed = (this._imageViewAdapter._imageSource.imageInfo.width / this._imageViewAdapter._imageSource.imageInfo.height)
            !== ((data.cropRegion.cropX2 - data.cropRegion.cropX1) / (data.cropRegion.cropY2 - data.cropRegion.cropY1));
        this._imageCropper = new Imcms.Image.ImageCropper({
            container: this._builder.ref("imageView").getHTMLElement(),
            freeTransformed: isFreeTransformed,
            onCropChanged: this._onCropRegionChanged.bind(this)
        });
        if (this._isShowed) {
            setTimeout(function () {
                this._imageCropper.initialize();
                this._imageCropper.changeCropping(data.cropRegion.cropX1, data.cropRegion.cropY1, data.cropRegion.cropX2, data.cropRegion.cropY2);
                this._imageCropper.changeDestinationRect(data.cropRegion.cropX2, data.cropRegion.cropY2);
            }.bind(this), 250);
        }
    },
    buildExtra: function () {
        this._frame = new Imcms.FrameBuilder()
            .title("Image Editor")
            .tooltip((this._loopId) ? "L" + this._loopId + "-E" + this._entryId + "-N" + this._id : this._id)
            .click($.proxy(this.open, this))
            .build()
            .prependTo(this._element);
    },
    _getSource: function (data) {
        this._source = data;
        this._isLoaded = false;
        this.buildImageView(data)
            .buildInfoView(data);
    },
    buildImageView: function (data) {
        $(this._builder.ref("imageView").getHTMLElement()).empty();
        this._imageViewAdapter = new Imcms.Image.ImageViewAdapter({
            element: this._builder.ref("imageView").getHTMLElement(),
            onImageLoaded: $.proxy(this.onImageLoaded, this, data)
        });
        this._imageViewAdapter.update(data);
        return this;
    },
    onImageLoaded: function (data) {
        this.buildCropper(data);
        this._isLoaded = true;
    },
    buildInfoView: function (data) {
        $(this._builder.ref("infoView").getHTMLElement()).empty();
        this._infoViewAdapter = new Imcms.Image.ImageInfoAdapter({
            infoRef: this._builder.ref("infoView"),
            onDisplaySizeChanged: this._onDisplaySizeChanged.bind(this),
            onChooseFile: $.proxy(this._onChooseFile, this),
            currentElement: this._element
        });
        this._infoViewAdapter.update(data);

        Imcms.Editors.Language.read(this.addLanguageSwitches.bind(this));
        return this;
    },

    addLanguageSwitches: function (id) {
        $.each(id, $.proxy(this.addLanguageSwitch, this));
    },
    addLanguageSwitch: function (language, code) {
        var languageContainer = $(this._builder.ref("image-editor-language").getHTMLElement());
        if (languageContainer.find("#" + code + "Switch").length == 0) {
            languageContainer.append($("<img>").addClass("content-preview-image")
                .addClass(this._language === code ? 'active' : '')
                .attr("id", code + "Switch")
                .attr("src", Imcms.Linker._contextPath + '/images/ic_' + language.toLowerCase() + '.png')
                .data("imageInfo", '').on("click", this._onLanguageChanged.bind(this, code)))
        }
    },

    _onLanguageChanged: function (lang) {
        var data = $(this._element).data();
        this._language = lang;

        if (data.loop && data.entry) {
            this._loader.getByLoopItemRef(data.no, data.loop, data.entry, data.meta, this._language, this.initSource.bind(this));
        }
        else {
            this._loader.getById(data.no, data.meta, this._language, this.initSource.bind(this));
        }
    },

    _onDisplaySizeChanged: function (size) {
        this._imageCropper.changeDestinationRect(+size.width, +size.height);
    },
    _onChooseFile: function () {
        Imcms.Editors.Content.showDialog({
            onApply: $.proxy(this._onFileChosen, this),
            onCancel: $.proxy(this._onFileChosen, this)
        });
        $(this._builder[0]).fadeOut("fast");
    },
    _onSaveReloadTask: function (showProcessWindow) {
        var $element = $(this._element);
        return {
            showProcessWindow: showProcessWindow,
            reloadContent: {
                element: $element,
                callback: function () {
                    new Imcms.Image.Editor($element[0], Imcms.Editors.Image);
                }
            }
        }
    },
    _onRemoveImage: function () {
        var data = $(this._element).data();
        if (data.loop && data.entry) {
            this._loader.removeLoopItem(data.no, data.loop, data.entry, data.meta, this._language,
                Imcms.BackgroundWorker.createTask(this._onSaveReloadTask(true)));
        }
        else {
            this._loader.remove(data.no, data.meta, this._language,
                Imcms.BackgroundWorker.createTask(this._onSaveReloadTask(true)));
        }
        this.close();
    },
    _onFileChosen: function (data) {
        if (data) {
            var clonedData = jQuery.extend(true, {}, data);
            clonedData.source = jQuery.extend(true, {}, data);
            clonedData.cropRegion = {
                cropX1: 0,
                cropX2: clonedData.imageInfo.width,
                cropY1: 0,
                cropY2: clonedData.imageInfo.height,
                width: clonedData.imageInfo.width,
                height: clonedData.imageInfo.height,
                valid: true
            };
            this._getSource(Imcms.Utils.merge(clonedData, this._source));
        }
        else {
            this._getSource(this._source);
        }
        this.open();
    },
    _onCropRegionChanged: function (region) {
        this._infoViewAdapter.updateCropping(region);
    },
    confirm: function (title, message, condition, onSuccess) {
        var opt = {
            autoOpen: false,
            modal: true,
            width: 400,
            height: 200,
            title: title,
            buttons: {
                "Confirm": function () {
                    onSuccess();
                    $(this).dialog("close");
                },
                "Cancel": function () {
                    $(this).dialog("close");
                }
            }
        };

        if (condition()) {
            var dialog = $('<div><span class="ui-icon ui-icon-alert" style="float:left; margin:12px 12px 20px 0;"></span>' + message + '</div>');
            dialog.dialog(opt).dialog("open");
            $(".ui-dialog").css("z-index","9995");
        } else {
            onSuccess();
        }
    },
    save: function () {
        this._infoViewAdapter._validate.bind(this, "divHeight");
        this._infoViewAdapter._validate.bind(this, "divWidth");

        if (this._infoViewAdapter._isValid) {
            var collectedData = this._infoViewAdapter.collect();
            if (this._loopId && this._entryId) {
                this._loader.saveLoopItem(this._id,
                    this._meta,
                    this._infoViewAdapter.isSharedMode(),
                    this._loopId,
                    this._entryId,
                    this._language,
                    collectedData,
                    Imcms.BackgroundWorker.createTask(this._onSaveReloadTask(true))
                );

            } else {
                this._loader.save(this._id,
                    this._meta,
                    this._infoViewAdapter.isSharedMode(),
                    this._language,
                    collectedData,
                    Imcms.BackgroundWorker.createTask(this._onSaveReloadTask(true))
                );
            }
            this.close();
        } else {
            $('<div>Not valid values</div>').dialog({
                title: "Error",
                resizable: false,
                modal: true,
                buttons: {
                    "Ok": function () {
                        $(this).dialog("close");
                    }
                }
            }).parent().addClass("ui-state-error");
        }
    },
    close: function () {
        this._source = this._primarySource;
        this._isShowed = false;
        $(this._builder[0]).fadeOut("fast");

        // fire event imcmsEditorClose for listeners in any another part of app
        Imcms.Events.fire("imcmsEditorClose");
    },
    open: function () {
        this._getSource(this._source);
        $(this._builder[0]).find("img").css({maxHeight: $(window).height() - 95});
        $(this._builder[0]).fadeIn("fast").find(".imcms-content").css({height: $(window).height() - 95});
        this._isShowed = true;

        /* if (this._isLoaded) {
         setTimeout(function () {

         this._imageCropper.initialize();
         this._imageCropper.changeCropping(this._source.cropRegion.cropX1, this._source.cropRegion.cropY1, this._source.cropRegion.cropX2, this._source.cropRegion.cropY2);
         this._imageCropper.changeDestinationRect(this._source.displayImageSize.width, this._source.displayImageSize.height);
         }.bind(this), 250);
         }*/
    }
};

Imcms.Image.ImageViewAdapter = function (options) {
    this.init(options);
};
Imcms.Image.ImageViewAdapter.prototype = {
    _loader: {},
    _options: {},
    _parent: {},
    _imageView: {},
    _imageSource: {
        urlPathRelativeToContextPath: ""
    },
    init: function (options) {
        this._options = options;
        this._parent = options.element;
        this.buildView();
    },
    buildView: function () {
        this._imageView = $("<img>").appendTo(this._parent).load(this._options.onImageLoaded);
    },
    update: function (src) {
        this._imageSource = src;
        this._imageView.attr("src", src.urlPathRelativeToContextPath === "" ? "" : Imcms.Linker._contextPath + src.urlPathRelativeToContextPath)
    }
};

Imcms.Image.ImageInfoAdapter = function (options) {
    this._infoRef = options.infoRef;
    this._options = Imcms.Utils.merge(options, this._options);
    this.init();
};
Imcms.Image.ImageInfoAdapter.prototype = {
    _infoRef: {},
    _imageSource: {
        realImageSize: {},
        displayImageSize: {},
        cropRegion: {}
    },
    _isValid: true,
    _options: {
        infoRef: null,
        currentElement: null,
        onChooseFile: function () {
        },
        onCropChanged: function () {
        },
        onDisplaySizeChanged: function () {
        }
    },
    init: function () {
        this.buildView();
    },
    buildView: function () {
        //Setting default values if image src is empty(but data still can exist in DB)
        if (!this._imageSource.urlPathRelativeToContextPath) {
            this._imageSource = {
                realImageSize: {
                    width: "",
                    height: ""
                },
                cropRegion: {
                    cropX1: -1,
                    cropY1: -1,
                    cropX2: -1,
                    cropY2: -1
                },
                width: "",
                height: "",
                alternateText: "",
                name: "",
                linkUrl: ""
            };
            this._divWidth = 0;
            this._divHeight = 0;
        }

        if (this._imageSource.imageInfo) {
            this._imageSource.realImageSize.width = this._imageSource.imageInfo.width;
            this._imageSource.realImageSize.height = this._imageSource.imageInfo.height;
        }

        $(this._infoRef.getHTMLElement()).empty();
        var imgName = this._imageSource.generatedUrlPathRelativeToContextPath;
        if (imgName) {
            var pageImgArea = $(this._options.currentElement).find("img");

            // If located element is "cap" get style values from prev tag(added at admin mode)
            if (pageImgArea.attr("cap")) {
                pageImgArea.prev();
            }

            var minWidth = parseInt(pageImgArea.css("min-width"), 10);
            var minHeight = parseInt(pageImgArea.css("min-height"), 10);
            var maxWidth = parseInt(pageImgArea.css("max-width"), 10);
            var maxHeight = parseInt(pageImgArea.css("max-height"), 10);

            var objectWidth = parseInt(pageImgArea.css("width"), 10);
            var objectHeight = parseInt(pageImgArea.css("height"), 10);

            var realWidth = -1;
            var realHeight = -1;

            if (!isNaN(objectHeight) && objectHeight >= minHeight) {
                if (!isNaN(maxHeight) && objectHeight < maxHeight) {
                    realHeight = maxHeight
                } else {
                    realHeight = maxHeight;
                }
            } else {
                realHeight = minHeight;
            }

            if (!isNaN(objectWidth) && objectWidth >= minWidth) {
                if (!isNaN(maxWidth) && objectWidth < maxWidth) {
                    realWidth = maxWidth
                } else {
                    realWidth = maxWidth;
                }
            } else {
                realWidth = minWidth;
            }

            if (isNaN(realWidth)) {
                this._divWidth = NaN;
            } else {
                this._divWidth = realWidth;
            }

            if (isNaN(realHeight)) {
                this._divHeight = NaN;
            } else {
                this._divHeight = realHeight;
            }

            this._generatedWidth = pageImgArea.width();
            this._generatedHeight = pageImgArea.height();
        }
        this._infoRef
            .div()
            .setClass("field size-field")
            .text()
            .disabled()
            .name("width")
            .value(this._imageSource.realImageSize.width || "")
            .label("Original size")
            .placeholder("width")
            .attr("imageInfo", "")
            .end()
            .text()
            .disabled()
            .name("height")
            .placeholder("height")
            .value(this._imageSource.realImageSize.height || "")
            .attr("imageInfo", "")
            .end()
            .end()
            .div()
            .setClass("field choose-image-field")
            .button()
            .html("Choose…")
            .setClass("imcms-neutral choose-image")
            .on("click", this._options.onChooseFile)
            .label(this._imageSource.urlPathRelativeToContextPath || "")
            .end()
            .end()
            .div()
            .setClass("field free-transformation-field")
            .checkbox()
            .name("freeTransform")
            .on("change", this._onFreeTransformStateChanged.bind(this))
            .end()
            .end()

            .div()
            .setClass("field size-field")
            .number()
            .name("divWidth")
            .placeholder("width")
            .value("")
            .label("Display size")
            .attr("imageInfo", "")
            .attr("disabled", true)
            .attr("min", 0)
            .attr("max", this._divWidth)
            .on("change", this._deformationCheck.bind(this))
            .on("change", this._validate.bind(this, "divWidth"))
            .end()
            .number()
            .name("divHeight")
            .placeholder("height")
            .value("")
            .attr("imageInfo", "")
            .attr("disabled", true)
            .attr("min", 0)
            .attr("max", this._divHeight)
            .on("change", this._deformationCheck.bind(this))
            .on("change", this._validate.bind(this, "divHeight"))
            .end()
            .end()

            .div()
            .setClass("field size-field")
            .number()
            .on("change", this._onDisplaySizeChanged.bind(this))
            .name("displayWidth")
            .placeholder("width")
            .value(this._imageSource.realImageSize.width || "")
            .label("Crop size")
            .attr("max", this._imageSource.realImageSize.width || "")
            .attr("min", 0)
            .attr("imageInfo", "")
            .end()
            .number()
            .on("change", this._onDisplaySizeChanged.bind(this))
            .name("displayHeight")
            .placeholder("height")
            .value(this._imageSource.realImageSize.height || "")
            .attr("max", this._imageSource.realImageSize.height || "")
            .attr("min", 0)
            .attr("imageInfo", "")
            .end()
            .end()

            .div()
            .button()
            .html("Reset display size")
            .setClass("imcms-neutral reset")
            .on("click", this._onDisplaySizeChanged.bind(this))
            .end()
            .end()

            .div()
            .setClass("field cropping-field")
            .text()
            .name("leftCrop")
            .placeholder("left")
            .value(this._imageSource.cropRegion.cropX1)
            .on("change", this._onCropChanged.bind(this))
            .end()
            .text()
            .name("topCrop")
            .placeholder("top")
            .value(this._imageSource.cropRegion.cropY1)
            .on("change", this._onCropChanged.bind(this))
            .end()
            .text()
            .name("rightCrop")
            .placeholder("right")
            .value(this._imageSource.cropRegion.cropX2)
            .on("change", this._onCropChanged.bind(this))
            .end()
            .text()
            .name("bottomCrop")
            .placeholder("bottom")
            .value(this._imageSource.cropRegion.cropY2)
            .on("change", this._onCropChanged.bind(this))
            .label("Cropping")
            .end()
            .end()
            .div()
            .setClass("field")
            .text()
            .name("alternateText")
            .label("Alt text (alt)")
            .value(this._imageSource.alternateText)
            .placeholder("altertext")
            .end()
            .end()
            .div()
            .setClass("field")
            .text()
            .name("imageName")
            .label("Image name")
            .value(this._imageSource.name)
            .placeholder("name")
            .end()
            .end()
            .div()
            .setClass("field")
            .text()
            .name("linkUrl")
            .label("Image link URL")
            .value(this._imageSource.linkUrl)
            .placeholder("url")
            .end()
            .end()
            .div()
            .setClass("field shared-mode-field")
            .checkbox()
            .name("sharedMode")
            .end()
            .end()

            .div()
            .setClass("admin-panel-language field")
            .reference("image-editor-language")
            .end();
    },
    update: function (source) {
        this._imageSource = source;
        this.buildView();
    },
    collect: function () {
        var $infoRef = $(this._infoRef.getHTMLElement());
        this._imageSource.realImageSize.width = $infoRef.find("input[name=width]").val();
        this._imageSource.realImageSize.height = $infoRef.find("input[name=height]").val();
        //Sending display width and height to zoom out to max possible display size to avoid big images
        this._imageSource.width = $infoRef.find("input[name=divWidth]").val();
        this._imageSource.height = $infoRef.find("input[name=divHeight]").val();
        this._imageSource.cropRegion.cropX1 = $infoRef.find("input[name=leftCrop]").val();
        this._imageSource.cropRegion.cropY1 = $infoRef.find("input[name=topCrop]").val();
        this._imageSource.cropRegion.cropX2 = $infoRef.find("input[name=rightCrop]").val();
        this._imageSource.cropRegion.cropY2 = $infoRef.find("input[name=bottomCrop]").val();
        this._imageSource.alternateText = $infoRef.find("input[name=alternateText]").val();
        this._imageSource.name = $infoRef.find("input[name=imageName]").val();
        this._imageSource.linkUrl = $infoRef.find("input[name=linkUrl]").val();
        return this._imageSource;
    },
    isSharedMode: function () {
        var $infoRef = $(this._infoRef.getHTMLElement());

        return $infoRef.find("input[name=sharedMode]").is(":checked");
    },
    updateCropping: function (croppingOptions) {
        var $infoRef = $(this._infoRef.getHTMLElement());

        var cropWidth = croppingOptions.cropX2 - croppingOptions.cropX1;
        var cropHeight = croppingOptions.cropY2 - croppingOptions.cropY1;

        $infoRef.find("input[name=leftCrop]").val(croppingOptions.cropX1);
        $infoRef.find("input[name=topCrop]").val(croppingOptions.cropY1);
        $infoRef.find("input[name=rightCrop]").val(croppingOptions.cropX2);
        $infoRef.find("input[name=bottomCrop]").val(croppingOptions.cropY2);
        // if ($infoRef.find("input[name=freeTransform]").prop("checked")) {
        $infoRef.find("input[name=displayHeight]").val(cropHeight);
        $infoRef.find("input[name=displayWidth]").val(cropWidth);

        //Finding out zoom factor
        var zoomFactor = 1;
        if (!isNaN(this._divWidth)) {
            zoomFactor = (cropWidth) / this._divWidth;
        } else {
            if (!isNaN(this._divHeight)) {
                zoomFactor = (cropHeight) / this._divHeight;
            }
        }

        // This only show display area and don't take effect on page(same for width an height)
        var divHeight = 0, divWidth = 0;

        if (this._generatedHeight != null) {
            $infoRef.find("input[name=divHeight]").val(this._generatedHeight);
            this._generatedHeight = null;
        } else {
            if (isNaN(this._divHeight)) {
                if (isNaN(this._divWidth)) {
                    //If there CSS rules wasn't found it will just show cropped values
                    divHeight = cropHeight;
                } else {
                    if (zoomFactor >= 1) {
                        // If selected area is bigger then allowed by css it will be zoomed out to fit size with saving proportions
                        divHeight = Math.round((cropHeight) / zoomFactor);
                    } else {
                        // If selected area is smaller than available div then size(factor < 1) will be reduced to fit image without zooming
                        divHeight = cropHeight;
                    }
                }
            } else {
                //If one of css limitations exists
                if ((cropHeight) > this._divHeight) {
                    divHeight = this._divHeight;
                } else {
                    divHeight = cropHeight;
                }
            }
            $infoRef.find("input[name=divHeight]").val(divHeight);
        }

        if (this._generatedWidth != null) {
            $infoRef.find("input[name=divWidth]").val(this._generatedWidth);
            this._generatedWidth = null;
        } else {
            if (isNaN(this._divWidth)) {
                if (isNaN(this._divHeight)) {
                    //If there CSS rules wasn't found it will just show cropped values
                    divWidth = cropWidth;
                } else {
                    if (zoomFactor >= 1) {
                        // If selected area is bigger then allowed by css it will be zoomed out to fit size with saving proportions
                        divWidth = Math.round((cropWidth) / zoomFactor);
                    } else {
                        // If selected area is smaller than available div then size(factor < 1) will be reduced to fit image without zooming
                        divWidth = cropWidth;
                    }
                }
            } else {
                //If one of css limitations exists
                if ((cropWidth) > this._divWidth) {
                    divWidth = this._divWidth;

                } else {
                    divWidth = cropWidth;
                }
            }
            $infoRef.find("input[name=divWidth]").val(divWidth);
        }
        this._deformationCheck();
    },
    _onDisplaySizeChanged: function () {
        var $element = $(this._infoRef.getHTMLElement());
        this._options.onDisplaySizeChanged({
            height: $element.find("input[name=displayHeight]").val(),
            width: $element.find("input[name=displayWidth]").val()
        })
    },
    _onFreeTransformStateChanged: function () {
        var $element = $(this._infoRef.getHTMLElement()),
            state = $element.find("input[name=freeTransform]").prop("checked");
        this._options.onDisplaySizeChanged({
            height: state ? null : $element.find("input[name=displayHeight]").val(),
            width: state ? null : $element.find("input[name=displayWidth]").val()
        });
        $element.find("input[name=displayHeight]").prop("disabled", state);
        $element.find("input[name=displayWidth]").prop("disabled", state);
        $element.find("input[name=divHeight]").prop("disabled", !state);
        $element.find("input[name=divWidth]").prop("disabled", !state);
    },
    _onCropChanged: function () {

    },
    _validate: function (inputName) {
        var $infoRef = $(this._infoRef.getHTMLElement());
        var element = $infoRef.find("input[name=" + inputName + "]");
        if ($(element).next().hasClass('validation-error')) {
            $(element).next().remove();
        }
        if (element[0].checkValidity()) {
            this._isValid = true;
        } else {
            element.after($("<div class='validation-error'>" + element[0].validationMessage + "</div>"));
            this._isValid = false;
        }
    },

    _deformationCheck: function () {
        var $infoRef = $(this._infoRef.getHTMLElement());
        var divHeightEl = $infoRef.find("input[name=divHeight]");
        if (divHeightEl.next().hasClass('warning-message')) {
            divHeightEl.next().remove();
        }
        if ((Math.round((($infoRef.find("input[name=displayWidth]").val() / $infoRef.find("input[name=displayHeight]").val())) * 10) / 10) !== (Math.round((($infoRef.find("input[name=divWidth]").val() / divHeightEl.val())) * 10) / 10)) {
            divHeightEl.after($("<div class='warning-message'>This may cause visual distortion</div>"));
        }
    }
};

Imcms.Image.ImageCropper = function (options) {
    this._target = options.container;
    this._isFreeTransformed = options.freeTransformed;
    this._img = $(this._target).find("img")[0];
    this._onCropChanged = options.onCropChanged || this._onCropChanged;
};
Imcms.Image.ImageCropper.prototype = {
    _img: {},
    _target: {},
    _imageShader: {},
    _onCropChanged: function () {

    },
    initialize: function () {
        $(this._target).addClass("image-cropper");

        this.setCroppingMode(this._img);
        this.show();
    },
    validateCropping: function (left, top, right, bottom) {
        return right - left > 0 && bottom - top > 0;
    },
    changeCropping: function (left, top, right, bottom) {
        if (!this.validateCropping(left, top, right, bottom)) {
            return;
        }
        var imageCroppingFrame = $(this._imageCroppingFrame);
        var image = imageCroppingFrame.find("img");
        var grip = $(this._grip);
        var factor = image[0].naturalWidth / image.width();
        var width, height;
        width = right - left;
        height = bottom - top;
        left /= factor;
        top /= factor;
        right /= factor;
        bottom /= factor;
        width /= factor;
        height /= factor;
        imageCroppingFrame.css({left: left - 1, top: top - 1, width: width, height: height});
        image.css({left: left * -1, top: top * -1});
        grip.css(
            {
                left: right - 4,
                top: bottom - 4
            }
        );
    },
    changeDestinationRect: function (width, height) {
        var imageCroppingFrame = $(this._imageCroppingFrame);
        this.destinationWidth = width;
        this.destinationHeight = height;

        this.imageCroppingFrameWidth = imageCroppingFrame.width();
        this.imageCroppingFrameHeight = imageCroppingFrame.height();
        this.x = 0;
        this.y = 0;
        this.isResizing = true;
        this.processMovingOrResizing({
            pageX: 0,
            pageY: 0
        });
        this.endMovingOrResizing();
    },
    show: function (width, height, callback) {
        this.destinationWidth = width;
        this.destinationHeight = height;
        this._callback = callback;
        //mt.forms.beginModal();
        // mt.forms.beginForm("imageUploaderForm", "/mt/general/imageuploaderform", "image-uploader-form");
        this._imageCroppingFrame.mouseenter(this.setMovingMode.bind(this));
        this._imageCroppingFrame.on("mouseleave", this.resetMovingMode.bind(this));
        this._grip.on("mouseenter", this.setResizingMode.bind(this));
        this._grip.on("mouseleave", this.resetResizingMode.bind(this));
        $(document).on("mousedown", this.beginMovingOrResizing.bind(this));
        $(document).on("mousemove", this.processMovingOrResizing.bind(this));
        $(document).on("mouseup", this.endMovingOrResizing.bind(this));
        return false;
    },

    collect: function () {
        var image = $(this._img);
        var imageCroppingFrame = this._imageCroppingFrame;
        var factor = image[0].naturalWidth / image.width();
        var x = parseInt((imageCroppingFrame.position().left + 1) * factor);
        var y = parseInt((imageCroppingFrame.position().top + 1) * factor);
        var width = parseInt(imageCroppingFrame.width() * factor);
        var height = parseInt(imageCroppingFrame.height() * factor);
        return {
            cropX1: x,
            cropY1: y,
            cropX2: x + width,
            cropY2: y + height
        }
    },

    cancel: function () {
        return this.close();
    },

    close: function () {
        if (!this._imageCroppingFrame) return;
        this._imageCroppingFrame.off("mouseenter", this.setMovingMode.bind(this));
        this._imageCroppingFrame.off("mouseleave", this.resetMovingMode.bind(this));
        if (!this._grip) return;
        this._grip.off("mouseenter", this.setResizingMode.bind(this));
        this._grip.off("mouseleave", this.resetResizingMode.bind(this));
        $(document).off("mousedown", this.beginMovingOrResizing.bind(this));
        $(document).off("mousemove", this.processMovingOrResizing.bind(this));
        $(document).off("mouseup", this.endMovingOrResizing.bind(this));
        return false;
    },
    update: function (filename) {
        console.log("updateFileName", filename);
        this._imageShader.remove();
        this._imageCroppingFrame.remove();
        this._imageFragment.remove();
        this._grip.remove();
        this._img.attr("src", filename)
            .load(function () {
                setTimeout(this.setCroppingMode.bind(this), 250)
            }.bind(this))
    },
    setCroppingMode: function (img) {
        $("#imageUploadingIndicator").hide();

        var $this = this,
            imageCropper = $(this._target).show(),
            image = $(img);
        this._imageShader = $this.createImageShader(image).appendTo(imageCropper);
        this._imageCroppingFrame = $this.createImageCroppingFrame(image).appendTo(imageCropper);
        this._imageFragment = $this.createImageFragment(image).appendTo(this._imageCroppingFrame);
        this._grip = $this.createGrip(this._imageCroppingFrame).appendTo(imageCropper);

        $this.isCropping = true;

        return false;
    },

    createImageShader: function (image) {
        return $("<div>")
            .addClass("image-shader")
            .css(
                {
                    width: image.width(),
                    height: image.height()
                }
            );
    },

    createImageCroppingFrame: function (image) {
        var imageCroppingFrame = $("<div>").addClass("image-cropping-frame").attr("id", "imageCroppingFrame");
        var width = this.destinationWidth == null ? 100 : this.destinationWidth;
        var height = this.destinationHeight == null ? 100 : this.destinationHeight;
        var factor;

        if (width > image.width()) {
            factor = image.width() / width;

            width = image.width();
            height *= factor;
        }

        if (height > image.height()) {
            factor = image.height() / height;

            width *= factor;
            height = image.height();
        }

        imageCroppingFrame.css(
            {
                width: width,
                height: height
            }
        );

        return imageCroppingFrame;
    },

    createImageFragment: function (image) {
        return $("<img>")
            .addClass("image-fragment")
            .attr("src", image.attr("src"))
            .css({width: image.width(), height: image.height()});
    },

    createGrip: function (imageCroppingFrame) {
        return $("<div>")
            .addClass("grip")
            .attr("id", "grip")
            .css({left: imageCroppingFrame.width() - 5, top: imageCroppingFrame.height() - 5});
    },

    setMovingMode: function () {
        this.move = true;
    },

    resetMovingMode: function () {
        this.move = null;
    },

    setResizingMode: function () {
        this.resize = true;
    },

    resetResizingMode: function () {
        this.resize = null;
    },

    beginMovingOrResizing: function (e) {
        if (!this.isCropping) {
            return true;
        }

        if (!this.move && !this.resize) {
            return true;
        }

        this.x = e.pageX;
        this.y = e.pageY;

        if (this.move) {
            this.isMoving = true;
            this.imageCroppingFrameX = $(this._imageCroppingFrame).position().left;
            this.imageCroppingFrameY = $(this._imageCroppingFrame).position().top;
        }

        else if (this.resize) {
            this.isResizing = true;
            this.imageCroppingFrameWidth = $(this._imageCroppingFrame).width();
            this.imageCroppingFrameHeight = $(this._imageCroppingFrame).height();
        }

        return false;
    },

    processMovingOrResizing: function (e) {
        if (this.isMoving) {
            this.processMoving(e);
            this._onCropChanged(this.collect());
        }

        else if (this.isResizing) {
            if (!this._isFreeTransformed) {
                this.processResizing(e);
            } else {
                this._isFreeTransformed = false;
            }
            this._onCropChanged(this.collect());
        }
    },

    processMoving: function (e) {
        var imageCroppingFrame = $(this._imageCroppingFrame);
        var image = imageCroppingFrame.find("img");
        var grip = $(this._grip);
        var xOffset = e.pageX - this.x;
        var yOffset = e.pageY - this.y;
        var x = this.imageCroppingFrameX + xOffset;
        var y = this.imageCroppingFrameY + yOffset;

        if (x < -1) {
            x = -1;
        }

        if (x > image.width() - imageCroppingFrame.width() - 1) {
            x = image.width() - imageCroppingFrame.width() - 1;
        }

        if (y < -1) {
            y = -1;
        }

        if (y > image.height() - imageCroppingFrame.height() - 1) {
            y = image.height() - imageCroppingFrame.height() - 1;
        }

        imageCroppingFrame.css({left: x, top: y});
        image.css({left: x * -1 - 1, top: y * -1 - 1});
        grip.css(
            {
                left: imageCroppingFrame.position().left + imageCroppingFrame.width() - 4,
                top: imageCroppingFrame.position().top + imageCroppingFrame.height() - 4
            }
        );
    },
    processResizing: function (e) {
        var imageCroppingFrame = $(this._imageCroppingFrame);
        var image = $(this._img);
        var grip = $(this._grip);
        var xOffset = e.pageX - this.x;
        var yOffset = e.pageY - this.y;
        var width, height, factor;
        var state = $(this._img).parent().parent().find("input[name=freeTransform]").prop("checked");
        if (e.target || state) {

            if (!this.destinationWidth && !this.destinationHeight) {
                width = this.imageCroppingFrameWidth + xOffset;

                if (imageCroppingFrame.position().left + width > image.width() - 1) {
                    width = image.width() - imageCroppingFrame.position().left - 1;
                }

                height = this.imageCroppingFrameHeight + yOffset;

                if (imageCroppingFrame.position().top + height > image.height() - 1) {
                    height = image.height() - imageCroppingFrame.position().top - 1;
                }
            }

            else {
                var offset = (xOffset + yOffset) / 2;
                width = this.imageCroppingFrameWidth + offset;

                if (imageCroppingFrame.position().left + width > image.width() - 1) {
                    width = image.width() - imageCroppingFrame.position().left - 1;
                }

                factor = this.destinationWidth / width;
                height = this.destinationHeight / factor;

                if (imageCroppingFrame.position().top + height > image.height() - 1) {
                    factor = height / (image.height() - imageCroppingFrame.position().top - 1);

                    width = width / factor;
                    height = image.height() - imageCroppingFrame.position().top - 1;
                }
            }
        } else {
            var factor = image[0].naturalWidth / image.width();
            width = this.destinationWidth / factor;
            height = this.destinationHeight / factor;

            if (imageCroppingFrame.position().left + width >= image.width() ||
                imageCroppingFrame.position().top + width >= image.height()) {

                var image2 = imageCroppingFrame.find("img");
                var x = (imageCroppingFrame.position().left + width);
                var y = (imageCroppingFrame.position().top + height);

                if (x < -1) {
                    x = -1;
                }

                if (x > image2.width() - 1) {
                    x = image2.width() - width - 1;
                } else {
                    x -= width;
                }

                if (y < -1) {
                    y = -1;
                }

                if (y > image2.height() - 1) {
                    y = image2.height() - height - 1;
                } else {
                    y -= height;
                }

                imageCroppingFrame.css({left: x, top: y});
                image2.css({left: x * -1 - 1, top: y * -1 - 1});
            }
        }

        imageCroppingFrame.css({width: width, height: height});
        grip.css(
            {
                left: imageCroppingFrame.position().left + imageCroppingFrame.width() - 4,
                top: imageCroppingFrame.position().top + imageCroppingFrame.height() - 4
            }
        );
    },
    endMovingOrResizing: function () {
        if (!this.isCropping) {
            return true;
        }

        this.isMoving = null;
        this.isResizing = null;
        this.x = null;
        this.y = null;
        return false;
    }
};

Imcms.Image.ImageInTextEditor = function (textEditor) {
    this._window = new Imcms.Image.ImageInTextEditor.Window(textEditor);
};
Imcms.Image.ImageInTextEditor.prototype = {
    _textEditor: {},
    _window: {},
    onFreeImageIndexReceived: function (imageNo) {
        this._window.initViewForEmptyImage(imageNo);
        this.openWindow();
    },
    onBrowserOpen: function () {
        $.ajax({
            url: Imcms.Linker.getContextPath() + "/api/content/image/emptyNo/" + Imcms.document.meta + "/LOWER",
            success: this.onFreeImageIndexReceived.bind(this)
        });
    },
    onExistingImageEdit: function (imageObj) {
        this._window.initViewForExistingImage(imageObj);
        this.openWindow();
    },
    openWindow: function () {
        setTimeout(this._window.openWindow.bind(this._window), 300);
    }
};

Imcms.Image.ImageInTextEditor.Window = function (textEditor) {
    this._textEditor = textEditor;
    this.init();
};
Imcms.Image.ImageInTextEditor.Window.prototype = {
    _realElement: {},
    _element: {},
    _id: {},
    _meta: {},
    _builder: {},
    _primarySource: {},
    _source: {},
    _imageViewAdapter: {},
    _frame: {},
    _loader: {},
    _infoViewAdapter: {},
    _imageCropper: {},
    _isShowed: false,
    _isLoaded: false,
    _language: '',
    mixinFromImageEditor: function (functionName) {
        this[functionName] = Imcms.Image.Editor.prototype[functionName].bind(this);
    },
    init: function () {
        this._loader = Imcms.Editors.Image;

        var editorKeys = Object.keys(Imcms.Image.Editor.prototype)
            .filter(function (key) {
                return (typeof Imcms.Image.Editor.prototype[key] === 'function')
            });
        Object.keys(Imcms.Image.ImageInTextEditor.Window.prototype).forEach(function (key) {
            editorKeys.remove(key);
        });
        // rebinding methods to not duplicate code
        editorKeys.forEach(this.mixinFromImageEditor.bind(this));
    },
    openWindow: function () {
        this.open();
        this._textEditor.focusManager.blur();
        this._textEditor.element.$.blur();
    },
    initViewForEmptyImage: function (imageNo) {
        this.generateEmptyImageTag(imageNo);
        this.initView();
    },
    initViewForExistingImage: function (image) {
        this._realElement = image.selectedElement;
        this.generateImageTag(image.no, image.src);
        this.initView();
    },
    initView: function () {
        this.buildView();
        this.getCurrentImageWithCallback(this.initSource.bind(this));
    },
    getCurrentImageWithCallback: function (callback) {
        this._loader.getById(this._id, this._meta, this._language, callback);
    },
    generateEmptyImageTag: function (imageNo) {
        this.generateImageTag(imageNo, Imcms.Linker.getContextPath() + "/imcms/eng/images/admin/ico_image.gif");
    },
    generateImageTag: function (imageNo, src) {
        this._id = imageNo;
        this._meta = Imcms.document.meta;
        this._language = Imcms.language.code;

        this._element = $("<div>")
            .addClass("editor-base editor-image")
            .attr("data-no", this._id)
            .attr("data-meta", this._meta);

        $("<img>").attr("cap", "")
            .attr("src", src)
            .appendTo(this._element);
    },
    save: function () {
        var collectedData = this._infoViewAdapter.collect();

        if (collectedData.name) {
            this._loader.save(this._id,
                this._meta,
                this._infoViewAdapter.isSharedMode(),
                this._language,
                collectedData,
                this._onSaveChangesCallback.bind(this)
            );
        }

        this.close();
    },
    close: function () {
        Imcms.Image.Editor.prototype.close.call(this);
        $(this._textEditor.element.$).focus();
    },
    _onSaveChangesCallback: function () {
        this._textEditor.focusManager.blur();
        this._textEditor.element.$.blur();
        this.getCurrentImageWithCallback(this._onGetImageAfterSavingCallback.bind(this));
    },
    _onGetImageAfterSavingCallback: function (image) {
        var imageSource = Imcms.Linker.getContextPath() + image.generatedUrlPathRelativeToContextPath,
            element = '<img class="internalImageInTextEditor" data-no="' + this._id + '" data-meta="' + this._meta + '"'
                + ' src="' + imageSource + '"/>';
        this._textEditor.insertHtml(element, 'unfiltered_html');
    },
    _onRemoveImage: function () {
        $(this._realElement).remove();
        this._loader.remove(this._id, this._meta, this._language, this.close.bind(this));
    }
};
