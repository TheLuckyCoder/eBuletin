import { useState } from "react";

export const useKeys = () => {
  const [pubKey, setPubKey] = useState(
    "042b5e6991a99b37d8cbe752e53a13190615487834d7365045ed2acf5b637ea94940a326647d51709e8d0e71079393d2cc5815d02f48ff184271e6fa3897d3758c"
  );
  const [privateKey, setPrivateKey] = useState(
    "1bd963d71f8605b8fa33d3b1861e650d4525c7f51bd38b1240348ab50cfc13d0"
  );
  return { pubKey, privateKey };
};
