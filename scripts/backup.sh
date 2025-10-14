#!/bin/bash

BACKUP_DIR="/opt/syamailcoin_backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/syamailcoin_backup_$DATE.tar.gz"

echo "Syamailcoin Backup Script"
echo "========================="

mkdir -p $BACKUP_DIR

echo "Backing up database..."
sudo -u postgres pg_dump syamailcoin > $BACKUP_DIR/database_$DATE.sql

echo "Backing up data files..."
cd /opt/SyamailcoinPublic
tar -czf $BACKUP_FILE \
    data/ \
    config/application.properties \
    $BACKUP_DIR/database_$DATE.sql

rm $BACKUP_DIR/database_$DATE.sql

echo "Backup created: $BACKUP_FILE"
echo "Size: $(du -h $BACKUP_FILE | cut -f1)"

echo "Cleaning old backups (keeping last 7 days)..."
find $BACKUP_DIR -name "syamailcoin_backup_*.tar.gz" -mtime +7 -delete

echo "Backup complete!"
