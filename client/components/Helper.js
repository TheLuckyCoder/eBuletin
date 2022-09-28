import { Tooltip } from "@mui/material";
import React from "react";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";

export const Helper = ({ text, rest }) => {
  return (
    <Tooltip {...rest} title={text}>
      <HelpOutlineIcon fontSize="small" />
    </Tooltip>
  );
};
