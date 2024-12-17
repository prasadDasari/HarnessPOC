#!/bin/bash

echo "Starting port-forwarding for hello-world-java-service..."
kubectl port-forward svc/hello-world-java-service 8080:8080 &
PF_PID=$!

# Give port-forward some time to start
sleep 5

echo "Testing API Endpoint via localhost..."

response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/hello)

if [ "$response" -ne 200 ]; then
  echo "API validation failed with status code: $response"
  kill $PF_PID
  exit 1
else
  echo "API validation successful!"
  echo "Response: Hello, World from Harness Integration!"
fi

# Clean up port-forward
kill $PF_PID


