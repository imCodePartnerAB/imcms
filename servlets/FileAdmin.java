import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;

public class FileAdmin extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String rootpaths			= Utility.getDomainPref( "fileadmin_rootpaths",host ) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		// Check if user logged on
		User user ;
		if ( (user = Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 

		// Is user superadmin?
		String sqlStr  = "select role_id from users,user_roles_crossref\n" ;
		sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
		sqlStr += "and user_roles_crossref.role_id = 0\n" ;
		sqlStr += "and users.user_id = " + user.getInt("user_id") ;
	
		if ( IMCServiceRMI.sqlQuery(imcserver,sqlStr).length == 0 ) {
			Utility.redirect(req,res,start_url) ;
			return ;
		}

		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
		res.setHeader("Pragma","no-cache;") ;

		File fd1 = null ;
		File fd2 = null ;

		if ( rootpaths != null ) {
			StringTokenizer st = new StringTokenizer(rootpaths,File.pathSeparator) ;

			switch (st.countTokens()) {
				default:
					fd1 = Utility.getAbsolutePathFromString(st.nextToken()) ;
					fd2 = Utility.getAbsolutePathFromString(st.nextToken()) ;
					break ;
				case 1:
					fd1 = Utility.getAbsolutePathFromString(st.nextToken()) ;
					fd2 = fd1 ;
				case 0:
			} 
		}

		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream() ;
		out.print(parseFileAdmin(user,host,fd1,fd2)) ;
	}

	/**
		Check to see if the path is a child to one of the rootpaths
	*/
	private boolean isUnderRoot(File path, File[] roots) throws IOException {
		for ( int i=0 ; i<roots.length ; i++ ) {
			if ( path.getCanonicalPath().startsWith(roots[i].getCanonicalPath()) ) {
				return true ;
			}
		}
		return false ;
	}

	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String rootpaths			= Utility.getDomainPref( "fileadmin_rootpaths",host ) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		User user ;
		if ( (user = Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 

		// Is user superadmin?
		String sqlStr  = "select role_id from users,user_roles_crossref\n" ;
		sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
		sqlStr += "and user_roles_crossref.role_id = 0\n" ;
		sqlStr += "and users.user_id = " + user.getInt("user_id") ;
	
		if ( IMCServiceRMI.sqlQuery(imcserver,sqlStr).length == 0 ) {
			Utility.redirect(req,res,start_url) ;
			return ;
		}
		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
		res.setHeader("Pragma","no-cache;") ;

		int length = req.getContentLength() ;
		ServletInputStream in = req.getInputStream() ;
		byte buffer[] = new byte[ length ] ;
		int bytes_read = 0;
		while ( bytes_read < length ) {
			bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
		}
		String contentType = req.getContentType() ;	
		// Min klass tar emot datan och plockar ut det som är intressant...
		MultipartFormdataParser mp = new MultipartFormdataParser(buffer,contentType) ;
		
		if ( mp.getParameter("cancel") != null ) {
			Utility.redirect(req,res,"AdminManager") ;
			return ;
		}
		
		File[] roots = null ;
		if ( rootpaths != null ) {
			StringTokenizer st = new StringTokenizer(rootpaths,File.pathSeparator) ;
			roots = new File[st.countTokens()] ;
			for ( int i=0 ; i<roots.length ; i++ ) {
			    String oneRoot = st.nextToken().trim() ;
			    roots[i] = Utility.getAbsolutePathFromString(oneRoot) ;
			}
		}
		File dir1 = new File(mp.getParameter("dir1")) ;
		if ( !isUnderRoot(dir1,roots) ) {
			doGet(req,res) ;
		}
		File dir2 = new File(mp.getParameter("dir2")) ;
		if ( !isUnderRoot(dir2,roots) ) {
			doGet(req,res) ;
		}
		String[] files1 = mp.getParameterValues("files1") ;
		String[] files2 = mp.getParameterValues("files2") ;
		String name = mp.getParameter("name") ;

		if ( mp.getParameter("change1") != null ) {	//User wants to change dir1
			if ( files1 != null && files1.length == 1 ) {	//Has the user chosen just one dir?
				File foo = new File(files1[0]) ;
				if ( !foo.isAbsolute() ) {					//Is the dir one of the roots?
					foo = new File(dir1, files1[0]) ;		//No? Treat it like a relative path...
					if ( foo.isDirectory() && isUnderRoot(foo,roots) ) {				//It IS a directory, i hope?
						dir1 = foo ;
					}
				} else if ( isUnderRoot(foo,roots) ) {
					dir1 = foo ;							//It's one of the roots.
				}
			}
		} else if ( mp.getParameter("change2") != null ) {	//User wants to change dir2
			if ( files2 != null && files2.length == 1 ) {	//Has the user chosen just one dir?
				File foo = new File(files2[0]) ;
				if ( !foo.isAbsolute() ) {					//Is the dir one of the roots?
					foo = new File(dir2,files2[0]) ;		//No? Treat it like a relative path...
					if ( foo.isDirectory() && isUnderRoot(foo,roots)) {			//It IS a directory, i hope?
						dir2 = foo ;
					}
				} else if ( isUnderRoot(foo,roots) ) {
					dir2 = foo ;
				}
			}
		} else if ( mp.getParameter("mkdir1") != null ) {
			if ( name != null && name.length() > 0 ) {
				File newname = new File(dir1,name) ;
				if ( !newname.exists() ) {
					newname.mkdir() ;
				}
			} else {
				Vector vec = new Vector() ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminNameBlank.html",lang_prefix)) ;
				return ;
			}
		} else if ( mp.getParameter("mkdir2") != null ) {
			if ( name != null && name.length() > 0 ) {
				File newname = new File(dir2,name) ;
				if ( !newname.exists() ) {
					newname.mkdir() ;
				}
			} else {
				Vector vec = new Vector() ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminNameBlank.html",lang_prefix)) ;
				return ;
			}
		} else if ( mp.getParameter("delete1") != null ) {
			String optionlist = "" ;
			String files = "" ;
			File[] farray = makeFileTreeList(makeAbsoluteFileList(dir1,files1),false) ;
			String[] filelist = makeRelativeFileList(dir1,farray) ;
			if (filelist != null && filelist.length>0) {
				for ( int i = 0 ; i<filelist.length ; i++ ) {
					File foo = new File(dir1,filelist[i]) ;
					String bar = foo.getCanonicalPath() + (foo.isDirectory() ? File.separator : " ["+foo.length()+"]") ;
					optionlist += "<option>"+bar+"</option>" ;
					files += filelist[i] + File.pathSeparator ;
				}
				Vector vec = new Vector() ;
				vec.add("#filelist#") ;
				vec.add(optionlist) ;
				vec.add("#files#") ;
				vec.add(files) ;
				vec.add("#source#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminDeleteWarning.html",lang_prefix)) ;
				return ;
			}
		} else if ( mp.getParameter("delete2") != null ) {
			String optionlist = "" ;
			String files = "" ;
			File[] farray = makeFileTreeList(makeAbsoluteFileList(dir2,files2),false) ;
			String[] filelist = makeRelativeFileList(dir2,farray) ;
			if (filelist != null && filelist.length>0) {
				for ( int i = 0 ; i<filelist.length ; i++ ) {
					File foo = new File(dir2,filelist[i]) ;
					String bar = foo.getCanonicalPath() + (foo.isDirectory() ? File.separator : " ["+foo.length()+"]") ;
					optionlist += "<option>"+bar+"</option>" ;
					files += filelist[i] + File.pathSeparator ;
				}
				Vector vec = new Vector() ;
				vec.add("#filelist#") ;
				vec.add(optionlist) ;
				vec.add("#files#") ;
				vec.add(files) ;
				vec.add("#source#") ;
				vec.add(dir2.getCanonicalPath()) ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminDeleteWarning.html",lang_prefix)) ;	
				return ;
			}
		} else if ( mp.getParameter("deleteok") != null ) {
			String files, path ;
			if ( (files = mp.getParameter("files")) != null && (path = mp.getParameter("source")) != null) {
				StringTokenizer st = new StringTokenizer (files,File.pathSeparator) ;
				while ( st.hasMoreTokens() ) {
					File foo = new File(path,st.nextToken()) ;
					if ( foo.exists() && isUnderRoot(foo.getParentFile(),roots)) {
						foo.delete() ;
					}
				}
			}
		} else if ( mp.getParameter("upload1") != null ) {
			String file = mp.getParameter("file") ;
			if ( file == null || file.length() < 1 ) {
				Vector vec = new Vector() ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminFileBlank.html",lang_prefix)) ;
				return ;
			}
			String filename = (new File(mp.getFilename("file"))).getName() ;
			File new_file = new File(dir1,filename) ;
			int counter = 0 ;
			while ( new_file.exists() ) {
				if ( new_file.getName().endsWith(String.valueOf(counter)) ) {	//If the filename ends with the number "counter"
					String fname = new_file.getName() ;
					fname = fname.substring(0,fname.length()-String.valueOf(counter).length()) ;	//Cut off the number from the filename
					new_file = new File(dir1,fname+String.valueOf(++counter)) ;	//Increase and append the number to the filename
				} else {
					new_file = new File(dir1,new_file.getName()+String.valueOf(counter)) ;	//Increase and append the number to the filename
				}
			}
			FileOutputStream fout = new FileOutputStream(new_file) ;
			fout.write(file.getBytes("8859_1")) ;
			fout.flush() ;
			fout.close() ;
			if ( !new_file.getName().equals(filename) ) {
				Vector vec = new Vector() ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				vec.add("#filename#") ;
				vec.add(new_file.getName()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminFileExisted.html",lang_prefix)) ;
				return ;
			}
		} else if ( mp.getParameter("upload2") != null ) {
			String file = mp.getParameter("file") ;
			if ( file == null || file.length() < 1 ) {
				Vector vec = new Vector() ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminFileBlank.html",lang_prefix)) ;
				return ;
			}
			String filename = (new File(mp.getFilename("file"))).getName() ;
			File new_file = new File(dir2,filename) ;
			int counter = 0 ;
			while ( new_file.exists() ) {
				if ( new_file.getName().endsWith(String.valueOf(counter)) ) {	//If the filename ends with the number "counter"
					String fname = new_file.getName() ;
					fname = fname.substring(0,fname.length()-String.valueOf(counter).length()) ;	//Cut off the number from the filename
					new_file = new File(dir2,fname+String.valueOf(++counter)) ;	//Increase and append the number to the filename
				} else {
					new_file = new File(dir2,new_file.getName()+String.valueOf(counter)) ;	//Increase and append the number to the filename
				}
			}
			FileOutputStream fout = new FileOutputStream(new_file) ;
			fout.write(file.getBytes("8859_1")) ;
			fout.flush() ;
			fout.close() ;
			if ( !new_file.getName().equals(filename) ) {
				Vector vec = new Vector() ;
				vec.add("#dir1#") ;
				vec.add(dir1.getCanonicalPath()) ;
				vec.add("#dir2#") ;
				vec.add(dir2.getCanonicalPath()) ;
				vec.add("#filename#") ;
				vec.add(new_file.getName()) ;
				res.setContentType("text/html") ;
				ServletOutputStream out = res.getOutputStream() ;
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminFileExisted.html",lang_prefix)) ;
				return ;
			}
		} else if ( mp.getParameter("download1") != null ) {
			if ( files1 != null && files1.length == 1 ) {	//Has the user chosen just one file?
				File file = new File(dir1,files1[0]) ;
				try {
					res.setContentType("application/octetstream;name=\""+file.getName()+"\"") ;
					res.setHeader("Content-Disposition","attachment;filename=\""+file.getName()+"\"") ;
					BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file)) ;
					buffer = new byte[65536] ;
					res.setContentLength(fin.available()) ;
					ServletOutputStream out = res.getOutputStream() ;
					while ( (bytes_read = fin.read(buffer))!=-1 ) {
						out.write(buffer,0,bytes_read) ;
					}
					return ;
				} catch ( FileNotFoundException ex ) {
					imcode.util.log.Log log = imcode.util.log.Log.getLog( this.getClass().getName() );
					log.log( imcode.util.log.LogLevels.DEBUG, "Exception occured" + ex.getMessage() );	   					
				}
			}
		} else if ( mp.getParameter("download2") != null ) {
			if ( files2 != null && files2.length == 1 ) {	//Has the user chosen just one file?
				File file = new File(dir2,files2[0]) ;
				try {
					res.setContentType("application/octetstream;name=\""+file.getName()+"\"") ;
					res.setHeader("Content-Disposition","attachment;filename=\""+file.getName()+"\"") ;
					BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file)) ;
					buffer = new byte[65536] ;
					res.setContentLength(fin.available()) ;
					ServletOutputStream out = res.getOutputStream() ;
					while ( (bytes_read = fin.read(buffer))!=-1 ) {
						out.write(buffer,0,bytes_read) ;
					}
					return ;
				} catch ( FileNotFoundException ex ) {
					
				}
			}
		} else if ( mp.getParameter("rename1") != null ) {
			if ( files1 != null && files1.length == 1 ) {	//Has the user chosen just one file?
				if ( name != null && name.length() > 0 ) {
					File f1 = new File(dir1, files1[0]) ;
					File f2 = new File(dir1, name) ;
					if ( f1.exists() ) {
						f1.renameTo(f2) ;
					}
				} else {
					Vector vec = new Vector() ;
					vec.add("#dir1#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dir2#") ;
					vec.add(dir2.getCanonicalPath()) ;
					res.setContentType("text/html") ;
					ServletOutputStream out = res.getOutputStream() ;
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminNameBlank.html",lang_prefix)) ;
					return ;
				}
			}
		} else if ( mp.getParameter("rename2") != null ) {
			if ( files2 != null && files2.length == 1 ) {	//Has the user chosen just one file?
				if ( name != null && name.length() > 0 ) {
					File f1 = new File(dir2, files2[0]) ;
					File f2 = new File(dir2, name) ;
					if ( f1.exists() ) {
						f1.renameTo(f2) ;
					}
				} else {
					Vector vec = new Vector() ;
					vec.add("#dir1#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dir2#") ;
					vec.add(dir2.getCanonicalPath()) ;
					res.setContentType("text/html") ;
					ServletOutputStream out = res.getOutputStream() ;
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminNameBlank.html",lang_prefix)) ;
					return ;
				}
			}
		} else if ( mp.getParameter("copy1") != null ) {
			if ( files1 != null && !dir1.equals(dir2)) {
				File[] source = makeFileTreeList(makeAbsoluteFileList(dir1,files1),true) ;
				String[] filelist = makeRelativeFileList(dir1,source) ;
				String option_list = "" ;
				String file_list = "" ;
				for ( int i=0 ; i<filelist.length ; i++ ) {
					File dest = new File(dir2, filelist[i]) ;
					file_list += filelist[i] + File.pathSeparator ;
					if ( dest.exists() ) {
						String foo = dest.getCanonicalPath() + (dest.isDirectory() ? File.separator : " ["+dest.length()+"]") ;
						option_list += "<option>" + foo + "</option>" ;
					}
				}
				if ( option_list.length() > 0 ) {
					Vector vec = new Vector() ;
					vec.add("#filelist#") ;
					vec.add(option_list) ;
					vec.add("#source#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dest#") ;
					vec.add(dir2.getCanonicalPath()) ;
					vec.add("#files#") ;
					vec.add(file_list) ;
					vec.add("#dir1#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dir2#") ;
					vec.add(dir2.getCanonicalPath()) ;
					res.setContentType("text/html") ;
					ServletOutputStream out = res.getOutputStream() ;
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminCopyOverwriteWarning.html",lang_prefix)) ;
					return ;
				} else {
					File[] dest = makeAbsoluteFileList(dir2,filelist) ;
					for ( int i=0 ; i < source.length ; i++ ) {
						if ( source[i].isDirectory() ) {
							dest[i].mkdir() ;
							continue ;
						}
						FileInputStream fin = new FileInputStream(source[i]) ;
						FileOutputStream fout = new FileOutputStream(dest[i]) ;
						buffer = new byte[65536] ;
						while ( (bytes_read = fin.read(buffer)) != -1 ) {
							fout.write(buffer,0,bytes_read) ;
						}
					}
				}
			}
		} else if ( mp.getParameter("copy2") != null ) {
			if ( files2 != null && !dir2.equals(dir1)) {
				File[] source = makeFileTreeList(makeAbsoluteFileList(dir2,files2),true) ;
				String[] filelist = makeRelativeFileList(dir2,source) ;
				String option_list = "" ;
				String file_list = "" ;
				for ( int i=0 ; i<filelist.length ; i++ ) {
					File dest = new File(dir1, filelist[i]) ;
					file_list += filelist[i] + File.pathSeparator ;
					if ( dest.exists() ) {
						String foo = dest.getCanonicalPath() + (dest.isDirectory() ? File.separator : " ["+dest.length()+"]") ;
						option_list += "<option>" + foo + "</option>" ;
					}
				}
				if ( option_list.length() > 0 ) {
					Vector vec = new Vector() ;
					vec.add("#filelist#") ;
					vec.add(option_list) ;
					vec.add("#source#") ;
					vec.add(dir2.getCanonicalPath()) ;
					vec.add("#dest#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#files#") ;
					vec.add(file_list) ;
					vec.add("#dir1#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dir2#") ;
					vec.add(dir2.getCanonicalPath()) ;
					res.setContentType("text/html") ;
					ServletOutputStream out = res.getOutputStream() ;
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminCopyOverwriteWarning.html",lang_prefix)) ;
					return ;
				} else {
					File[] dest = makeAbsoluteFileList(dir1,filelist) ;
					for ( int i=0 ; i < source.length ; i++ ) {
						if ( source[i].isDirectory() ) {
							dest[i].mkdir() ;
							continue ;
						}
						FileInputStream fin = new FileInputStream(source[i]) ;
						FileOutputStream fout = new FileOutputStream(dest[i]) ;
						buffer = new byte[65536] ;
						while ( (bytes_read = fin.read(buffer)) != -1 ) {
							fout.write(buffer,0,bytes_read) ;
						}
					}
				}
			}
		} else if ( mp.getParameter("copyok") != null ) {
			String src = mp.getParameter("source") ;
			String dst = mp.getParameter("dest") ;
			String files = mp.getParameter("files") ;
			if ( src != null && dst != null && files != null ) {
				File srcdir = new File(src) ;
				File dstdir = new File(dst) ;
				if ( isUnderRoot(srcdir,roots) && isUnderRoot(dstdir,roots) ) {
					StringTokenizer st = new StringTokenizer (files,File.pathSeparator) ;
					while ( st.hasMoreTokens() ) {
						String foo = st.nextToken() ;
						File source = new File(srcdir,foo) ;
						File dest = new File(dstdir,foo) ;
						if ( source.isDirectory() ) {
							dest.mkdir() ;
							continue ;
						}
						FileInputStream fin = new FileInputStream(source) ;
						FileOutputStream fout = new FileOutputStream(dest) ;
						buffer = new byte[65536] ;
						while ( (bytes_read = fin.read(buffer)) != -1 ) {
							fout.write(buffer,0,bytes_read) ;
						}
					}
				}
			}
		} else if ( mp.getParameter("move1") != null ) {
			if ( files1 != null && !dir1.equals(dir2)) {
				File[] source = makeFileTreeList(makeAbsoluteFileList(dir1,files1),false) ;
				String[] filelist = makeRelativeFileList(dir1,source) ;
				String option_list = "" ;
				String file_list = "" ;
				for ( int i=0 ; i<filelist.length ; i++ ) {
					File dest = new File(dir2, filelist[i]) ;
					file_list += filelist[i] + File.pathSeparator ;
					if ( dest.exists() ) {
						String foo = dest.getCanonicalPath() + (dest.isDirectory() ? File.separator : " ["+dest.length()+"]") ;
						option_list += "<option>" + foo + "</option>" ;
					}
				}
				if ( option_list.length() > 0 ) {
					Vector vec = new Vector() ;
					vec.add("#filelist#") ;
					vec.add(option_list) ;
					vec.add("#source#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dest#") ;
					vec.add(dir2.getCanonicalPath()) ;
					vec.add("#files#") ;
					vec.add(file_list) ;
					vec.add("#dir1#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dir2#") ;
					vec.add(dir2.getCanonicalPath()) ;
					res.setContentType("text/html") ;
					ServletOutputStream out = res.getOutputStream() ;
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminCopyOverwriteWarning.html",lang_prefix)) ;
					return ;
				} else {
					File[] dest = makeAbsoluteFileList(dir2,filelist) ;
					for ( int i=0 ; i < source.length ; i++ ) {
						dest[i].getParentFile().mkdirs() ;
						if ( source[i].isFile() ) {
							FileInputStream fin = new FileInputStream(source[i]) ;
							FileOutputStream fout = new FileOutputStream(dest[i]) ;
							buffer = new byte[65536] ;
							while ( (bytes_read = fin.read(buffer)) != -1 ) {
								fout.write(buffer,0,bytes_read) ;
							}
							fout.close() ;
							fin.close() ;
						}
						if ( source[i].length() == dest[i].length() ) {
							source[i].delete() ;
						}
					}
				}
			}
		} else if ( mp.getParameter("move2") != null ) {
			if ( files2 != null && !dir2.equals(dir1)) {
				File[] source = makeFileTreeList(makeAbsoluteFileList(dir2,files2),false) ;
				String[] filelist = makeRelativeFileList(dir2,source) ;
				String option_list = "" ;
				String file_list = "" ;
				for ( int i=0 ; i<filelist.length ; i++ ) {
					File dest = new File(dir1, filelist[i]) ;
					file_list += filelist[i] + File.pathSeparator ;
					if ( dest.exists() ) {
						String foo = dest.getCanonicalPath() + (dest.isDirectory() ? File.separator : " ["+dest.length()+"]") ;
						option_list += "<option>" + foo + "</option>" ;
					}
				}
				if ( option_list.length() > 0 ) {
					Vector vec = new Vector() ;
					vec.add("#filelist#") ;
					vec.add(option_list) ;
					vec.add("#source#") ;
					vec.add(dir2.getCanonicalPath()) ;
					vec.add("#dest#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#files#") ;
					vec.add(file_list) ;
					vec.add("#dir1#") ;
					vec.add(dir1.getCanonicalPath()) ;
					vec.add("#dir2#") ;
					vec.add(dir2.getCanonicalPath()) ;
					res.setContentType("text/html") ;
					ServletOutputStream out = res.getOutputStream() ;
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					out.print(IMCServiceRMI.parseDoc(imcserver, vec,"FileAdminMoveOverwriteWarning.html",lang_prefix)) ;
					return ;
				} else {
					File[] dest = makeAbsoluteFileList(dir1,filelist) ;
					for ( int i=0 ; i < source.length ; i++ ) {
						dest[i].getParentFile().mkdirs() ;
						if ( source[i].isFile() ) {
							FileInputStream fin = new FileInputStream(source[i]) ;
							FileOutputStream fout = new FileOutputStream(dest[i]) ;
							buffer = new byte[65536] ;
							while ( (bytes_read = fin.read(buffer)) != -1 ) {
								fout.write(buffer,0,bytes_read) ;
							}
							fout.close() ;
							fin.close() ;
						}
						if ( source[i].length() == dest[i].length() ) {
							source[i].delete() ;
						}
					}
				}
			}
		} else if ( mp.getParameter("moveok") != null ) {
			String src = mp.getParameter("source") ;
			String dst = mp.getParameter("dest") ;
			String files = mp.getParameter("files") ;
			if ( src != null && dst != null && files != null ) {
				File srcdir = new File(src) ;
				File dstdir = new File(dst) ;
				if ( isUnderRoot(srcdir,roots) && isUnderRoot(dstdir,roots) ) {
					StringTokenizer st = new StringTokenizer (files,File.pathSeparator) ;
					while ( st.hasMoreTokens() ) {
						String foo = st.nextToken() ;
						File source = new File(srcdir,foo) ;
						File dest = new File(dstdir,foo) ;
						dest.getParentFile().mkdirs() ;
						if ( source.isFile() ) {
							FileInputStream fin = new FileInputStream(source) ;
							FileOutputStream fout = new FileOutputStream(dest) ;
							buffer = new byte[65536] ;
							while ( (bytes_read = fin.read(buffer)) != -1 ) {
								fout.write(buffer,0,bytes_read) ;
							}
							fout.close() ;
							fin.close() ;
						}
						if ( source.length() == dest.length() ) {
							source.delete() ;
						}
					}
				}
			}
		}
	
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream() ;
		out.print(parseFileAdmin(user,host,dir1,dir2)) ;
	}
