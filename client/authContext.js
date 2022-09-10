import { createContext, useState } from "react";
import { isLoggedIn } from "./helpers/auth";

export const AuthContext = createContext({
  isAuthenticated: isLoggedIn(),
  setIsAuthenticated: () => {},
  user: null,
  privateKey: null,
  setPrivateKey: () => {},
});

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(isLoggedIn());
  const [privateKey, setPrivateKey] = useState(null);
  const [user, setUser] = useState(null);

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
