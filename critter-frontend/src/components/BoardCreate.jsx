import React, { useState } from 'react';
import { createBoard } from '../api/boardApi';

export default function BoardCreate({ user, onBackToList, refreshUser }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const handleCreate = async () => {
    if (!title || !content) return alert("제목과 내용을 입력해주세요!");

    try {
      await createBoard({
        writerId: user.userId,
        title,
        content
      });
      alert("글이 등록되었습니다!");
      if (refreshUser) {
        await refreshUser(); 
      }
      onBackToList(); // 성공하면 게시판 목록으로 이동
    } catch (e) {
      console.error("등록 실패", e);
      alert("글 등록에 실패했습니다.");
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <h2>✏️ 새 글 작성</h2>
      <input 
        placeholder="제목을 입력하세요" 
        value={title} 
        onChange={(e) => setTitle(e.target.value)} 
        style={{ width: '100%', padding: '10px', marginBottom: '10px' }} 
      />
      <textarea 
        placeholder="내용을 입력하세요" 
        value={content} 
        onChange={(e) => setContent(e.target.value)} 
        style={{ width: '100%', height: '200px', padding: '10px', marginBottom: '10px' }} 
      />
      <div style={{ display: 'flex', gap: '10px' }}>
        <button onClick={handleCreate} style={{ padding: '10px 20px', backgroundColor: '#2ecc71', color: 'white', border: 'none', borderRadius: '5px' }}>등록</button>
        <button onClick={onBackToList} style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '5px' }}>취소</button>
      </div>
    </div>
  );
}