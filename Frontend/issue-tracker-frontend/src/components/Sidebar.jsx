import { NavLink } from "react-router-dom";
import useAuth from "../context/useAuth";

const Sidebar = () => {
  const { user } = useAuth();

  if (!user) return null;

  return (
    <div className="sidebar">
      <h2 className="sidebar-title">ProjectReport</h2>

      <nav className="sidebar-links">
        <NavLink to="/dashboard" className="sidebar-link">
          Dashboard
        </NavLink>

        <NavLink to="/projects" className="sidebar-link">
          Projects
        </NavLink>

        {user.role === "ADMIN" && (
          <NavLink to="/users" className="sidebar-link">
            Users
          </NavLink>
        )}
      </nav>
    </div>
  );
};

export default Sidebar;
