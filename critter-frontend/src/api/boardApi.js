import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/boards';

export const fetchBoards = () => axios.get(API_BASE);
export const createBoard = (boardData) => axios.post(API_BASE, boardData);
export const fetchBoardDetail = (id) => axios.get(`${API_BASE}/${id}`);
export const postComment = (boardId, commentData) => 
    axios.post(`${API_BASE}/${boardId}/comments`, commentData);