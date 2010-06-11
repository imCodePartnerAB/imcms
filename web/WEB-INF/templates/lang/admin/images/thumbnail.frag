#set( $max = 64 )
#set( $width = $max )
#set( $height = $max )
#if( $imageSize.Width > 0 && $imageSize.Height > 0 )
    #if( $imageSize.Width > $max || $imageSize.Height > $max )
        #if( $imageSize.Width > $imageSize.Height )
            #set( $height = ($max * $imageSize.Height) / $imageSize.Width )
        #else
            #set( $width = ($max * $imageSize.Width) / $imageSize.Height )
        #end
    #else
        #set( $width = $imageSize.Width )
        #set( $height = $imageSize.Height )
    #end
#end
<a href="$imageUrl"><img src="$imageUrl" alt="<? web/WEB-INF/templates/lang/admin/images/thumbnail.frag/alt ?>" border="0" width="$width" height="$height"></a>
#if( $imageSize.Width > 0 && $imageSize.Height > 0 )<br>${imageSize.Width}x${imageSize.Height}<br>$fileSize#end
