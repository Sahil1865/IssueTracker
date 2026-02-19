import fetchStats from "../api/ticketApi";
import { useEffect, useState } from "react";
import useAuth from "../context/useAuth";
import { fetchProjects } from "../api/projectApi";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [stats, setStats] = useState({
    total: 0,
    open: 0,
    inProgress: 0,
    done: 0,
  });

  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const statsRes = await fetchStats();
      const projectRes = await fetchProjects();

      setStats({
        total: statsRes.data?.total || 0,
        open: statsRes.data?.open || 0,
        inProgress: statsRes.data?.inProgress || 0,
        done: statsRes.data?.done || 0,
      });

      setProjects(projectRes.data || []);
    } catch (err) {
      console.log("Dashboard error:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="dashboard loading">Loading dashboard...</div>;
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1 className="dashboard-title">Dashboard</h1>
        <p className="dashboard-subtitle">
          Welcome back, {user?.email?.split("@")[0]}
        </p>
        <br></br>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <StatCard title="Total Tickets" value={stats.total} />
        <StatCard title="Open" value={stats.open} type="open" />
        <StatCard title="In Progress" value={stats.inProgress} type="progress" />
        <StatCard title="Done" value={stats.done} type="done" />
      </div>

      {/* Projects Section */}
      <div className="section">
        <div className="section-header">
          <h2>Your Projects</h2>
          <br></br>
          {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
            <button
              className="primary-btn"
              onClick={() => navigate("/projects")}
            >
              Manage Projects
            </button>
          )}
        </div>

        {projects.length === 0 ? (
          <div className="empty-state">
            <p>No projects yet.</p>
          </div>
        ) : (
          <div className="project-grid">
            {projects.map((project) => (
              <div
                key={project.id}
                className="project-card clickable"
                onClick={() => navigate(`/projects/${project.id}`)}
              >
                <h3>{project.title}</h3>
                <p>{project.description}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

const StatCard = ({ title, value, type }) => {
  return (
    <div className="stat-card">
      <span className="stat-title">{title }</span><br></br>
      <span className={`stat-number ${type || ""}`}>
        {value}
      </span>
    </div>
  );
};

export default Dashboard;
