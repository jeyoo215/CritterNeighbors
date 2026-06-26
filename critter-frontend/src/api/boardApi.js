import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/boards';

export const fetchBoards = () => axios.get(API_BASE);
export const createBoard = (boardData) => axios.post(API_BASE, boardData);
export const fetchBoardDetail = (id) => axios.get(`${API_BASE}/${id}`);
export const postComment = (boardId, commentData) => 
    axios.post(`${API_BASE}/${boardId}/comments`, commentData);
export const getCommentsByBoard = (boardId) => 
    axios.get(`${API_BASE}/${boardId}/comments`);


// 게시글 수정/삭제
export const updateBoard = (id, boardData) => axios.put(`${API_BASE}/${id}`, boardData);
export const deleteBoard = (id) => axios.delete(`${API_BASE}/${id}`);

// 댓글 수정/삭제
export const updateComment = (commentId, commentData) => 
    axios.put(`${API_BASE}/comments/${commentId}`, commentData);
export const deleteComment = (commentId) => 
    axios.delete(`${API_BASE}/comments/${commentId}`);