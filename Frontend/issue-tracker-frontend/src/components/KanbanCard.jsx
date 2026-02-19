import { useDraggable } from "@dnd-kit/core";
import { CSS } from "@dnd-kit/utilities";

const KanbanCard = ({ ticket }) => {
  const { attributes, listeners, setNodeRef, transform } =
    useDraggable({ id: ticket.id });

  const style = {
    transform: CSS.Translate.toString(transform),
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className="kanban-card"
    >
      <strong>{ticket.title}</strong>
      <p>{ticket.description}</p>
    </div>
  );
};

export default KanbanCard;
