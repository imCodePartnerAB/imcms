#!/usr/bin/perl -w

use strict ;
use warnings ;

use HTML::PullParser ;
use File::Find ;
use Cwd ;

my $currentdir = getcwd ;

undef $/ ;

sub tag {
    my ($filename, $tagindex) = @_ ;

    return "$filename/$tagindex" ;
}

sub output_tag {
    my ($TAGFILE, $filename, $tagindex, $chunk) = @_ ;
    return unless $chunk ;
    return if $chunk =~ m!^<[^<>]+>$! ;

    $chunk =~ s!\\!\\\\!g ;
    $chunk =~ s!\t!\\t!g ;
    $chunk =~ s!\r!\\r!g ;
    $chunk =~ s!\n( *)!\\n$1\\\n\t\t!g ;

    my $tag = tag($filename,$tagindex) ;
    print $TAGFILE "$tag = $chunk\n\n" ;
}

sub is_text {
    my $tagname = shift ;

    return !$tagname || grep { $tagname eq $_ } qw( a b i em strong br ) ; 
}

sub wanted {

    $File::Find::prune = 1 if $_ eq 'CVS' ;
    return if -d ;
    return if -B ;
    return if /\.(?:out|css|js|vbs|properties)$/ ;
    
    my $filename = $_ ;
    my $filepath = $File::Find::name ;
    $filepath =~ s!^./!! ;

    my $htmlparser = HTML::PullParser->new( file => $filename,
					    start => 'tagname, text, "S"',
					    end => 'tagname, text, "E"',
					    text => 'undef, text',
					    comment => '" ", text',
					    declaration => '" ", text',
					    process => '" ", text',
					    default => '" ", text'
					) || die $! ;

    my $outfilename = "$filename.out" ;
    open OUTFILE, '>', $outfilename or die "Failed to open file $outfilename: $!\n" ;

    my $tagindex = 0 ;
    my $textchunk = '' ;
    while (my $token = $htmlparser->get_token) {
	my $tagname = $token->[0] ;
	if ($token and is_text($tagname)) {
	    $textchunk .= $token->[1] ;
	    next ;
	}
	if ($textchunk =~ /\S/ and $textchunk !~ /^\s*(?:<[^>]+>\s*)+\s*$/ and $textchunk !~ /\s*(?:&nbsp;\s*)+\s*/) {
	    $tagindex++ ;

	    my $starttoken = '<? ' ;
	    my $endtoken = ' ?>' ;
	    if ($textchunk =~ s/^(\s+)//) {
		$starttoken = "$1$starttoken";
	    }
	    if ($textchunk =~ s/(\s+)$//) {
		$endtoken = "$endtoken$1" ;
	    }
	    print OUTFILE $starttoken,tag($filepath,$tagindex),$endtoken ;
	    output_tag(*TAGFILE, $filepath, $tagindex, $textchunk) ;
	} elsif ($textchunk) {
	    print OUTFILE $textchunk ;
	}
	print OUTFILE $token->[1] ;
        $textchunk = '' ;
    }

    close OUTFILE ;
    rename $outfilename, $filename ;
}

@ARGV = '.' unless @ARGV ;

foreach my $directory (@ARGV) {
    chdir $currentdir ;
    chdir $directory or die $! ;
    open TAGFILE, '>', "imcms_sv.properties" or die "Failed to open tags file: $!\n" ;
    find( { wanted => \&wanted }, '.') ;
    close TAGFILE ;
}
