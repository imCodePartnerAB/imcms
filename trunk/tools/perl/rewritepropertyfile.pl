#!/usr/bin/perl -w

use strict ;
use warnings ;

use Properties ;

foreach my $filename (@ARGV) {
    my $infilename = $filename ;
    my $outfilename = $filename ;
    my $p = new Properties($infilename) ;
    
    $p->save($outfilename) ;
}
