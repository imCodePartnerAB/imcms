#!/bin/bash

# Script for recursive file transfer to AWS S3.
#
# Arguments:
# -b | --bucket   - s3 bucket name
# -d | --dir      - uploaded dir path
# -a | --access   - s3 access key
# -s | --secret   - s3 secret key
# -u | --url      - s3 server url (endpoint)
#
# Examples of object keys:
# file   - directory/text.txt
# folder - directory/

if (( $#  < 10 )); then
	echo "Not enough arguments"
	exit
fi

while [ $# -gt 0 ] ; do
  case $1 in
    -b | --bucket) bucketName="$2" ;;
    -d | --dir) uploadDir="$2" ;;
    -a | --access) accessKey="$2" ;;
    -s | --secret) secretKey="$2" ;;
    -u | --url) urlServer="$2" ;;
  esac
  shift
done


aws configure set aws_access_key_id "$accessKey" --profile ceph
aws configure set aws_secret_access_key "$secretKey" --profile ceph
aws configure set output "json" --profile ceph
aws --profile=ceph --endpoint="$urlServer" s3api create-bucket --bucket "$bucketName"

mainFolderFilename=$(basename "$uploadDir")


echo "$(date) Start creating folders"

emptyFilename="emptyFile"
touch $emptyFilename

echo "Create folder $mainFolderFilename/"
aws --profile=ceph --endpoint="$urlServer" s3api put-object --bucket "$bucketName" --key "$mainFolderFilename/" --body $emptyFilename

nestedFolders=$(find "$uploadDir" -type d -printf "%P\n")
for dir in $nestedFolders
	do
	  echo "Create folder $mainFolderFilename/$dir/"
    	aws --profile=ceph --endpoint="$urlServer" s3api put-object --bucket "$bucketName" --key "$mainFolderFilename/$dir/" --body $emptyFilename
	done

rm $emptyFilename


echo "$(date) Start copying files"

for entry in "$uploadDir"/*
	do
    filename="$(basename "$entry")"

		if [ -d "$entry" ]; then
		    echo "$(date) Start copying files from $filename recursively"
			  aws --profile=ceph --endpoint="$urlServer" s3 cp "$entry" s3://"$bucketName/$mainFolderFilename/$filename" --recursive
		    echo "$(date) End of copying files from $filename recursively"
		else
		  aws --profile=ceph --endpoint="$urlServer" s3 cp "$uploadDir/${filename}" s3://"$bucketName/$mainFolderFilename/${filename}"
		fi

  done