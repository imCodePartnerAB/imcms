#!/usr/bin/perl -w

use strict ;
use warnings ;

use List::Util qw( max ) ;
use GD::Graph::boxplot ;

my %tests ;

unless (@ARGV) {
    opendir CWD, '.' ;
    @ARGV = grep { -d } readdir CWD ;
    closedir CWD ;
}

foreach my $dir (@ARGV) {

    open SUM, '<', "$dir/summary" or next ;
    (my $test = $dir) =~ s!-\d+$!! ;
    while (<SUM>) {
        my ( $name, $concurrency, $milliseconds_per_request,
            $requests_per_second )
          = /^(\S+)\s+(\S+)\s+(\S+)\s+(\S+)/ ;
        push @{$tests{$test}{milliseconds_per_request}}, $milliseconds_per_request ;
        push @{$tests{$test}{requests_per_second}}, $requests_per_second ;
    }
    close SUM ;
}

my @x_values ;
my @msdata ;
my @reqdata ;

foreach my $test (sort keys %tests) {
    push @x_values, $test ;
    push @msdata, $tests{$test}{milliseconds_per_request} ;
    #push @reqdata, $tests{$test}{requests_per_second} ;
}

my $graph = GD::Graph::boxplot->new(1600,1200) ;

$graph->set(
    y1_label           => 'Milliseconds',
    y_min_value       => 200,
    two_axes          => 1,
    transparent       => 0
);

my @data = (
    \@x_values,
    \@msdata,
    #\@reqdata
) ;

my $gd = $graph->plot( \@data ) or die $graph->error;

open(IMG, '>box.png') or die $!;
print IMG $gd->png;
close IMG ;
