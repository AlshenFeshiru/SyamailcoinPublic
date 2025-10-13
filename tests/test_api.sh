#!/bin/bash

API_URL="http://localhost:8080/api"

echo "Testing Syamailcoin API Endpoints"
echo "=================================="

echo ""
echo "1. Testing System Status..."
curl -s $API_URL/delta/status | jq '.'

echo ""
echo "2. Testing Wallet Creation..."
WALLET_RESPONSE=$(curl -s -X POST $API_URL/wallet/create)
echo $WALLET_RESPONSE | jq '.'
WALLET_ADDRESS=$(echo $WALLET_RESPONSE | jq -r '.address')

echo ""
echo "3. Testing Balance Check..."
curl -s $API_URL/wallet/balance/$WALLET_ADDRESS | jq '.'

echo ""
echo "4. Testing Delta Operation..."
curl -s -X POST $API_URL/delta/perform \
  -H "Content-Type: application/json" \
  -d "{\"walletAddress\":\"$WALLET_ADDRESS\"}" | jq '.'

echo ""
echo "5. Testing Balance After Delta..."
curl -s $API_URL/wallet/balance/$WALLET_ADDRESS | jq '.'

echo ""
echo "6. Testing Price Endpoint..."
curl -s $API_URL/payment/price | jq '.'

echo ""
echo "API tests complete!"
