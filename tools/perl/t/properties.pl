#!/usr/bin/perl -w

use warnings ;
use strict ;

use Test::More tests => 23 ;

use lib '..' ;
use Properties ;

my @keys = (qw( 	empty 		        true
				    undefined	        unicode
				    tab 		        formfeed
				    newline 	        carriagereturn
				    backslash 	        backslash\\in\\key\\
				    equals=in=key=      backslash\\=and\\=equals\\=in\\=key\\=
				    linecontinuation ), 'space in key ' ) ;

chdir 't' unless -e 'test.properties' ;

my $p = new Properties('test.properties') ;

ok( defined $p, 'new' ) ;

is( $p->{empty}, '', 'empty' ) ;

is( $p->{true}, '1', 'true = 1' ) ;

ok( !defined($p->{undefined}), 'defined' ) ;

is( $p->{unicode}, 'A', '\u0041 => A' ) ;

is( $p->{tab}, "\t", 'tab') ;

is( $p->{newline}, "\n", 'newline') ;

is( $p->{carriagereturn}, "\r", 'carriagereturn') ;

is( $p->{formfeed}, "\f", 'formfeed') ;

is( $p->{backslash}, '\\', 'backslash') ;

is( $p->{'linecontinuation'}, 'foobar', 'linecontinuation') ;

ok( defined($p->{'backslash\\in\\key\\'}), 'backslash in key' ) ;

ok( defined($p->{'equals=in=key='}), 'equals in key' ) ;

ok( defined($p->{'space in key '}), 'space in key' ) ;

ok( defined($p->{'backslash\\=and\\=equals\\=in\\=key\\='}), 'backslash and equals in key' ) ;

is( Properties::escape_key(" ... "), "\\ ...\\ ", 'escape space' ) ;

is( Properties::escape_key("...\f..."), "...\\f...", 'escape formfeed' ) ;

is( Properties::escape_key("...\r..."), "...\\r...", 'escape carriagereturn' ) ;

is( Properties::escape_key("...\n"), "...\\n", 'escape newline' ) ;

is( Properties::escape_key("...\n ... "), "...\\n\\ \\\n\t\t...\\ ", 'escape newline with trail' ) ;

is( Properties::escape_key(" \n \r "), "\\ \\n\\ \\\n\t\t\\r\\ ", 'escape spaces, newline, and carriagereturn' ) ;

unlink '/tmp/test.properties' ;
$p->save('/tmp/test.properties') ;
ok(-f '/tmp/test.properties', 'save') ;

# Check for unwanted keys

my %copy = %$p ;

delete $copy{$_} for @keys ;

ok( (0 == keys %copy), 'unwanted keys') or diag("Unwanted keys:\n", map { "\t".Properties::escape_key($_) } keys %copy) ;

