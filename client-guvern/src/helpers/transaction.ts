import {ISignature, ITransaction, ITransactionData, ITransactionInformation} from "../types/transaction";

import * as secp from '@noble/secp256k1';

function dataToJson(data: any) {
  const str = JSON.stringify(data, null, 0);
  const ret = new Uint8Array(str.length);
  for (let i = 0; i < str.length; i++) {
    ret[i] = str.charCodeAt(i);
  }
  return ret
}

/*
private fun createSignatureData(sig: ECDSASignature, publicKey: BigInteger, messageHash: ByteArray): SignatureData {
    // Now we have to work backwards to figure out the recId needed to recover the signature.
    var recId = -1
    for (i in 0..3) {
        val k = recoverFromSignature(i, sig, messageHash)
        if (k != null && k == publicKey) {
            recId = i
            break
        }
    }
    if (recId == -1) {
        throw RuntimeException(
            "Could not construct a recoverable key. Are your credentials valid?"
        )
    }
    val headerByte = recId + 27

    // 1 header + 32 bytes for R + 32 bytes for S
    val v = byteArrayOf(headerByte.toByte())
    val r = toBytesPadded(sig.r, 32)
    val s = toBytesPadded(sig.s, 32)
    return SignatureData(v, r, s)
}
 */

async function createTransaction(
  privateKey: Uint8Array,
  receiverAddress: string,
  information: ITransactionInformation
): Promise<ITransaction> {
  const data: ITransactionData = {
    information
  }

  // const privKey = secp.utils.randomPrivateKey()
  const pubKey = secp.getPublicKey(privateKey)
  const msgHash = await secp.utils.sha256(dataToJson(data))
  const signature = await secp.sign(msgHash, privateKey)
  console.log(secp.verify(signature, msgHash, pubKey))

  const secSignature: ISignature = { // TODO
    v: "",
    r: "",
    s: "",
  }

  return {
    hash: secp.utils.bytesToHex(msgHash),
    sender: "",
    receiver: receiverAddress,
    data,
    signature: secSignature,
    nonce: 0 // TODO
  }
}
