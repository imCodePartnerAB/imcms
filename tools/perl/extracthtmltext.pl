#!/usr/bin/perl -w

use strict;
use warnings;

use HTML::TokeParser::Simple;
use File::Basename ;
use File::Find;
use Cwd;
use Getopt::Std ;

use Properties ;

my $tagstarttoken = '<? ';
my $tagendtoken   = ' ?>';
my $tagindexstart = 0;

my $currentdir = getcwd;

undef $/;

my %options ;

getopt('o', \%options) ;

my $properties_filename = $options{o} ;

die "No properties output file! Specify with -o <file>!\n" unless $properties_filename ;

my $properties ;

sub tag {
    my ( $filename, $tagindex ) = @_;

    return $tagstarttoken . tagname( $filename, $tagindex ) . $tagendtoken;
}

sub tagname {
    my ( $filename, $tagindex ) = @_;

    return "$filename/$tagindex";
}

sub is_text {
    my $token = shift;

    return $token->is_text
      or $token->is_tag
      and grep { lc $token->return_tag eq $_ } qw( a b i em strong br tt );
}

sub ws_and_nbsp_trim {
    $_[0] or return ( '', '' );

    my $ws_regexp = qr/((?:\s|\&[Nn][Bb][Ss][Pp]\;|\x{00a0})*)/;

    my ( $ws_before_tag, $ws_after_tag ) = ( '', '' );

    $ws_before_tag = $1 if $_[0] =~ s/^$ws_regexp//;
    $ws_after_tag  = $1 if $_[0] =~ s/$ws_regexp$//;

    return wantarray ? ( $ws_before_tag, $ws_after_tag ) : $_[0];
}

sub handle_textchunk {
    my ( $textchunk, $filepath, $tagindexref ) = @_;

    if (    $textchunk =~ /\w/
        and $textchunk !~ /^\s*(?:(?:<[^>]+>|&nbsp;)\s*)+\s*$/i )
    {
        my $tagindex = ++${$tagindexref};

        my ( $ws_before_tag, $ws_after_tag ) = ws_and_nbsp_trim($textchunk);

        print OUTFILE $ws_before_tag, tag( $filepath, $tagindex ),
          $ws_after_tag;
        $properties->{tagname($filepath, $tagindex)} = $textchunk ;
    }
    elsif ($textchunk) {
        print OUTFILE $textchunk;
    }
}

sub handle_formbuttonvalue {
    my ( $token, $filepath, $tagindexref ) = @_;

    return unless $token->is_start_tag('input');

    my $token_is_formbutton =
      grep { lc( $token->return_attr()->{type} ) eq $_ }
      qw/ button submit reset /;
    return unless $token_is_formbutton;

    my $button_text = $token->return_attr->{value};
    return unless $button_text;

    my $tagindex = ++${$tagindexref};

    my ( $ws_before_tag, $ws_after_tag ) = ws_and_nbsp_trim($button_text);

    $token->set_attr( 'value',
        $ws_before_tag . tag( $filepath, $tagindex ) . $ws_after_tag );
    $properties->{tagname($filepath, $tagindex)} = $button_text ;
}

sub handle_attributes {
    my ( $token, $filepath, $tagindexref ) = @_;

    return unless $token->is_start_tag();

    foreach my $attribute (qw( alt title )) {

        my $attributevalue = $token->return_attr->{$attribute};
        next unless $attributevalue;

        my $tagindex = ++${$tagindexref};

        my ( $ws_before_tag, $ws_after_tag ) =
          ws_and_nbsp_trim($attributevalue);

        $token->set_attr( $attribute,
            $ws_before_tag . tag( $filepath, $tagindex ) . $ws_after_tag );
        $properties->{tagname($filepath, $tagindex)} = $attributevalue ;
    }
}

sub wanted {

    my $filepath = $File::Find::name;
    my $filename = $_;
    
    $File::Find::prune = 1 if $filename eq 'CVS';
    return if -d;
    return if -B;
    return if /^\./;
    return if /\.(?:out|css|js|vbs|properties|old|new)$/;

    $filepath =~ s!^./!!;

    my $htmlparser = HTML::TokeParser::Simple->new($filename) || die $!;

    $htmlparser->xml_mode(1);
    $htmlparser->unbroken_text(1);
    $htmlparser->attr_encoded(1);

    my $outfilename = "$filename.out";
    open OUTFILE, '>', $outfilename
      or die "Failed to open file $outfilename: $!\n";

    my $tagindex  = $tagindexstart;
    my $textchunk = '';

    while ( my $token = $htmlparser->get_token ) {
        if ( is_text($token) ) {
            $textchunk .= $token->as_is;
            next;
        }

        handle_textchunk( $textchunk, $filepath, \$tagindex );
        handle_formbuttonvalue( $token, $filepath, \$tagindex );
        handle_attributes( $token, $filepath, \$tagindex );

        print OUTFILE $token->as_is;
        $textchunk = '';
    }

    handle_textchunk( $textchunk, $filepath, \$tagindex );

    close OUTFILE;
    rename $outfilename, $filename;
}

@ARGV = '.' unless @ARGV;

foreach my $filepath (@ARGV) {
    chdir $currentdir;
    $properties = new Properties() ;
    my $dir = -d $filepath ? $filepath : dirname $filepath ;
    my $findpath = -d $filepath ? '.' : basename $filepath ;
    chdir $dir or die $!;

    $properties->load($properties_filename) if -f $properties_filename && -r _ ;
    find( { wanted => \&wanted }, $findpath );
    $properties->save($properties_filename) ;
}
