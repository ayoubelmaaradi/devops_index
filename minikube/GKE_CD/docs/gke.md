


# CD Jenkins GKE

Give Jenkins permissions on GKE cluster 
Grant cluster role admin.

    kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value account)

Grant cluster role admin to tiller (helm..) creating service account

    kubectl create serviceaccount tiller --namespace kube-system
Creating cluster role binding

    kubectl create clusterrolebinding tiller-admin-binding --clusterrole=cluster-admin --serviceaccount=kube-system:tiller

Configure the Jenkins service account to be able to deploy to the cluster.


    kubectl create clusterrolebinding jenkins-deploy --clusterrole=cluster-admin --serviceaccount=default:cd-jenkins


Get fronend ip Loadbalacer

    export FRONTEND_SERVICE_IP=$(kubectl get -o jsonpath="{.status.loadBalancer.ingress[0].ip}" --namespace=production services gceme-frontend)


Creating a repo in cloud source code

    gcloud source repos create default


CD to Google cloud kubrenetes

    https://cloud.google.com/solutions/continuous-delivery-jenkins-kubernetes-engine


## **Devops on GCP**

    https://cloud.google.com/devops

## **GKE docs and tutorials**

    https://cloud.google.com/kubernetes-engine/docs/tutorials