/*	
	private Object[] push (Object[] foo, Object[] bar) {
		if ((foo == null) && (bar != null)) {
			return bar ;
		} else if ( bar == null ) {
			return foo ;
		}
		Object[] baz = new Object[foo.length+bar.length] ;
		int j = 0 ;
		for ( int i=0; i<foo.length ; i++ ) {
			baz[j++] = foo[i] ;
		}
		for ( int i=0; i<bar.length ; i++ ) {
			baz[j++] = bar[i] ;
		}
		return baz ;
	}
*/	
	/**
		Takes a list of files that are supposed to share a common parent, and returns them in an array.
	*/
	private File[] makeAbsoluteFileList (File parent, String[] files) {
		if ( files == null || parent == null ) {
			return null ;
		}
		LinkedList list = new LinkedList() ;
		for ( int i=0 ; i<files.length ; i++ ) {
			if ( !files[i].equals("..") && !(new File(files[i]).isAbsolute()) ) {
				list.add(new File(parent, files[i])) ;
			}
		}
		File[] result = new File[list.size()] ;
		for ( int i=0 ; i<result.length ; i++ ) {
			result[i] = (File)list.removeFirst() ;
		}
		return result ;
	}
	
	/**
		Takes a list of files that share a common parent, orphans them, and returns them in an array.
	*/
	private String[] makeRelativeFileList (File parent, File[] files) throws IOException {
		if ( files == null || parent == null ) {
			return null ;
		}
		String[] result = new String[files.length] ;
		String ppath = parent.getCanonicalPath() ;
		for ( int i=0 ; i<files.length ; i++ ) {
			String path = files[i].getCanonicalPath() ;
			if ( path.startsWith(ppath) ) {
				result[i] = path.substring(ppath.length()) ;
			} else {
				throw new IllegalArgumentException (ppath + " not parent of " + path) ;
			}
		}
		return result ;
	}
	
	/**
		Takes a list of files and dirs in one dir, and recursively adds the files of the subdirs.
	*/
	private File[] makeFileTreeList (File[] files, boolean dirfirst) {
		if ( files == null ) {
			return new File[0] ;
		}
		LinkedList list = new LinkedList() ;
		for ( int i=0 ; i<files.length ; i++ ) {
			if ( dirfirst ) {
				list.add(files[i]) ;
			}
			if ( files[i].isDirectory() ) {
				File[] sub_list = makeFileTreeList(files[i].listFiles(), dirfirst) ;
				for ( int j=0 ; j<sub_list.length ; j++ ) {
					list.add(sub_list[j]) ;
				}
				sub_list = null ;
			}
			if ( !dirfirst ) {
				list.add(files[i]) ;
			}
		}
		File[] result = new File[list.size()] ;
		for ( int i=0 ; i<result.length ; i++ ) {
			result[i] = (File) list.removeFirst() ;
		}
		return result ;
	}

	private String parseFileAdmin (User user, String host, File fd1, File fd2) throws IOException {
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String rootpaths			= Utility.getDomainPref( "fileadmin_rootpaths",host ) ;

		String files1 = "" ;
		String files2 = "" ;
		Vector vec = new Vector () ;
		File[] rootlist = null ;
		if ( rootpaths != null ) {
			StringTokenizer st = new StringTokenizer(rootpaths,File.pathSeparator) ;
			rootlist = new File[st.countTokens()] ;
			for ( int i = 0 ; i<rootlist.length && st.hasMoreTokens() ; i++ ) {
			    String oneRoot = st.nextToken().trim() ;
			    rootlist[i] = Utility.getAbsolutePathFromString(oneRoot) ;
			}
		}
		DirectoryFilter dirfilt = new DirectoryFilter() ;
		NotDirectoryFilter notdirfilt = new NotDirectoryFilter() ;
		if ( fd1!=null ) {
			vec.add("#dir1#") ;
			vec.add(fd1.getCanonicalPath()) ;
			String optionlist = "" ;
			for ( int i = 0 ; i < rootlist.length ; i++ ) {
				String foo = rootlist[i].getCanonicalPath() + File.separator ; 
				optionlist+="<option value=\""+foo+"\">"+foo+"</option>" ;
			}
			optionlist+="<option value=\"..\">.."+File.separator+"</option>" ;
			File[] filelist = fd1.listFiles(dirfilt) ;
			for ( int i = 0 ; i < filelist.length ; i++ ) {
				String foo = filelist[i].getName() + File.separator ; 
				optionlist+="<option value=\""+foo+"\">"+foo+"</option>" ;
			}
			filelist = fd1.listFiles(notdirfilt) ;
			for ( int i = 0 ; i < filelist.length ; i++ ) {
				String foo = filelist[i].getName() + " ["+filelist[i].length()+"]" ; 
				optionlist+="<option value=\""+filelist[i].getName()+"\">"+foo+"</option>" ;
			}
			vec.add("#files1#") ;
			vec.add(optionlist) ;
		} else {
			vec.add("#dir1#") ;
			vec.add("") ;
			vec.add("#files1#") ;
			vec.add("") ;
		}
		if ( fd2!=null ) {
			vec.add("#dir2#") ;
			vec.add(fd2.getCanonicalPath()) ;
			String optionlist = "" ;
			for ( int i = 0 ; i < rootlist.length ; i++ ) {
				String foo = rootlist[i].getCanonicalPath() + File.separator ; 
				optionlist+="<option value=\""+foo+"\">"+foo+"</option>" ;
			}
			optionlist+="<option value=\"..\">.."+File.separator+"</option>" ;
			File[] filelist = fd2.listFiles(dirfilt) ;
			for ( int i = 0 ; filelist!=null && i < filelist.length ; i++ ) {
				String foo = filelist[i].getName() + File.separator ; 
				optionlist+="<option value=\""+foo+"\">"+foo+"</option>" ;
			}
			filelist = fd2.listFiles(notdirfilt) ;
			for ( int i = 0 ; filelist!=null && i < filelist.length ; i++ ) {
				String foo = filelist[i].getName() + " ["+filelist[i].length()+"]" ; 
				optionlist+="<option value=\""+filelist[i].getName()+"\">"+foo+"</option>" ;
			}
			vec.add("#files2#") ;
			vec.add(optionlist) ;
		} else {
			vec.add("#dir2#") ;
			vec.add("") ;
			vec.add("#files2#") ;
			vec.add("") ;
		}
		
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		return IMCServiceRMI.parseDoc(imcserver, vec,"FileAdmin.html",lang_prefix) ;
	}

	private class DirectoryFilter implements FileFilter {
		public boolean accept (File path) {
			return path.isDirectory() ;
		}
	}

	private class NotDirectoryFilter implements FileFilter {
		public boolean accept (File path) {
			return !path.isDirectory() ;
		}
	}
}
