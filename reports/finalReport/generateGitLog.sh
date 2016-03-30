#!/bin/bash
# file courtesy of amritamaz/gitlog2latex.sh, taken from https://gist.github.com/amritamaz/68d72c602a29635168ed#file-gitlog2latex-sh
# you will need a linux-like environment, wek, pandoc, tr, sed installed.

echo "Generating Git commit log..."
OUTPUT="docs/gitlog.tex"

echo "\begin{center}
\begin{longtabu} to \textwidth {|
    X[3,l]|
    X[2,c]|
    X[8,l]|}
    \hline
    \textbf{Author} & \textbf{Date} & \textbf{Message} \\\\ \\hline" > $OUTPUT

#awk '{printf "|%s\n",$0}' < gitlog1.md >> gitlog2.md

git log --reverse --pretty=format:"%an SEPARATORSEPARATOR %ad SEPARATORSEPARATOR %s" --date=short > $OUTPUT.tmp
sed -i -e 's/[&\%\$#_\{\}~\^\\]/-/g' $OUTPUT.tmp
sed -i -e 's/SEPARATORSEPARATOR/ \& /g' $OUTPUT.tmp
cat $OUTPUT.tmp | while read line; do echo $line \\\\ \\hline; done>> $OUTPUT

echo "
\caption{Git commit history}
\label{table:gitCommits}

\end{longtabu}
\end{center}" >> $OUTPUT

rm $OUTPUT.tmp*
echo "Finished generating commit log!"
