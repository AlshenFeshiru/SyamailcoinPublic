#!/bin/bash

echo "Syamailcoin System Health Check"
echo "================================"

echo ""
echo "Services Status:"
systemctl is-active syamailcoin-backend && echo "Backend: OK" || echo "Backend: FAILED"
systemctl is-active syamailcoin-node && echo "Node: OK" || echo "Node: FAILED"
systemctl is-active postgresql && echo "PostgreSQL: OK" || echo "PostgreSQL: FAILED"
systemctl is-active nginx && echo "Nginx: OK" || echo "Nginx: FAILED"

echo ""
echo "Port Status:"
netstat -tulpn | grep -E ':(8080|8333|5432|80|443)'

echo ""
echo "Disk Usage:"
df -h | grep -E '(Filesystem|/opt|/var)'

echo ""
echo "Memory Usage:"
free -h

echo ""
echo "Database Status:"
sudo -u postgres psql -d syamailcoin -c "SELECT COUNT(*) as blocks FROM blocks;"

echo ""
echo "API Status:"
curl -s http://localhost:8080/api/delta/status | jq '.totalBlocks, .currentStage'

echo ""
echo "Health check complete!"
