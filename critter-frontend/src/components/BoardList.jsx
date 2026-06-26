import React, { useEffect, useState } from 'react';
import { fetchBoards, createBoard } from '../api/boardApi';

// 🆕 onSelectBoard props를 추가로 받아야 합니다!
export default function BoardList({ user, onBackToLobby, onSelectBoard, onLogout }) {
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
    <div style={{ padding: '20px' }}>
      <h2>📢 질문 게시판</h2>
      <button onClick={onBackToLobby}>🏠 로비로 돌아가기</button>
      <button onClick={onLogout} style={{ marginLeft: '10px', color: 'red' }}>로그아웃 하기</button>

      <div style={{ margin: '20px 0', border: '1px solid #ddd', padding: '15px' }}>
        <input placeholder="제목" value={title} onChange={(e) => setTitle(e.target.value)} style={{ display: 'block', marginBottom: '5px' }} />
        <textarea placeholder="내용" value={content} onChange={(e) => setContent(e.target.value)} style={{ display: 'block', marginBottom: '10px' }} />
        <button onClick={handleWrite}>글 등록하기</button>
      </div>

      <div>
        {boards.map(board => (
          // 🚨 핵심 수정: 클릭 시 onSelectBoard(ID)가 실행되도록 연결!
          // cursor: 'pointer'를 추가해서 클릭 가능한 모양으로 만들었습니다.
          <div 
            key={board.boardId} 
            onClick={() => onSelectBoard(board.boardId)} 
            style={{ borderBottom: '1px solid #eee', padding: '10px', cursor: 'pointer' }}
          >
            <h3>{board.title}</h3>
            <p>작성자: {board.writer?.nickname || "알수없음"}</p>
          </div>
        ))}
      </div>
    </div>
  );
}