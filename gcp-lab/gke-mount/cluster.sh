#!/usr/bin/bash

# lab-gcp-301223
export PROJECT=$(gcloud info --format='value(config.project)')
export CLUSTER="cicd-cluster"
export ZONE="europe-west1-b"
export SA="gke-admin"
# gke-admin@lab-gcp-301223.iam.gserviceaccount.com
export SA_EMAIL=${SA}@${PROJECT}.iam.gserviceaccount.com

if [[ "$1" == "create" ]]; then
  echo "creating a cluster"
  gcloud container clusters create cicd-cluster --num-nodes=3 --zone=europe-west1-b
fi


if [[ "$1" == "delete" ]]; then
  echo "creating a cluster"
  gcloud container clusters delete cicd-cluster
fi


if [[ "$1" == "config"  ]]; then
  echo "Creating cluster role binding for $SA_EMAIL"
  kubectl create clusterrolebinding admin-binding \
    --clusterrole=cluster-admin --user=$SA_EMAIL
  gcloud container clusters get-credentials cicd-cluster --zone=europe-west1-b
fi