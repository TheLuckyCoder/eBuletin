import { useEffect, useState } from "react";

import { PrivateKey } from "eciesjs";
import { getAddressFromPublicKey } from "../helpers/general";

export const useKeys = () => {
  const [pubKey, setPubKey] = useState(null);
  const [privateKey, setPrivateKey] = useState(null);
  const [loadingKeys, setLoading] = useState(true);
  const [keysError, setError] = useState(null);
  const [address, setAddress] = useState(null);

  const getPublicKey = (prvKey) => {
    const key = PrivateKey.fromHex(prvKey);
    return key.publicKey.uncompressed.toString("hex");
  };

  const initKeys = async () => {
    setLoading(true);
    setError(false);
    try {
      const privateKey = window.sessionStorage.getItem("privateKey");
      if (!privateKey) {
        throw new Error("No private key found");
      }
      const publicKey = getPublicKey(privateKey);
      if (!publicKey) {
        throw new Error("No public key found");
      }
      const address = await getAddressFromPublicKey(publicKey);
      setPrivateKey(privateKey);
      setAddress(address);
      setPubKey(publicKey);
    } catch (e) {
      console.error(e);
      setError(e.message || "Something went wrong while initializing keys");
    }
    setLoading(false);
  };

  useEffect(() => {
    initKeys();
  }, []);

  return { pubKey, privateKey, loadingKeys, keysError, address };
};
