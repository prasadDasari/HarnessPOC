#!/bin/bash

# Starting script execution
echo "Running script to test /hello and /api/health/heartbeat endpoints"

# Define namespace and port
NAMESPACE="default"  # Replace with your actual namespace if needed
PORT="8080"

# Dynamically fetch the release name based on the prefix "release"
RELEASE_NAME=$(helm list -n $NAMESPACE -q --filter "^release" | head -n 1)  # Fetch the first release that matches the prefix
if [ -z "$RELEASE_NAME" ]; then
  echo "No release found with the prefix 'release' in namespace $NAMESPACE."
  exit 1
fi

echo "Using release name: $RELEASE_NAME"

# Use the correct service and deployment names based on the pattern
SERVICE_NAME="${RELEASE_NAME}-hello-world-chart"
DEPLOYMENT_NAME="${RELEASE_NAME}-hello-world-chart"

# Debugging output: Get deployment details
echo "Getting deployment details..."
kubectl get deployment $DEPLOYMENT_NAME -n $NAMESPACE
kubectl describe deployment $DEPLOYMENT_NAME -n $NAMESPACE

# Port forward to expose the service locally
echo "Setting up port forward to expose service locally on port 8080..."
kubectl port-forward svc/$SERVICE_NAME $PORT:$PORT -n $NAMESPACE &  # Run port-forward in background
PORT_FORWARD_PID=$!

# Wait for the port-forward to be established
sleep 5  # Adjust as needed to ensure the port-forward is up and running

# Test the /hello endpoint using Kubernetes DNS
URL="http://localhost:$PORT/api/hello"
RESPONSE=$(curl --write-out "%{http_code}" --silent --output /dev/null "$URL")
VERSION_RESPONSE=$(curl --silent "$URL")  # Capture the version string from /hello

# Check if the response is 200 for /hello
if [ "$RESPONSE" -eq 200 ]; then
  echo "Test passed: Successfully received response from /hello endpoint."
  echo "Version information from /hello: $VERSION_RESPONSE"  # Log the version information returned by the endpoint
else
  echo "Test failed: Endpoint /hello returned status code $RESPONSE."
  # Kill the port-forward process before exiting
  kill $PORT_FORWARD_PID
  exit 1
fi

# Fetch IMAGE_TAG from deployment
IMAGE_TAG=$(kubectl get deployment $DEPLOYMENT_NAME -n $NAMESPACE -o=jsonpath='{.spec.template.spec.containers[0].env[?(@.name=="IMAGE_TAG")].value}' 2>/dev/null)

# Check if IMAGE_TAG is found
if [ -z "$IMAGE_TAG" ]; then
  echo "Warning: IMAGE_TAG not found. Skipping health check."
else
  echo "Detected IMAGE_TAG: $IMAGE_TAG"

  # Check if the deployed version is v2
  if [ "$IMAGE_TAG" == "v2" ]; then
    echo "Detected v2 deployment. Testing /api/health/heartbeat for failure..."

    # Test the /api/health/heartbeat endpoint for v2, expecting a DOWN status (503)
    URL="http://localhost:$PORT/api/health/heartbeat"
    RESPONSE=$(curl --write-out "%{http_code}" --silent --output /dev/null "$URL")

    # If the health check returns 503, it indicates failure for v2, which will trigger rollback
    if [ "$RESPONSE" -eq 503 ]; then
      echo "Test passed: /api/health/heartbeat correctly failed for v2."
      # Exit with failure to indicate rollback should be triggered
      exit 1
    else
      # If response is not 503, it's an unexpected result
      echo "Test failed: /api/health/heartbeat should fail for v2 (expected 503), but received status code $RESPONSE."
      # Kill the port-forward process before exiting
      kill $PORT_FORWARD_PID
      exit 1
    fi
  else
    echo "Detected v1 deployment. Skipping /api/health/heartbeat check as it should pass for v1."
  fi
fi

# Kill the port-forward process after completion
kill $PORT_FORWARD_PID