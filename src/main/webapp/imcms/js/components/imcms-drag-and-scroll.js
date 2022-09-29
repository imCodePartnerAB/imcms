define(
	'imcms-drag-and-scroll',
	[
		'jquery'
	],
	function ($) {
		function enableDragAndScroll($area) {
			let mouseDown = false, lastPosition, position, difference;
			setCursor($area, 'grab');

			$area.on("mousedown mouseup mousemove", function (e) {
				const $this = $(this);

				if (e.type === "mousedown") {
					setCursor($area, 'grabbing');
					mouseDown = true;
					lastPosition = [e.clientX, e.clientY];
				}

				if (e.type === "mouseup") {
					mouseDown = false;
					setCursor($area, 'grab');
				}

				if (e.type === "mousemove" && mouseDown === true) {
					position = [e.clientX, e.clientY];
					difference = [(position[0] - lastPosition[0]), (position[1] - lastPosition[1])];

					$this.scrollLeft($this.scrollLeft() - difference[0]);
					$this.scrollTop($this.scrollTop() - difference[1]);

					lastPosition = [e.clientX, e.clientY];
				}
			});
			$area.on("mouseenter mouseleave", function () {
				mouseDown = false;
			});
		}

		function setCursor($area, cursor) {
			$area.css('cursor', cursor);
		}

		return {
			enableDragAndScroll: enableDragAndScroll
		}
	}
)
