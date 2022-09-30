import {
  ISignature,
  ITransaction,
  ITransactionData,
  ITransactionInformation,
} from "../types/transaction";
import { Buffer } from "buffer";

import * as secp from "@noble/secp256k1";
import { Signature } from "@noble/secp256k1";

declare type PrivKey = Uint8Array | string | bigint | number;

function stringToByteArray(str: string) {
  const ret = new Uint8Array(str.length);
  for (let i = 0; i < str.length; i++) {
    ret[i] = str.charCodeAt(i);
  }
  return ret;
}

function dataToJson(data: any) {
  const str = JSON.stringify(data, null, 0);
  return stringToByteArray(str);
}

function bigIntToUInt8Array(value: bigint) {
  let hex = value.toString(16);
  console.log("hex", hex, hex.length);
  if (hex.length === 63) {
    hex = "0" + hex;
  }
  return secp.utils.hexToBytes(hex);
}

export async function signMessage(prvKey: string, message: string) {
  const msgHash = await secp.utils.sha256(stringToByteArray(message));
  console.log("hash: ", msgHash);
  const [signature, recId] = await secp.sign(msgHash, prvKey, {
    recovered: true,
  });

  console.log("signature: ", signature);

  return createSignatureData(Signature.fromHex(signature), recId);
}

function arraycopy(
  src: Uint8Array,
  srcPos: number,
  dst: Uint8Array,
  dstPos: number,
  length: number
) {
  while (length--) dst[dstPos++] = src[srcPos++];
  return dst;
}

function toBytesPadded(value: bigint, length: number): Uint8Array {
  const result = new Uint8Array(length);
  const bytes = bigIntToUInt8Array(value);
  let bytesLength: number;
  let srcOffset: number;

  if (bytes[0] === 0) {
    bytesLength = bytes.length - 1;
    srcOffset = 1;
  } else {
    bytesLength = bytes.length;
    srcOffset = 0;
  }

  if (bytesLength > length) {
    throw Error(`Input is too large to put in byte array of size ${length}`);
  }

  const destOffset = length - bytesLength;
  return arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
}

function createSignatureData(signature: Signature, recId: number): ISignature {
  const headerByte = recId + 27;

  // 1 header + 32 bytes for R + 32 bytes for S
  
  return {
    v: secp.utils.bytesToHex(Uint8Array.of(headerByte)),
    r: secp.utils.bytesToHex(toBytesPadded(signature.r, 32)),
    s: secp.utils.bytesToHex(toBytesPadded(signature.s, 32)),
  };
}

export function generatePrivateKey(): Uint8Array {
  return secp.utils.randomPrivateKey();
}

export function generatePublicKey(privateKey: PrivKey) {
  return secp.getPublicKey(privateKey, false);
}

export async function generateBlockchainAddress(
  publicKey: Uint8Array
): Promise<string> {
  const sha = await secp.utils.sha256(publicKey);
  const shaHex = secp.utils.bytesToHex(sha);
  const last20Hex = shaHex.slice(shaHex.length - 20);
  return "0x" + last20Hex.toString();
}

export const getAddressFromPublicKey = async (pub: string) => {
  // remove "04"
  let publicKey = pub;
  if (publicKey.substring(0, 2) == "04") {
    publicKey = publicKey.substring(2);
  }
  const textAsBuffer = new TextEncoder().encode(publicKey);
  const shaBuffer = await window.crypto.subtle.digest("SHA-256", textAsBuffer);
  console.log(shaBuffer);
  const hashArray = Array.from(new Uint8Array(shaBuffer));
  const digest = hashArray.map((b) => b.toString(16).padStart(2, "0")).join("");
  const last40Char = digest.substring(digest.length - 40);
  console.log(last40Char);
  return "0x" + last40Char;
};



export async function createTransaction(
  privateKey: PrivKey,
  receiverAddress: string,
  information: ITransactionInformation,
  nonce: number
): Promise<ITransaction> {
  const data: ITransactionData = {
    information,
  };

  const pubKey = secp.getPublicKey(privateKey);
  const pubKeyStr = secp.utils.bytesToHex(pubKey);
  const sender = await getAddressFromPublicKey(pubKeyStr);
  const msgHash = await secp.utils.sha256(
    stringToByteArray(
      sender + receiverAddress + JSON.stringify(data, null, 0) + nonce
    )
  );
  const [signature, redId] = await secp.sign(msgHash, privateKey, {
    recovered: true,
  });
  console.log(secp.verify(signature, msgHash, pubKey));

  const secSignature = createSignatureData(Signature.fromHex(signature), redId);

  return {
    hash: secp.utils.bytesToHex(msgHash),
    sender: sender,
    receiver: receiverAddress,
    data,
    signature: secSignature,
    nonce,
  };
}
