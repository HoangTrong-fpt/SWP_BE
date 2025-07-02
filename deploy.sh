echo "Building app..."
./mvnw clean package

echo "Deploy files to server..."
scp -r  target/be.jar root@14.225.218.46:/var/www/be/

ssh root@14.225.218.46 <<EOF
pid=\$(sudo lsof -t -i:8080)

if [ -z "\$pid" ]; then
    echo "Start server..."
else
    echo "Restart server..."
    sudo kill -9 "\$pid"
fi
cd /var/www/be
nohup java -jar be.jar > app.log 2>&1 &
EOF
exit
echo "Done!"