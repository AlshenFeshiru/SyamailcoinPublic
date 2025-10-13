package syamailcoin

import (
    "encoding/binary"
    "fmt"
    "math"
)

var IV = []uint32{
    0x243F6A88, 0x85A308D3, 0x13198A2E, 0x03707344,
    0xA4093822, 0x299F31D0, 0x082EFA98, 0xEC4E6C89, 0x452821E6,
}

const (
    GAMMA = 1.05
    R     = 10.0
    TAU   = 0.5
    PHI   = 0.9
)

type SAI288 struct {
    state [9]uint32
}

func NewSAI288() *SAI288 {
    s := &SAI288{}
    copy(s.state[:], IV)
    return s
}

func (s *SAI288) rotateLeft(value uint32, shift int) uint32 {
    shift &= 31
    return (value << shift) | (value >> (32 - shift))
}

func (s *SAI288) rotateRight(value uint32, shift int) uint32 {
    shift &= 31
    return (value >> shift) | (value << (32 - shift))
}

func (s *SAI288) calculateF(t int) float64 {
    expGrowth := math.Pow(GAMMA, float64(t)/R)
    weightedSum := 0.0
    limit := t
    if limit > 8 {
        limit = 8
    }
    for j := 0; j <= limit; j++ {
        weightedSum += float64(s.state[j]) * math.Pow(PHI, float64(j))
    }
    return expGrowth * TAU * weightedSum
}

func (s *SAI288) Hash(input []byte) string {
    copy(s.state[:], IV)
    
    blockSize := 72
    numBlocks := (len(input) + blockSize - 1) / blockSize
    
    for block := 0; block < numBlocks; block++ {
        offset := block * blockSize
        length := blockSize
        if offset+length > len(input) {
            length = len(input) - offset
        }
        
        blockData := make([]byte, blockSize)
        copy(blockData, input[offset:offset+length])
        
        M := make([]uint32, 18)
        for i := 0; i < 18; i++ {
            idx := i * 4
            if idx+3 < blockSize {
                M[i] = binary.BigEndian.Uint32(blockData[idx : idx+4])
            }
        }
        
        for t := 0; t < 64; t++ {
            fValue := s.calculateF(t)
            fLong := uint32(fValue)
            
            f1 := (s.state[(t+1)%9] ^ M[t%18]) + fLong
            f1 ^= s.rotateLeft(s.state[(t+4)%9], int(PHI*float64(t))%32)
            
            f2 := (s.state[(t+5)%9] + M[int(float64(t)*PHI)%18])
            f2 ^= s.rotateRight(s.state[(t+7)%9], t%29)
            
            s.state[t%9] = f1 + f2 + s.state[t%9]
        }
    }
    
    result := ""
    for i := 0; i < 9; i++ {
        result += fmt.Sprintf("%08x", s.state[i])
    }
    return result[:72]
}

func Hash(input string) string {
    hasher := NewSAI288()
    return hasher.Hash([]byte(input))
}
