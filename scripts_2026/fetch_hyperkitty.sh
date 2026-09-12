#!/bin/bash
# Download monthly mbox exports from HyperKitty (Mailman 3) for one list
# usage: fetch_hyperkitty.sh <list> <from yyyy-mm> <to yyyy-mm>
list=$1; from=$2; to=$3
y=${from%-*}; m=$((10#${from#*-})); ty=${to%-*}; tm=$((10#${to#*-}))
mkdir -p "hk_$list"; ok=0; empty=0
while [ $y -lt $ty ] || { [ $y -eq $ty ] && [ $m -le $tm ]; }; do
  mm=$(printf '%02d' $m); ny=$y; nm=$((m+1)); if [ $nm -gt 12 ]; then nm=1; ny=$((y+1)); fi; nmm=$(printf '%02d' $nm)
  f="hk_$list/$y-$mm.mbox.gz"
  if [ ! -s "$f" ]; then
    curl -s -o "$f" "https://mail.python.org/archives/list/$list@python.org/export/$list@python.org-$y-$mm.mbox.gz?start=$y-$mm-01&end=$ny-$nmm-01"
    if ! gzip -t "$f" 2>/dev/null; then rm -f "$f"; empty=$((empty+1)); else ok=$((ok+1)); fi
  else ok=$((ok+1)); fi
  y=$ny; m=$nm
done
echo "$list: $ok months, $empty empty/failed ($(du -sh "hk_$list" | cut -f1))"
