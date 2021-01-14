#!/usr/bin/bash

ACTION=$1

LISTVMS=$(gcloud compute instances list | grep ^gke | awk '{ print $1}')



for item in $LISTVMS; do
  echo $item
  gcloud compute instances $ACTION $item
done