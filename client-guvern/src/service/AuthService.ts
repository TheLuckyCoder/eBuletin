import axios from "../axiosConfig";
import { serverUrl } from "./general";

export const signIn = (username: string, password: string) => {
  return axios.post(serverUrl() + "/auth/login", { username, password });
};

export const signUp = (data: RegisterInput) => {
  return axios.post(serverUrl() + "/auth/signup", data);
};
