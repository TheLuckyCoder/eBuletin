import elliptic from "elliptic";

export const generateEcdsaKeyPair = (seed: string) => {
  const ecdsa = elliptic.ec;
  const ec = new ecdsa("secp256k1");
  const keyPair = ec.keyFromPrivate(seed);
  return keyPair;
};

export const generatePublicKeyFromPrivateKey = (privateKey: string) => {
  console.log("privateKey", privateKey);
  const keyPair = generateEcdsaKeyPair(privateKey);
  const publicKey = getHexPublicKey(keyPair);
  return publicKey;
};

export const getHexPublicKey = (keyPair: elliptic.ec.KeyPair) => {
  const publicKey = keyPair.getPublic("hex");
  return publicKey;
};
