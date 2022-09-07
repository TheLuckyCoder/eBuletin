import { createContext, useState } from "react";
import { isLoggedIn } from "./helpers/auth";

export const AuthContext = createContext({
  isAuthenticated: isLoggedIn(),
  setIsAuthenticated: () => {},
  user: null,
});

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(isLoggedIn());
  const [user, setUser] = useState(null);

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, setIsAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
};
