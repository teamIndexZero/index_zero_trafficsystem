#!/bin/bash

echo "Generating Source listing includes..."
OUTPUT="docs/listings.tex"

echo "\lstset{literate=
    {˚}{{\textdegree}}1
    {¬}{{\textdegree}}1
}
" > $OUTPUT
find ../.. -name *.java | while read line; do echo "\lstinputlisting[language=Java]{$line}" >> $OUTPUT; done;

echo "Finished generating listings!"