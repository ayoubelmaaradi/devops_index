#!/usr/bin/bash
export PROJECT_ID="lab-gcp301223"
export SA=""
export SA_EMAIL=""
export ZONE=""



if [ "$1" == "create"]; then
  echo "creating a cluster"
  gcloud container clusters create cicd-cluster --num-nodes=3 --zone europe-west1-b
fi


if [ "$1" == "delete"]; then
  echo "creating a cluster"
  gcloud container clusters delete cicd-cluster
fi


if [ -z "$SA" ]; then
  echo "Creating cluster role binding for $SA_EMAIL"
  kubectl create clusterrolebinding admin-binding \
    --clusterrole cluster-admin --user $SA_EMAIL
fi