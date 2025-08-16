#!/bin/bash

set -euo pipefail

# CareerCompass Kubernetes Deployment Script - Dev Environment
echo "Deploying CareerCompass (dev) to Kubernetes..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
	echo "kubectl is not installed. Please install kubectl first."
	exit 1
fi

# Check if we're connected to a Kubernetes cluster
if ! kubectl cluster-info &> /dev/null; then
	echo "Not connected to a Kubernetes cluster. Please configure kubectl."
	exit 1
fi

echo "Connected to Kubernetes cluster: $(kubectl config current-context)"

# Function to wait for deployment to be ready
wait_for_deployment() {
	local namespace=$1
	local deployment=$2
	echo "⏳ Waiting for $deployment to be ready..."
	kubectl wait --for=condition=available --timeout=300s deployment/$deployment -n $namespace
	if [ $? -eq 0 ]; then
		echo "$deployment is ready"
	else
		echo "$deployment failed to become ready"
		return 1
	fi
}

# Always run from the script directory so relative paths work
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Deploy in order
echo "Deploying namespace..."
kubectl apply -f namespace.yml

echo "Deploying ConfigMap..."
kubectl apply -f configmap.yml

echo "Deploying PostgreSQL instances..."
kubectl apply -f user-service-postgre.yml
kubectl apply -f job-service-postgre.yml
kubectl apply -f user-job-service-postgre.yml

echo "Waiting for all databases to be ready..."
wait_for_deployment careercompass user-postgres
wait_for_deployment careercompass job-postgres
wait_for_deployment careercompass userjob-postgres

echo "Deploying RabbitMQ..."
kubectl apply -f rabbitmq.yml
wait_for_deployment careercompass rabbitmq

echo "Deploying Python Engine..."
kubectl apply -f python-engine.yml
wait_for_deployment careercompass python-engine

echo "Deploying User Service..."
kubectl apply -f user-service.yml
wait_for_deployment careercompass user-service

echo "Deploying Job Service..."
kubectl apply -f job-service.yml
wait_for_deployment careercompass job-service

echo "Deploying UserJob Service..."
kubectl apply -f user-job-service.yml
wait_for_deployment careercompass userjob-service

echo "Deploying API Gateway..."
kubectl apply -f api-gateway.yml
wait_for_deployment careercompass api-gateway

echo "Deploying Frontend..."
kubectl apply -f frontend.yml
wait_for_deployment careercompass frontend

echo ""
echo "Deployment completed successfully!"
echo ""
echo "Checking deployment status..."
kubectl get pods -n careercompass

echo ""
echo " Database Overview:"
echo "- User Service → user-postgres:5432/userdb"
echo "- Job Service → job-postgres:5432/jobdb"
echo "- UserJob Service → userjob-postgres:5432/userjobdb"
echo ""
echo " Service URLs:"
echo "- Frontend: http://localhost:30000"
echo "- API Gateway: http://localhost:30080"
echo "- RabbitMQ Management: http://localhost:30672"
echo ""
echo " Useful commands:"
echo "- Check pods: kubectl get pods -n careercompass"
echo "- Check services: kubectl get svc -n careercompass"
echo "- View logs: kubectl logs -f deployment/<service-name> -n careercompass"
echo "- Connect to User DB: kubectl exec -it deployment/user-postgres -n careercompass -- psql -U postgres -d userdb"
echo "- Connect to Job DB: kubectl exec -it deployment/job-postgres -n careercompass -- psql -U postgres -d jobdb"
echo "- Connect to UserJob DB: kubectl exec -it deployment/userjob-postgres -n careercompass -- psql -U postgres -d userjobdb"
echo "- Delete all: kubectl delete namespace careercompass"
echo ""
echo " CareerCompass is now running on Kubernetes (dev)!"