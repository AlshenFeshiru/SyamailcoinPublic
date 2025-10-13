#!/bin/bash

echo "========================================="
echo "SYAMAILCOIN COMPLETE SETUP"
echo "Server IP: 104.128.155.98"
echo "GitHub: AlshenFeshiru/SyamailcoinPublic"
echo "========================================="

echo "Step 1: Installing system dependencies..."
apt update
apt install -y openjdk-17-jdk maven postgresql postgresql-contrib nginx git build-essential golang-go rustc

echo "Step 2: Setting up PostgreSQL..."
systemctl start postgresql
systemctl enable postgresql

sudo -u postgres psql -f scripts/init_database.sql

echo "Step 3: Compiling SAI-288 implementations..."
./compile.sh

echo "Step 4: Installing Node.js for WebRTC..."
curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
apt install -y nodejs

echo "Step 5: Building Java Spring Boot application..."
mvn clean package -DskipTests

echo "Step 6: Configuring Nginx..."
cp config/nginx.conf /etc/nginx/sites-available/syamailcoin
ln -sf /etc/nginx/sites-available/syamailcoin /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx

echo "Step 7: Creating systemd services..."
cat > /etc/systemd/system/syamailcoin-node.service << EOL
[Unit]
Description=Syamailcoin Node
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/SyamailcoinPublic
ExecStart=/opt/SyamailcoinPublic/run.sh
Restart=always

[Install]
WantedBy=multi-user.target
EOL

cat > /etc/systemd/system/syamailcoin-backend.service << EOL
[Unit]
Description=Syamailcoin Spring Boot Backend
After=network.target postgresql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/SyamailcoinPublic
ExecStart=/usr/bin/java -jar target/syamailcoin-node-1.0.0.jar --spring.config.location=config/application.properties
Restart=always

[Install]
WantedBy=multi-user.target
EOL

systemctl daemon-reload
systemctl enable syamailcoin-node
systemctl enable syamailcoin-backend

echo "Step 8: Setting up SSL (requires domain)..."
echo "After domain is configured, run: certbot --nginx -d syamailcoin.org -d www.syamailcoin.org"

echo ""
echo "========================================="
echo "SETUP COMPLETE!"
echo "========================================="
echo ""
echo "Server Information:"
echo "- IP Address: 104.128.155.98"
echo "- GitHub Repo: github.com/AlshenFeshiru/SyamailcoinPublic"
echo ""
echo "Next steps:"
echo "1. Configure domain DNS to point to 104.128.155.98"
echo "2. Run SSL setup: certbot --nginx -d syamailcoin.org"
echo "3. Start services:"
echo "   systemctl start syamailcoin-backend"
echo "   systemctl start syamailcoin-node"
echo ""
echo "Access URLs:"
echo "- Website: http://104.128.155.98"
echo "- API: http://104.128.155.98:8080/api"
echo "- Node: Port 8333"
echo ""
echo "After domain configured:"
echo "- Website: https://syamailcoin.org"
echo "- API: https://syamailcoin.org/api"
echo ""
