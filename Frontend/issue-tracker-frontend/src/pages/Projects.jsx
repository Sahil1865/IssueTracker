import { useEffect, useState } from "react";
import { fetchProjects } from "../api/projectApi";
import useAuth from "../context/useAuth";
import { useNavigate } from "react-router-dom";
import CreateProjectModal from "../components/CreateProjectModal";


const Projects = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);


  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      const res = await fetchProjects();
      setProjects(res.data || []);
    } catch (err) {
      console.log("Projects load error:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="page">Loading projects...</div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Projects</h1>
        <br></br> 
        {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
          <button
            className="primary-btn"
            onClick={() => setIsModalOpen(true)}
          >
          Create Project
          </button>

        )}
      </div>

      {projects.length === 0 ? (
        <div className="empty-state">
          <p>No projects available.</p>
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
      {isModalOpen && (
        <CreateProjectModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSuccess={loadProjects}   
        />

      )}
    </div>
  );
};

export default Projects;