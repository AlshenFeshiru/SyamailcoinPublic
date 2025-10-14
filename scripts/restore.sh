#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: ./restore.sh /path/to/backup.tar.gz"
    exit 1
fi

BACKUP_FILE=$1

echo "Syamailcoin Restore Script"
echo "=========================="
echo "WARNING: This will overwrite current data!"
read -p "Continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Restore cancelled."
    exit 0
fi

echo "Stopping services..."
systemctl stop syamailcoin-backend syamailcoin-node

echo "Extracting backup..."
cd /opt/SyamailcoinPublic
tar -xzf $BACKUP_FILE

echo "Restoring database..."
sudo -u postgres psql -d syamailcoin -f database_*.sql

echo "Cleaning up..."
rm database_*.sql

echo "Starting services..."
systemctl start syamailcoin-backend syamailcoin-node

echo "Restore complete!"
