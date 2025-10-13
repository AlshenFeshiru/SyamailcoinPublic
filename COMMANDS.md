# Syamailcoin - Quick Command Reference

## Service Management

```bash
# Start all services
systemctl start syamailcoin-backend syamailcoin-node postgresql nginx

# Stop all services
systemctl stop syamailcoin-backend syamailcoin-node

# Check service status
systemctl status syamailcoin-backend
systemctl status syamailcoin-node

# View logs
journalctl -u syamailcoin-backend -f
journalctl -u syamailcoin-node -f
Node Operations
# Run node manually
cd /opt/SyamailcoinPublic
./run.sh

# Node commands:
> status          # Show system status
> balance         # Show wallet balance
> delta           # Perform Delta operation
> blockrecursive  # Show all blocks
> quit            # Exit node
API Testing
# Get system status
curl http://localhost:8080/api/delta/status | jq

# Create wallet
curl -X POST http://localhost:8080/api/wallet/create | jq

# Perform Delta
curl -X POST http://localhost:8080/api/delta/perform \
  -H "Content-Type: application/json" \
  -d '{"walletAddress":"SAC..."}' | jq
Database
# Access database
sudo -u postgres psql -d syamailcoin

# Common queries:
SELECT * FROM wallets;
SELECT * FROM blocks ORDER BY block_index DESC LIMIT 10;
SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 10;
Updates
# Pull latest code
cd /opt/SyamailcoinPublic
git pull origin main

# Rebuild
./compile.sh
mvn clean package

# Restart services
systemctl restart syamailcoin-backend syamailcoin-node
Network Information
Server: 104.128.155.98
Node Port: 8333
API Port: 8080
GitHub: github.com/AlshenFeshiru/SyamailcoinPublic
For detailed documentation, see docs/ directory
