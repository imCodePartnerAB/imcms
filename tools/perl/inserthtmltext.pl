#!/usr/bin/perl -w

use warnings ;
use strict ;

use Cwd ;
use File::Find ;

use Properties ;

my $properties ;

sub wanted {
    $File::Find::prune = 1 if $_ eq 'CVS';
    return if -d;
    return if -B;
    return if /^\./;
    return if /\.(?:out|css|js|vbs|properties|old|new)$/;

    my $filename = $_ ;
    my $outfilename = "$filename.out" ;
    open OUTPUT, '>', $outfilename ;
    open INPUT, '<', $filename ;

    while (<INPUT>) {    
        s{(<\? (\S+) \?>)} {
            my $key = $2 ;
            exists $properties->{$key}
                ? $properties->{$key}
                : $1 ;
        }eg ;
        print OUTPUT ;
    }
    close INPUT ;
    close OUTPUT ;    

    rename $outfilename, $filename ;
}

my $currentdir = getcwd ;

@ARGV = '.' unless @ARGV ;

foreach my $directory (@ARGV) {
    chdir $currentdir ;
    chdir $directory or die $! ;
    $properties = new Properties("../insert.properties") ;
    find( { wanted => \&wanted }, '.') ;

    foreach my $property_file ('../imcms_eng.properties', '../imcms_swe.properties') {
        my $original_properties = new Properties($property_file) ;
        foreach my $key (keys %$properties) {
             delete $original_properties->{$key} ;   
        }
        $original_properties->save("$property_file.new") ;
    }
}
