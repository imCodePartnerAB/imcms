/**
 * Created by Shadowgun on 26.03.2015.
 */
Imcms.Image = {};
Imcms.Image.API = function () {
};
Imcms.Image.API.prototype = {
    read: function (request, response) {
        Imcms.Logger.log("Image.API::read :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("content.image", request.object),
                type: "GET",
                success: response
            }), request);
    },
    update: function (request, response) {
        Imcms.Logger.log("Image.API::update :",
            $.ajax.bind($, {
                url: Imcms.Linker.get("content.image", request.object),
                type: "POST",
                data: request,
                success: response
            }), request);
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
    getById: function (id, meta, callback) {
        this._api.read({object: ((meta || Imcms.document.meta) + "-" + id)},
            Imcms.Logger.log.bind(this, "Image::getById : ", callback));
    },
    getByLoopItemRef: function (id, loopId, entryId, meta, callback) {
        this._api.read({object: ((meta || Imcms.document.meta) + "-" + id + "?loopId=" + loopId + "&entryId=" + entryId)},
            Imcms.Logger.log.bind(this, "Image::getByLoopItemRef : ", callback));
    },
    getByPath: function (path, callback) {
        this._api.read({object: path}, callback);
    },
    getPreview: function (folder, name, width, height, extension, callback) {
        this._api.read({object: folder + name + "-" + width + "-" + height + "." + extension},
            callback);
    },
    save: function (id, meta, isShared, data, callback) {
        this._api.update({
            sharedMode: isShared,
            imageDomainObject: JSON.stringify(data),
            object: ((meta || Imcms.document.meta) + "-" + id)
        }, Imcms.Logger.log.bind(this, "Image::save : ", callback));
    },
    saveLoopItem: function (id, meta, isShared, loopId, entryId, data, callback) {
        this._api.update({
            sharedMode: isShared,
            imageDomainObject: JSON.stringify(data),
            object: ((meta || Imcms.document.meta) + "-" + id + "?loopId=" + loopId + "&entryId=" + entryId)
        }, Imcms.Logger.log.bind(this, "Image::saveLoopItem : ", callback));
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
    init: function () {
        var data = $(this._element).data();
        this._id = data.no;
        this._meta = data.meta;
        this._loopId = data.loop;
        this._entryId = data.entry;
        this.buildView().buildExtra();
        if (data.loop && data.entry) {
            this._loader.getByLoopItemRef(this._id, data.loop, data.entry, this._meta, this.initSource.bind(this));
        }
        else {
            this._loader.getById(this._id, this._meta, this.initSource.bind(this));
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
            .class("imcms-header")
            .div()
            .html("Image Editor")
            .class("imcms-title")
            .end()
            .button()
            .reference("closeButton")
            .class("imcms-close-button")
            .on("click", $.proxy(this.close, this))
            .end()
            .end()
            .div()
            .class("imcms-content")
            .div()
            .class("image")
            .reference("imageView")
            .end()
            .div()
            .class("info")
            .reference("infoView")
            .end()
            .end()
            .div()
            .class("imcms-footer")
            .button()
            .html("Save and close")
            .class("imcms-positive imcms-save-and-close")
            .on("click", $.proxy(this.save, this))
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
        this._imageCropper = new Imcms.Image.ImageCropper({
            container: this._builder.ref("imageView").getHTMLElement(),
            onCropChanged: this._onCropRegionChanged.bind(this)
        });
        if (this._isShowed) {
            setTimeout(function () {
                this._imageCropper.initialize();
                this._imageCropper.changeCropping(data.cropRegion.cropX1, data.cropRegion.cropY1, data.cropRegion.cropX2, data.cropRegion.cropY2);
                this._imageCropper.changeDestinationRect(data.displayImageSize.width, data.displayImageSize.height);
            }.bind(this), 250);
        }
    },
    buildExtra: function () {
        this._frame = new Imcms.FrameBuilder()
            .title("Image Editor")
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
            onChooseFile: $.proxy(this._onChooseFile, this)
        });
        this._infoViewAdapter.update(data);
        return this;
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
    _onFileChosen: function (data) {
        if (data) {
            var clonedData = jQuery.extend(true, {}, data);
            clonedData.source = jQuery.extend(true, {}, data);
            clonedData.cropRegion = {
                cropX1: 0,
                cropX2: clonedData.imageInfo.width / 4,
                cropY1: 0,
                cropY2: clonedData.imageInfo.height / 4,
                width: clonedData.imageInfo.width / 4,
                height: clonedData.imageInfo.height / 4,
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
    save: function () {
        var collectedData = this._infoViewAdapter.collect();
        if (this._loopId && this._entryId) {
            this._loader.saveLoopItem(this._id,
                this._meta,
                this._infoViewAdapter.isSharedMode(),
                this._loopId, this._entryId,
                collectedData,
                Imcms.BackgroundWorker.createTask({
                    showProcessWindow: true,
                    refreshPage: true
                })
            );
        }
        else {
            this._loader.save(this._id,
                this._meta,
                this._infoViewAdapter.isSharedMode(),
                collectedData,
                Imcms.BackgroundWorker.createTask({
                    showProcessWindow: true,
                    refreshPage: true
                })
            );
        }
        this.close();
    },
    close: function () {
        this._source = this._primarySource;
        this._isShowed = false;
        $(this._builder[0]).fadeOut("fast");

        // fire event imcmsEditorClose for listeners in any another part of app
        window.dispatchEvent(new CustomEvent("imcmsEditorClose", {
            detail: {
                editor: "image"
            }
        }))
    },
    open: function () {
        this._getSource(this._source);
        $(this._builder[0]).find("img").css({maxHeight: $(window).height() - 95});
        $(this._builder[0]).fadeIn("fast").find(".imcms-content").css({height: $(window).height() - 95});
        this._isShowed = true;

        /*if (this._isLoaded) {
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
        this._imageView.attr("src", Imcms.Linker._contextPath + src.urlPathRelativeToContextPath)
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
    _options: {
        infoRef: null,
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
        $(this._infoRef.getHTMLElement()).empty();
        this._infoRef
            .div()
            .class("field size-field")
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
            .class("field choose-image-field")
            .button()
            .html("Choose…")
            .class("imcms-neutral choose-image")
            .on("click", this._options.onChooseFile)
            .label(this._imageSource.urlPathRelativeToContextPath || "")
            .end()
            .end()
            .div()
            .class("field free-transformation-field")
            .checkbox()
            .name("freeTransform")
            .on("change", this._onFreeTransformStateChanged.bind(this))
            .end()
            .end()
            .div()
            .class("field size-field")
            .text()
            .on("change", this._onDisplaySizeChanged.bind(this))
            .name("displayWidth")
            .placeholder("width")
            .value(this._imageSource.width || "")
            .label("Display size")
            .attr("imageInfo", "")
            .end()
            .text()
            .on("change", this._onDisplaySizeChanged.bind(this))
            .name("displayHeight")
            .placeholder("height")
            .value(this._imageSource.height || "")
            .attr("imageInfo", "")
            .end()
            .end()
            .div()
            .class("field cropping-field")
            .text()
            .name("leftCrop")
            .placeholder("left")
            .value(this._imageSource.cropRegion.cropX1)
            .on("change", this._onCropChanged.bind(this))
            .label("Cropping")
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
            .end()
            .end()
            .div()
            .class("field")
            .text()
            .name("alternateText")
            .label("Alt text (alt)")
            .value(this._imageSource.alternateText)
            .placeholder("altertext")
            .end()
            .end()
            .div()
            .class("field")
            .text()
            .name("imageName")
            .label("Image name")
            .value(this._imageSource.name)
            .placeholder("name")
            .end()
            .end()
            .div()
            .class("field")
            .text()
            .name("linkUrl")
            .label("Image link URL")
            .value(this._imageSource.linkUrl)
            .placeholder("url")
            .end()
            .end()
            .div()
            .class("field shared-mode-field")
            .checkbox()
            .name("sharedMode")
            .end()
            .end()
    },
    update: function (source) {
        this._imageSource = source;
        this.buildView();
    },
    collect: function () {
        var $infoRef = $(this._infoRef.getHTMLElement());
        this._imageSource.realImageSize.width = $infoRef.find("input[name=width]").val();
        this._imageSource.realImageSize.height = $infoRef.find("input[name=height]").val();
        this._imageSource.width = $infoRef.find("input[name=displayWidth]").val();
        this._imageSource.height = $infoRef.find("input[name=displayHeight]").val();
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
        $infoRef.find("input[name=leftCrop]").val(croppingOptions.cropX1);
        $infoRef.find("input[name=topCrop]").val(croppingOptions.cropY1);
        $infoRef.find("input[name=rightCrop]").val(croppingOptions.cropX2);
        $infoRef.find("input[name=bottomCrop]").val(croppingOptions.cropY2);
        if ($infoRef.find("input[name=freeTransform]").prop("checked")) {
            $infoRef.find("input[name=displayHeight]").val(croppingOptions.cropY2 - croppingOptions.cropY1);
            $infoRef.find("input[name=displayWidth]").val(croppingOptions.cropX2 - croppingOptions.cropX1);
        }
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
    },
    _onCropChanged: function () {

    }
};

Imcms.Image.ImageCropper = function (options) {
    this._target = options.container;
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
            this.processResizing(e);
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
