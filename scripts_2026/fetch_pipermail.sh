#!/bin/bash
# Download pipermail monthly archives (plain text, reader-compatible) for one list
# usage: fetch_pipermail.sh <list> <from yyyy-mm> <to yyyy-mm>
list=$1; from=$2; to=$3
months=(January February March April May June July August September October November December)
y=${from%-*}; m=$((10#${from#*-})); ty=${to%-*}; tm=$((10#${to#*-}))
mkdir -p "$list"; ok=0; miss=0
while [ $y -lt $ty ] || { [ $y -eq $ty ] && [ $m -le $tm ]; }; do
  f="$y-${months[$((m-1))]}.txt"
  if [ ! -s "$list/$f" ]; then
    code=$(curl -s -o "$list/$f" -w '%{http_code}' "https://mail.python.org/pipermail/$list/$f")
    if [ "$code" != "200" ]; then rm -f "$list/$f"; miss=$((miss+1)); else ok=$((ok+1)); fi
  else ok=$((ok+1)); fi
  m=$((m+1)); if [ $m -gt 12 ]; then m=1; y=$((y+1)); fi
done
echo "$list: $ok files, $miss missing  ($(du -sh "$list" | cut -f1))"
