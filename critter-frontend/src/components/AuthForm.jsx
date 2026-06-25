import React, { useState } from 'react';
import api from '../api/axios';

export default function AuthForm({ onLoginSuccess }) {
  const [isRegister, setIsRegister] = useState(false); // 가입/로그인 토글 상태
  const [formData, setFormData] = useState({ userName: '', password: '', nickname: '' });
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (isRegister) {
        // 회원가입 요청
        await api.post('/users/register', {
          userName: formData.userName,
          password: formData.password,
          nickname: formData.nickname
        });
        alert('회원가입 성공! 로그인해 주세요.');
        setIsRegister(false);
      } else {
        // 로그인 요청
        const response = await api.post('/users/login', {
          userName: formData.userName,
          password: formData.password
        });
        alert(`환영합니다, ${response.data.nickname}님!`);
        onLoginSuccess(response.data); // 부모 컴포넌트(App.jsx)로 유저 정보 전달
      }
    } catch (err) {
      // 🚨 백엔드 GlobalExceptionHandler가 던져주는 예외 메시지 표출
      setError(err.response?.data || '인증 처리에 실패했습니다.');
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '100px auto', padding: '30px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', borderRadius: '8px', textAlign: 'center' }}>
      <h2>{isRegister ? '🐾 크리터 회원가입' : '🔑 크리터 로그인'}</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' }}>
        <input
          type="text"
          placeholder="아이디"
          value={formData.userName}
          onChange={(e) => setFormData({ ...formData, userName: e.target.value })}
          required
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={formData.password}
          onChange={(e) => setFormData({ ...formData, password: e.target.value })}
          required
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
        {isRegister && (
          <input
            type="text"
            placeholder="닉네임"
            value={formData.nickname}
            onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
            required
            style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
          />
        )}
        {error && <p style={{ color: 'red', fontSize: '14px' }}>{error}</p>}
        <button type="submit" style={{ padding: '12px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          {isRegister ? '가입하기' : '로그인'}
        </button>
      </form>
      <button onClick={() => setIsRegister(!isRegister)} style={{ marginTop: '15px', background: 'none', border: 'none', color: '#7f8c8d', cursor: 'pointer', textDecoration: 'underline' }}>
        {isRegister ? '이미 계정이 있으신가요? 로그인' : '처음이신가요? 회원가입'}
      </button>
    </div>
  );
}