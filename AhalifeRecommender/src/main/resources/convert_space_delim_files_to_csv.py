#!/usr/bin/python
import csv

with open("aha.csv") as fin, open("aha-csv.csv", 'w') as fout:
    o=csv.writer(fout)
    for line in fin:
        o.writerow(line.split())

