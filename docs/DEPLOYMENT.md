# Syamailcoin - Complete Deployment Guide

## Server Information
- IP Address: 104.128.155.98
- GitHub: AlshenFeshiru/SyamailcoinPublic

## Pre-Deployment Checklist

- [ ] Server accessible via SSH
- [ ] Root access confirmed
- [ ] Domain syamailcoin.org purchased at Njalla
- [ ] DNS configured to 104.128.155.98

## Step-by-Step Deployment

### Phase 1: Server Preparation

```bash
ssh root@104.128.155.98
apt update && apt upgrade -y
apt install -y curl wget git vim htop
Phase 2: Clone Repository
cd /opt
rm -rf SyamailcoinPublic
git clone https://github.com/AlshenFeshiru/SyamailcoinPublic.git
cd SyamailcoinPublic
Phase 3: Run Setup
chmod +x scripts/setup.sh
./scripts/setup.sh
Phase 4: Compile
./compile.sh
mvn clean package
Phase 5: Start Services
systemctl start syamailcoin-backend
systemctl start syamailcoin-node
systemctl start nginx
Phase 6: SSL Setup (After Domain)
apt install -y certbot python3-certbot-nginx
certbot --nginx -d syamailcoin.org -d www.syamailcoin.org
Verification
curl http://104.128.155.98:8080/api/delta/status
curl http://104.128.155.98
Contact
Email: alshenfeshiru@zohomail.com
GitHub: AlshenFeshiru/SyamailcoinPublic
