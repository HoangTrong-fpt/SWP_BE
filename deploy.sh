#!/bin/bash

echo "Cleaning & Building project..."
./mvnw clean package -DskipTests || { echo "Build failed"; exit 1; }

echo "Checking build output..."
ls -lh target/be.jar

echo "Deploying to server..."
scp target/be.jar root@14.225.218.46:/var/www/be/

echo "Restarting remote server app..."
ssh root@14.225.218.46 <<'EOF'
pid=$(lsof -t -i:8080)
if [ -n "$pid" ]; then
    echo "Killing process $pid"
    kill -9 $pid
else
    echo "No running process found on port 8080"
fi
cd /var/www/be
nohup java -jar be.jar > app.log 2>&1 &
echo "App restarted"
EOF

echo "Deployment complete."
