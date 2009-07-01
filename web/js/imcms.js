
var common = new function() {
	var self = this;
	
	$(function() {
		self.contextPath = $("#contextPath").val() || "";
	});
};

$.extend(String.prototype, {
	escapeHTML: function() {
		var div = document.createElement("div");
		div.appendChild(document.createTextNode(this));
		
		return div.innerHTML;
	}
});

function createInput(attributes) {
	var text = "<input ";
	
	for (var name in attributes) {
		if (attributes.hasOwnProperty(name)) {
			text += name + "=\"" + attributes[name].escapeHTML() + "\" ";
		}
	}
	
	text += "/>";
	
	return $(text);
}

function initSearch() {
	var optionComparator = function(a, b) {
		var aText = $(a).text(), 
			bText = $(b).text();
		
		if (aText == bText) {
			return 0;
		}
		
		return (aText < bText ? -1 : 1);
	};
	
	var initSelectColumns = function(id) {
		$("#add_" + id).click(function() {
			var selected = $("#" + id + " option:selected");
			
			if (!selected.length) {
				return false;
			}
			
			var rightSelect = $("#search_" + id);
			
			selected.each(function() {
				var option = $(this);
				
				option.remove();
				rightSelect.append(option);
				
				var hiddenInput = createInput({
					type: "hidden", 
					id: id + "_" + option.val(), 
					name: id, 
					value: option.val()
				});
				
				rightSelect.parent().append(hiddenInput);
			});
			
			var sorted = $("option", rightSelect).sort(optionComparator);
			rightSelect.append(sorted);
			
			return false;
		});
		
		$("#remove_" + id).click(function() {
			var selected = $("#search_" + id + " option:selected");
			
			if (!selected.length) {
				return false;
			}
			
			var leftSelect = $("#" + id);
			
			selected.each(function() {
				var option = $(this);
				
				option.remove();
				leftSelect.append(option);
				
				$("#" + id + "_" + option.val()).remove();
			});
			
			var sorted = $("option", leftSelect).sort(optionComparator);
			leftSelect.append(sorted);
			
			return false;
		});
	};
	
	var init = function() {
		// two column selection boxes
		initSelectColumns("creators");
		initSelectColumns("publishers");
		initSelectColumns("categories");
		
		// date presets
		$("select[id^=preset_]").change(function() {
			var select = $(this), 
				suffix = select.attr("id").split("_")[1];
			
			var option = select.find("option:selected");
			
			var parts = option.val().split(";");
			
			var fromDate = parts[0] || "", 
				toDate	 = parts[1] || "";
			
			$("#" + suffix + "From").val(fromDate);
			$("#" + suffix + "To").val(toDate);
		});
		
		// calendar buttons
		var datePattern = $("#datePattern").val();
		
		$("img[id$=Btn]").each(function() {
			var img = $(this);
			
			var inputId = img.attr("id").split("Btn")[0];
			
			Calendar.setup({
				button: inputId + "Btn", 
				inputField: inputId, 
				ifFormat: datePattern
			});
			
			img.mouseover(function() {
				$(this).css("backgroundColor", "#009");
			});
			img.mouseout(function() {
				$(this).css("backgroundColor", "");
			});
		});
		
		// toggles for folds
		$("a[id^=close_]").click(function() {
			var foldId = $(this).attr("id").split("_")[1];
			
			var fold = $("#fold_" + foldId), 
				foldCollapsed = $("#" + foldId + "Collapsed");
			
			if (fold.is(":visible")) {
				fold.hide();
				foldCollapsed.val(true);
			} else {
				fold.show();
				foldCollapsed.val(false);
			}
			
			return false;
		});
		
		// toggles all folds
		$("#toggleAll").click(function() {
			var foldsSelector = "[id^=fold_]", 
				folds = $(foldsSelector), 
				collapsed = $("input[id$=Collapsed]:hidden"), 
			
				allVisible = ($(foldsSelector + ":visible").length == folds.length);
			
			if (allVisible) {
				folds.hide();
				collapsed.val(true);
			} else {
				folds.show();
				collapsed.val(false);
			}
						
			return false;
		});
		
		var locked = false;
		var doRequest = function(params) {
			if (locked) {
				return;
			}
			
			locked = true;
			
			$.ajax({
				url: common.contextPath + "/newadmin/search", 
				data: $("#searchForm").serialize() + "&" + $.param(params.data), 
				type: "POST", 
				dataType: "html", 
				success: params.success, 
				complete: function() {
					locked = false;
				}
			});
		};
		
		var search = function() {
			doRequest({
				data: { searchAction: "yes" }, 
				success: function(data)	{
					$("#searchResults").nextAll().remove().end().replaceWith(data);
				}
			});
			
			return false;
		};
		
		$("#search").click(search);
		$("#searchForm").submit(search);
		
		$("#clear").click(function() {
			doRequest({
				data: { clearAction: "yes" }, 
				success: function(data) {
					$("#searchForm").replaceWith(data);
					init();
				}
			});
			
			return false;
		});
	};
	
	$(init);
}
