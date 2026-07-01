import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/boards';

// 게시판 조회
export const fetchBoards = () => axios.get(API_BASE);

// 게시글 등록
export const createBoard = (boardData) => axios.post(API_BASE, boardData);

// 상세 게시글 조회
export const fetchBoardDetail = (id) => axios.get(`${API_BASE}/${id}`);

// 이전글 다음글 조회
export const fetchNeighbors = (boardId) => {
  return axios.get(`${API_BASE}/${boardId}/neighbors`);
};

// 댓글 등록
export const postComment = (boardId, commentData) => 
    axios.post(`${API_BASE}/${boardId}/comments`, commentData);

// 댓글 조회
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