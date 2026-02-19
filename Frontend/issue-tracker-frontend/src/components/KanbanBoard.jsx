import {
  DndContext,
  closestCorners
} from "@dnd-kit/core";
import {
  SortableContext,
  verticalListSortingStrategy
} from "@dnd-kit/sortable";
import KanbanColumn from "./KanbanColumn";
import { useState } from "react";

const statuses = ["TODO", "IN_PROGRESS", "COMPLETED"];

const KanbanBoard = ({ tickets, onStatusChange }) => {
  const [items, setItems] = useState(tickets);

  const grouped = statuses.reduce((acc, status) => {
    acc[status] = items.filter(t => t.status === status);
    return acc;
  }, {});

  const handleDragEnd = async (event) => {
  const { active, over } = event;

  if (!over) return;

  const ticketId = active.id;
  const newStatus = over.id;

  const ticket = items.find(t => t.id === ticketId);
  if (!ticket || ticket.status === newStatus) return;

  // Save previous state
  const previousItems = [...items];

  // Optimistic update
  const updated = items.map(t =>
    t.id === ticketId ? { ...t, status: newStatus } : t
  );

  setItems(updated);

  try {
    await onStatusChange(ticketId, newStatus);
  } catch (err) {
    console.log("Rollback due to error:", err);

    // Revert UI
    setItems(previousItems);
  }
};


  return (
    <DndContext collisionDetection={closestCorners} onDragEnd={handleDragEnd}>
      <div className="kanban-board">
        {statuses.map(status => (
          <SortableContext
            key={status}
            items={grouped[status].map(t => t.id)}
            strategy={verticalListSortingStrategy}
          >
            <KanbanColumn
              id={status}
              title={status}
              tickets={grouped[status]}
            />
          </SortableContext>
        ))}
      </div>
    </DndContext>
  );
};

export default KanbanBoard;
