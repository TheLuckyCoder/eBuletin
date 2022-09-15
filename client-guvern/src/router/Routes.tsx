import React from "react";
import { Route, Routes } from "react-router-dom";
import { Explorer, Home, Login } from "../pages";
import { ProtectedRoute } from "./ProtectedRoute";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        }
      />
      <Route
        path="/explorer"
        element={
          <ProtectedRoute>
            <Explorer />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};
