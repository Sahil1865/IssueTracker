import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchTicketById } from "../api/ticketApi";
import { fetchComments, createComment } from "../api/commentApi";
import useAuth from "../context/useAuth";

const TicketDetails = () => {
  const { ticketId } = useParams();
  const { user } = useAuth();

  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");


  useEffect(() => {
    const loadTicket = async () => {
    try {
        const res = await fetchTicketById(ticketId);
        setTicket(res.data);

        const commentsRes = await fetchComments(ticketId);
        setComments(commentsRes.data || []);
    } catch (err) {
        console.log("Ticket details error:", err);
    } finally {
        setLoading(false);
    }
    };

    loadTicket();
  }, [ticketId]);

  const handleAddComment = async (e) => {
  e.preventDefault();

  if (!newComment.trim()) return;

  try {
    const res = await createComment({
      ticketId: Number(ticketId),
      content: newComment,
    });

    setComments([...comments, res.data]);
    setNewComment("");
  } catch (err) {
    console.log("Comment error:", err);
  }
};


  if (loading) return <div className="page">Loading ticket...</div>;
  if (!ticket) return <div className="page">Ticket not found.</div>;

  return (
    <div className="page">
      <h1>{ticket.title}</h1>

      <p style={{ marginBottom: "20px", color: "#94a3b8" }}>
        {ticket.description}
      </p>

      <div className="ticket-meta">
        <div>
          <strong>Status:</strong>{" "}
          <span className={`status ${ticket.status}`}>
            {ticket.status}
          </span>
        </div>

        <div>
          <strong>Priority:</strong> {ticket.priority}
        </div>

        {ticket.assigneeName && (
              <div >
                Assigned to: <strong>{ticket.assigneeName}</strong>
              </div>
            )}
      </div>

      <hr style={{ margin: "40px 0", borderColor: "#1e293b" }} />

        <h2>Comments</h2>

        <form onSubmit={handleAddComment} className="comment-form">
        <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Write a comment..."
            required
        />
        <button className="primary-btn">Add Comment</button>
        </form>

        <div className="comments-list">
        {comments.length === 0 ? (
            <p style={{ color: "#94a3b8" }}>No comments yet.</p>
        ) : (
            comments.map((comment) => (
            <div key={comment.id} className="comment-card">
                <strong>{comment.username}</strong>
                <p>{comment.content}</p>
            </div>
            ))
        )}
        </div>

    </div>

    
  );
};

export default TicketDetails;
