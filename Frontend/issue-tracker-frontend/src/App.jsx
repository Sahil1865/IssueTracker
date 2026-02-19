import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./auth/Login";
import Register from "./auth/Register";
import Dashboard from "./pages/Dashboard";
import Projects from "./pages/Projects";
import Layout from "./components/Layout";
import ProtectedRoute from "./auth/ProtectedRoute";
import ProjectDetails from "./pages/ProjectDetails";
import TicketDetails from "./pages/TicketDetails";
import Navbar from "./components/Navbar";

const App = () => {
  return (
    <BrowserRouter>

      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        

        <Route
          path="/dashboard"
          element={
            <Layout>
              <Dashboard />
            </Layout>
          }
        />

        <Route
          path="/projects"
          element={
            <Layout>
              <Projects />
            </Layout>
          }
        />


        <Route 
          path="/projects/:id" 
          element={
          <Layout>
            <ProjectDetails />
          </Layout>
          } 
        />

        <Route
          path="/tickets/:ticketId"
          element={
            <Layout>
              <TicketDetails />
            </Layout>
          }
        />


      </Routes>
    </BrowserRouter>
  );
};

export default App;
