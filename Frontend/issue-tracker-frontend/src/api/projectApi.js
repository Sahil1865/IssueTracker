import axios from "./axios";

export const fetchProjects = () => {
  return axios.get("/projects");
};

export const createProject = (data) => {
  return axios.post("/projects", data);
};

export const fetchProjectById = (id) => {
  return axios.get(`/projects/${id}`);
};

export const addProjectMember = (projectId, userId) => {
  return axios.put(`/projects/${projectId}/members?userId=${userId}`);
};

export const removeProjectMember = (projectId, userId) => {
  return axios.delete(`/projects/${projectId}/members/${userId}`);
};