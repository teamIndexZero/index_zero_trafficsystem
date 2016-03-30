#!/bin/bash

OUTPUT=listings.ps

a2ps `find ../.. -name *.java ` -2 --landscape --pretty-print=java --border=no --media=letter --compact=1 --margin=12 -o listings.ps
ps2pdf listings.ps listings.pdf
