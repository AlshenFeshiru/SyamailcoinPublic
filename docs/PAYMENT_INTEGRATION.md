# Payment Gateway Integration Guide

## Overview
Syamailcoin supports multiple payment methods for purchasing SAC tokens:
- USDT (Tether) - Cryptocurrency
- Mastercard - Fiat payment

## Integration Steps

### 1. Stripe Integration (for Mastercard)

#### Setup:
1. Create Stripe account at https://stripe.com
2. Get API keys from Stripe Dashboard
3. Add keys to config/application.properties

### 2. Coinbase Commerce Integration (for USDT)

#### Setup:
1. Create Coinbase Commerce account at https://commerce.coinbase.com
2. Get API key from Settings

## Price Calculation

Current SAC price determination:
1. Base minimum: 0.0002231668235294118 SAC
2. Market-driven by demand and Delter activity

## Treasury Management

Treasury wallet: SACTREASURY001

Bonus allocation (10-99 SAC random):
- Source: Remaining supply allocation
- Triggered: After VirtualNAND purchase + setup completion
- Valid until: Stage 3 (200,448 remaining)

## Support

For integration issues:
- Email: alshenfeshiru@zohomail.com
- GitHub: AlshenFeshiru/SyamailcoinPublic
