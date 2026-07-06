import React, { useState } from 'react';
import { createBoard } from '../api/boardApi';
import { useTranslation } from 'react-i18next';

export default function BoardCreate({ user, onBackToList, refreshUser }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const { t, i18n } = useTranslation('board');
  const getInitialCategory = () => {
    const lang = i18n.language; //
    if (lang === 'jpn') return 'JAPANESE';
    return 'KOREAN';
  };
  const [category, setCategory] = useState(getInitialCategory());

  const handleCreate = async () => {
    if (!title || !content) return alert(t('create.alert.content_error'));

    try {
      await createBoard({
        writerId: user.userId,
        category,
        title,
        content
      });
      alert(t('create.alert.success'));
      if (refreshUser) {
        await refreshUser(); 
      }
      onBackToList(); // 성공하면 게시판 목록으로 이동
    } catch (e) {
      console.error("등록 실패", e);
      alert(t('create.alert.write_error'));
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <h2>{t('create.title')}</h2>
      <input 
        placeholder={t('create.input_title')}
        value={title}
        onChange={(e) => setTitle(e.target.value)} 
        style={{ width: '100%', padding: '10px', marginBottom: '10px' }} 
      />
      <select 
        value={category} 
        onChange={(e) => setCategory(e.target.value)}
        style={{ width: '100%', padding: '10px', marginBottom: '10px' }}
      >
        <option value="KOREAN">{t('category.korean')}</option>
        <option value="JAPANESE">{t('category.japanese')}</option>
      </select>
      <textarea 
        placeholder={t('create.input_content')} 
        value={content} 
        onChange={(e) => setContent(e.target.value)} 
        style={{ width: '100%', height: '200px', padding: '10px', marginBottom: '10px' }} 
      />
      <div style={{ display: 'flex', gap: '10px' }}>
        <button onClick={handleCreate} style={{ padding: '10px 20px', backgroundColor: '#2ecc71', color: 'white', border: 'none', borderRadius: '5px' }}>
          {t('create.btn_submit')}
        </button>
        <button onClick={onBackToList} style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '5px' }}>
          {t('create.btn_cancel')}
        </button>
      </div>
    </div>
  );
}