#!/usr/bin/perl -w

use warnings ;
use strict ;

use Sort::Naturally ;

my %properties ;

my $current_property = '' ;
while (<>) {
    if (my $propertyline = /^\s*[^#!\s]\S*/ .. !/\\$/) {
        $current_property .= $_ ;
        next unless $propertyline =~ /E0$/ ;
    } elsif (/^\s*[#!]/) {
        next ;
    }
    
    if ($current_property) {
        my ($key, $value) = split /\s*[=:\s]\s*/, $current_property, 2 ;
        $current_property = '' ;
        $properties{$key} = $value ;
    }
}

foreach my $key (sort { ncmp($a, $b) } keys %properties) {
    print "$key = $properties{$key}\n" ;
}
