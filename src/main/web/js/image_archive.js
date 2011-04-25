
$.extend(String.prototype, {
    trim: function() {
        return $.trim(this);
    }, 
    escapeHTML: function() {
        var div = document.createElement("div");
        div.appendChild(document.createTextNode(this));
        
        return div.innerHTML;
    }
});

$.extend($, {
    join: function(array, separator) {
        var i, len, 
            val = "";
        
        for (i = 0, len = array.length; i < len; i++) {
            val += array[i];
            if (i < (len - 1)) {
                val += separator;
            }
        }
        
        return val;
    }
});

var common = (function() {
    var objectToParams = function(params) {
        var args = [];
        if (typeof params != "undefined") {
            for (var param in params) if (params.hasOwnProperty(param)) {
                args.push(param + "=" + encodeURIComponent(params[param]));
            }
        }
        
        return $.join(args, "&");
    };
    
    var obj = {
        getRelativeUrl: function(baseUrl, params) {
            return this.getUrl(this.contextPath + baseUrl, params);
        }, 
        getUrl: function(baseUrl, params) {
            var url = baseUrl;
            if (this.sessionId.length) {
                url += ";jsessionid=" + this.sessionId;
            }
            
            var parameters = objectToParams(params);
            if (parameters.length) {
                url += "?" + parameters;
            }
            
            return url;
        }
    };
    
    $(function() {
        obj.sessionId = "";
        
        var index, 
            jsessionid = $("#jsessionid").val();
        if ((index = jsessionid.indexOf(";jsessionid")) != -1) {
            obj.sessionId = jsessionid.substring(index + ";jsessionid=".length);
        }
        
        obj.contextPath = $("#contextPath").val();
    });
    
    return obj;
})();

var setupCalendar = function(prefix) {
    Calendar.setup({
        inputField: prefix + "Dt", 
        button: prefix + "DtBtn", 
        ifFormat: "%Y-%m-%d"
    });
};
var setupChangeData = function() {
    var categoryIds = [];
    var keywords = [];
    var imageKeywords = [];
    
    $("#imageCategories option").each(function() {
        categoryIds.push($(this).val());
    });
    
    $("#availableKeywords option").each(function() {
        keywords.push($(this).val());
    });
    $("#assignedKeywords option").each(function() {
        imageKeywords.push($(this).val());
    });
    
    $("#addCategory").click(function() {
        var selected = $("#availableCategories :selected");
        if (selected.length) {
            selected.appendTo("#imageCategories");
            
            selected.each(function() {
                categoryIds.push($(this).val());
            });
        }
        
        return false;
    });
    $("#deleteCategory").click(function() {
        var selected = $("#imageCategories :selected");
        if (selected.length) {
            selected.appendTo("#availableCategories");
            
            selected.each(function() {
                var index = $.inArray($(this).val(), categoryIds);
                if (index != -1) {
                    categoryIds.splice(index, 1);
                }
            });
        }
        
        return false;
    });
    
    $("#addKeyword").click(function() {
        var selected = $("#availableKeywords :selected");
        if (selected.length) {
            selected.appendTo("#assignedKeywords");
            
            selected.each(function() {
                var keyword = $(this).val();
                imageKeywords.push(keyword);
                
                var index = $.inArray(keyword, keywords);
                if (index != -1) {
                    keywords.splice(index, 1);
                }
            });
        }
        
        return false;
    });
    $("#deleteKeyword").click(function() {
        var selected = $("#assignedKeywords :selected");
        if (selected.length) {
            selected.appendTo("#availableKeywords");
            
            selected.each(function() {
                var keyword = $(this).val();
                var index = $.inArray(keyword, imageKeywords);
                if (index != -1) {
                    imageKeywords.splice(index, 1);
                }
                
                keywords.push(keyword);
            });
        }
        
        return false;
    });
    
    var createKeywordStarted = false;
    $("#createKeyword").click(function() {
    	if (createKeywordStarted) {
    		return false;
    	}
    	createKeywordStarted = true;
    	
        var keyword = $("#keyword").val().trim().toLowerCase();
        if (keyword.length > 50) {
            keyword = keyword.substring(0, 50);
        }
        
        if (keyword.length && $.inArray(keyword, keywords) == -1 
        		&& $.inArray(keyword, imageKeywords) == -1) {
        	$.ajax({
        		url: common.getRelativeUrl("/web/archive/service/keyword/add"),  
        		data: { keyword: keyword }, 
        		dataType: "text", 
        		success: function() {
    				keywords.push(keyword);
                    
                    keyword = keyword.escapeHTML();
                    $("#availableKeywords").prepend('<option value="' + keyword +'">' + keyword + '</option>');
        		}, 
        		complete: function() {
        			$("#keyword").val("");
        			createKeywordStarted = false;
        		}
        	});
        } else {
        	$("#keyword").val("");
        	createKeywordStarted = false;
        }
        
        return false;
    });
    
    $("#categories").parent("form").submit(function() {
        $("#categories").val($.join(categoryIds, ","));
        
        var i, len, 
            keywordNames = [], 
            imageKeywordNames = [];
        
        for (i = 0, len = keywords.length; i < len; i++) {
            keywordNames.push(encodeURIComponent(keywords[i]));
        }
        for (i = 0, len = imageKeywords.length; i < len; i++) {
            imageKeywordNames.push(encodeURIComponent(imageKeywords[i]));
        }
        
        $("#keywords").val($.join(keywordNames, "/"));
        $("#imageKeywords").val($.join(imageKeywordNames, "/"));
    });
    
    if ($("#licenseDt").length) {
        setupCalendar("license");
        setupCalendar("licenseEnd");
        setupCalendar("publish");
        setupCalendar("archive");
        setupCalendar("publishEnd");
        
        $("a[id$=DtBtn]").click(function() {
            $(this).blur();
        });
    }
    
    $("#rotateRight").click(function() {
        var form = $("#changeData");
        
        form.append("<input type='hidden' name='rotateRight' value='r'/>");
        form.submit();
    });
    $("#rotateLeft").click(function() {
        var form = $("#changeData");
        
        form.append("<input type='hidden' name='rotateLeft' value='l'/>");
        form.submit();
    });
};

