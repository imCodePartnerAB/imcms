<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
                Event.observe(window, "load", 
                    function() {
                        var scale = 1, 
                            availableWidth = screen.availWidth - (30 * 2 + 24 * 2);

                        if (${imageWidth} > availableWidth) {
                            var origRatio = ${imageWidth} / ${imageHeight}, 
                                newWidth = availableWidth, 
                                newHeight = Math.floor(newWidth / origRatio);
                            
                            scale = ${imageWidth} / newWidth;
                            
                            $("image").setStyle({
                                width: newWidth + "px", 
                                height: newHeight + "px"
                            });
                        }

                        var updateValues = function(coords, dimensions) {
                            $("crop_x1").value = Math.round(coords.x1 * scale);
                            $("crop_y1").value = Math.round(coords.y1 * scale);

                            <%-- little adjustment, it seems that at some scales the 
                                 coordinates, also width and height can't reach maximum (image width/height) --%>
                            var cropX2 = coords.x2 * scale, 
                                cropY2 = coords.y2 * scale;
                            if ((${imageWidth} - cropX2) < 0.6) {
                                cropX2 = ${imageWidth};
                            }
                            if ((${imageHeight} - cropY2) < 0.6) {
                                cropY2 = ${imageHeight};
                            }

                            var width = dimensions.width * scale, 
                                height = dimensions.height * scale;
                            if ((${imageWidth} - width) < 0.6) {
                                width = ${imageWidth};
                            }
                            if ((${imageHeight} - height) < 0.6) {
                                height = ${imageHeight};
                            }
                            
                            $("crop_x2").value = Math.round(cropX2);
                            $("crop_y2").value = Math.round(cropY2);
                            $("width").value = Math.round(width);
                            $("height").value = Math.round(height);
                        };
                        
                        var options = {
                        	onEndCrop: updateValues, 
                            displayOnInit: true
                        };

                        if (${image.width gt 0 and image.height gt 0}) {
                            options.ratioDim = {
                                x: ${image.width}, 
                                y: ${image.height}
                            };
                        }
                        
                        if (${region.valid}) {
                            options.onloadCoords = {
                                x1: Math.floor(${region.cropX1} / scale), 
                                y1: Math.floor(${region.cropY1} / scale), 
                                x2: Math.ceil(${region.cropX2} / scale), 
                                y2: Math.ceil(${region.cropY2} / scale)
                            };
                        }
                        
                        var cropper = new Cropper.Img("image", options);

                        Event.observe("reset", "click", function() {
                            var options = cropper.options;
                            options.onloadCoords = null;
                            options.displayOnInit = false;
                            
                            cropper.reset();

                            var startCoords = { x1: 0, y1: 0, x2: 0, y2: 0 };
                            cropper.setAreaCoords( startCoords, false, false, 1);
                            
                            $("crop_x1").value = 0;
                            $("crop_y1").value = 0;
                            $("crop_x2").value = 0;
                            $("crop_y2").value = 0;
                            $("width").value = 0;
                            $("height").value = 0;
                        });
                    }
                );
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
            </style>
        </head>
        <body id="body" bgcolor="#FFFFFF">
            <div id="outer_container">
                <div id="inner_container">
                    #gui_outer_start()
                    #gui_head( "<? global/imcms_administration ?>" )
                    
                    <form method="post" action="${contextPath}/servlet/PageDispatcher">
                        <input type="hidden" name="${const.IN_REQUEST}" value="${cropPage.sessionAttributeName}"/>
                        <input type="hidden" name="scale" value="1"/>
                        #gui_mid()
                    
                        <table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
                            <tr>
                                <td colspan="2" align="center">
                                    <c:url var="imageUrl" value="/servlet/ImagePreview">
                                        <c:param name="path" value="${image.urlPathRelativeToContextPath}"/>

                                        <c:param name="rangle" value="${image.rotateDirection.angle}"/>
                                    </c:url>
                                    
                                    <c:set var="alternateText" value="${fn:escapeXml(image.alternateText)}"/>
                                    <img id="image" src="${fn:escapeXml(imageUrl)}" alt="${alternateText}" title="${alternateText}"/>
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
                                            <td><input id="crop_x1" name="${const.PARAM_CROP_X1}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropX1 : ''}"/></td>
                                            <td>&nbsp;</td>
                                            <td><input id="crop_y1" name="${const.PARAM_CROP_Y1}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropY1 : ''}"/></td>
                                            <td>&nbsp;</td>
                                            <td><input id="crop_x2" name="${const.PARAM_CROP_X2}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropX2 : ''}"/></td>
                                            <td>&nbsp;</td>
                                            <td><input id="crop_y2" name="${const.PARAM_CROP_Y2}" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.valid ? region.cropY2 : ''}"/></td>
                                            <td>&nbsp;&nbsp;</td>
                                            <td><input id="width" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.width}"/></td>
                                            <td>&nbsp;X&nbsp;</td>
                                            <td><input id="height" type="text" class="imcmsDisabled" readonly="readonly" size="4" maxlength="4" value="${region.height}"/></td>
                                            <td>&nbsp;&nbsp;</td>
                                            <td><input id="reset" type="button" class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/4008 ?>"/></td>

                                            <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                            <td><input type="submit" class="imcmsFormBtnSmall" name="${const.REQUEST_PARAMETER__ROTATE_LEFT}" value="<? web/imcms/lang/jsp/crop_img.jsp/rotate_left ?>"/></td>
                                            <td>&nbsp;</td>
                                            <td><input type="submit" class="imcmsFormBtnSmall" name="${const.REQUEST_PARAMETER__ROTATE_RIGHT}" value="<? web/imcms/lang/jsp/crop_img.jsp/rotate_right ?>"/></td>
                                        </tr>
                                    </table>
                                </td>
                                <td align="right">
                                    <input type="submit" class="imcmsFormBtn" name="${const.REQUEST_PARAMETER__OK}" value=" <? web/imcms/lang/jsp/crop_img.jsp/crop ?> "/>
                                    <input type="submit" class="imcmsFormBtn" name="${const.REQUEST_PARAMETER__CANCEL}" value=" <? web/imcms/lang/jsp/crop_img.jsp/cancel ?> "/>
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
