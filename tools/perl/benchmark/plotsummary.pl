#!/usr/bin/perl -w

use strict ;
use warnings ;

use List::Util qw( max ) ;
use Chart::Graph::Gnuplot qw( gnuplot ) ;

my $max_concurrency = 1 ;
my %tests ;

foreach my $dir (@ARGV) {

    open SUM, '<', "$dir/summary" or next ;

    while (<SUM>) {
        my ( $name, $concurrency, $milliseconds_per_request,
            $requests_per_second )
          = /^(\S+)\s+(\S+)\s+(\S+)\s+(\S+)/ ;
        $tests{$name}[$concurrency]->{milliseconds_per_request} =
          $milliseconds_per_request ;
        $tests{$name}[$concurrency]->{requests_per_second} =
          $requests_per_second ;

        $max_concurrency = max( $concurrency, $max_concurrency ) ;
    }
    close SUM ;

    my @milliseconds_per_request_plots ;
    my @requests_per_second_plots ;

    foreach my $name ( keys %tests ) {
        my $milliseconds_per_request_plot = [
            {
                type  => 'matrix',
                style => 'linespoints',
                title => "$name ms/req"
            }
        ] ;
        my $requests_per_second_plot = [
            {
                type  => 'matrix',
                style => 'linespoints',
                title => "$name req/s",
                axes  => 'x1y2'
            }
        ] ;

        my @tests = @{ $tests{$name} } ;
        foreach my $concurrency ( 1 .. $#tests ) {
            my $test = $tests[$concurrency] ;
            next unless $test ;
            if ( my $milliseconds_per_request =
                $test->{milliseconds_per_request} )
            {
                push @{ $milliseconds_per_request_plot->[1] },
                  [ $concurrency, $milliseconds_per_request ] ;
            }
            if ( my $requests_per_second = $test->{requests_per_second} ) {
                push @{ $requests_per_second_plot->[1] },
                  [ $concurrency, $requests_per_second ] ;
            }
        }
        push @milliseconds_per_request_plots, $milliseconds_per_request_plot ;
        push @requests_per_second_plots,      $requests_per_second_plot ;
    }

    my $extra_options = "
        set ytics border nomirror norotate
        set xtics 1
        set mytics 4
        set my2tics 5
        set grid xtics ytics y2tics mytics my2tics
    " ;

    my %options = (
        'title'         => $dir,
        'output file'   => "$dir.png",
        size            => [ 2, 2 ],
        xrange          => '[1:]',
        yrange          => '[:1000]',
        y2tics          => 'on',
        'x-axis label'  => 'concurrent requests',
        'y-axis label'  => 'milliseconds/request',
        'y2-axis label' => 'requests/second',
        extra_opts      => $extra_options,
    ) ;

    gnuplot( \%options, @milliseconds_per_request_plots,
        @requests_per_second_plots ) ;
}
