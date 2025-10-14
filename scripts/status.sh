#!/bin/bash

echo "==========================================="
echo "SYAMAILCOIN SYSTEM STATUS"
echo "Server: 104.128.155.98"
echo "==========================================="

echo ""
echo "Services:"
echo "---------"
services=("syamailcoin-backend" "syamailcoin-node" "postgresql" "nginx")
for service in "${services[@]}"; do
    if systemctl is-active --quiet $service; then
        echo "✓ $service: RUNNING"
    else
        echo "✗ $service: STOPPED"
    fi
done

echo ""
echo "System Resources:"
echo "-----------------"
echo "CPU Usage: $(top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print 100 - $1"%"}')"
echo "Memory: $(free -h | awk '/^Mem:/ {print $3 " / " $2}')"
echo "Disk: $(df -h / | awk 'NR==2 {print $3 " / " $2 " (" $5 " used)"}')"

echo ""
echo "Network:"
echo "--------"
echo "Port 8080 (API): $(lsof -i :8080 > /dev/null 2>&1 && echo "LISTENING" || echo "CLOSED")"
echo "Port 8333 (Node): $(lsof -i :8333 > /dev/null 2>&1 && echo "LISTENING" || echo "CLOSED")"
echo "Port 80 (HTTP): $(lsof -i :80 > /dev/null 2>&1 && echo "LISTENING" || echo "CLOSED")"

echo ""
echo "Syamailcoin Stats:"
echo "------------------"
STATS=$(curl -s http://localhost:8080/api/delta/status 2>/dev/null)
if [ $? -eq 0 ]; then
    echo "Total Blocks: $(echo $STATS | jq -r '.totalBlocks // "N/A"')"
    echo "Current Stage: $(echo $STATS | jq -r '.currentStage // "N/A"')"
    echo "Stage Remaining: $(echo $STATS | jq -r '.stageRemaining // "N/A"') SAC"
    echo "Total Remaining: $(echo $STATS | jq -r '.totalRemaining // "N/A"') SAC"
else
    echo "Unable to fetch stats (API not responding)"
fi

echo ""
echo "Database:"
echo "---------"
DB_COUNT=$(sudo -u postgres psql -d syamailcoin -t -c "SELECT COUNT(*) FROM blocks;" 2>/dev/null | tr -d ' ')
echo "Blocks in DB: ${DB_COUNT:-N/A}"

echo ""
echo "==========================================="
