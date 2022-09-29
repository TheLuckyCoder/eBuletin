package bugs.decentralized.utils


import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.AccountAddress
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

@Component
class JwtTokenUtil : Serializable {
    fun getAddressFromToken(token: String): AccountAddress {
        return AccountAddress(getClaimFromToken(token, Claims::getSubject))
    }
    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken<Date>(token, Claims::getExpiration)
    }

    fun getAuthorityRoleFromToken(token: String): String {
        return getClaimFromToken(token) {
            this["authority"] as String
        }
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: Claims.() -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claims.claimsResolver()
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(BlockchainApplication.KEYS.privateHex)
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(accountAddress: AccountAddress, role: String): String {
        return doGenerateToken(accountAddress.value, role)
    }
    [
    {
        "blockNumber": 0,
        "timestamp": 0,
        "transactions": [
        {
            "hash": "efe77d75e7914b540838a45e98196aea8e81d9c9248c5b6fc7c03c56b22709e3",
            "sender": "0x1ad40f5934e44a710c3f9c8f8c69dfd0a94efd4c",
            "receiver": "0x1ad40f5934e44a710c3f9c8f8c69dfd0a94efd4c",
            "data": {
            "information": {
            "role": "admin"
        }
        },
            "signature": {
            "v": "1c",
            "r": "67107ba81ccdae048d7e82d2d1e0d6dcf000e8eb8468e6a6768b0c45a7dfffc8",
            "s": "672b2ade0953d65177dd10683e50daa2eff6086f6203f8bd0107f578571c75391e"
        },
            "nonce": 0
        }
        ],
        "parentHash": "GENESIS",
        "nodeAddress": "0x0",
        "stateHash": "0"
    },
    {
        "blockNumber": 1,
        "timestamp": 1664486358377,
        "transactions": [
        {
            "hash": "fab1e2e4c746317dea8cca1a3354572d4257636cb4811a2385891dd74c9a6d7e",
            "sender": "0x1ad40f5934e44a710c3f9c8f8c69dfd0a94efd4c",
            "receiver": "0x1189986f617f9520df364a7ee89674ea61eb6ab5",
            "data": {
            "information": {
            "email": "razvan.filea@gmail.com",
            "role": "government"
        }
        },
            "signature": {
            "v": "1b",
            "r": "13c6218b365dc08220b430c9c3fe1016ad9c76614e973d42d0b1000fdc82043e",
            "s": "64ecc1e9e34e22ccc88722b8b346dd49e5848fcc4b70bb090aa951f0f94fd671"
        },
            "nonce": 1
        }
        ],
        "parentHash": "4624668b0cc131415e72fcc39360f509460dae4ec9ce2d6300682303a6e03ed8",
        "nodeAddress": "0x830a188885d932ab9ee1419cf4d83d52fe695d02",
        "stateHash": "b00af9fde405632241fc4e7e3118c79bf8a2fd73d5a2cde335afc7f89b06adf0"
    },
    {
        "blockNumber": 2,
        "timestamp": 1664488131022,
        "transactions": [
        {
            "hash": "598a591cf4e1ec82eda245b1ff7f784ae8a8f49a31b54447091319f5a506169d",
            "sender": "0x3B098BB268B24C40157D681315E94586A50A446C",
            "receiver": "0x3B098BB268B24C40157D681315E94586A50A446C",
            "data": {
            "information": {
            "email": "tudor.esan@icloud.com",
            "role": "citizen"
        }
        },
            "signature": {
            "v": "1b",
            "r": "448b2103119ba550e8e50d5384427897a5b7438c65fe41d13f1a07efcacbd1e4",
            "s": "59b06fdd107e0a3b6b17246ac8f4d2fbebe3930dfab3876aee9116c3ca25c465"
        },
            "nonce": 0
        }
        ],
        "parentHash": "9f012e039a5891006ae7701dd3d3860cbb557e3b9c978687429648956f12e9f4",
        "nodeAddress": "0x830a188885d932ab9ee1419cf4d83d52fe695d02",
        "stateHash": "3392b5b23871276d83b100e5886b8472c72e7e3a3c5843b35d05c48f40bf0f00"
    },
    {
        "blockNumber": 3,
        "timestamp": 1664489330947,
        "transactions": [
        {
            "hash": "af307a32ddd6d322487141339b0be5e7476cec2dae4a91396d1954a7c23ae174",
            "sender": "0x1189986f617f9520df364a7ee89674ea61eb6ab5",
            "receiver": "0x3B098BB268B24C40157D681315E94586A50A446C",
            "data": {
            "information": {
            "idCard": {
            "cnp": "24",
            "lastName": "dsf",
            "firstName": "sdf",
            "birthLocation": "243",
            "sex": "M",
            "series": "sdfs",
            "seriesNumber": "123",
            "validity": "fdsdf",
            "issuedBy": "wer"
        }
        }
        },
            "signature": {
            "v": "1b",
            "r": "f830adc48cb267a0197b884f8aa469cbc74efe97a1e9db1f9cfc2528b7111172",
            "s": "1d2e284968711b01f788e08333c4be821cb23b84218e55747b36db6851214458"
        },
            "nonce": 0
        }
        ],
        "parentHash": "6c37058ed7b15f51104aafc18b64cd300993018c642d7eff4a06b6c67861a06f",
        "nodeAddress": "0x830a188885d932ab9ee1419cf4d83d52fe695d02",
        "stateHash": "426eb9df644bb16efc3abea15ae9e7b7ae86258fd845edb830451b7e6858c31c"
    },
    {
        "blockNumber": 4,
        "timestamp": 1664489414550,
        "transactions": [
        {
            "hash": "800996506feb0e88d4b715e0796f61eaaa3acd704f2527194489b9feaa0f5ae3",
            "sender": "0x9E5570896F5F93600F4F9B58B6C9C6DCD5047A4C",
            "receiver": "0x9E5570896F5F93600F4F9B58B6C9C6DCD5047A4C",
            "data": {
            "information": {
            "email": "tudor@tud.com",
            "role": "citizen"
        }
        },
            "signature": {
            "v": "1b",
            "r": "7f012e4b086c9cb7571588b3a3d8aedbf940ef353fbea20f082bcbadca25a7d5",
            "s": "70939585bdbd4b48f32b06fb66e1e6f4ffc0a0bb3b20636d461165aa7dfd66ae"
        },
            "nonce": 0
        }
        ],
        "parentHash": "06fb765ddea6902159970c7783463e506cfbe9fa3ca8e33725327e60ec963d84",
        "nodeAddress": "0x830a188885d932ab9ee1419cf4d83d52fe695d02",
        "stateHash": "71abfc026ecdbdfbb74cb08d59cb0065dada6723a086a67cfed0cb8031169372"
    },
    {
        "blockNumber": 5,
        "timestamp": 1664489588162,
        "transactions": [
        {
            "hash": "3b2c0ed7b4659fe190547e0f03892a8caca0c1c51be3d1e015aac2640eff9369",
            "sender": "0x1189986f617f9520df364a7ee89674ea61eb6ab5",
            "receiver": "0x9E5570896F5F93600F4F9B58B6C9C6DCD5047A4C",
            "data": {
            "information": {
            "idCard": {
            "cnp": "24",
            "lastName": "dsf",
            "firstName": "sdf",
            "birthLocation": "243",
            "sex": "M",
            "series": "sdfs",
            "seriesNumber": "123",
            "validity": "fdsdf",
            "issuedBy": "wer"
        }
        }
        },
            "signature": {
            "v": "1b",
            "r": "44fe01371b0a79c57fd92281e7ad5a7972763a530a03334b7598e98ec6b82060",
            "s": "1ce9238d6161af5a6f6c0e59d459da32bb758ef8ac37b88c617bdf0a0234c923"
        },
            "nonce": 1
        }
        ],
        "parentHash": "6d72e1342a58d5fcce423dcfad073039ca0277d72a2353896970bf4f9a2444bf",
        "nodeAddress": "0x830a188885d932ab9ee1419cf4d83d52fe695d02",
        "stateHash": "98ea09442bd8898a4c01109a76a44de3a707cc02cbef0e0ab0618775f8650353"
    }
    ]
    private fun doGenerateToken(subject: String, role: String): String {
        val claims: Claims = Jwts.claims().setSubject(subject)
        claims["authority"] = role
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(BlockchainApplication.NODE.address)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .signWith(SignatureAlgorithm.HS256, BlockchainApplication.KEYS.privateHex)
            .compact()
    }

    fun validateToken(token: String, accountAddress: AccountAddress): Boolean {
        val username = getAddressFromToken(token)
        if (isTokenExpired(token))
            return false

        return try {
            check(username == accountAddress)
            accountAddress.validate()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    companion object {
        const val ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 30
    }
}
