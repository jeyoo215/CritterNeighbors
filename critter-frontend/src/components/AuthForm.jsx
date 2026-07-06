import React, { useState } from 'react';
import api from '../api/axios';
import { useTranslation } from 'react-i18next';

export default function AuthForm({ onLoginSuccess }) {
  const [isRegister, setIsRegister] = useState(false);
  const [formData, setFormData] = useState({ userName: '', password: '', nickname: '' });
  const [error, setError] = useState('');
  const { t } = useTranslation('auth');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      if (isRegister) {
        await api.post('/users/register', formData);
        alert(t('alert.register_success'));
        setIsRegister(false);
        setFormData({ userName: '', password: '', nickname: '' });
      } else {
        const response = await api.post('/users/login', {
          userName: formData.userName,
          password: formData.password
        });
        alert(t('alert.login_success', { nickname: response.data.nickname }));
        onLoginSuccess(response.data);
      }
    } catch (err) {
      // 💡 하얀 화면 방지: 에러 객체를 문자열로 안전하게 변환
      let message = t('message.error_default');
      if (err.response && err.response.data) {
        const data = err.response.data;
        message = typeof data === 'string' ? data : (data.message || t('message.error_auth'));
      }
      setError(message);
      setFormData(prev => ({ ...prev, password: '' }));
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '100px auto', padding: '30px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)', borderRadius: '8px', textAlign: 'center' }}>
      <h2>{isRegister ? t('title.register') : t('title.login')}</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' }}>
        <input 
          type="text" 
          placeholder={t('input.username')} 
          value={formData.userName} 
          onChange={(e) => setFormData({...formData, userName: e.target.value})} 
          required 
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }} 
        />
        <input 
          type="password" 
          placeholder={t('input.password')} 
          value={formData.password} 
          onChange={(e) => setFormData({...formData, password: e.target.value})} 
          required 
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }} 
        />
        {isRegister && (
          <input 
            type="text" 
            placeholder={t('input.nickname')} 
            value={formData.nickname} 
            onChange={(e) => setFormData({...formData, nickname: e.target.value})} 
            required 
            style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }} 
          />
        )}
        
        {error && <p style={{ color: 'red', fontSize: '14px', margin: '0' }}>{error}</p>}
        
        <button type="submit" style={{ padding: '12px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          {isRegister ? t('button.submit.register') : t('button.submit.login')}
        </button>
      </form>
      <button onClick={() => { setIsRegister(!isRegister); setError(''); }} style={{ marginTop: '15px', background: 'none', border: 'none', color: '#7f8c8d', cursor: 'pointer', textDecoration: 'underline' }}>
        {isRegister ? t('button.toggle.to_login') : t('button.toggle.to_register')}
      </button>
    </div>
  );
}