import React from 'react';

export default function Shop({ roomTheme, userPoints, onAdopt, onClose }) {
  // 상점에 있는 크리처 목록 (실제로는 백엔드에서 받아와야 해!)
  const critters = [
    { id: 1, name: '수달', habitat: 'RIVER', price: 20 },
    { id: 2, name: '펭귄', habitat: 'ICE', price: 30 },
    { id: 3, name: '도마뱀', habitat: 'DESERT', price: 25 },
  ];

  return (
    <div style={{ padding: '20px', background: '#f9f9f9', borderRadius: '10px' }}>
      <h3>🛒 크리처 상점 (내 포인트: {userPoints}P)</h3>
      <button onClick={onClose}>닫기</button>
      
      <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
        {critters.map((critter) => {
          const isCompatible = critter.habitat === roomTheme;
          return (
            <div key={critter.id} style={{ border: '1px solid #ccc', padding: '10px' }}>
              <h4>{critter.name}</h4>
              <p>서식지: {critter.habitat}</p>
              <p>가격: {critter.price}P</p>
              
              <button 
                disabled={!isCompatible || userPoints < critter.price}
                onClick={() => onAdopt(critter)}
              >
                {isCompatible 
                  ? (userPoints >= critter.price ? "입양하기" : "포인트 부족") 
                  : `${critter.habitat}에서는 살 수 없어요!`}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
}