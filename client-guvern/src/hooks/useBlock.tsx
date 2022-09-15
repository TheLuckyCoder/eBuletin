import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export const useBlock = (when: boolean) => {
  const navigate = useNavigate();

  const preventNavigation = (event: PopStateEvent) => {
    console.log();
    if (when) {
      console.log("hello?");
      const goBack = window.confirm("Are you sure you want to go back?");
      if (goBack) {
        navigate(-1);
      } else {
        window.history.pushState(null, document.title, window.location.href);
      }
    }
  };

  useEffect(() => {
    if (when) {
      window.history.pushState(null, document.title, window.location.href);
      window.addEventListener("popstate", preventNavigation);
    }
    return () => {
      window.removeEventListener("popstate", preventNavigation);
    };
  }, [when]);
  return;
};
