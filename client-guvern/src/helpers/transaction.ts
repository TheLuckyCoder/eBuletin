import {ISignature, ITransaction, ITransactionData, ITransactionInformation} from "../types/transaction";

import * as secp from '@noble/secp256k1';
import {Signature} from '@noble/secp256k1';

declare type PrivKey = Uint8Array | string | bigint | number

function stringToByteArray(str: string) {
  const ret = new Uint8Array(str.length);
  for (let i = 0; i < str.length; i++) {
    ret[i] = str.charCodeAt(i);
  }
  return ret
}

function dataToJson(data: any) {
  const str = JSON.stringify(data, null, 0);
  return stringToByteArray(str)
}

function arraycopy(src: Uint8Array, srcPos: number, dst: Uint8Array, dstPos: number, length: number) {
  while (length--)
    dst[dstPos++] = src[srcPos++];
  return dst
}

function toBytesPadded(value: bigint, length: number): Uint8Array {
  const result = new Uint8Array(length)
  const bytes = stringToByteArray(value.toString(2))
  let bytesLength: number
  let srcOffset: number

  if (bytes[0] === 0) {
    bytesLength = bytes.length - 1
    srcOffset = 1
  } else {
    bytesLength = bytes.length
    srcOffset = 0
  }

  if (bytesLength > length) {
    throw Error(`Input is too large to put in byte array of size ${length}`)
  }

  const destOffset = length - bytesLength
  return arraycopy(bytes, srcOffset, result, destOffset, bytesLength)
}

function createSignatureData(signature: Signature, recId: number): ISignature {
  const headerByte = recId + 27

  // 1 header + 32 bytes for R + 32 bytes for S
  return {
    v: secp.utils.bytesToHex(Uint8Array.of(headerByte)),
    r: secp.utils.bytesToHex(toBytesPadded(signature.r, 32)),
    s: secp.utils.bytesToHex(toBytesPadded(signature.s, 32)),
  }
}

export function generatePrivateKey(): Uint8Array {
  return secp.utils.randomPrivateKey()
}

export async function createTransaction(
  privateKey: PrivKey,
  receiverAddress: string,
  information: ITransactionInformation,
  nonce: number,
): Promise<ITransaction> {
  const data: ITransactionData = {
    information
  }

  const pubKey = secp.getPublicKey(privateKey)
  const msgHash = await secp.utils.sha256(dataToJson(data))
  const [signature, redId] = await secp.sign(msgHash, privateKey, {recovered: true})
  console.log(secp.verify(signature, msgHash, pubKey))

  const secSignature = createSignatureData(Signature.fromHex(signature), redId);

  return {
    hash: secp.utils.bytesToHex(msgHash),
    sender: "",
    receiver: receiverAddress,
    data,
    signature: secSignature,
    nonce
  }
}
