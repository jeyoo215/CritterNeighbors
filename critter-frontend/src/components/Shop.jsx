import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';

export default function Shop({ roomId, currentUser, setCurrentUser, currentRoom, onClose, onAdoptSuccess }) {
  //roomTheme, userPoints
  const [critters, setCritters] = useState([]);
  // 각 크리처별로 유저가 입력한 이름을 저장할 객체 상태
  const [nicknames, setNicknames] = useState({});

  const { t } = useTranslation('shop');

  // 1. 서버에서 상점 아이템 목록 가져오기
  useEffect(() => {
    const fetchShopItems = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/ecosystems/${roomId}/critters/shop-items`);
        if (!response.ok) throw new Error(t('error.load_fail'));
        const data = await response.json();
        setCritters(data);
      } catch (error) {
        console.error("상점 목록 로딩 실패:", error);
      }
    };
    fetchShopItems();
  }, [roomId]);

  // 2. 입양 요청 처리
  const handleAdopt = async (critter) => {
    const translatedName = t(`item.critter.${critter.type.toLowerCase()}`);
    const name = prompt(t('alert.naming', { name: translatedName }), translatedName);
    if (name === null) return; 
    const finalName = name.trim() === '' ? translatedName : name;

    try {
      const response = await fetch(`http://localhost:8080/api/ecosystems/${roomId}/critters`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.userId,
          critterName: finalName,
          critterType: critter.type
        }),
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        setCurrentUser(data.user);
        alert(t('alert.success', {name: finalName}));
        if (onAdoptSuccess) onAdoptSuccess(); 
        onClose();
        
      } else {
        const errorMsg = await response.text();
        alert(errorMsg || t('error.adopt_fail'));
      }
    } catch (error) {
      console.error("입양 실패:", error);
      alert(t('error.server'));
    }
  };

  return (
    <div style={{ 
      padding: '20px', 
      background: '#f9f9f9', 
      borderRadius: '10px',
      width: '400px', // 적당한 상점 너비
      margin: '0 auto',
      position: 'relative'
    }}>

      <button 
        onClick={onClose} 
        style={{ 
          position: 'absolute', 
          top: '10px', 
          right: '10px', 
          background: '#ccc', 
          border: 'none', 
          borderRadius: '4px', 
          cursor: 'pointer' 
        }}
      >
        ✕
      </button>

      <h3 style={{ marginTop: 0 }}>{t('title')}</h3>
      <p>{t('my_points', {points: currentUser.point})}</p>
      
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(3, 1fr)', // 3열로 고정
        gap: '10px', 
        marginTop: '20px',
        maxHeight: '400px', // 9개 정도 들어갈 높이
        overflowY: 'auto',  // 넘치면 스크롤
        padding: '5px'
      }}>
        {critters.map((critter) => {
          const isCompatible = critter.theme === currentRoom.roomTheme;
          return (
            <div key={critter.id} style={{ 
              border: '1px solid #ccc', 
              padding: '10px', 
              borderRadius: '8px', 
              background: 'white',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between', // 내용물 정렬
              fontSize: '12px' // 폰트 크기 조절
            }}>
              <h4 style={{ margin: '0 0 5px 0' }}>{t(`item.${critter.name}`)}</h4>
              <p style={{ margin: '0' }}>{critter.theme}</p>
              <p style={{ margin: '0 0 10px 0' }}>{critter.price}P</p>
              
              <button 
                disabled={!isCompatible || currentUser.point < critter.price}
                onClick={() => handleAdopt(critter)}
                style={{ padding: '5px', cursor: 'pointer', width: '100%' }}
              >
                {isCompatible ? (currentUser.point >= critter.price 
                  ? t('btn_adopt.adopt')
                  : t('btn_adpot.no_point'))
                  : t('btn_adopt.incompatible')}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
}