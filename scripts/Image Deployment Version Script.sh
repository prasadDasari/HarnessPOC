#!/bin/bash

# Starting script execution
echo "Running script to test /hello and /actuator/health endpoints"

# Define namespace and port
NAMESPACE="default"
PORT="8080"

# Dynamically fetch the release name based on the prefix "release"
RELEASE_NAME=$(helm list -n $NAMESPACE -q --filter "^release" | head -n 1)  # Fetch the first release that matches the prefix
if [ -z "$RELEASE_NAME" ]; then
  echo "No release found with the prefix 'release' in namespace $NAMESPACE."
  exit 1
fi

echo "Using release name: $RELEASE_NAME"

# Fetch the service name based on the Helm release
SERVICE_NAME=$(kubectl get svc -l app.kubernetes.io/instance=$RELEASE_NAME -n $NAMESPACE -o jsonpath='{.items[0].metadata.name}')

# Check if service is found
if [ -z "$SERVICE_NAME" ]; then
  echo "Service not found for release $RELEASE_NAME in namespace $NAMESPACE."
  exit 1
fi

# Test the /hello endpoint using Kubernetes DNS
URL="http://$SERVICE_NAME.$NAMESPACE.svc.cluster.local:$PORT/api/hello"
RESPONSE=$(curl --write-out "%{http_code}" --silent --output /dev/null "$URL")

# Check if the response is 200
if [ "$RESPONSE" -eq 200 ]; then
  echo "Test passed: Successfully received response from /hello endpoint."
else
  echo "Test failed: Endpoint /hello returned status code $RESPONSE."
  exit 1
fi

# Wait for a few seconds to ensure pod is available
echo "Waiting for pod to be available..."
sleep 5

# Get the pod name using label selector for service, only for v2
POD_NAME=""
if [ "$IMAGE_TAG" == "v2" ]; then
  POD_NAME=$(kubectl get pods -l app=$SERVICE_NAME -n $NAMESPACE -o=jsonpath='{.items[0].metadata.name}')
fi

# If pod name is found for v2, proceed with the IMAGE_TAG check
if [ -z "$POD_NAME" ] && [ "$IMAGE_TAG" == "v2" ]; then
  echo "No pod found for service $SERVICE_NAME in namespace $NAMESPACE"
  exit 1
fi

# If the pod is found, retrieve the IMAGE_TAG from the pod
if [ "$IMAGE_TAG" == "v2" ]; then
  IMAGE_TAG=$(kubectl get pod $POD_NAME -n $NAMESPACE -o=jsonpath='{.spec.containers[0].env[?(@.name=="IMAGE_TAG")].value}')
fi

# Test the /actuator/health endpoint to check if the version is v2 and simulate failure
URL="http://$SERVICE_NAME.$NAMESPACE.svc.cluster.local:$PORT/actuator/health"
RESPONSE=$(curl --write-out "%{http_code}" --silent --output /dev/null "$URL")

echo "IMAGE_TAG from pod: $IMAGE_TAG"
echo "Health endpoint response code: $RESPONSE"

# If the IMAGE_TAG is v2, check the health endpoint and make sure it fails
if [ "$IMAGE_TAG" == "v2" ]; then
  if [ "$RESPONSE" -ne 200 ]; then
    echo "Test passed: /actuator/health correctly failed for v2."
  else
    echo "Test failed: /actuator/health should fail for v2, but received status code $RESPONSE."
    exit 1
  fi
else
  # For v1, we expect the /actuator/health to pass
  if [ "$RESPONSE" -eq 200 ]; then
    echo "Test passed: /actuator/health returned 200 for v1."
  else
    echo "Test failed: /actuator/health should pass for v1, but returned status code $RESPONSE."
    exit 1
  fi
fi