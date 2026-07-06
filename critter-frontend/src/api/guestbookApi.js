import api from './axios';

const API_BASE = "/ecosystems";

export const fetchGuestbooks = async (roomId) => {
  const response = await api.get(`${API_BASE}/${roomId}/guestbook`);
  return response.data;
};

export const postGuestbook = async (roomId, writerId, content) => {
  const response = await api.post(`${API_BASE}/${roomId}/guestbook`, {
    writerId, content
  });
  return await response.data;
};