#!/usr/bin/perl -w

use strict ;
use warnings ;

use File::Path ;
use LWP::Simple ;
use Term::ProgressBar ;

my $AB = '/usr/sbin/ab' ;
my $benchmark_seconds = 120 ;
my $min_concurrency = 1 ;
my $max_concurrency = 30 ;
my $concurrency_increment = 1 ;
my $verbosity = 1 ;

my $dir = '.' ;
my $previous_ARGV = '' ;
my %inputfiles ;
my $benchmark_count = 0 ;

while (<>) {
    push @{$inputfiles{$ARGV}}, $_ ;
    $benchmark_count += ($max_concurrency / $concurrency_increment);
}

my $progressbar = Term::ProgressBar->new({ 
    name => sprintf('Benchmark %3d/%-3d', 1, $benchmark_count),
    count => $benchmark_count,
    ETA => 'linear'
});
my $benchmarks_run = 0 ;

foreach my $inputfile (keys %inputfiles) {

    $dir = "$inputfile-results" ;
    while (-e $dir) {
        $dir =~ s/$|\.(\d+)$/ '.'.(($1 || 0) + 1) /e ; 
    }
    mkdir $dir or die "Could not mkdir: $!\n" ;

    my @inputlines = @{$inputfiles{$inputfile}} ;
    
    foreach my $line (@inputlines) {
        my ($name, $url) = $line =~ /^(\S+)\s+(\S+)/ ;
        unless (get $url) {
            warn "Failed to get $url";
            next ;
        }
    
        for (my $concurrency = $min_concurrency; $concurrency <= $max_concurrency; $concurrency += $concurrency_increment) {
            $progressbar->message( "Benchmarking $name with $concurrency concurrent threads for $benchmark_seconds seconds..." ) ;
            $progressbar->name( sprintf 'Benchmark %3d/%-3d',($benchmarks_run+1), $progressbar->target  ) ;
            $progressbar->update($benchmarks_run) ;
            sleep 2 ;
            my $result = benchmark($name, $dir, $url, $benchmark_seconds, $concurrency) ;
    
            open OUT, '>>', "$dir/$name" or warn $! ;
            print OUT $result, "\f\n" ;
            close OUT ;
            my ($mean_time_per_request) = $result =~ /^Time per request:\s+ (\d+\.\d+|inf).*\(mean\)/im or warn "No mean time found!" ;
            my ($requests_per_second) = $result =~ /^Requests per second:\s+(\d+\.\d+)/im or warn "No requests/s found!" ;
            my ($complete_requests) = $result =~ /^Complete requests:\s+(\d+)/im or warn "No request count found." ;
            $progressbar->message( sprintf '%.2f ms/request  %.2f requests/s', $mean_time_per_request , $requests_per_second ) ;
            
            open SUM, '>>', "$dir/summary" ;
            print SUM "$name $concurrency $mean_time_per_request $requests_per_second\n" ;
            close SUM ;
            ++$benchmarks_run ;
            
            if (0 == $requests_per_second) {
                $progressbar->target($progressbar->target - ($max_concurrency - $concurrency)) ;
                last ;
            }
        }
    }
}
$progressbar->update($benchmarks_run) ;

sub benchmark {
    my ($name, $dir, $url, $benchmark_seconds, $concurrency) = @_ ;

    BENCHMARK: {    
        open AB, '-|', $AB, '-q', '-t', $benchmark_seconds, '-c', $concurrency, '-v', $verbosity, $url or die "Could not execute ab: $!\n";
        local $/ = undef ;
        my $result = <AB> ;
        close AB or die "Could not close ab: $!\n" ;
        if ($result =~ /results.*not.*reliable/i) {
            $progressbar->message( "Results not reliable. Retrying..." ) ;
            redo BENCHMARK ;
        }
        return $result ;
    }
}
