#!/usr/bin/perl -w

use strict ;

undef $/ ;

while (<>) {

    while (/(.)(.)/gs) {
	print $1 ;
    }
    
}
