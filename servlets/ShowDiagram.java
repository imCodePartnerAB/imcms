import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.image.* ;
import java.text.DecimalFormat;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.JLabel;

import com.klg.jclass.chart.*;
import com.klg.jclass.chart.beans.SimpleChart;

import com.sun.image.codec.jpeg.* ;

import imcode.util.* ;

/**
	This class reads the diagram files and outputs a diagram in GIF-format
*/
public class ShowDiagram extends HttpServlet {

	public void init( ServletConfig config ) throws ServletException {

		super.init( config );

	}

	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

		String host = req.getHeader("Host") ;
		String file_path = Utility.getDomainPref("diagram_path",host) ;
		ServletOutputStream out = res.getOutputStream( );  // binary output!

		Frame frame = null;
		DataSource ds = new DataSource( );

		String type = req.getParameter( "type" ) ;
		String data = req.getParameter( "diaD" ) ;
		String prefs = req.getParameter( "diaP" ) ;

		FileParser fp = new FileParser( );

		LinkedList list =	fp.getData( file_path+data );
		ds.num_points = list.size( );
		ds.pointLabels = new String[ds.num_points];
		ds.xvalues = new double[ds.num_points];

		//Count the number of series
		for( int i=0;i<ds.num_points;i++ ) {
			StringTokenizer st = new StringTokenizer( (String)list.get( i ), "|" );
			ds.pointLabels[i] = st.nextToken( );
			ds.xvalues[i] = i;
			int series = 0;
			int last_success = 0;
			while( st.hasMoreTokens( ) ) {
				String line = st.nextToken( );
				series++;
				try {
					Double.parseDouble( line );
					last_success = series;
				}
				catch( NumberFormatException e ) {
					continue;
				}
			}
			if( last_success > ds.num_series ) {
				ds.num_series = last_success;
			}
		}			
		//Allocate data
		ds.yvalues = new double[ds.num_series][ds.num_points];
		//Read the data
		for( int i=0;i<ds.num_points;i++ ) {
			StringTokenizer st = new StringTokenizer( (String)list.get( i ), "|" );
			st.nextToken( );
			for( int j=0;j<ds.num_series;j++ ) {
				String line;
				if( st.hasMoreTokens( ) ) {
					line = st.nextToken( );
				} else {
					ds.yvalues[j][i] = Double.POSITIVE_INFINITY;
					continue;
				}
				try {
					ds.yvalues[j][i] = Double.parseDouble( line );
				}
				catch( NumberFormatException e ) {
					ds.yvalues[j][i] = Double.POSITIVE_INFINITY;
					continue;
				}
			}
		}			

		ds.seriesLabels = new String[ds.num_series];

		//finished reading the datafile. Now for the prefs...
		Properties pref = fp.getProperties( file_path+prefs );

		for( int i=0;i<ds.num_series;i++ ) {
			ds.seriesLabels[i] = pref.getProperty( "SERIESTITLE"+String.valueOf( i+1 ) );
		}

		int width,height;

		try {	width = Integer.parseInt( pref.getProperty( "WIDTH" ) ) ; 
		}
		catch( NumberFormatException e ) { width = 650; 
		}

		try {	height = Integer.parseInt( pref.getProperty( "HEIGHT" ) ) ; 
		}
		catch( NumberFormatException e ) {	height = 500; 
		}

		String header = pref.getProperty( "HEADER" );
		String footer = pref.getProperty( "FOOTER" );
		String xheader = pref.getProperty( "XHEADER" );
		String yheader = pref.getProperty( "YHEADER" );
		String xmax = pref.getProperty( "HORIZAXISMAX" ).replace( ',','.' ) ;
		String xmin = pref.getProperty( "HORIZAXISMIN" ).replace( ',','.' );
		String ymax = pref.getProperty( "VERTAXISMAX" ).replace( ',','.' );
		String ymin = pref.getProperty( "VERTAXISMIN" ).replace( ',','.' );
		String xnum = pref.getProperty( "BOTTOMAXISINCREMENT" ).replace( ',','.' );
		String ynum = pref.getProperty( "LEFTAXISINCREMENT" ).replace( ',','.' );

		SimpleChart chart;

