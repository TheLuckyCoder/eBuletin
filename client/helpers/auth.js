export const isLoggedIn = () => {
  return false;
};

export const deleteKeys = () => {
  window.localStorage.removeItem("encryptedPrivateKey");
  window.localStorage.removeItem("publicKey");
  window.sessionStorage.removeItem("privateKey");
};

