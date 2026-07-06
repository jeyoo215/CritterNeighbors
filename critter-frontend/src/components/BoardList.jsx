import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { fetchBoards, createBoard } from '../api/boardApi';

// 🆕 onSelectBoard props를 추가로 받아야 합니다!
export default function BoardList({ user, onBackToLobby, onSelectBoard, onGoToCreate, onLogout }) {

  const styles = {
    container: { padding: '20px', maxWidth: '800px', margin: '0 auto', fontFamily: 'Arial, sans-serif' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' },
    button: { backgroundColor: '#3498db', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '5px', cursor: 'pointer' },
    boardItem: { border: '1px solid #eee', padding: '15px', borderRadius: '8px', marginBottom: '10px', cursor: 'pointer', transition: '0.2s' },
    boardItemHover: { backgroundColor: '#f9f9f9' }
  };

  const [boards, setBoards] = useState([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const loadBoards = async () => {
    try {
      const res = await fetchBoards();
      setBoards(res.data);
    } catch (e) {
      console.error("목록 불러오기 실패", e);
    }
  };

  useEffect(() => {
    loadBoards();
  }, []);

  const handleWrite = async () => {
    if (!user || !user.userId) return alert("로그인 정보가 없습니다.");
    if (!title || !content) return alert("제목과 내용을 입력하세요!");
    
    try {
      await createBoard({ 
        writerId: user.userId, 
        title, 
        content 
      });
      setTitle('');
      setContent('');
      loadBoards(); 
    } catch (e) {
      console.error("글 등록 실패", e);
      alert("글 등록에 실패했습니다.");
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>📢 질문 게시판</h2>
        <div>
          <button onClick={onBackToLobby} style={{ ...styles.button, backgroundColor: '#95a5a6' }}>🏠 로비</button>
          <button onClick={onLogout} style={{ ...styles.button, backgroundColor: '#e74c3c', marginLeft: '10px' }}>로그아웃</button>
        </div>
      </div>

      <button onClick={onGoToCreate} style={{ ...styles.button, marginBottom: '20px' }}>✏️ 글쓰기</button>
      
      {Array.isArray(boards) && boards.map(board => (
        <div key={board.boardId} onClick={() => onSelectBoard(board.boardId)} style={styles.boardItem}>
          <h3 style={{ margin: '0 0 5px 0' }}>{board.title}</h3>
          <p style={{ margin: 0, fontSize: '14px', color: '#777' }}>작성자: {board.writer?.nickname || "알수없음"}</p>
        </div>
      ))}
    </div>
  );
}