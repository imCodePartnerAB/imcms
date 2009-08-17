
var common = new function() {
	var self = this;
	
	$(function() {
		self.contextPath = $("#contextPath").val() || "";
		self.adminModul = $("#adminModul").val() || "";
		
		self.escapeId = function(id) {
			return id.replace(/\./g, "\\.");
		}
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

var optionComparator = function(a, b) {
	var aText = $(a).text(), 
		bText = $(b).text();
	
	if (aText == bText) {
		return 0;
	}
	
	return (aText < bText ? -1 : 1);
};

function initAdmin() {
	
	var init = function() {	
		switch (common.adminModul) {
		
		case ("SEARCH_FORM"):
			initSearch();
			break;
		
		case ("PERMISSION_GROUPS"):
			initPermissionGroups();
			break;
			
		case ("PROFILE_NEW"):
			initProfileNew();
			break;
			
		case ("CHANGE_SEVERAL"):
			initChangeSeveral();
			break;
			
		default:
			initFold();
			break;
		}
	}
	
	$(init);
}

function initFold() {
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
}

var initSelectColumns = function(id) {
	id = common.escapeId(id);
	$("#add_" + id).click(function() {
		var selected = $("#unselected_" + id + " option:selected");
		
		if (!selected.length) {
			return false;
		}
		
		var rightSelect = $("#selected_" + id);
		
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
		var selected = $("#selected_" + id + " option:selected");
		
		if (!selected.length) {
			return false;
		}
		
		var leftSelect = $("#unselected_" + id);
		
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

var initCalendar = function(calBtnSelector, callback) {
	$(calBtnSelector).each(function() {
		var btn = $(this);
		
		$(callback(btn));
		
		btn.mouseover(function() {
			$(this).css("backgroundColor", "#009");
		});
		btn.mouseout(function() {
			$(this).css("backgroundColor", "");
		});

	});
}

function initSearch() {	
	var init = function() {
		initFold();
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
		initCalendar("img[id$='Btn']", function(img) {
			var inputId = img.attr("id").split("Btn")[0];
			
			Calendar.setup({
				button: inputId + "Btn", 
				inputField: inputId, 
				ifFormat: datePattern
			} );
		} );
		
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


function initPermissionGroups() {
	var init =  function() {
		initFold();
		
		var form = $("#pgc");
		
		$("#selectButton").click( function() {
			var selected = $("#permissionGroupsList option:selected");
			
			$("#name").val(selected.text());
			
			// Save an id of selected group
			$("#selectedGroup").val(selected.val());
			
			// Load selected Permission group
			$.ajax( {
				cache: false,
				url: common.contextPath + "/newadmin/profiles/partialPermGroups/" + selected.val(),
				dataType: "html",
				type: "POST",
				success: function(html) {
					$("#partial_perm_group").html(html);
				}
			});	
		});
		
		$("#renameButton").click( function() {
			doSubmit("rename");
		}); 
		
		$("#saveButton").click( function() {
			doSubmit("savePermissions")
		});
		
		var doSubmit = function(strategyType) {
			$("#strategyType").val(strategyType);
			form.submit();
		}
	};
	
	$(init);
}

var initProfileNew = function() {
	var init = function() {
		initFold();
		
		initSelectColumns("category_types");
		initSelectColumns("categories");
		
		var form = $("#pnc");
		var profile = $("#profile");
		var newName = $("#newName");
		var changedName = $("#changedName");
		
		// Change (radiobutton)
		$("#profileAction1").click( function() {
			changeCreateType( {
				onTypeSelect: function() {
					profile.removeAttr("disabled");
					changedName.removeAttr("disabled");
				}, 
				
				onProfileChange: function() {
					changedName.val($("#profile option:selected").text());
				}
			});
		});
		
		// Create new based on profile (radiobutton)
		$("#profileAction2").click( function() {
			changeCreateType( {
				onTypeSelect: function() {
					profile.removeAttr("disabled");		
					changedName.removeAttr("disabled");					
				}, 
				
				onProfileChange: function() {
					changedName.val($("#profile option:selected").text() + "_NEW");
				}
			});
		} );
		
		// Create new (radiobutton)
		$("#profileAction3").click( function() {
			changeCreateType( {
				onTypeSelect: function() {
					newName.removeAttr("disabled");
				},
				
				onProfileChange: function() {
					;
				}
			});
		} );
		
		var changeCreateType = function(behaviour) {
			disableControlls();
			$(behaviour.onTypeSelect);
			
			$(behaviour.onProfileChange);
			profile.change(behaviour.onProfileChange);
			
			$("#okButton").click( function() {
				if ($("[name='profileAction']:checked").val()) {
					$(".postInit :input").removeAttr("disabled");
				}
			});
		}
		
		var disableControlls = function() {
			profile.attr("disabled", true);
			newName.attr("disabled", true);
			changedName.attr("disabled", true);
			
			$(".postInit :input").attr("disabled", true);
		} 
		disableControlls();
		
		$("saveButton").click( function() {
			form.submit();
		});
	}
	
	$(init);
}

var initChangeSeveral = function() {
	var init = function() {
		initFold();
		initSelectColumns("categories.value");
		
		var initUnlockControls = function( controls ) {
			controls.each( function() {
				if ( $(this).attr("id").indexOf("action") != -1 ) {
					$(this).click( function() {
						controls.each( function() {
							$(this).removeAttr("disabled");
						} );
					} );
				}
			});			
		}
		
		
		var lockControls = function(controls) {
			controls.each( function() {
				if ( $(this).attr("id").indexOf("action") == -1 ) {
					$(this).attr("disabled", true);
				}
			} );
		}
		
		var columnsSelection = function(id) {
			return "#selected_" + id + ", " + 
				   "#unselected_" + id + ", " +
				   "#add_" + id + ", " + 
				   "#remove_" + id;
		}
		
		var calendarSelection = function(id) {
			return "#" +common.escapeId("csc." + id + ".Btn");
		}
		
		var extendSelection = function(id, selection) {
			switch (id) {
				case ("categories"): 
					selection += ", " + columnsSelection(id);
					break;
				case ("publicationDateTime"):
				case ("archiveDateTime"):
				case ("expiredDateTime"):
				case ("publishOnDateTime"):
				case ("lastChangeDateTime"):
					selection +=", " + calendarSelection(id);
					break;
			}

			return selection
		}
		
		$("[type='radio'][value='LOCK']").each( function() {
			var prefix = $(this).attr("id").split('.')[0];
			
			var selection = extendSelection(prefix, "[id^='" + prefix + "'][value!='LOCK']");
			var controls = $( selection );
			
			$(this).click( function() {
				lockControls( controls );
			});	
			
			initUnlockControls( controls );
		}); 
		
		var dateTimePattern = $("#dateTimePattern").val();
		var dateTimeDelimiter = $("#dateTimeDelimiter").val();
		
		initCalendar("input[type='image'][id$='Btn']", function(btn) {
			var idPrefix = btn.attr("id").split(".")[1];
			Calendar.setup({
				button: btn.attr("id"),
				showsTime: true,
				daFormat: dateTimePattern,
				onSelect: function(cal, dateTime) {				
					var dateId = "#" + common.escapeId(idPrefix + ".value.date");
					var timeId = "#" + common.escapeId(idPrefix + ".value.time");
					
					$(dateId).val(dateTime.split(dateTimeDelimiter)[0]);
					$(timeId).val(dateTime.split(dateTimeDelimiter)[1]);
				}
			} );
		} );
	}
	
	$(init);
}