import React, { useState } from 'react';
import api from '../api/axios';

export default function AuthForm({ onLoginSuccess }) {
  const [isRegister, setIsRegister] = useState(false);
  const [formData, setFormData] = useState({ userName: '', password: '', nickname: '' });
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      if (isRegister) {
        await api.post('/users/register', formData);
        alert('회원가입 성공! 로그인해 주세요.');
        setIsRegister(false);
        setFormData({ userName: '', password: '', nickname: '' });
      } else {
        const response = await api.post('/users/login', {
          userName: formData.userName,
          password: formData.password
        });
        alert(`환영합니다, ${response.data.nickname}님!`);
        onLoginSuccess(response.data);
      }
    } catch (err) {
      // 💡 하얀 화면 방지: 에러 객체를 문자열로 안전하게 변환
      let message = '인증에 실패했습니다.';
      if (err.response && err.response.data) {
        const data = err.response.data;
        message = typeof data === 'string' ? data : (data.message || "아이디 또는 비밀번호를 확인해주세요.");
      }
      setError(message);
      // 로그인 실패 시 비밀번호창만 초기화
      setFormData(prev => ({ ...prev, password: '' }));
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '100px auto', padding: '30px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', borderRadius: '8px', textAlign: 'center' }}>
      <h2>{isRegister ? '🐾 크리터 회원가입' : '🔑 크리터 로그인'}</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' }}>
        <input type="text" placeholder="아이디" value={formData.userName} onChange={(e) => setFormData({...formData, userName: e.target.value})} required style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }} />
        <input type="password" placeholder="비밀번호" value={formData.password} onChange={(e) => setFormData({...formData, password: e.target.value})} required style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }} />
        {isRegister && <input type="text" placeholder="닉네임" value={formData.nickname} onChange={(e) => setFormData({...formData, nickname: e.target.value})} required style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }} />}
        
        {/* 에러 메시지 렌더링 */}
        {error && <p style={{ color: 'red', fontSize: '14px', margin: '0' }}>{error}</p>}
        
        <button type="submit" style={{ padding: '12px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          {isRegister ? '가입하기' : '로그인'}
        </button>
      </form>
      <button onClick={() => { setIsRegister(!isRegister); setError(''); }} style={{ marginTop: '15px', background: 'none', border: 'none', color: '#7f8c8d', cursor: 'pointer', textDecoration: 'underline' }}>
        {isRegister ? '이미 계정이 있으신가요? 로그인' : '처음이신가요? 회원가입'}
      </button>
    </div>
  );
}