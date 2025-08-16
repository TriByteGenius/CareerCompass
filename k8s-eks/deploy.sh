set -e

echo "========================================"
echo "  Simple CareerCompass EKS Deployment"
echo "========================================"

# Configuration
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-"your-dockerhub-username"}
IMAGE_TAG=${IMAGE_TAG:-"latest"}

echo "DockerHub Username: $DOCKERHUB_USERNAME"
echo "Image Tag: $IMAGE_TAG"

# Replace variables in YAML files
echo "Preparing deployment files..."
find . -name "*.yml" -exec sed -i "s/\${DOCKERHUB_USERNAME}/$DOCKERHUB_USERNAME/g" {} \;
find . -name "*.yml" -exec sed -i "s/\${IMAGE_TAG}/$IMAGE_TAG/g" {} \;

# Deploy everything
echo "Deploying to Kubernetes..."
kubectl apply -f namespace.yml
kubectl apply -f configmap.yml
kubectl apply -f postgres.yml
kubectl apply -f rabbitmq.yml
kubectl apply -f api-gateway.yml
kubectl apply -f user-service.yml
kubectl apply -f job-service.yml
kubectl apply -f userjob-service.yml
kubectl apply -f python-engine.yml
kubectl apply -f frontend.yml

echo "Deployment completed!"
echo "Check status with: kubectl get pods -n careercompass-prod"
echo "Get URLs with: kubectl get svc -n careercompass-prod"