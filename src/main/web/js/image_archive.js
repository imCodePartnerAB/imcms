
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

    $('#uploadButton').click(function() {
        $('#uploadify').uploadifyUpload();
        return false;
    });

    var redirectOnAllComplete;
    $('#uploadify').uploadify({
        'uploader': common.getRelativeUrl('/js/jquery.uploadify-v2.1.4/uploadify.swf'),
        onAllComplete: function(event, data) {
            if(redirectOnAllComplete.length > 0) {
                window.location.replace(redirectOnAllComplete);
            }
        },
        onComplete: function(a, b, c, resp, info){
            var data = $.parseJSON(resp);
            if(data) {
                if(data.redirect) {
                    window.location.replace(data.redirect);
                }

                if(data.redirectOnAllComplete) {
                    redirectOnAllComplete = data.redirectOnAllComplete;
                }

                if(data.errors) {
                    var errorMessage = "";
                    for(var error in data.errors) {
                        errorMessage += " " + data.errors[error] + "\n";
                    }
                    $("#uploadify" + b).find('.percentage').text(" - " + errorMessage);
                    $("#uploadify" + b).find('.uploadifyProgress').hide();
                    $("#uploadify" + b).addClass('uploadifyError');
                } else {
                    $("#uploadify" + b).fadeOut(250,function() {jQuery(this).remove()});
                }
            }

            return false;
        },
        /* using getUrl() cos the context path is added by c:url in the view */
        'script': common.getUrl($('#uploadify').parents('form:first').attr('action')),
        'multi': true,
        'auto' : false,
        'fileDataName': 'file',
        'width': 64,
        'height': 20,
        'queueID': 'uploadifyQueue',
        'buttonImg': common.getRelativeUrl('/js/jquery.uploadify-v2.1.4/browse.png'),
        'cancelImg': common.getRelativeUrl('/js/jquery.uploadify-v2.1.4/cancel.png'),
        'onSelectOnce' : function(event, data) {
            $('#uploadify').uploadifySettings("scriptData", { 'fileCount' : data.fileCount })
        }
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


        $(".detailedTooltipThumb").each(function(){
            var imageId = $(this).attr("data-image-id");

              $(this).qtip({
                prerender: true,
                content: {
                    text: 'Loading...',
                    ajax: false
                },
                position: {
                    my: 'center center',
                    at: 'center center',
                    effect: false,
                    viewport: $("#searchResults"),
                    target: false
                },
                show: {
                    effect: false,
                    solo: true
                },
                hide: {
                    fixed: true
                },
                style: {
                    classes: 'ui-tooltip-light ui-tooltip-shadow'
                },
                events: {
                    render: function(event, api) {
                        $.ajax({
                            url: common.getRelativeUrl('/web/archive/detailed_thumb'),
                            type: 'GET',
                            data: { id : imageId },
                            dataType: 'html',
                            success: function(data) {
                                api.set('content.text', data);
                            }
                        });
                    }
                }
            });
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


        function toggleBulkSelectionCheckboxes(tableClassOrId) {
            if($(tableClassOrId + " .use").length == $(tableClassOrId + " .use:checked").length) {
                $(tableClassOrId + " .allCanUse").attr("checked", "checked");
            } else {
                $(tableClassOrId + " .allCanUse").removeAttr("checked");
            }

            if($(tableClassOrId + " .edit").length == $(tableClassOrId + " .edit:checked").length) {
                $(tableClassOrId + " .allCanEdit").attr("checked", "checked");
            } else {
                $(tableClassOrId + " .allCanEdit").removeAttr("checked");
            }
        }

        // sets click hanlders for bulk selection and  
        function setupBulkSelectionCheckboxes(tableClassOrId) {
            $(tableClassOrId + " .use").click(function() {
                toggleBulkSelectionCheckboxes(tableClassOrId);
            });

            $(tableClassOrId + " .edit").click(function() {
                toggleBulkSelectionCheckboxes(tableClassOrId);
            });

            toggleBulkSelectionCheckboxes(tableClassOrId);

            $(tableClassOrId + " .allCanUse").click(function(){
                if($(this).is(":checked")) {
                    $(tableClassOrId + " .use").attr("checked", "checked");
                } else {
                    $(tableClassOrId + " .use").removeAttr("checked");
                }
            });
    
            $(tableClassOrId + " .allCanEdit").click(function(){                
                if($(this).is(":checked")) {
                    $(tableClassOrId + " .edit").attr("checked", "checked");
                } else {
                    $(tableClassOrId + " .edit").removeAttr("checked");
                }
            });
        }

        setupBulkSelectionCheckboxes(".roleTable");
        setupBulkSelectionCheckboxes(".libraryCategoriesTable");

        $("#saveCategoriesBtn").click(function() {
            var categoryRightStr = "";
            var dataRows = $(".roleTable tr.dataRow");
            dataRows.each(function(){
                categoryRightStr += $(this).find("input[type='hidden']").val();
                categoryRightStr += ",";
               if($(this).find(".use:checked").length) {
                   categoryRightStr += "1,";
               } else {
                   categoryRightStr += "0,";
               }

               if($(this).find(".edit:checked").length) {
                   categoryRightStr += "1-";
               } else {
                   categoryRightStr += "0-";
               }
            });
            $("#categoryIds").val(categoryRightStr);
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
        
        $("#saveLibraryRolesBtn").click(function() {
            var libraryRoles = "";
            var dataRows = $(".libraryCategoriesTable tr.dataRow");
            dataRows.each(function(){
                libraryRoles += $(this).find("input[type='hidden']").val();
                libraryRoles += ",";
               if($(this).find(".use:checked").length) {
                   libraryRoles += "1,";
               } else {
                   libraryRoles += "0,";
               }

               if($(this).find(".edit:checked").length) {
                   libraryRoles += "1-";
               } else {
                   libraryRoles += "0-";
               }
            });
            $("#libraryRolesStr").val(libraryRoles);
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

        $("#fileNames").bind("sortEnd", function(){
            $("#fileNames th").each(function(index, value){
                if($(value).hasClass("headerSortUp")) {
                    var sortBy = index + "-1";
                    var url = common.getRelativeUrl("/web/archive/external-files/sort", {
                    sortBy: sortBy
                    });

                    $.get(url);
                    return false;
               } else if($(value).hasClass("headerSortDown")) {
                    var sortBy = index + "-0";
                    var url = common.getRelativeUrl("/web/archive/external-files/sort", {
                    sortBy: sortBy
                    });

                    $.get(url);
                    return false;
               }
            });
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
            var selected = $("#fileNames :checked:first");
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
