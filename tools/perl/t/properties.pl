use Test::More tests => 15 ;

use Properties ;

my $p = new Properties('t/test.properties') ;

ok( defined $p, 'new' ) ;

is( $p->{empty}, '', 'empty' ) ;

ok( $p->{true} == 1, 'true = 1' ) ;

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

ok( defined($p->{'backslash\\=and\\=equals\\=in\\=key\\='}), 'backslash and equals in key' ) ;

my %copy = %$p ;

delete $copy{$_} foreach qw( true undefined unicode tab formfeed newline carriagereturn backslash ) ;

ok( (0 == keys %copy) or (print join("\n", keys %copy), "\n") and 0, 'extra keys') ;

