<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="com.imcode.imcms.flow.Page"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> --%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<vel:velocity>
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title><? web/imcms/lang/jsp/crop_img.jsp/title ?></title>
            
            <link rel="stylesheet" type="text/css" href="${contextPath}/imcms/css/imcms_admin.css.jsp"/>
            <script src="${contextPath}/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
            <script src="${contextPath}/imcms/jscropper/prototype.js" type="text/javascript"></script>
            <script src="${contextPath}/imcms/jscropper/scriptaculous.js?load=builder,dragdrop" type="text/javascript"></script>
            <script src="${contextPath}/imcms/jscropper/cropper.js" type="text/javascript"></script>

            <script type="text/javascript">
                
                (function($) {
                    $(function() {
                        var CROPPER_SPACE = 6, 
                            scale = 1, 
                            contWidth = $(window).width() - (30 * 2 + 24 * 2), 
                            contHeight = $(window).height() - $("#image").offset().top - (2 + 21 + 36 + 10 + 1 + 12 + 30 + CROPPER_SPACE), 
                            imageWidth = parseInt($("#image_width").val(), 10), 
                            imageHeight = parseInt($("#image_height").val(), 10), 
                            ratio = imageWidth / imageHeight, 
                            cropper;
                        
                        $("#image-cont").css({
                            width: contWidth + "px", 
                            height: contHeight + "px"
                        });
                        
                        var zoomFactors = [2, 5, 10, 30, 50, 67, 80, 90, 100, 110, 120, 133, 150, 170, 200, 240, 300], 
                            zoomActual, 
                            zoomIndex, 
                            zoom = 100;

                        for (var i = 0; i < zoomFactors.length; ++i) {
                            if (zoomFactors[i] == 100) {
                                zoomActual = i;
                                zoomIndex = i;
                                break;
                            }
                        }

                        function updateImageZoom() {
                            var img = $("#image,.imgCrop_wrap");

                            if (zoomIndex != -1) {
                                zoomIndex = Math.max(Math.min(zoomIndex, zoomFactors.length - 1), 0);
                                zoom = zoomFactors[zoomIndex];
                            }

                            var percent = zoom / 100, 
                                newWidth = Math.round(imageWidth * percent), 
                                newHeight = Math.round(newWidth / ratio);

                            scale = imageWidth / newWidth;

                            img.attr({
                                width: newWidth, 
                                height: newHeight
                            });

                            $("#zoom-value").text(Math.floor(zoom));
                            
                            if (cropper != null) {
                                var cropX1 = parseInt($("#crop_x1").val(), 10), 
                                    cropY1 = parseInt($("#crop_y1").val(), 10), 
                                    cropX2 = parseInt($("#crop_x2").val(), 10), 
                                    cropY2 = parseInt($("#crop_y2").val(), 10);

                                var options = cropper.options;                                
                                options.onloadCoords = null;
                                
                                if (!isNaN(cropX1)) {
                                    var finalX1 = Math.min(cropX1, cropX2), 
                                        finalX2 = Math.max(cropX1, cropX2),
                                        finalY1 = Math.min(cropY1, cropY2), 
                                        finalY2 = Math.max(cropY1, cropY2), 
                                        area = (finalX2 - finalX1) * (finalY2 - finalY1);
                                    
                                    if (area > 0) {
                                        options.onloadCoords = {
                                            x1: Math.round(finalX1 / scale), 
                                            y1: Math.round(finalY1 / scale), 
                                            x2: Math.round(finalX2 / scale), 
                                            y2: Math.round(finalY2 / scale)
                                        };
                                    }
                                }

                                options.displayOnInit = (options.onloadCoords != null);
                                
                                cropper.reset();
                            }
                        }
                        
                        $("#zoom-in,#zoom-out").click(function() {
                            var zoomin = ($(this).attr("id").indexOf("zoom-in") != -1);


                            if (zoomIndex == -1) {
                                var found = false, 
                                    start = (zoomin ? 0 : zoomFactors.length - 1), 
                                    end = (zoomin ? zoomFactors.length : -1);

                                for (var i = start; i != end; (zoomin ? ++i : --i)) {
                                    if ((zoomin && zoomFactors[i] > zoom) ||
                                        (!zoomin && zoomFactors[i] < zoom)) {
                                        zoomIndex = i;
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found) {
                                    return false;
                                }
                            } else {
                                if (zoomin) {
                                    ++zoomIndex;
                                } else {
                                    --zoomIndex;
                                }
                            }

                            updateImageZoom();

                            return false;
                        });

                        $("#zoom-actual").click(function() {
                            zoomIndex = zoomActual;
                            updateImageZoom();

                            return false;
                        });

                        function zoomFit() {
                            zoomIndex = -1;

                            if ((imageWidth > contWidth) || (imageHeight > contHeight)) {
                                var finalWidth;

                                if (imageWidth > contWidth && imageHeight > contHeight) {
                                    var targetRatio = contWidth / contHeight;

                                    if (ratio > targetRatio) {
                                        finalWidth = contWidth;
                                    } else {
                                        finalWidth = Math.floor(contHeight * ratio);
                                    }
                                } else if (imageWidth > contWidth) {
                                    finalWidth = contWidth;
                                } else {
                                    finalWidth = Math.floor(contHeight * ratio);
                                }

                                zoomIndex = -1;
                                zoom = (finalWidth / imageWidth) * 100;

                            } else {
                                zoomIndex = zoomActual;
                            }

                            updateImageZoom();
                        }

                        $("#zoom-fit").click(function() {
                            zoomFit();
                            return false;
                        });

                        if ($("#zoom-fit").length != 0) {
                            zoomFit();
                        }
                        
                        function updateValues(coords, dimensions) {
                            var cropX1 = coords.x1 * scale, 
                                cropY1 = coords.y1 * scale, 
                                cropX2 = coords.x2 * scale, 
                                cropY2 = coords.y2 * scale;
                            
                            <%-- little adjustment, it seems that at some scales the 
                                 coordinates, also width and height can't reach maximum (image width/height) --%>
                            
                            if ((imageWidth - cropX2) < 0.6) {
                                cropX2 = imageWidth;
                            }
                            if ((${imageHeight} - cropY2) < 0.6) {
                                cropY2 = imageHeight;
                            }

                            cropX1 = Math.round(cropX1);
                            cropY1 = Math.round(cropY1);
                            cropX2 = Math.round(cropX2);
                            cropY2 = Math.round(cropY2);

                            var width = (cropX2 - cropX1), 
                                height = (cropY2 - cropY1);
                            
                            $("#crop_x1").val(cropX1);
                            $("#crop_y1").val(cropY1);
                            $("#crop_x2").val(cropX2);
                            $("#crop_y2").val(cropY2);
                            $("#width").val(width);
                            $("#height").val(height);
                        }
                        
                        var options = {
                        	onEndCrop: updateValues, 
                            displayOnInit: true
                        };

                        if ($("#force_width").length != 0) {
                            var forceWidth = parseInt($("#force_width").val(), 10), 
                                forceHeight = parseInt($("#force_height").val(), 10);
                            
                            options.ratioDim = {
                                x: forceWidth, 
                                y: forceHeight
                            };
                        }
                        
                        if ($("#region_x1").length != 0) {
                            var regionX1 = parseInt($("#region_x1").val(), 10), 
                                regionY1 = parseInt($("#region_y1").val(), 10), 
                                regionX2 = parseInt($("#region_x2").val(), 10), 
                                regionY2 = parseInt($("#region_y2").val(), 10);
                            
                            
                            options.onloadCoords = {
                                x1: Math.round(regionX1 / scale), 
                                y1: Math.round(regionY1 / scale), 
                                x2: Math.round(regionX2 / scale), 
                                y2: Math.round(regionY2 / scale)
                            };
                        }
                        
                        $("#image-cont").height((contHeight + CROPPER_SPACE) + "px");

                        $("#reset").click(function() {
                            var options = cropper.options;
                            options.onloadCoords = null;
                            options.displayOnInit = false;
                            
                            cropper.reset();

                            var startCoords = { x1: 0, y1: 0, x2: 0, y2: 0 };
                            cropper.setAreaCoords( startCoords, false, false, 1);
                            
                            $("#crop_x1,#crop_y1,#crop_x2,#crop_y2,#width,#height").val("0");
                            
                            return false;
                        });
                        
                        var img = $("#image")[0];
                        if (img.width > 0 || img.height > 0) {
                            cropper = new Cropper.Img("image", options);
                            
                            if ($(".imgCrop_wrap").length == 0) {
                                cropper.onLoad();
                            }
                            
                        } else {
                            $("#image").on("load", function() {
                                cropper = new Cropper.Img("image", options);
                            });
                        }
                    });
                })(jQ);
                
            </script>

            <style type="text/css">
                HTML {
                    height: 100%;
                }
                BODY {
                    margin: 0 !important;
                    padding: 0 !important;
                }
                #outer_container {
                    margin: 0 !important;
                    padding: 0 !important;
                }
                #inner_container {
                    margin: 30px 10px !important;
                    padding: 0 !important;
                }
                .imgCrop_wrap {
                    margin: 0 auto;
                }
            </style>
        </head>
        <body id="body" bgcolor="#FFFFFF">
            <div id="outer_container">
                <div id="inner_container">
                    #gui_outer_start()
                    #gui_head( "<? global/imcms_administration ?>" )
                    
                    <form method="post" action="${contextPath}/servlet/PageDispatcher">
                        <input type="hidden" name="${constant.IN_REQUEST}" value="${cropPage.sessionAttributeName}"/>
                        <input type="hidden" id="image_width" value="${imageWidth}"/>
                        <input type="hidden" id="image_height" value="${imageHeight}"/>
                        
                        <c:if test="${forceCropRatio and image.width gt 0 and image.height gt 0}">
                            <input type="hidden" id="force_width" value="${image.width}"/>
                            <input type="hidden" id="force_height" value="${image.height}"/>
                        </c:if>
                        
                        <c:if test="${region.valid}">
                            <input type="hidden" id="region_x1" value="${region.cropX1}"/>
                            <input type="hidden" id="region_y1" value="${region.cropY1}"/>
                            <input type="hidden" id="region_x2" value="${region.cropX2}"/>
                            <input type="hidden" id="region_y2" value="${region.cropY2}"/>
                        </c:if>
                        #gui_mid()
                    
                        <table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
                            <tr>
                                <td colspan="2">
                                    <c:url var="imageUrl" value="/servlet/ImagePreview">
                                        <c:param name="path" value="${image.urlPathRelativeToContextPath}"/>

                                        <c:param name="rangle" value="${image.rotateDirection.angle}"/>
                                    </c:url>
                                    
                                    <div id="image-cont" style="overflow:auto;">
                                        <c:set var="alternateText" value="${fn:escapeXml(image.alternateText)}"/>
                                        <img id="image" src="${fn:escapeXml(imageUrl)}" alt="${alternateText}" title="${alternateText}"/>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">#gui_hr( "blue" )</td>
                            </tr>
                            <tr>
                                <td>
                                    <table cellspacing="0" cellpadding="0" border="0">
                                        <tr>
                                            <td>
                                                <label for="crop_x1">
                                                    <b><? web/imcms/lang/jsp/crop_img.jsp/x1 ?></b>
                                                </label>
                                            </td>
                                            <td>&nbsp;</td>
                                            <td>
                                                <label for="crop_y1">
                                                    <b><? web/imcms/lang/jsp/crop_img.jsp/y1 ?></b>
                                                </label>
                                            </td>
                                            <td>&nbsp;</td>
                                            <td>
                                                <label for="crop_x2">
                                                    <b><? web/imcms/lang/jsp/crop_img.jsp/x2 ?></b>
                                                </label>
                                            </td>
                                            <td>&nbsp;</td>
                                            <td>
                                                <label for="crop_y2">
                                                    <b><? web/imcms/lang/jsp/crop_img.jsp/y2 ?></b>
                                                </label>
                                            </td>
                                            <td>&nbsp;</td>
                                            <td>
                                                <label for="width">
                                                    <b><? web/imcms/lang/jsp/crop_img.jsp/width ?></b>
                                                </label>
                                            </td>
                                            <td></td>
                                            <td>
                                                <label for="height">
                                                    <b><? web/imcms/lang/jsp/crop_img.jsp/height ?></b>
                                                </label>
                                            </td>
                                            <td>&nbsp;</td>
                                            <td></td>
                                            <td>&nbsp;</td>
                                            <td></td>
                                            <td>&nbsp;</td>
                                            <td></td>
                                        </tr>
                                        <tr>
                                            <td><input id="crop_x1" name="${constant.PARAM_CROP_X1}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropX1 : ''}"/></td>
                                            <td>&nbsp;</td>
                                            <td><input id="crop_y1" name="${constant.PARAM_CROP_Y1}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropY1 : ''}"/></td>
                                            <td>&nbsp;</td>
                                            <td><input id="crop_x2" name="${constant.PARAM_CROP_X2}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropX2 : ''}"/></td>
                                            <td>&nbsp;</td>
                                            <td><input id="crop_y2" name="${constant.PARAM_CROP_Y2}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropY2 : ''}"/></td>
                                            <td>&nbsp;&nbsp;</td>
                                            <td><input id="width" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.width}"/></td>
                                            <td>&nbsp;X&nbsp;</td>
                                            <td><input id="height" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.height}"/></td>
                                            <td>&nbsp;&nbsp;</td>
                                            <td><input id="reset" type="button" class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/4008 ?>"/></td>

                                            <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td><input type="submit" class="imcmsFormBtnSmall" name="${constant.REQUEST_PARAMETER__ROTATE_LEFT}" value="<? web/imcms/lang/jsp/crop_img.jsp/rotate_left ?>"/></td>
                                            <td>&nbsp;</td>
                                            <td><input type="submit" class="imcmsFormBtnSmall" name="${constant.REQUEST_PARAMETER__ROTATE_RIGHT}" value="<? web/imcms/lang/jsp/crop_img.jsp/rotate_right ?>"/></td>
                                            <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td>
                                                <fmt:message var="zoomText" key="web/imcms/lang/jsp/crop_img.jsp/zoom"/>
                                                <c:out value="${zoomText}"/>: <span id="zoom-value">100</span>%
                                                <fmt:message var="zoomInText" key="web/imcms/lang/jsp/crop_img.jsp/zoom_in"/>
                                                <a id="zoom-in" href="#" style="margin-left:10px;"><img src="<%=request.getContextPath()%>/imcms/images/zoom_in.gif" width="22" height="22" style="vertical-align:middle;border:none;" alt="${fn:escapeXml(zoomInText)}" title="${fn:escapeXml(zoomInText)}"/></a>
                                                <fmt:message var="zoomOutText" key="web/imcms/lang/jsp/crop_img.jsp/zoom_out"/>
                                                <a id="zoom-out" href="#" style="margin-left:10px;"><img src="<%=request.getContextPath()%>/imcms/images/zoom_out.gif" width="22" height="22" style="vertical-align:middle;border:none;" alt="${fn:escapeXml(zoomOutText)}" title="${fn:escapeXml(zoomOutText)}"/></a>
                                                <fmt:message var="zoomActualText" key="web/imcms/lang/jsp/crop_img.jsp/actual_size"/>
                                                <a id="zoom-actual" href="#" style="margin-left:10px;"><img src="<%=request.getContextPath()%>/imcms/images/zoom_actual_size.gif" width="22" style="vertical-align:middle;border:none;" height="22" alt="${fn:escapeXml(zoomActualText)}" title="${fn:escapeXml(zoomActualText)}"/></a>
                                                <fmt:message var="zoomFitText" key="web/imcms/lang/jsp/crop_img.jsp/best_fit"/>
                                                <a id="zoom-fit" href="#" style="margin-left:10px;"><img src="<%=request.getContextPath()%>/imcms/images/zoom_fit.gif" width="22" height="22" style="vertical-align:middle;border:none;" alt="${fn:escapeXml(zoomFitText)}" title="${fn:escapeXml(zoomFitText)}"/></a>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td align="right">
                                    <input type="submit" class="imcmsFormBtn" name="${constant.REQUEST_PARAMETER__OK}" value=" <? web/imcms/lang/jsp/crop_img.jsp/crop ?> "/>
                                    <input type="submit" class="imcmsFormBtn" name="${constant.REQUEST_PARAMETER__CANCEL}" value=" <? web/imcms/lang/jsp/crop_img.jsp/cancel ?> "/>
                                </td>
                            </tr>
                        </table>
                    </form>
                    
                    #gui_bottom()
                    #gui_outer_end()
                </div>
            </div>
        </body>
    </html>
</vel:velocity>
