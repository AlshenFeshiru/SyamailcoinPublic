const IV: [u32; 9] = [
    0x243F6A88, 0x85A308D3, 0x13198A2E, 0x03707344,
    0xA4093822, 0x299F31D0, 0x082EFA98, 0xEC4E6C89, 0x452821E6,
];

const GAMMA: f64 = 1.05;
const R: f64 = 10.0;
const TAU: f64 = 0.5;
const PHI: f64 = 0.9;

pub struct SAI288 {
    state: [u32; 9],
}

impl SAI288 {
    pub fn new() -> Self {
        SAI288 { state: IV }
    }
    
    fn rotate_left(value: u32, shift: u32) -> u32 {
        let shift = shift & 31;
        value.rotate_left(shift)
    }
    
    fn rotate_right(value: u32, shift: u32) -> u32 {
        let shift = shift & 31;
        value.rotate_right(shift)
    }
    
    fn calculate_f(&self, t: usize) -> f64 {
        let exp_growth = GAMMA.powf(t as f64 / R);
        let mut weighted_sum = 0.0;
        let limit = if t > 8 { 8 } else { t };
        for j in 0..=limit {
            weighted_sum += self.state[j] as f64 * PHI.powi(j as i32);
        }
        exp_growth * TAU * weighted_sum
    }
    
    pub fn hash(&mut self, input: &[u8]) -> String {
        self.state = IV;
        
        let block_size = 72;
        let num_blocks = (input.len() + block_size - 1) / block_size;
        
        for block in 0..num_blocks {
            let offset = block * block_size;
            let length = std::cmp::min(block_size, input.len() - offset);
            let mut block_data = [0u8; 72];
            block_data[..length].copy_from_slice(&input[offset..offset + length]);
            
            let mut m = [0u32; 18];
            for i in 0..18 {
                let idx = i * 4;
                if idx + 3 < block_size {
                    m[i] = u32::from_be_bytes([
                        block_data[idx],
                        block_data[idx + 1],
                        block_data[idx + 2],
                        block_data[idx + 3],
                    ]);
                }
            }
            
            for t in 0..64 {
                let f_value = self.calculate_f(t);
                let f_long = f_value as u32;
                
                let mut f1 = self.state[(t + 1) % 9] ^ m[t % 18];
                f1 = f1.wrapping_add(f_long);
                f1 ^= Self::rotate_left(self.state[(t + 4) % 9], ((PHI * t as f64) as u32) % 32);
                
                let mut f2 = self.state[(t + 5) % 9].wrapping_add(m[((t as f64 * PHI) as usize) % 18]);
                f2 ^= Self::rotate_right(self.state[(t + 7) % 9], (t % 29) as u32);
                
                self.state[t % 9] = f1.wrapping_add(f2).wrapping_add(self.state[t % 9]);
            }
        }
        
        let mut result = String::new();
        for i in 0..9 {
            result.push_str(&format!("{:08x}", self.state[i]));
        }
        result[..72].to_string()
    }
    
    pub fn hash_string(input: &str) -> String {
        let mut hasher = SAI288::new();
        hasher.hash(input.as_bytes())
    }
}
