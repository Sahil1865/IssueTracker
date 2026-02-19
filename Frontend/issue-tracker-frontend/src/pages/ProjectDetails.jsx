import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchTicketsByProject } from "../api/ticketApi";
import {
  fetchProjectById,
  addProjectMember,
  removeProjectMember
} from "../api/projectApi";
import KanbanBoard from "../components/KanbanBoard";
import { updateTicketStatus } from "../api/ticketApi";
import { fetchUsers } from "../api/userApi";
import useAuth from "../context/useAuth";
import CreateTicketModal from "../components/CreateTicketModal";
import { useNavigate } from "react-router-dom";



const ProjectDetails = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [tickets, setTickets] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const loadData = async () => {
    try {
      const projectRes = await fetchProjectById(id);
      setProject(projectRes.data);

      const usersRes = await fetchUsers();
      setAllUsers(usersRes.data);

      const ticketRes = await fetchTicketsByProject(id);
      setTickets(ticketRes.data || []);

    } catch (err) {
      console.log("ProjectDetails error:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleAddMember = async (userId) => {
    try {
      await addProjectMember(id, userId);
      await loadData();
    } catch (err) {
      console.log("Add member error:", err);
    }
  };

  const handleRemoveMember = async (memberId) => {
    try {
      await removeProjectMember(id, memberId);
      await loadData();
    } catch (err) {
      console.log("Remove member error:", err);
    }
  };

  const handleStatusChange = async (ticketId, newStatus) => {
  return updateTicketStatus(ticketId, { newStatus });
  };


  if (loading) return <div className="page">Loading project...</div>;
  if (!project) return <div className="page">Project not found.</div>;

  // Separate members & non-members
  const projectMembers = project.members || [];
  const nonMembers = allUsers.filter(
    (u) => !projectMembers.some((m) => m.id === u.id)
  );

  return (
    <div className="page">
      <div className="page-header">
        <h1>{project.title}</h1>

        {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
          <button
            className="primary-btn"
            onClick={() => setIsModalOpen(true)}
          >
            Create Ticket
          </button>
        )}
      </div>

      <p style={{ marginBottom: "30px", color: "#94a3b8" }}>
        {project.description}
      </p>

      {/* ===== MEMBERS SECTION ===== */}
      <h2>Project Members</h2>

      <div className="member-list">
        {projectMembers.map((member) => (
          <div key={member.id} className="member-card">
            <span>{member.name}</span>
            <span className="member-role">{member.role}</span>

            {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
              <button
                className="remove-member-btn"
                onClick={() => handleRemoveMember(member.id)}
              >
                ✕
              </button>
            )}
          </div>
        ))}
      </div>

      {/* ===== ALL USERS SECTION ===== */}
      {(user?.role === "ADMIN" || user?.role === "MANAGER") && (
        <>
          <h2 style={{ marginTop: "30px" }}>All Users</h2>

          <div className="user-list">
            {nonMembers.map((u) => (
              <div key={u.id} className="user-card">
                <span>
                  {u.name} ({u.role})
                </span>

                <button
                  className="primary-btn small"
                  onClick={() => handleAddMember(u.id)}
                >
                  Add
                </button>
              </div>
            ))}
          </div>
        </>
      )}

      {/* ===== TICKETS SECTION ===== */}
      <h2 style={{ marginTop: "40px" }}>Tickets</h2>

      <KanbanBoard tickets={tickets} onStatusChange={handleStatusChange} />
      {tickets.length === 0 ? (
        <div className="empty-state">
          <p>No tickets in this project.</p>
        </div>
      ) : (
        <div className="ticket-grid">
          {tickets.map((ticket) => (
            
        <div
          key={ticket.id}
          className="ticket-card clickable"
          onClick={() => navigate(`/tickets/${ticket.id}`)}
        >
            <h3>{ticket.title}</h3>
            <p>{ticket.description}</p>

            {ticket.assigneeName && (
              <p className="assignee">
                Assigned to: <strong>{ticket.assigneeName}</strong>
              </p>
            )}

            <span className={`status ${ticket.status}`}>
              {ticket.status}
            </span>
        </div>

          ))}
        </div>
      )}

      <CreateTicketModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={loadData}
        projectId={id}
        members={projectMembers}
      />
    </div>
  );
};

export default ProjectDetails;
