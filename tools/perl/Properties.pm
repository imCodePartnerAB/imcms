package Properties;

use Sort::Naturally ;

sub new {
    my ( $proto, $filename ) = @_;
    my $class = ref($proto) || $proto;
    my $self  = {};

    bless $self, $class;

    $self->load($filename) if $filename;

    return $self;
}

sub save {
    my ( $self, $filename ) = @_ ;

    die "No filename." unless $filename;

    open OUTPUT, '>', $filename or die "Failed to open $filename: $!\n";

    foreach my $key ( nsort keys %{$self} ) {
        my $value = $self->{$key};

        $value =~ s!\\!\\\\!g;
        $value =~ s!\t!\\t!g;
        $value =~ s!\r!\\r!g;
        $value =~ s!\n( *)!\\n$1\\\n\t\t!g;

        print OUTPUT "$key = $value\n\n" ;
    }
    
    close OUTPUT ;

}

sub load {
    my ( $self, $filename ) = @_;

    die "No filename." unless $filename;

    open INPUT, '<', $filename or die "Failed to open $filename: $!\n";

    my $current_property = '';
    while (<INPUT>) {
        my $line_continues = /(\\+)$/ && (1 == (1 & length($1)));
        if ( my $entryline = /^\s*[^#!\s]\S*/ .. !$line_continues ) {
            chomp;
            s!^\s*!!;
            s!\\$!! if $line_continues;
            $current_property .= $_;
            next unless $entryline =~ /E0$/;
        }
        elsif (/^\s*[#!]/) {
            next;
        }

        if ($current_property) {
            ( $key, $value ) = map { unescape($_) } split /\s*(?<!\\)[=:\s]\s*/,
              $current_property, 2;
            $value = '' unless defined $value;
            $current_property = '';
            $self->{$key} = $value;
        }
    }
    close INPUT;
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

1;
