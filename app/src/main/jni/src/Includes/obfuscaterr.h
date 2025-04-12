#include <cstddef>
#include <cstdint>
#include <string>

namespace ay
{
    // Stronger key schedule function
    constexpr uint8_t scramble_byte(uint8_t ch, std::size_t i, uint8_t base_key)
    {
        uint8_t key = base_key;
        key ^= static_cast<uint8_t>((i * 197 + 91) ^ (key << (i % 3))) ^ 0xA5;
        return ch ^ ((key << 1) | (key >> 7));
    }

    template <std::size_t N, uint8_t BASE_KEY>
    class obfuscator
    {
    public:
        constexpr obfuscator(const char* data)
        {
            for (std::size_t i = 0; i < N; i++)
                m_data[i] = scramble_byte(static_cast<uint8_t>(data[i]), i, BASE_KEY);
        }

        constexpr const uint8_t* getData() const { return m_data; }
        constexpr std::size_t getSize() const { return N; }
        constexpr uint8_t getKey() const { return BASE_KEY; }

    private:
        uint8_t m_data[N]{};
    };

    template <std::size_t N, uint8_t BASE_KEY>
    class OBFUSCATE_data
    {
    public:
        OBFUSCATE_data(const obfuscator<N, BASE_KEY>& obf)
        {
            for (std::size_t i = 0; i < N; i++)
                m_data[i] = obf.getData()[i];
        }

        ~OBFUSCATE_data()
        {
            for (std::size_t i = 0; i < N; i++)
                m_data[i] = 0;
        }

        operator char*()
        {
            decrypt();
            return reinterpret_cast<char*>(m_data);
        }

        operator std::string()
        {
            decrypt();
            return std::string(reinterpret_cast<char*>(m_data));
        }

        void decrypt()
        {
            if (is_encrypted())
                for (std::size_t i = 0; i < N; i++)
                    m_data[i] = scramble_byte(m_data[i], i, BASE_KEY);
        }

        void encrypt()
        {
            if (!is_encrypted())
                for (std::size_t i = 0; i < N; i++)
                    m_data[i] = scramble_byte(m_data[i], i, BASE_KEY);
        }

        bool is_encrypted() const
        {
            return m_data[N - 1] != '\0';
        }

    private:
        uint8_t m_data[N];
    };

    template <std::size_t N, uint8_t BASE_KEY = 0x5A>
    constexpr auto make_obfuscator(const char(&data)[N])
    {
        return obfuscator<N, BASE_KEY>(data);
    }
}

// Public macros
#define OBFUSCATE(data) OBFUSCATE_KEY(data, 0x5A)
#define OBFUSCATE_KEY(data, key) \
    []() -> ay::OBFUSCATE_data<sizeof(data)/sizeof(data[0]), key>& { \
        constexpr auto n = sizeof(data)/sizeof(data[0]); \
        static_assert(data[n - 1] == '\0', "String must be null terminated"); \
        constexpr auto obf = ay::make_obfuscator<n, key>(data); \
        static auto storage = ay::OBFUSCATE_data<n, key>(obf); \
        return storage; \
    }()
