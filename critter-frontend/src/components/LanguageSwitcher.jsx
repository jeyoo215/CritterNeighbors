import React from 'react';
import { useTranslation } from 'react-i18next';

export default function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const changeLanguage = (lng) => {
    // 넘어온 언어 코드로 변경!
    i18n.changeLanguage(lng);
  };

  return (
    <div style={containerStyle}>
      <button onClick={() => changeLanguage('kor')} style={textButtonStyle}>
        한국어
      </button>
      <span style={{ color: '#ccc' }}>|</span>
      <button onClick={() => changeLanguage('jpn')} style={textButtonStyle}>
        日本語
      </button>
    </div>
  );
}

const containerStyle = {
  display: 'flex',
  gap: '10px',
  alignItems: 'center',
  padding: '10px 20px',
  backgroundColor: 'transparent',
  fontFamily: 'Arial, sans-serif'
};

const textButtonStyle = {
  background: 'none',
  border: 'none',
  color: '#5f6368',
  cursor: 'pointer',
  fontSize: '16px',
  padding: '5px',
  transition: 'color 0.2s'
};