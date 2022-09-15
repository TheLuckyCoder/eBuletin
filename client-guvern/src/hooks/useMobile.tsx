import React, { useEffect } from "react";

export const useMobile = () => {
  const [isMobile, setIsMobile] = React.useState(false);

  useEffect(() => {
    const updateWidth = () => {
      setIsMobile(window.innerWidth < 768);
    };

    window.addEventListener("resize", updateWidth);
    updateWidth();
    return () => window.removeEventListener("resize", updateWidth);
  }, []);

  return isMobile;
};
