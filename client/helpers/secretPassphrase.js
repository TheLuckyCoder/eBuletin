const bip39 = require("bip39");
import { encrypt, decrypt } from "eciesjs";
import { decryptJsonData, encryptJsonData } from "./general";
const STRENGTH = 256;

export const generateBip39Mnemonic = () => {
  const mnemonic = bip39.generateMnemonic(STRENGTH);
  return mnemonic;
};

export const generateSeed = (mnemonic) => {
  const seed = bip39.mnemonicToSeedSync(mnemonic);
  return seed;
};

export const generateEcdsaKeyPair = (seed) => {
  const ecdsa = require("elliptic").ec;
  const ec = new ecdsa("secp256k1");
  const keyPair = ec.keyFromPrivate(seed);
  return keyPair;
};


export const getHexPublicKey = (keyPair) => {
  const publicKey = keyPair.getPublic("hex");
  return publicKey;
};

export const getHexPrivateKey = (keyPair) => {
  const privateKey = keyPair.getPrivate("hex");
  return privateKey;
};

export const generatePublicKeyFromPrivateKey = (privateKey) => {
  const keyPair = generateEcdsaKeyPair(privateKey);
  const publicKey = getHexPublicKey(keyPair);
  return publicKey;
};

export const testGenerateBip39Mnemonic = () => {
  const mnemonic = generateBip39Mnemonic();
  const seed = generateSeed(mnemonic);
  const keyPair = generateEcdsaKeyPair(seed);
  console.log("mnemonic", mnemonic);
  console.log("seed", seed);
  console.log("publicKey", getHexPublicKey(keyPair));
  console.log("privateKey", getHexPrivateKey(keyPair));
};

export const testRecoveredKeyPair = () => {
  const mnemonic = generateBip39Mnemonic();
  const seed = generateSeed(mnemonic);
  const keyPair = generateEcdsaKeyPair(seed);
  const publicKey = getHexPublicKey(keyPair);
  const privateKey = getHexPrivateKey(keyPair);

  const data = { message: "hello world" };

  const encrypted = encryptJsonData(publicKey, data);
  const decrypted = decryptJsonData(encrypted, privateKey);
  console.log(
    "FIRST DECRIPTION",
    decrypted,
    decrypted.message === data.message
  );

  const recoveredKeyPair = generateEcdsaKeyPair(seed);
  const recoveredPublicKey = getHexPublicKey(recoveredKeyPair);
  const recoveredPrivateKey = getHexPrivateKey(recoveredKeyPair);
  const decrypted2 = decryptJsonData(encrypted, recoveredPrivateKey);

  console.log(
    "SECOND DECRIPTION",
    decrypted2,
    decrypted2.message === data.message
  );
};
