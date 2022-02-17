#!/bin/bash

# Script to collect the data from the log that refers to attachment view and download
# Written by Nic Brough, October 2018

# Set the log file to be read, a temporary workfile and where the output should go.

APP_LOG="/opt/confluence/home/logs/atlassian-confluence.log"
OUT_LOG="/opt/efs/confluence/sharedhome/attachments/ver003/159/223/3473409/33/61/8061283/8061284/1"
TMP_LOG=/tmp/attachmentlogscan1.tmp

# clean temporary file
rm $TMP_LOG

cp $OUT_LOG $TMP_LOG

# Search for the line the plugin outputs and strip stuff we don't need from it
grep "onAttachmentViewEvent ~:~Attachment view at" $APP_LOG | awk -F"~:~" ' {print $3,","$4,","$5,","$6,","$7,","$8}' >> $TMP_LOG

# append data to the results file.  Note that the logger captures only the minute of download.  This is because an attachment download
# can fire between 1 and 4 times, so de-duplicating the events by using the same minute is an attempt to remove repeated recrods
rm $OUT_LOG
touch $OUT_LOG

cat $TMP_LOG | sort | uniq >> $OUT_LOG
chown confluence:confluence $OUT_LOG
rm $TMP_LOG


