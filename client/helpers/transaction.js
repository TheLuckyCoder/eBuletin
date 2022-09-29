import * as secp from "@noble/secp256k1";
import { Signature } from "@noble/secp256k1";
import { getAddressFromPublicKey } from "./general";

function stringToByteArray(str) {
  const ret = new Uint8Array(str.length);
  for (let i = 0; i < str.length; i++) {
    ret[i] = str.charCodeAt(i);
  }
  return ret;
}

function bigIntToUInt8Array(value) {
  const hex = value.toString(16);
  return secp.utils.hexToBytes(hex);
}

function dataToJson(data) {
  const str = JSON.stringify(data, null, 0);
  return stringToByteArray(str);
}

function arraycopy(src, srcPos, dst, dstPos, length) {
  while (length--) dst[dstPos++] = src[srcPos++];
  return dst;
}

function toBytesPadded(value, length) {
  const result = new Uint8Array(length);
  const bytes = bigIntToUInt8Array(value);
  let bytesLength;
  let srcOffset;

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

function createSignatureData(signature, recId) {
  const headerByte = recId + 27;
  // 1 header + 32 bytes for R + 32 bytes for S
  return {
    v: secp.utils.bytesToHex(Uint8Array.of(headerByte)),
    r: secp.utils.bytesToHex(toBytesPadded(signature.r, 32)),
    s: secp.utils.bytesToHex(toBytesPadded(signature.s, 32)),
  };
}

export function generatePrivateKey() {
  return secp.utils.randomPrivateKey();
}

export function generatePublicKey(privateKey) {
  return secp.getPublicKey(privateKey, false);
}

export async function signMessage(prvKey, message) {
  const msgHash = await secp.utils.sha256(stringToByteArray(message));
  const [signature, recId] = await secp.sign(msgHash, prvKey, {
    recovered: true,
  });

  return createSignatureData(Signature.fromHex(signature).normalizeS(), recId);
}

export async function createTransaction(
  privateKey,
  receiverAddress,
  information,
  nonce = 0
) {
  const data = {
    information,
  };

  const pubKey = secp.getPublicKey(privateKey);
  const sender = await getAddressFromPublicKey(pubKey);
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
