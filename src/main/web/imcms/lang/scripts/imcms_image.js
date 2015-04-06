/**
 * Created by Shadowgun on 26.03.2015.
 */
Imcms.Image = {};
Imcms.Image.API = function () {
};
Imcms.Image.API.prototype = {
    path: "/api/content/image",
    read: function (request, response) {
        Imcms.Logger.log("Image.API::read :",
            $.ajax.bind($, {
                url: this.path + "/" + request.object,
                type: "GET",
                success: response
            }), request);
    },
    update: function (request, response) {
        Imcms.Logger.log("Image.API::update :",
            $.ajax.bind($, {
                url: this.path + "/" + request.object,
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
    getByPath: function (path, callback) {
        this._api.read({object: path}, callback);
    },
    getPreview: function (folder, name, width, height, extension, callback) {
        this._api.read({object: folder + name + "-" + width + "-" + height + "." + extension},
            callback);
    },
    save: function (id, meta, data, callback) {
        this._api.update({
            imageSource: JSON.stringify(data.imageSource),
            imageInfo: JSON.stringify(data.imageInfo),
            cropRegion: JSON.stringify(data.cropRegion),
            object: ((meta || Imcms.document.meta) + "-" + id)
        }, Imcms.Logger.log.bind(this, "Image::save : ", callback));
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
    _loader: {},
    _builder: {},
    _imageViewAdapter: {},
    _infoViewAdapter: {},
    _imageCropper: {},
    _isShowed: false,
    _isLoaded: false,
    init: function () {
        var data = $(this._element).data().prettify();
        this._id = data.no;
        this._meta = data.meta;
        this.buildView().buildExtra();
        this._loader.getById(this._id, this._meta, this._getSource.bind(this));
    },
    buildView: function () {
        this._builder = new JSFormBuilder("<div>")
            .form()
            .div()
            .class("header")
            .div()
            .html("Document Editor")
            .class("title")
            .end()
            .button()
            .html("Save and close")
            .class("positive save-and-close")
            .on("click", this.save.bind(this))
            .end()
            .button()
            .html("Close without saving")
            .class("neutral close-without-saving")
            .end()
            .end()
            .div()
            .class("content")
            .div()
            .class("image")
            .reference("imageView")
            .end()
            .div()
            .class("info")
            .reference("infoView")
            .end()
            .end()
            .end();
        $(this._builder[0]).appendTo("body").addClass("editor-form");
        return this;
    },
    buildCropper: function () {
        if (this._imageCropper instanceof Imcms.Image.ImageCropper) {
            this._imageCropper.close();
        }
        this._imageCropper = new Imcms.Image.ImageCropper(this._builder.ref("imageView").getHTMLElement());
        if (this._isShowed) {
            setTimeout(this._imageCropper.initialize.bind(this._imageCropper), 250);
        }
    },
    buildExtra: function () {
        this._frame = new Imcms.FrameBuilder()
            .title("Image Editor")
            .click($.proxy(this.open, this))
            .build()
            .appendTo(this._element);
    },
    _getSource: function (data) {
        this._isLoaded = false;
        this.buildImageView(data)
            .buildInfoView(data);
    },
    buildImageView: function (data) {
        $(this._builder.ref("imageView").getHTMLElement()).empty();
        this._imageViewAdapter = new Imcms.Image.ImageViewAdapter({
            element: this._builder.ref("imageView").getHTMLElement(),
            onChooseFile: $.proxy(this._onChooseFile, this),
            onImageLoaded: $.proxy(this.onImageLoaded, this)
        });
        this._imageViewAdapter.update(data);
        return this;
    },
    onImageLoaded: function () {
        this.buildCropper();
        this._isLoaded = true;
    },
    buildInfoView: function (data) {
        $(this._builder.ref("infoView").getHTMLElement()).empty();
        this._infoViewAdapter = new Imcms.Image.ImageInfoAdapter(this._builder.ref("infoView"));
        this._infoViewAdapter.update(data);
        return this;
    },
    _onChooseFile: function () {
        Imcms.Editors.Content.showDialog({
            onApply: $.proxy(this._onFileChosen, this)
        });
        $(this._builder[0]).fadeOut("fast");
    },
    _onFileChosen: function (data) {
        this._getSource(data);
        this.open();
    },
    save: function () {
        var collectedData = this._infoViewAdapter.collect();
        this._loader.save(this._id, this._meta, {
            imageSource: collectedData,
            imageInfo: collectedData.imageInfo,
            cropRegion: this._imageCropper.collect()
        });
        this._isShowed = false;
    },
    open: function () {
        $(this._builder[0]).fadeIn("fast").find(".content").css({height: $(window).height() - 100});
        this._isShowed = true;
        if (this._isLoaded) {
            setTimeout(this._imageCropper.initialize.bind(this._imageCropper), 250);
        }
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
    _imageSource: {},
    init: function (options) {
        this._options = options;
        this._parent = options.element;
        this.buildView();
    },
    buildView: function () {
        this._imageView = $("<img>").appendTo(this._parent).load(this._options.onImageLoaded);
        $("<button>")
            .attr("type", "button")
            .click(this._options.onChooseFile).appendTo(this._parent);
    },
    update: function (src) {
        this._imageSource = src;
        this._imageView.attr("src", src.urlPathRelativeToContextPath)
    }
};

Imcms.Image.ImageInfoAdapter = function (infoRef) {
    this._infoRef = infoRef;
    this.init();
};
Imcms.Image.ImageInfoAdapter.prototype = {
    _infoRef: {},
    _imageSource: {
        imageInfo: {},
        displaySize: {}
    },
    init: function () {
        this.buildView();
    },
    buildView: function () {
        $(this._infoRef.getHTMLElement()).empty();
        this._infoRef
            .div()
            .class("field")
            .text()
            .disabled()
            .name("width")
            .value(this._imageSource.imageInfo.width || "")
            .label("width")
            .attr("imageInfo", "")
            .end()
            .end()
            .div()
            .class("field")
            .text()
            .disabled()
            .name("height")
            .value(this._imageSource.imageInfo.height || "")
            .label("height")
            .attr("imageInfo", "")
            .end()
            .end()
            .div()
            .html(this._imageSource.urlPathRelativeToContextPath || "")
            .end()
            .div()
            .class("field")
            .text()
            .disabled()
            .name("width")
            .value(this._imageSource.displaySize.width || "")
            .label("width")
            .attr("imageInfo", "")
            .end()
            .end()
            .div()
            .class("field")
            .text()
            .disabled()
            .name("height")
            .value(this._imageSource.displaySize.height || "")
            .label("height")
            .attr("imageInfo", "")
            .end()
            .end()
    },
    update: function (source) {
        this._imageSource = source;
        this.buildView();
    },
    collect: function () {
        var $infoRef = $(this._infoRef.getHTMLElement());
        this._imageSource.imageInfo.width = $infoRef.find("input[name=width]").val();
        this._imageSource.imageInfo.height = $infoRef.find("input[name=height]").val();
        return this._imageSource;
    }
};
Imcms.Image.ImageCropper = function (container) {
    this._target = container;
    this._img = $(container).find("img")[0];
};
Imcms.Image.ImageCropper.prototype = {
    _img: {},
    _target: {},
    _imageShader: {},

    initialize: function () {
        $(this._target).addClass("image-cropper");

        this.setCroppingMode(this._img);
        this.show();
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
        this._imageCroppingFrame.off("mouseenter", this.setMovingMode.bind(this));
        this._imageCroppingFrame.off("mouseleave", this.resetMovingMode.bind(this));
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

        if (width > image.width()) {
            var factor = image.width() / width;

            width = image.width();
            height *= factor;
        }

        if (height > image.height()) {
            var factor = image.height() / height;

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
        }

        else if (this.isResizing) {
            this.processResizing(e);
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

        if (this.destinationWidth == null && this.destinationHeight == null) {
            var width = this.imageCroppingFrameWidth + xOffset;

            if (imageCroppingFrame.position().left + width > image.width() - 1) {
                width = image.width() - imageCroppingFrame.position().left - 1;
            }

            var height = this.imageCroppingFrameHeight + yOffset;

            if (imageCroppingFrame.position().top + height > image.height() - 1) {
                height = image.height() - imageCroppingFrame.position().top - 1;
            }
        }

        else {
            var offset = (xOffset + yOffset) / 2;
            var width = this.imageCroppingFrameWidth + offset;

            if (imageCroppingFrame.position().left + width > image.width() - 1) {
                width = image.width() - imageCroppingFrame.position().left - 1;
            }

            var factor = this.imageCroppingFrameWidth / width;
            var height = this.imageCroppingFrameHeight / factor;

            if (imageCroppingFrame.position().top + height > image.height() - 1) {
                var factor = height / (image.height() - imageCroppingFrame.position().top - 1);

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

    endMovingOrResizing: function (e) {
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