var initAddImage = function() {
    $(function() {
        setupChangeData();
    });
};

var initImageCard = function() {
    $(function() {
        setupChangeData();
    });
};

var initSearchImage = function() {
    $(function() {
        setupCalendar("license");
        setupCalendar("licenseEnd");
        setupCalendar("active");
        setupCalendar("activeEnd");
        
        $("a[id$=DtBtn]").click(function() {
            $(this).blur();
        });
    });
};

var initPreferences = function() {
    $(function() {
        var categoryIds = [];
        
        $("#assignedCategories option").each(function() {
            categoryIds.push($(this).val());
        });
        
        $("#roles").change(function() {
            location.href = common.getRelativeUrl("/web/archive/preferences/role", {
                id: $(":selected", $(this)).val()
            });
        });
        
        $("#addCategory").click(function() {
            var selected = $("#freeCategories :selected");
            if (selected.length) {
                selected.appendTo("#assignedCategories");
                
                selected.each(function() {
                    categoryIds.push($(this).val());
                });
            }
            
            return false;
        });
        $("#deleteCategory").click(function() {
            var selected = $("#assignedCategories :selected");
            if (selected.length) {
                selected.appendTo("#freeCategories");
                
                selected.each(function() {
                    var index = $.inArray($(this).val(), categoryIds);
                    if (index != -1) {
                        categoryIds.splice(index, 1);
                    }
                });
            }
            
            return false;
        });
        
        $("#saveCategories").click(function() {
            $("#categoryIds").val($.join(categoryIds, ","));
        });
        
        
        $("#library").change(function() {
            location.href = common.getRelativeUrl("/web/archive/preferences/library", {
                id: $(":selected", $(this)).val()
            });
        });
        
        var attachLibraryRoleDelete = function(cont) {
            $("input[id^=deleteLibraryRole_]", cont).click(function() {
                var input = $(this);
                var tr = input.parents("tr");
                var roleId = input.attr("id").split("_")[1];
                var roleName = $("td:first", tr).text();
                
                var option = $('<option value="' + roleId + '">' + roleName.escapeHTML() + '</option>');
                $("#availableLibraryRoles").append(option);
                tr.remove();
                
                return false;
            });
        };
        
        var deleteText = $("#deleteText").val();
        var makeRow = function(roleId, roleName) {
            return $('<tr id="libraryRoleRow_' + roleId + '">\
                          <td style="min-width:60px;">' + roleName.escapeHTML() + '</td>\
                          <td style="min-width:60px;text-align:center;">\
                              <input type="radio" name="permission_' + roleId + '" value="0" checked="checked"/>\
                          </td>\
                          <td style="min-width:60px;text-align:center;">\
                              <input type="radio" name="permission_' + roleId + '" value="1"/>\
                          </td>\
                          <td style="min-width:60px;text-align:center;">\
                              <input type="button" id="deleteLibraryRole_' + roleId + '" value="' + deleteText.escapeHTML() + '" class="btnBlue small"/>\
                          </td>\
                      </tr>');
        };
        
        $("#addLibraryRole").click(function() {
            var selected = $("#availableLibraryRoles :selected");
            var table = $("#libraryRolesTbl");
            
            selected.each(function() {
                var role = $(this);
                var roleId = role.val();
                var roleName = role.text();
                
                var row = makeRow(roleId, roleName);
                table.append(row);
                attachLibraryRoleDelete(row);
                
                role.remove();
            });
            
            return false;
        });
        
        attachLibraryRoleDelete($("#libraryRolesTbl"));
        
        $("#saveLibraryRoles").click(function() {
            var libraryRoles = [];
            
            $("#libraryRolesTbl :checked").each(function() {
                var input = $(this);
                var tr = input.parents("tr");
                var roleName = $("td:first", tr).text();
                var roleId = input.attr("name").split("_")[1];
                var value = input.val();
                
                libraryRoles.push(encodeURIComponent(roleName) + "/" + roleId + "/" + value);
            });
            
            $("#libraryRolesStr").val($.join(libraryRoles, "/"));
        });
    });
};

