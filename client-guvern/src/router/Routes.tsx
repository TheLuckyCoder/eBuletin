import React from "react";
import {Route, Routes} from "react-router-dom";
import {Explorer, Home, Login, IdCardForm, DriverLicenseForm, MedicalCardForm} from "../pages";
import {ProtectedRoute} from "./ProtectedRoute";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login/>}/>
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Home/>
          </ProtectedRoute>
        }
      />
      <Route
        path="/explorer"
        element={
          <ProtectedRoute>
            <Explorer/>
          </ProtectedRoute>
        }
      />
      <Route
        path="/create/id_card"
        element={
          <ProtectedRoute>
            <IdCardForm/>
          </ProtectedRoute>
        }
      />
      <Route
        path="/create/driver_license"
        element={
          <ProtectedRoute>
            <DriverLicenseForm/>
          </ProtectedRoute>
        }
      />
      <Route
        path="/create/medical_card"
        element={
          <ProtectedRoute>
            <MedicalCardForm/>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};
