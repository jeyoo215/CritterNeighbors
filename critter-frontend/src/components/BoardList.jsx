import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { fetchBoards, createBoard } from '../api/boardApi';
import { useTranslation } from 'react-i18next';

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
  const [category, setCategory] = useState('ALL');
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const { t } = useTranslation('board');

  const loadBoards = async () => {
    try {
      const res = await fetchBoards(category);
      setBoards(res.data);
    } catch (e) {
      console.error("목록 불러오기 실패", e);
    }
  };

  useEffect(() => {
    loadBoards();
  }, [category]);

  const handleCategoryChange = (cat) => {
    setCategory(cat);
  };

  const handleWrite = async () => {
    if (!user || !user.userId) return alert(t('list.alert.login_error'));
    if (!title || !content) return alert(t('create.alert.content_error'));
    
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
      alert(t('create.alert.write_error'));
    }
  };

const formatBoardDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();

  const isToday = date.toDateString() === now.toDateString();
  if (isToday) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false});
  } else {
    return date.toLocaleDateString();
  }
}

  const textButtonStyle = {
  background: 'none',
  border: 'none',
  color: '#5f6368', // 구글스러운 짙은 회색
  cursor: 'pointer',
  fontSize: '16px',
  padding: '5px',
  transition: 'color 0.2s'
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>{t('list.title')}</h2>
        <div>
          <button onClick={onBackToLobby} style={{ ...styles.button, backgroundColor: '#95a5a6' }}>{t('list.btn_lobby')}</button>
          <button onClick={onLogout} style={{ ...styles.button, backgroundColor: '#e74c3c', marginLeft: '10px' }}>{t('list.btn_logout')}</button>
        </div>
      </div>

      <div style={{ marginBottom: '15px', display: 'flex', gap: '10px' }}>
        <button onClick={() => handleCategoryChange('ALL')} style={textButtonStyle}>
          {t('category.all')}
        </button>
        <button onClick={() => handleCategoryChange('KOREAN')} style={textButtonStyle}>
          {t('category.korean')}
        </button>
        <button onClick={() => handleCategoryChange('JAPANESE')} style={textButtonStyle}>
          {t('category.japanese')}
        </button>
      </div>

      <button onClick={onGoToCreate} style={{ ...styles.button, marginBottom: '20px' }}>{t('list.btn_write')}</button>
      
      {Array.isArray(boards) && boards.map(board => (
        <div key={board.boardId} onClick={() => onSelectBoard(board.boardId)} style={styles.boardItem}>
          <h3 style={{ margin: '0 0 5px 0' }}>{board.title}</h3>
          <p style={{ margin: 0, fontSize: '14px', color: '#777' }}>
            {t('list.writer', { nickname: board.writer?.nickname || t('list.unknown_writer') })}
          </p>
          <p style={{ margin: 0, fontSize: '14px', color: '#777' }}>
            {formatBoardDate(board.createdAt)}
          </p>
        </div>
      ))}
    </div>
  );
}