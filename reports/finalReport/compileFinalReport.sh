#!/usr/bin/env bash
./generateGitLog.sh
./generateListings.sh

pdflatex master.tex

# print listings to pdf
pdfunite master.pdf listings.pdf team_index_zero.pdf