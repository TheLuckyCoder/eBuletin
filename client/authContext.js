import { createContext, useEffect, useState } from "react";
import { isLoggedIn } from "./helpers/auth";

export const AuthContext = createContext({
  isAuthenticated: isLoggedIn(),
  setIsAuthenticated: () => {},
  user: null,
  privateKey: null,
  setPrivateKey: () => {},
});

export const AuthProvider = ({ children }) => {
  const [privateKey, setPrivateKey] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(privateKey !== null);
  const [user, setUser] = useState(null);

  useEffect(() => {
    if (window.sessionStorage.getItem("privateKey") !== null) {
      setIsAuthenticated(true);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        setIsAuthenticated,
        privateKey,
        setPrivateKey,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
