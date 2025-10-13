#include "SAI288.h"
#include <cmath>
#include <cstring>
#include <sstream>
#include <iomanip>

const uint32_t SAI288::IV[9] = {
    0x243F6A88, 0x85A308D3, 0x13198A2E, 0x03707344,
    0xA4093822, 0x299F31D0, 0x082EFA98, 0xEC4E6C89, 0x452821E6
};

const double SAI288::GAMMA = 1.05;
const double SAI288::R = 10.0;
const double SAI288::TAU = 0.5;
const double SAI288::PHI = 0.9;

SAI288::SAI288() {
    memcpy(state, IV, sizeof(state));
}

uint32_t SAI288::rotateLeft(uint32_t value, int shift) {
    shift &= 31;
    return (value << shift) | (value >> (32 - shift));
}

uint32_t SAI288::rotateRight(uint32_t value, int shift) {
    shift &= 31;
    return (value >> shift) | (value << (32 - shift));
}

double SAI288::calculateF(int t) {
    double expGrowth = std::pow(GAMMA, static_cast<double>(t) / R);
    double weightedSum = 0.0;
    int limit = std::min(t, 8);
    for (int j = 0; j <= limit; j++) {
        weightedSum += state[j] * std::pow(PHI, j);
    }
    return expGrowth * TAU * weightedSum;
}

std::string SAI288::hash(const uint8_t* input, size_t length) {
    memcpy(state, IV, sizeof(state));
    
    const int blockSize = 72;
    int numBlocks = (length + blockSize - 1) / blockSize;
    
    for (int block = 0; block < numBlocks; block++) {
        size_t offset = block * blockSize;
        size_t len = std::min(static_cast<size_t>(blockSize), length - offset);
        uint8_t blockData[72] = {0};
        memcpy(blockData, input + offset, len);
        
        uint32_t M[18] = {0};
        for (int i = 0; i < 18; i++) {
            int idx = i * 4;
            if (idx + 3 < blockSize) {
                M[i] = (static_cast<uint32_t>(blockData[idx]) << 24) |
                       (static_cast<uint32_t>(blockData[idx + 1]) << 16) |
                       (static_cast<uint32_t>(blockData[idx + 2]) << 8) |
                       static_cast<uint32_t>(blockData[idx + 3]);
            }
        }
        
        for (int t = 0; t < 64; t++) {
            double fValue = calculateF(t);
            uint32_t fLong = static_cast<uint32_t>(fValue);
            
            uint32_t f1 = (state[(t + 1) % 9] ^ M[t % 18]) + fLong;
            f1 ^= rotateLeft(state[(t + 4) % 9], static_cast<int>(PHI * t) % 32);
            
            uint32_t f2 = (state[(t + 5) % 9] + M[static_cast<int>(t * PHI) % 18]);
            f2 ^= rotateRight(state[(t + 7) % 9], t % 29);
            
            state[t % 9] = (f1 + f2 + state[t % 9]);
        }
    }
    
    std::ostringstream result;
    for (int i = 0; i < 9; i++) {
        result << std::hex << std::setfill('0') << std::setw(8) << state[i];
    }
    return result.str().substr(0, 72);
}

std::string SAI288::hash(const std::string& input) {
    return hash(reinterpret_cast<const uint8_t*>(input.c_str()), input.length());
}
