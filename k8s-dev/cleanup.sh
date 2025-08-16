#!/bin/bash

# CareerCompass Kubernetes Cleanup Script
echo " Cleaning up CareerCompass Microservices from Kubernetes..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo " kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check if namespace exists
if ! kubectl get namespace careercompass &> /dev/null; then
    echo "ℹ CareerCompass namespace doesn't exist. Nothing to clean up."
    exit 0
fi

echo "🗑 Deleting CareerCompass namespace and all resources..."
kubectl delete namespace careercompass

echo " Waiting for namespace to be fully deleted..."
while kubectl get namespace careercompass &> /dev/null; do
    echo " Still waiting for namespace deletion..."
    sleep 5
done

echo " CareerCompass has been completely removed from Kubernetes!"
echo ""
echo " To redeploy, run: ./deploy.sh"