		try {
			// Create an unshown frame
			frame = new Frame( );
			frame.addNotify( );

			// Create new chart instance.

			chart = new SimpleChart( );
			ChartDataView dv = chart.getDataView( 0 );
			JCChartArea ca = chart.getChartArea( );
			int diatype;
			try {
				int index = prefs.indexOf( "_" )-1;
				diatype = Integer.parseInt( prefs.substring( index,index+1 ) );
			}
			catch( NumberFormatException e ) {diatype = 3;
			}
			catch( IndexOutOfBoundsException e ) {diatype = 3;
			}

			JCAxis xax = chart.getChartArea( ).getXAxis( 0 );
			JCAxis yax = chart.getChartArea( ).getYAxis( 0 );

			LabelGen lg = new LabelGen( );

			xax.setAnnotationMethod( JCAxis.POINT_LABELS );
			yax.setMin( 0 );
			yax.setLabelGenerator( lg );

			switch( diatype ) {

			case 1:
				dv.setChartType( JCChart.PLOT );
				break;
			case 2:
				dv.setChartType( JCChart.STACKING_BAR );
				break;
			default:
			case 3:
				dv.setChartType( JCChart.BAR );
				break;
			case 4:
				dv.setChartType( JCChart.BAR );
				dv.setInverted( true );
				//Swap the axes
				JCAxis tmp = xax;
				xax = yax;
				yax = tmp;
				break;
			case 5:
				dv.setChartType( JCChart.PIE );

				double yval[][] = new double[ds.num_points][ds.num_series];
				String seriesLabels[] = new String[ds.num_points];
				String pointLabels[] = new String[ds.num_series];

				for( int i = 0;i<ds.num_series;i++ ) {
					pointLabels[i] = ds.seriesLabels[i];
					for( int j = 0;j<ds.num_points;j++ ) {
						yval[j][i] = ds.yvalues[i][j];
					}
				}

				ds.yvalues = new double[ds.num_points][ds.num_series];
				for( int j = 0;j<ds.num_points;j++ ) {
					seriesLabels[j] = ds.pointLabels[j];
					for( int i = 0;i<ds.num_series;i++ ) {
						ds.yvalues[j][i] = yval[j][i];
					}
				}

				ds.seriesLabels = new String[ds.num_points];
				ds.pointLabels = new String[ds.num_series];
				int temp = ds.num_series;
				ds.num_series = ds.num_points;
				ds.num_points = temp;
				for( int i = 0;i<ds.num_series;i++ ) {
					ds.seriesLabels[i] = seriesLabels[i];
				}
				for( int j = 0;j<ds.num_points;j++ ) {
					ds.pointLabels[j] = pointLabels[j];
				}
				break;

			}

			if ( ds.num_points == 0 || ds.num_series == 0 ) {
				return ;
			}

			chart.setSize( width,height ) ;

			JCMultiColLegend mcl = new JCMultiColLegend( );
			mcl.setNumRows( 6 );
			chart.setLegend( mcl );

			xax.getTitle( ).setPlacement( JCLegend.SOUTH );
			xax.setGridVisible( true );
			yax.setGridVisible( true );
			xax.getGridStyle( ).getLineStyle( ).setColor( Color.lightGray );
			yax.getGridStyle( ).getLineStyle( ).setColor( Color.lightGray );
			xax.getGridStyle( ).getLineStyle( ).setPattern( JCLineStyle.SHORT_DASH );
			yax.getGridStyle( ).getLineStyle( ).setPattern( JCLineStyle.SHORT_DASH );

			JCAxisTitle xtitle = xax.getTitle() ;
			JCAxisTitle ytitle = yax.getTitle() ;
			xtitle.setFont(xtitle.getFont().deriveFont(Font.BOLD,12)) ;
			ytitle.setFont(ytitle.getFont().deriveFont(Font.BOLD,12)) ;
			xtitle.setText(xheader,true) ;
			ytitle.setText(yheader,true) ;			

			if( diatype==1 ) {
				xax.setMin( 0 );
				xax.setMax( ds.num_points-1 );
			}
			xax.setFont( xax.getFont( ).deriveFont( Font.PLAIN,10 ) ); 
			yax.setFont( yax.getFont( ).deriveFont( Font.PLAIN,10 ) );
			chart.getLegend( ).setFont( chart.getLegend( ).getFont( ).deriveFont( Font.PLAIN,11 ) );
			dv.setHoleValue( Double.POSITIVE_INFINITY );
			for( int i=0;i<ds.num_series;i++ ) {
				dv.getChartStyle( i ).setSymbolShape( JCSymbolStyle.NONE );
				dv.getChartStyle( i ).setLineWidth( 3 );
			}
			chart.setForeground( Color.darkGray );
			chart.setBackground( Color.white );
			chart.setLegendAnchor( SimpleChart.NORTH );
			chart.setLegendOrientation( SimpleChart.HORIZONTAL );

			if( header!=null&&header!="" ) {
				((JLabel)chart.getHeader( )).setText( header );
				((JLabel)chart.getHeader( )).setForeground( Color.darkGray );
			}
			if( footer!=null&&footer!="" ) {
				((JLabel)chart.getFooter( )).setText( footer );
				((JLabel)chart.getFooter( )).setForeground( Color.darkGray );
			}
			if( isDouble( xmax ) ) {
				xax.setMax( Double.parseDouble( xmax ) );
			}
			if( isDouble( xmin ) ) {
				xax.setMin( Double.parseDouble( xmin ) );
			}
			if( isDouble( ymax ) ) {
				yax.setMax( Double.parseDouble( ymax ) );
			}
			if( isDouble( ymin ) ) {
				yax.setMin( Double.parseDouble( ymin ) );
			}
			if( isDouble( xnum ) ) {
				xax.setNumSpacing( Double.parseDouble( xnum ) );
			}
			if( isDouble( ynum ) ) {
				yax.setNumSpacing( Double.parseDouble( ynum ) );
			}

			chart.getHeader( ).setVisible( true );
			chart.getFooter( ).setVisible( true );
			chart.getLegend( ).setVisible( true );

			//Load data
			dv.setDataSource( ds );

			frame.add( chart );
			Image image = chart.snapshot() ;

			// Encode the off screen image into a JPEG and send it to the client
			res.setContentType( "image/jpeg" );
			//			GifEncoder encoder = new GifEncoder( image, out );
			//JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(image) ;
			//jep.setQuality(0.80F,false) ;
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out) ;
			//			encoder.encode( );
			encoder.encode((BufferedImage)image) ;
		}
		finally {
			// Clean up resources

			if( frame != null ) frame.removeNotify( );
		}
	}

	public class LabelGen implements JCLabelGenerator {

		DecimalFormat df;

		public LabelGen( ) {
			df = new DecimalFormat( );
			df.setGroupingSize( 3 );
		}

		public Object makeLabel( double value,int precision ) {
			df.setMaximumFractionDigits( precision );
			return ( Object ) (String )df.format( value );
		}
	}

	public void log( String str ) {
		super.log( str );
		System.out.println( "ShowDiagram: " + str );
	}

	public boolean isDouble( String str ) {
		try {
			Double.parseDouble( str );
		}
		catch( NumberFormatException e ) {
			return false;
		}
		catch( NullPointerException e ) {
			return false;
		}
		return true;
	}

	public static String remove( String str, String rem ) {
		String line = str;
		int i;
		while( (i = line.indexOf( rem ))!=-1 ) {
			line = line.substring( 0,i )+line.substring( i+rem.length( ) );	
		}
		return line;		
	}

	public static String substringTo( String str, String to ) {
		int temp = str.indexOf( to ) ;
		if ( temp == -1 ) {
			return str ;
		}
		return str.substring( 0,temp );
	}

	public static String substringFrom( String str, String from ) {
		int temp = str.indexOf( from ) ;
		if ( temp == -1 ) {
			return "" ;
		}
		return str.substring( temp );
	}

	public class FileParser {
		public LinkedList getData( String file ) throws IOException {
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
			LinkedList data = new LinkedList( );
			while( true ) {
				String line = in.readLine( );
				if( line == null )
					break;
				line.trim( );
				if( line.startsWith( "|" ) )
					break;

				line = substringTo( line,"|" ) + remove( substringFrom( line,"|" ), " " ); //Removes spaces from line after first "|"
				line = line.replace( ',','.' );		//Replace all , with .
				data.add( line );
			}
			return data;
		}

		public Properties getProperties( String file ) throws IOException {
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
			Properties pref = new Properties( );
			while( true ) {
				String line = in.readLine( );
				if( line == null )
					break;
				StringTokenizer st = new StringTokenizer( line,"|" );
				pref.setProperty( st.nextToken( ),st.nextToken( ) );
			}
			in.close( );
			return pref;
		}
	}

	private class DataSource implements ChartDataModel,LabelledChartDataModel {
		double xvalues[];
		double yvalues[][];
		String pointLabels[];
		String seriesLabels[];
		int num_series, num_points;

		public double[] getXSeries( int index ) {
			return xvalues;
		}

		public double[] getYSeries( int index ) {
			return yvalues[index];
		}

		public int getNumSeries( ) {
			return num_series;
		}

		public String[] getPointLabels( ) {
			return pointLabels;
		}

		public String[] getSeriesLabels( ) {
			return seriesLabels;
		}

		public String getDataSourceName( ) {
			return "";
		}
	}
}

	

