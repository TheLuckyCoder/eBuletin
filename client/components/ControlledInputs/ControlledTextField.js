import { TextField } from "@mui/material";
import React, { useEffect, useRef } from "react";
import { Controller } from "react-hook-form";

export function ControlledTextField({
  control,
  name,
  label,
  defaultValue,
  rules,
  type,
  ...rest
}) {
  const textFieldRef = useRef(null);

  useEffect(() => {
    // Prevents incrementing when scrolling on number text field
    const handleWheel = (e) => e.preventDefault();
    textFieldRef.current.addEventListener("wheel", handleWheel);
  }, []);

  return (
    <Controller
      control={control}
      name={name}
      defaultValue={defaultValue}
      rules={rules}
      render={({ field: { onChange, value }, fieldState: { error } }) => {
        return (
          <>
            <TextField
              fullWidth
              type={type}
              variant="outlined"
              autoComplete="off"
              label={label}
              value={value || ""}
              onChange={onChange}
              error={!!error}
              helperText={error?.message}
              ref={textFieldRef}
              {...rest}
            />
          </>
        );
      }}
    />
  );
}
ControlledTextField.defaultProps = {
  defaultValue: "",
};
