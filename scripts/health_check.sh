#!/bin/bash
# Fetch the NODE_PORT dynamically and check the health endpoint

NODE_PORT=$(kubectl get svc hello-world-java-service -o jsonpath='{.spec.ports[0].nodePort}')
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}')

HEALTH_URL="http://${NODE_IP}:${NODE_PORT}/actuator/health"

echo "Checking health endpoint: ${HEALTH_URL}"

# Call the health endpoint
RESPONSE=$(curl --write-out "%{http_code}" --silent --output /dev/null "${HEALTH_URL}")

if [ "$RESPONSE" -eq 200 ]; then
    echo "Health Check Passed: Service is UP."
    exit 0
else
    echo "Health Check Failed: Service is DOWN. Rolling back..."
    exit 1
fi
