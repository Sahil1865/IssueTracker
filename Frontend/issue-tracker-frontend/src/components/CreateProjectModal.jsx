import { useState } from "react";
import { createProject } from "../api/projectApi";

const CreateProjectModal = ({ isOpen, onClose, onSuccess }) => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      await createProject({ title, description });

      onSuccess();   // reload projects
      onClose();     // close modal
    } catch (err) {
      console.log("Create project error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h2>Create Project</h2>

        <form onSubmit={handleSubmit}>
          <input
            className="auth-input"
            placeholder="Project Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />

          <textarea
            className="auth-input"
            placeholder="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          />

          <div className="modal-actions">
            <button type="button" className="primary-btn" onClick={onClose}>
              Cancel
            </button>

            <button className="primary-btn">
              {loading ? "Creating..." : "Create"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateProjectModal;
