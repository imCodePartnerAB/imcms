#!/usr/bin/perl -w

use strict ;
use warnings ;

use File::Path ;
use LWP::Simple ;
use Term::ProgressBar ;

my $AB                    = '/usr/sbin/ab' ;
my $benchmark_seconds     = 20 ;
my $min_concurrency       = 1 ;
my $max_concurrency       = 7 ;
my $concurrency_increment = 2 ;
my $verbosity             = 1 ;
my $benchmark_count_per_url = 1 + ( ($max_concurrency - $min_concurrency) / $concurrency_increment ) ;

my %inputfiles ;
my $benchmark_count = 0 ;
my $dir = my $origdir = shift ;
@ARGV or die "Usage: $0 <dir> <file-with-lines-of-whitespace-separated-name-and-url-pairs> [ another-file ... ]\n" ;

while (<>) {
    my ( $name, $url ) = /^(\S+)\s+(\S+)/ ;
    get $url or die "Failed to get $url" ;
    push @{ $inputfiles{$ARGV} }, { name => $name, url => $url } ;
    $benchmark_count += $benchmark_count_per_url ;
}

my $progressbar = Term::ProgressBar->new(
    {
        name  => sprintf( 'Benchmark %3d/%-3d', 1, $benchmark_count ),
        count => $benchmark_count,
        ETA   => 'linear'
    }
) ;
my $benchmarks_run = 0 ;

foreach my $inputfile ( keys %inputfiles ) {

    $dir = "$origdir/0" ;
    while ( -e $dir ) {
        $dir =~ s!^\Q$origdir\E(?:\/(\d+))?$! "$origdir/".(($1 || 0) + 1) !e ;
    }
    mkpath $dir or die "Could not mkdir: $!\n" ;

    my @inputlines = @{ $inputfiles{$inputfile} } ;

    foreach my $line (@inputlines) {
        my ($name, $url) = ($line->{name}, $line->{url}) ;
        $progressbar->message(
            "Benchmarking $name for $benchmark_seconds seconds per benchmark."
        ) ;
        for (
            my $concurrency = $min_concurrency ;
            $concurrency <= $max_concurrency ;
            $concurrency += $concurrency_increment
          )
        {
            $progressbar->name(
                sprintf 'Benchmark %3d/%-3d',
                ( $benchmarks_run + 1 ),
                $progressbar->target
            ) ;
            $progressbar->update($benchmarks_run) ;
            sleep 1 ;
            my ($result, $mean_time_per_request, $requests_per_second, $complete_requests) =
              benchmark( $name, $dir, $url, $benchmark_seconds, $concurrency ) ;

            open OUT, '>>', "$dir/$name" or warn $! ;
            print OUT $result, "\f\n" ;
            close OUT ;

            my $summaryfile = "$dir/summary" ;
            open SUM, '>>', $summaryfile ;
            print SUM "# Name Concurrency Mean_time_per_request Requests_per_second\n" unless -s SUM ;
            print SUM
              "$name $concurrency $mean_time_per_request $requests_per_second\n"
              ;
            close SUM ;
            ++$benchmarks_run ;

            if ( $mean_time_per_request > 10000 or 0 == $requests_per_second ) {
                $progressbar->target(
                    $progressbar->target - ( $max_concurrency - $concurrency )
                ) ;
                last ;
            }
        }
    }
}
$progressbar->update($benchmarks_run) ;

sub benchmark {
    my ( $name, $dir, $url, $benchmark_seconds, $concurrency ) = @_ ;

  BENCHMARK: {
        open AB, '-|', $AB, '-q', '-t', $benchmark_seconds, '-c', $concurrency,
          '-v', $verbosity, $url
          or die "Could not execute ab: $!\n" ;
        local $/ = undef ;
        my $result = <AB> ;
        close AB or die "Could not close ab: $!\n" ;
        my ($mean_time_per_request) = $result =~ /^Time per request:\s+ (\d+\.\d+|inf).*\(mean\)/im or warn "No mean time found!" ;
        my ($requests_per_second) = $result =~ /^Requests per second:\s+(\d+\.\d+)/im or warn "No requests/s found!" ;
        my ($complete_requests) = $result =~ /^Complete requests:\s+(\d+)/im or warn "No request count found." ;
        my $message = sprintf '%3d concurrent requests: %8.2f ms/request %6.2f requests/s', $concurrency, $mean_time_per_request, $requests_per_second ;
        if ( $result =~ /results.*not.*reliable/i ) {
            $progressbar->message("Results not reliable. ($message) Retrying... ") ;
            sleep 1 ;
            redo BENCHMARK;
        }
        $progressbar->message($message) ;
        return ($result, $mean_time_per_request, $requests_per_second, $complete_requests) ;
    }
}
