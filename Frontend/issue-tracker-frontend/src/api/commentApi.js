import axios from "./axios";

export const fetchComments = (ticketId) => {
  return axios.get(`/comments/ticket/${ticketId}`);
};

export const createComment = (data) => {
  return axios.post("/comments", data);
};
