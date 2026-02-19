import axios from "./axios";

export const fetchStats = () => {
  return axios.get("/ticket/stats");
};

export const fetchTicketsByProject = (projectId) => {
  return axios.get(`/ticket/project/${projectId}`);
};

export const createTicket = (data) => {
  return axios.post("/ticket", data);
};

export const fetchTicketById = (ticketId) => {
  return axios.get(`/ticket/${ticketId}`);
};

export const updateTicketStatus = (ticketId, data) => {
  return axios.put(`/ticket/${ticketId}/status`, data);
};

export default fetchStats;