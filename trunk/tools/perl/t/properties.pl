#!/usr/bin/perl -w

use warnings ;
use strict ;

use Test::More tests => 27 ;

use lib '..' ;
use Properties ;

my @keys = (qw( 	empty 		        true
			equals			space
			colon
			undefined	        unicode
			tab 		        formfeed
			newline 	        carriagereturn
			backslash 	        backslash\\in\\key\\
			equals=in=key=      	backslash\\=and\\=equals\\=in\\=key\\=
			linecontinuation ), 	'space in key ' ) ;

my $p = new Properties(*DATA) ;

ok( defined $p, 'new' ) ;

is( $p->{empty}, '', 'empty' ) ;

is( $p->{true}, '1', 'true = 1' ) ;

is( $p->{space}, '1', 'space' ) ;

is( $p->{equals}, '1', 'equals' ) ;

is( $p->{colon}, '1', 'colon' ) ;

ok( !defined($p->{undefined}), 'defined' ) ;

is( $p->{unicode}, 'A', '\u0041 => A' ) ;

is( $p->{tab}, "\t", 'tab') ;

is( $p->{newline}, "\n", 'newline') ;

is( $p->{carriagereturn}, "\r", 'carriagereturn') ;

is( $p->{formfeed}, "\f", 'formfeed') ;

is( $p->{backslash}, '\\', 'backslash') ;

is( $p->{'linecontinuation'}, 'foobar', 'linecontinuation') ;

is($p->{'backslash\\in\\key\\'}, 'foobar', 'backslash in key' ) ;

is( $p->{'equals=in=key='}, 'foobar', 'equals in key' ) ;

is( $p->{'space in key '}, 'foobar', 'space in key' ) ;

is( $p->{'backslash\\=and\\=equals\\=in\\=key\\='}, 'foobar', 'backslash and equals in key' ) ;

is( Properties::escape_key(" ... "), "\\ ...\\ ", 'escape space' ) ;

is( Properties::escape_key("...\f..."), "...\\f...", 'escape formfeed' ) ;

is( Properties::escape_key("...\r..."), "...\\r...", 'escape carriagereturn' ) ;

is( Properties::escape_key("...\n"), "...\\n", 'escape newline' ) ;

is( Properties::escape_key("...\n ... "), "...\\n\\ \\\n\t\t...\\ ", 'escape newline with trail' ) ;

is( Properties::escape_key(" \n \r "), "\\ \\n\\ \\\n\t\t\\r\\ ", 'escape spaces, newline, and carriagereturn' ) ;

unlink '/tmp/test.properties' ;
$p->save('/tmp/test.properties') ;
ok(-f '/tmp/test.properties', 'save') ;

use Data::Dumper ;

my $tp = Properties->new('/tmp/test.properties') ;
ok(eq_hash($tp, $p), 'dogfood') or print Dumper($tp, $p) ;

# Check for unwanted keys

my %copy = %$p ;

delete $copy{$_} for @keys ;

ok( (0 == keys %copy), 'unwanted keys') or diag("Unwanted keys:\n". join "\n", map { "'".Properties::escape_key($_)."'" } keys %copy) ;
__END__
true=1

equals = 1

colon : 1

space 1

empty

unicode=\u0041

tab=\t

formfeed=\f

newline=\n

carriagereturn=\r

backslash=\\

linecontinuation=foo\
                 bar

backslash\\in\\key\\ = foobar

equals\=in\=key\= = foobar

backslash\\\=and\\\=equals\\\=in\\\=key\\\= = foobar

space\ in\ key\ =foobar
