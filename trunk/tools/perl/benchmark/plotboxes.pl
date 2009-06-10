#!/usr/bin/perl -w

use strict ;
use warnings ;

use List::Util qw( max ) ;
use GD ;
use GD::Graph::boxplot ;

my %tests ;
my @tests_order ;

foreach my $dir (@ARGV) {

    foreach my $summary (glob("$dir/*/summary")) {
        open SUM, '<', $summary or next ;
        my $test = $dir ;
        push @tests_order, $test unless $tests{$test} ;
        while (<SUM>) {
            my ( $name, $concurrency, $milliseconds_per_request,
                $requests_per_second )
            = /^(\S+)\s+(\S+)\s+(\S+)\s+(\S+)/ ;
            push @{$tests{$test}{milliseconds_per_request}}, $milliseconds_per_request ;
            push @{$tests{$test}{requests_per_second}}, $requests_per_second ;
        }
        close SUM ;
    }
}

my @x_values ;
my @msdata ;
my @reqdata ;

foreach my $test (@tests_order) {
    push @x_values, $test ;
    push @msdata, $tests{$test}{milliseconds_per_request} ;
    #push @reqdata, $tests{$test}{requests_per_second} ;
}

my $graph = GD::Graph::boxplot->new(1600,1200) ;

$graph->set(
    y1_label           => 'Milliseconds',
    y_min_value       => 190,
   # y_max_value       => 8000,
    transparent       => 0
);

$graph->set_x_axis_font(gdGiantFont);
$graph->set_y_axis_font(gdGiantFont);

my @data = (
    \@x_values,
    \@msdata,
    #\@reqdata
) ;

my $gd = $graph->plot( \@data ) or die $graph->error;

open(IMG, '>box.png') or die $!;
print IMG $gd->png;
close IMG ;