var initExternalFiles = function() {
    $(function() {
        setupChangeData();
        
        var libraryId = $("#libraryId").val();
        
        var changeLibrary = function(id) {
            location.href = common.getRelativeUrl("/web/archive/external-files/library", {
                id: id
            });
        };
        
        $("#libraries option").dblclick(function() {
            changeLibrary($(this).val());
        });
        $("#changeLibrary").click(function() {
            var selected = $("#libraries :selected");
            if (selected.length) {
                changeLibrary(selected.val());
            }
            
            return false;
        });

        $("#listOfLibraries li").click(function(event) {
            event.stopPropagation();
            changeLibrary($(this).attr("data-library-id"));
        });

        var currentSortBy = $("input:radio:checked").val();
        $("input[name=sortBy]").click(function() {
            var sortBy = $(this).val();
            if (sortBy != currentSortBy) {
                location.href = common.getRelativeUrl("/web/archive/external-files/sort", {
                    sortBy: sortBy
                });
            }
        });
        
        var showFilePreview = function(id, name) {
            var url = common.getRelativeUrl("/web/archive/external-files/preview", {
                id: id, 
                name: name
            });
            var attrs = "width=640,height=480,directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,toolbar=no";
            
            window.open(url, "filepreview", attrs);
        };
        
        $("#show").click(function() {
            var selected = $("#fileNames :checked");
            if (selected.length) {
                showFilePreview(libraryId, selected.val());
            }
            
            return false;
        });
        $("#fileNames option").dblclick(function() {
            showFilePreview(libraryId, $(this).val());
        });
    });
};

var showPreview = function(id, width, height, temp) {
    var WINDOW_BORDERS = 40, 
        ERROR_MARGIN = 20;
    
    var imageWidth = Math.min(screen.availWidth - WINDOW_BORDERS, width);
    var imageHeight = Math.min(screen.availHeight - WINDOW_BORDERS, height);
    
    var windowWidth = Math.min(screen.availWidth, imageWidth + ERROR_MARGIN);
    var windowHeight = Math.min(screen.availHeight, imageHeight + ERROR_MARGIN);
    
    var left = Math.floor((screen.availWidth - windowWidth) * 0.5), 
        top = Math.floor((screen.availHeight - windowHeight) * 0.5);
    
    left = Math.max(left, 0);
    top = Math.max(top, 0);
    
    var params = {
        id: id
    };
    if (temp) {
        params.tmp = true;
    }
    
    var url = common.getRelativeUrl("/web/archive/preview", params);
    var attrs = "left=" + left + ",top=" + top + ",width=" + windowWidth + ",height=" + windowHeight + ",directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,toolbar=no";
    
    window.imageNewWidth = imageWidth;
    
    window.open(url, "preview", attrs);
};

var initImagePreview = function() {
    $(function() {
        if (window.opener && window.opener.imageNewWidth) {
            $("#image").css("width", window.opener.imageNewWidth + "px");
        }
    });
};
