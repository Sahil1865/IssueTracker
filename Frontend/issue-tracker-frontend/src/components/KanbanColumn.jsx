import { useDroppable } from "@dnd-kit/core";
import KanbanCard from "./KanbanCard";

const KanbanColumn = ({ id, title, tickets }) => {
  const { setNodeRef } = useDroppable({ id });

  return (
    <div ref={setNodeRef} className="kanban-column">
      <h3>{title}</h3>

      {tickets.map(ticket => (
        <KanbanCard key={ticket.id} ticket={ticket} />
      ))}
    </div>
  );
};

export default KanbanColumn;
