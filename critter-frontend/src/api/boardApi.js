import api from './axios';

const API_BASE = '/boards';

// 게시판 조회
export const fetchBoards = (category = 'ALL') => api.get(`/boards?category=${category}`);

// 게시글 등록
export const createBoard = (boardData) => api.post(API_BASE, boardData);

// 상세 게시글 조회
export const fetchBoardDetail = (id) => api.get(`${API_BASE}/${id}`);

// 이전글 다음글 조회
export const fetchNeighbors = (boardId) => {
  return api.get(`${API_BASE}/${boardId}/neighbors`);
};

// 댓글 등록
export const postComment = (boardId, commentData) => 
    api.post(`${API_BASE}/${boardId}/comments`, commentData);

// 댓글 조회
export const getCommentsByBoard = (boardId) => 
    api.get(`${API_BASE}/${boardId}/comments`);

// 게시글 수정/삭제
export const updateBoard = (id, boardData) => api.put(`${API_BASE}/${id}`, boardData);
export const deleteBoard = (id) => api.delete(`${API_BASE}/${id}`);

// 댓글 수정/삭제
export const updateComment = (commentId, commentData) => 
    api.put(`${API_BASE}/comments/${commentId}`, commentData);
export const deleteComment = (commentId) => 
    api.delete(`${API_BASE}/comments/${commentId}`);