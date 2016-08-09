Imcms.Admin = {};
Imcms.Admin.Panel = {
	init: function () {
		var draggable = false,
			entered = false,
			panelWidth = 0,
			cornerPointDistanceX = 0,
			cornerPointDistanceY = 0,
			$draggable = $(".admin-panel-draggable"),
			$doc = $(document),
			$adminPanel = $(".admin-panel").css({
				left: $.cookie("admin-panel-location-left", Number) || 0,
				top: $.cookie("admin-panel-location-top", Number) || 0,
                width: 837 //experimental value
			});

        $adminPanel.draggable({
            handle: ".admin-panel-draggable",
            containment: "window"
        });
        if (!$adminPanel.length) {
			return;
		}

		panelWidth = $adminPanel.width();
		$adminPanel.css("minWidth", panelWidth);

		$draggable.on("mouseenter", function () {
			entered = true
		});
		$draggable.on("mouseleave", function () {
			entered = false
		});
		$doc.on("mousedown", function (e) {
			draggable = entered;
			cornerPointDistanceX = e.pageX - $adminPanel.position().left;
			cornerPointDistanceY = e.pageY - $adminPanel.position().top;
			return !draggable;
		});
		$doc.on("mouseup", function () {
			draggable = false;
		});
		$doc.on("mousemove", function (e) {
			if (draggable) {
				var
					windowWidth = $(window).width(),
					x = e.pageX - cornerPointDistanceX,
					y = e.pageY - cornerPointDistanceY;

				x = x < 0 ? 0 : (x + panelWidth + 10 >= windowWidth) ? windowWidth - panelWidth - 10 : x;
				y = y < 0 ? 0 : y;
				$adminPanel.css({
					left: x,
					top: y
				});
				$.cookie("admin-panel-location-left", x, {expires: 9999999, path: '/'});
				$.cookie("admin-panel-location-top", y, {expires: 9999999, path: '/'});
			}
		});
		window.pageInfoCounter = 0;

		window.pageInfo = (function () {
			if (pageInfoCounter == 0) {
				pageInfoCounter++;
				Imcms.Editors.Document.getDocument(Imcms.document.meta, function (data) {
					var viewer = new Imcms.Document.Viewer({
						data: data,
						type: Imcms.document.type,
						loader: Imcms.Editors.Document,
						target: $("body")[0],
						onApply: function () {
							window.pageInfoCounter = 0;
							Imcms.Editors.Document.update(viewer.serialize(), Imcms.BackgroundWorker.createTask({
								showProcessWindow: true,
								refreshPage: true
							}));
						},
						onCancel: function () {
							window.pageInfoCounter = 0;
						}
					});
				});
			}
		});
	},
	docs: function () {
		Imcms.Editors.Document.show();
	}
};