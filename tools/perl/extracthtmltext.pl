#!/usr/bin/perl -w

use strict ;
use warnings ;

use HTML::PullParser ;
use File::Find ;

undef $/ ;

open TAGFILE, '>', ".tags" or die "Failed to open tags file: $!\n" ;

sub tag {
    my ($filename, $tagindex) = @_ ;

    return "$filename/$tagindex" ;
}

sub output_tag {
    my ($TAGFILE, $filename, $tagindex, $chunk) = @_ ;
    return unless $chunk ;
    return if $chunk =~ m!^<[^<>]+>$! ;

    $chunk =~ s/^\s*// ;
    $chunk =~ s/\s*$// ;
    $chunk =~ s/\s+/ /;
    $chunk =~ s!\\!\\\\!g ;
    $chunk =~ s!\n!\\n!g ;

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
    return if /\.(?:out|tags)$/ ;
    
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

    open OUTFILE, '>', "$filename.out" or die "Failed to open file $filename.new: $!\n" ;

    my $tagindex = 0 ;
    my $textchunk = '' ;
    my $inside_script_element = 0 ;
    while (my $token = $htmlparser->get_token) {
	my $tagname = $token->[0] ;
	if ('script' eq lc $tagname) {
	    $inside_script_element = $token->[2] eq 'S' ;
	}
	if ($token and is_text($tagname) and !$inside_script_element) {
	    $textchunk .= $token->[1] ;
	    next ;
	}
	if ($textchunk =~ /\S/ and $textchunk !~ /^\s*(?:<[^>]+>)+\s*$/ and $textchunk !~ /\s*&nbsp;\s*/) {
	    $tagindex++ ;
	    
	    print OUTFILE '<?',tag($filepath,$tagindex),'?>' ;
	    output_tag(*TAGFILE, $filepath, $tagindex, $textchunk) ;
	} elsif ($textchunk) {
	    print OUTFILE $textchunk ;
	}
	print OUTFILE $token->[1] ;
        $textchunk = '' ;
    }

    close OUTFILE ;
}

@ARGV = '.' unless @ARGV ;
find( { wanted => \&wanted }, @ARGV) ;

close TAGFILE ;
