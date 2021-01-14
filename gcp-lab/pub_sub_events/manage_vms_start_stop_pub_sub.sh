#!/usr/bin/bash

# creating topics

gcloud pubsub topics create start-instance-event

gcloud pubsub topics create stop-instance-event

# nodejs functions to cloud functions
git clone https://github.com/GoogleCloudPlatform/nodejs-docs-samples.git

# Deploy to cloud functions

gcloud functions deploy startInstancePubSub \
    --trigger-topic start-instance-event \
    --runtime nodejs10 \
    --allow-unauthenticated

gcloud functions deploy stopInstancePubSub \
    --trigger-topic stop-instance-event \
    --runtime nodejs10 \
    --allow-unauthenticated



# Call the functions ||||| echo '{"zone":"us-west1-b", "label":"env=dev"}' | base64 = --data '{"data":"dfdkgk"}"
gcloud functions call stopInstancePubSub \
    --data '{"data":"eyJ6b25lIjoidXMtd2VzdDEtYiIsICJsYWJlbCI6ImVudj1kZXYifQo="}'




# Configure schedulers of cloud jobs

# Start job
gcloud beta scheduler jobs create pubsub startup-dev-instances \
    --schedule '0 9 * * 1-5' \
    --topic start-instance-event \
    --message-body '{"zone":"us-west1-b", "label":"env=dev"}' \
    --time-zone 'America/Los_Angeles'

# stop job
gcloud beta scheduler jobs create pubsub shutdown-dev-instances \
    --schedule '0 17 * * 1-5' \
    --topic stop-instance-event \
    --message-body '{"zone":"us-west1-b", "label":"env=dev"}' \
    --time-zone 'America/Los_Angeles'