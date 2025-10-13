#ifndef SAI288_H
#define SAI288_H

#include <cstdint>
#include <string>

class SAI288 {
private:
    static const uint32_t IV[9];
    static const double GAMMA;
    static const double R;
    static const double TAU;
    static const double PHI;
    
    uint32_t state[9];
    
    uint32_t rotateLeft(uint32_t value, int shift);
    uint32_t rotateRight(uint32_t value, int shift);
    double calculateF(int t);
    
public:
    SAI288();
    std::string hash(const uint8_t* input, size_t length);
    std::string hash(const std::string& input);
};

#endif
