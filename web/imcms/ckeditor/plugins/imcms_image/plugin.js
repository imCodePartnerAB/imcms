
(function() {
	var commandName = 'imcms_image' ;
	
	var commandDef  = {
		exec : function(editor) {
			var image = editor.getSelection().getSelectedElement() ;
			var outparam = null ;
			
			if (typeof image == 'undefined') {
				image = image.getParent() ;
				if (image && 'img' != image.getName()) {
					image = null ;
				}
			}
			
			if (image) {
				var src = image.getAttribute('src') ;
				var url, format, width, height, cropX1, cropY1, cropX2, cropY2, rotateAngle, queryIndex, genFile, metaId ;
				
				if (src && (queryIndex = src.indexOf('?')) != -1) {
					var parts = src.substring(queryIndex + 1).split('&') ;
					
					for (var i = 0; i < parts.length; ++i) {
						var keyValue = parts[i].split('=') ;
						
						if (keyValue.length != 2) {
							continue ;
						}
						
						var key   = keyValue[0] ;
						var value = keyValue[1] ;
						
						switch (key) {
							case 'path':
								url = contextPath + decodeURIComponent(value) ;
								break ;
							case 'file_id':
								url = contextPath + value ;
								break ;
							case 'width':
								width = value ;
								break ;
							case 'height':
								height = value ;
								break ;
							case 'format':
								format = value ;
								break ;
							case 'crop_x1':
								cropX1 = value ;
								break ;
							case 'crop_y1':
								cropY1 = value ;
								break ;
							case 'crop_x2':
								cropX2 = value ;
								break ;
							case 'crop_y2':
								cropY2 = value ;
								break ;
							case 'rangle':
								rotateAngle = value ;
								break ;
                            case 'gen_file':
                                genFile = value;
                                break;
                            case 'meta_id':
                                metaId = value;
                                break;
							default:
								break ;
						}
					}
				}
				
				outparam = {
					'imageref'     : url, 
					'format_ext'   : format, 
					'crop_x1'      : cropX1, 
					'crop_y1'      : cropY1, 
					'crop_x2'      : cropX2, 
					'crop_y2'      : cropY2, 
					'rotate_angle' : rotateAngle || 0, 
					'alt_text'     : image.getAttribute('alt') || image.getAttribute('title'),
					'image_width'  : (image.getStyle('width') || image.getAttribute('width') || width || '0').replace(/[^\d]/g, ''),
					'image_height' : (image.getStyle('height') || image.getAttribute('height') || height || '0').replace(/[^\d]/g, ''),
					'image_border' : (image.getStyle('border-width') || image.getAttribute('border') || '0').replace(/[^\d]/g, ''),
					'image_align'  : image.getAttribute('align'),
					'v_space'      : (image.getStyle('margin-top') || image.getAttribute('vspace') || '0').replace(/[^\d]/g, ''),
					'h_space'      : (image.getStyle('margin-right') || image.getAttribute('hspace') || '0').replace(/[^\d]/g, ''),
					'image_name'   : image.getAttribute('id') || image.getAttribute('name')
				} ;

                if (genFile) {
                    outparam['gen_file'] = genFile;
                }
			}
			
            if (metaId == null) {
                metaId = editor.config.imcmsMetaId;
            }
            if (metaId != null) {
                outparam = outparam || {};
                outparam['meta_id'] = metaId;
            }
            
			var queryString = '' ;
			
			for (var p in outparam) {
				if (outparam[p]) {
					queryString += '&' + p + '=' + encodeURIComponent(outparam[p]) ;
				}
			}
			
			//console.log('queryString: ' + queryString) ;
			
			//console.log('image / image.src (before): ' + image + ' / ' + image.src + ' / ' + image.getAttribute('src')) ;
			
			var returnImage = window.showModalDialog(CKEDITOR_imcmsImageEditPath + queryString,null,'dialogWidth:' + screen.availWidth + 'px;dialogHeight:' + screen.availHeight + 'px;center:yes;resizable:yes;help:no') ;
			
			if (!returnImage) { // user must have pressed Cancel
				return false ;
			}
			
			//console.log('image: ' + image + '\nreturnImage.src: ' + returnImage.src) ;

            // Always recreate the image tag, otherwise the content will lag behind when
            // editing existing images.
			var img = editor.document.createElement('img') ;
            img.setAttribute('src', returnImage.src) ;
            editor.insertElement(img) ;
			
			for (var parameter in returnImage) {
				var parameterValue = returnImage[parameter] ;
				//console.log(parameter + ' : ' + parameterValue) ;
				switch (parameter) {
					case 'alt':
						img.setAttribute('alt', parameterValue || '') ;
						img.setAttribute('title', parameterValue) ;
						break ;
					case 'border':
						img.setStyle('border-width', CKEDITOR.tools.cssLength(parameterValue)) ;
						img.setAttribute('border', parseInt(parameterValue, 10) + '') ;
						break ;
					case 'align':
						img.setAttribute('align', parameterValue) ;
						break ;
					case 'vert':
						img.setStyle('margin-top', CKEDITOR.tools.cssLength(parameterValue)) ;
						img.setStyle('margin-bottom', CKEDITOR.tools.cssLength(parameterValue)) ;
						break ;
					case 'horiz':
						img.setStyle('margin-left', CKEDITOR.tools.cssLength(parameterValue)) ;
						img.setStyle('margin-right', CKEDITOR.tools.cssLength(parameterValue)) ;
						break ;
					case 'width':
						img.setAttribute('width', parseInt(parameterValue, 10) + '') ;
						break ;
					case 'height':
						img.setAttribute('height', parseInt(parameterValue, 10) + '') ;
						break ;
					case 'name':
						img.setAttribute('id', parameterValue) ;
						break ;
				}
			}
            
		}
	} ;
	
	CKEDITOR.plugins.add(commandName, {
		init : function(editor) {
			editor.addCommand(commandName, commandDef) ;
			editor.ui.addButton('ImcmsImage',{
				label   : editor.lang.common.image, 
				icon    : this.path + 'toolBarButton.png',
				command : commandName
			}) ;
			
			editor.on('doubleclick', function(evt) {
			var element = evt.data.element ;
				if (element.is('img') && !element.getAttribute('_cke_realelement')) {
					editor.execCommand(commandName) ;
				}
			}) ;
			
			if (editor.addMenuItems) {
				editor.addMenuItems({
					image : {
						label   : editor.lang.image.menu,
						command : commandName,
						group   : 'image'
					}
				}) ;
			}
			
			if (editor.contextMenu) {
				editor.contextMenu.addListener(function(element, selection) {
					if (!element || !element.is('img') || element.getAttribute('_cke_realelement') || element.isReadOnly()) {
						return null ;
					}
					return { image : CKEDITOR.TRISTATE_OFF } ;
				}) ;
			}
		}
	}) ;
	
})() ;