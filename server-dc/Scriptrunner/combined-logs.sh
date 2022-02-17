#!/bin/bash

# Script to collect the data from the log that refers to attachment view and download
# Written by Nic Brough, October 2018

# Set the log file to be read, a temporary workfile and where the output should go.


NODE1_LOG="/opt/efs/confluence/sharedhome/attachments/ver003/159/223/3473409/33/61/8061283/8061284/1"
NODE2_LOG="/opt/efs/confluence/sharedhome/attachments/ver003/159/223/3473409/33/61/8061283/8061285/1"
OUT_LOG="/opt/efs/confluence/sharedhome/attachments/ver003/159/223/3473409/33/61/8061283/11370497/1"
TMP_LOG=/tmp/attachmentlogscancombined.tmp

# clean temporary file
rm $TMP_LOG

cp $OUT_LOG $TMP_LOG
cat $NODE1_LOG >> $TMP_LOG
cat $NODE2_LOG >> $TMP_LOG

rm $OUT_LOG
touch $OUT_LOG

cat $TMP_LOG | sort | uniq >> $OUT_LOG
chown confluence:confluence $OUT_LOG
rm $TMP_LOG


