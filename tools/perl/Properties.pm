package Properties;

use warnings ;
use strict ;

use Sort::Naturally ;
use Scalar::Util 'openhandle';

sub new {
    my ( $proto, $filename ) = @_;
    my $class = ref($proto) || $proto;
    my $self  = {};

    bless $self, $class;

    $self->load($filename) if $filename;

    return $self;
}

sub save {
    my ( $self, $file ) = @_ ;

    die "No file or filehandle." unless $file ;

    my $opened ;
    my $fh ;
    if (!openhandle($file)) {
        open $fh, '>', $file or die "Failed to open $file: $!\n";
        $opened = 1 ;
    } else {
        $fh = $file ;   
    }

    foreach my $key ( nsort keys %{$self} ) {
        my $value = $self->{$key};
        next unless defined $value ;

        print $fh escape_key($key),' = ',escape_value($value),"\n\n" ;
    }
    
    close $fh if $opened ;
}

sub load {
    my ( $self, $file ) = @_;

    die "No filename." unless $file;

    my $opened ;
    my $fh ;
    if (!openhandle($file)) {
        open $fh, '<', $file or die "Failed to open $file: $!\n";
        $opened = 1 ;
    } else {
        $fh = $file ;
    }

    my $current_property = '';
    while (<$fh>) {
        my $line_continues = /(\\+)$/ && (1 & length($1));
        if ( my $entryline = /^\s*[^#!\s]\S*/ .. !$line_continues ) {
            chomp;
            s!^\s*!!;
            s!\\$!! if $line_continues;
            $current_property .= $_;
            next unless $entryline =~ /E0$/;
        } elsif (/^\s*[#!]/) {
            next;
        }

        if ($current_property) {
            my ($key, $value) = ($current_property, '') ;
            while ($current_property =~ /(\\*)[=:\s]/g) {
                if (!(length($1) & 1)) {
                    my $pos = pos($current_property) ;
                    $key = unescape(substr($current_property,0,$pos-1)) ;
                    $value = unescape(substr($current_property,$pos)) ;
                    last ;
                }
            }
            $self->{$key} = $value;
            $current_property = '';
        }
    }
    close $fh if $opened ;
}

sub unescape {
    local $_ = shift;

    my %escapes = (
        f => "\f",
        n => "\n",
        r => "\r",
        t => "\t",
    );

    s{\\(u(\d{4})|.)}
    { 
	    my $replacement = $1 ;
	    if ($2) {
	        $replacement = (chr hex $2) ;
    	} else {
	        $replacement = $escapes{$1} if exists $escapes{$1} ;
	    }
	    $replacement ;
    }seg;

    return $_;
}

sub escape_key {
    local $_ = escape_value(shift) ;
    
    s!([=:])!\\$1!g ;
    s/(?<!^\\) /\\ /g ;

    return $_ ;    
}

sub escape_value {
    local $_ = shift ;

    s!\\!\\!g ;
    s!\f!\\f!g ;
    s!\r!\\r!g ;
    s!\t!\\t!g ;
    s!\A !\\ ! ;
    s!([\x00-\x09\x0b-\x1f\x7f-\x{ffff}])!sprintf('\\u%04x',ord $1)!ge ;
    s/\n(\s*)(?=(\S?))/ $2 ? "\\n$1\\\n\t\t" : "\\n" /ge and s/(?!<\s)\\\n\t\t\z// ;
    s/^(\s*)([#!])/$1\\$2/mg ;

    return $_ ;
}

1;
