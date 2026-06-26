// api/guestbookApi.js
const API_BASE = "http://localhost:8080/api/ecosystems";

export const fetchGuestbooks = async (roomId) => {
  const response = await fetch(`${API_BASE}/${roomId}/guestbook`);
  return response.json();
};

export const postGuestbook = async (roomId, writerId, content) => {
  console.log("요청 URL:", `${API_BASE}/${roomId}/guestbook`);

  const response = await fetch(`${API_BASE}/${roomId}/guestbook`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ writerId, content })
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw errorData;
  }
  return await response.json();
};