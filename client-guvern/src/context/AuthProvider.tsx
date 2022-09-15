import { createContext, useState } from "react";
import { isLoggedIn } from "../helpers/authHelper";
interface IAuthContext {
  isAuthenticated: boolean;
  setIsAuthenticated: (isAuthenticated: boolean) => void;
  user: IUser | null;
}
export const AuthContext = createContext<IAuthContext>({
  isAuthenticated: isLoggedIn(),
  setIsAuthenticated: () => {},
  user: null,
});

interface AuthProviderProps {
  children: JSX.Element;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(isLoggedIn());
  const [user, setUser] = useState<IUser | null>(null);

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, setIsAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
};
