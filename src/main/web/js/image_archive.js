
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

/* used together with css to put html button underneath uploadify's flash for css styling */
var resizeUplodifyButtons = function() {
    var buttonWrapper = $(".UploadifyButtonWrapper");
    var objectWrapper = $(".UploadifyObjectWrapper");
    var object = $("object", buttonWrapper);
    var fakeButton = $("button", buttonWrapper);
    var width = fakeButton.outerWidth();
    var height = fakeButton.outerHeight();
    object.attr("width", width).attr("height", height);
    buttonWrapper.css("width", width + "px").css("height", height + "px");
    objectWrapper.hover(function() {
        $("button", this).addClass("Hover");
    }, function() {
        $("button", this).removeClass("Hover");
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
    
    $("#changeData").submit(function() {
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
        'queueID': 'uploadifyQueue',
        'hideButton': true,
        'wmode':'transparent',
        'cancelImg': common.getRelativeUrl('/js/jquery.uploadify-v2.1.4/cancel.png'),
        'onSelectOnce' : function(event, data) {
            $('#uploadify').uploadifySettings("scriptData", { 'fileCount' : data.fileCount })
        },
        'onSWFReady': resizeUplodifyButtons
    });
};

function toggleBulkSelectionCheckboxes(tableClassOrId, selectOneClass, selectAllClass) {
    if($(tableClassOrId + " " + selectOneClass).length == $(tableClassOrId + " " + selectOneClass + ":checked").length) {
        $(tableClassOrId + " " + selectAllClass).attr("checked", "checked");
    } else {
        $(tableClassOrId + " " + selectAllClass).removeAttr("checked");
    }
}

function setupBulkSelectionCheckboxes(tableClassOrId, selectOneClass, selectAllClass) {
    $(tableClassOrId + " " + selectOneClass).click(function() {
        toggleBulkSelectionCheckboxes(tableClassOrId, selectOneClass, selectAllClass);
    });

    toggleBulkSelectionCheckboxes(tableClassOrId, selectOneClass, selectAllClass);

    $(tableClassOrId + " " + selectAllClass).click(function(){
        if($(this).is(":checked")) {
            $(tableClassOrId + " " + selectOneClass).attr("checked", "checked");
        } else {
            $(tableClassOrId + " " + selectOneClass).removeAttr("checked");
        }
    });
}

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
                    viewport: $("body"),
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

        if($(".editCategoryTable td").length > 0) {
            $(".editCategoryTable").tablesorter({textExtraction: function(node) {
                    if($(node).find("input").length > 0) {
                        return $(node).find("input").val();
                    }
                    return node.innerHTML;
                }, sortList: [[0,0]], headers:{ 1 : {sorter:false}}
            });
        } else {
            $(".editCategoryTable").tablesorter({headers: { 0 : {sorter:false}, 1 : {sorter:false}}});
        }

        if($(".roleTable td").length > 0) {
            $(".roleTable").tablesorter({sortList: [[0,0]], headers:{ 1 : {sorter:false}, 2 : {sorter:false}}});
        } else {
            $(".roleTable").tablesorter({headers: { 0 : {sorter:false}, 1 : {sorter:false}, 2 : {sorter:false}}});
        }

        if($(".libraryCategoriesTable td")) {
            $(".libraryCategoriesTable").tablesorter({sortList: [[0,0]], headers:{ 1 : {sorter:false}}});
        } else {
            $(".libraryCategoriesTable").tablesorter({headers: { 0 : {sorter:false}, 1 : {sorter:false}}});
        }

        setupBulkSelectionCheckboxes(".roleTable", ".use", ".allCanUse");
        setupBulkSelectionCheckboxes(".roleTable", ".edit", ".allCanEdit");
        setupBulkSelectionCheckboxes(".libraryCategoriesTable", ".use", ".allCanUse");

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
        setupBulkSelectionCheckboxes("#fileNames", ".use", ".allCanUse");
        
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
    });
};

function setOverlayDimensions(width, height){
    var o = $("#lightbox", top.document);
    var maxWidth = $(top).width();
    var maxHeight = $(top).height();
    var ratio = 0;

    if(width > maxWidth || height > maxHeight) {
        if(width > maxWidth){
            ratio = maxWidth / width;
            width = maxWidth;
            o.css("width", width);
            o.css("height", Math.round(height * ratio));
            height = Math.round(height * ratio);
        }

        if(height > maxHeight){
            ratio = maxHeight / height;
            o.css("height", maxHeight);
            o.css("width", Math.round(width * ratio));
            width = Math.round(width * ratio);
        }
    } else {
        o.css("width", width);
        o.css("height", height);
    }
}

function lightbox(ajaxContentUrl, width, height){

    if($('#lightbox', top.document).size() == 0){
        var theLightbox = $('<div id="lightbox"/>');
        var theShadow = $('<div id="lightbox-shadow"/>');
        var closeBtn = $('<div id="lightbox-close"/>');
        $(theShadow).click(function(e){
            closeLightbox();
        });

        $(closeBtn).click(function(e){
            closeLightbox();
        });
        
        $('body', top.document).append(theShadow);
        $('body', top.document).append(theLightbox);
        $('body', top.document).append(closeBtn);
    }

    $('#lightbox', top.document).empty();

    if(ajaxContentUrl != null){
        $('#lightbox', top.document).append('<p class="loading">Loading...</p>');

        $.ajax({
            type: 'GET',
            url: ajaxContentUrl,
            success:function(data){
                $('#lightbox', top.document).empty();
                $('#lightbox', top.document).append(data);
            },
            error:function(){
                alert('AJAX Failure!');
            }
        });
    }

    setOverlayDimensions(width, height);
    $('#lightbox', top.document).css('margin-left', (-$('#lightbox', top.document).width() / 2)+'px');
    $('#lightbox', top.document).css('margin-top', (-$('#lightbox', top.document).height() / 2)+'px');
    $('#lightbox-close', top.document).css('margin-left', (($('#lightbox', top.document).width() / 2) -
        $('#lightbox-close', top.document).width()) + 'px');
    $('#lightbox-close', top.document).css('margin-top', (-$('#lightbox', top.document).height() / 2)+'px');

    $('#lightbox', top.document).show();
    $('#lightbox-shadow', top.document).show();
    $('#lightbox-close', top.document).show();
}

function closeLightbox(){

    $('#lightbox', top.document).hide();
    $('#lightbox-shadow', top.document).hide();
    $('#lightbox-close', top.document).hide();

    $('#lightbox', top.document).empty();
}

var showPreview = function(id, width, height, temp) {
    var params = {
        id: id
    };
    if (temp) {
        params.tmp = true;
    }
    
    var url = common.getRelativeUrl("/web/archive/preview", params);
    lightbox(url, width, height);
    return false;
};
