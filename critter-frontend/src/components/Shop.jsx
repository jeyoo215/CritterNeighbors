import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchShopItems, adoptCritterApi, buyFoodApi } from '../api/shopApi';

export default function Shop({ roomId, currentUser, isMyRoom, setCurrentUser, currentRoom, onBuyFood, onClose, onAdoptSuccess }) {
  //roomTheme, userPoints
  const [critters, setCritters] = useState([]);
  // 각 크리처별로 유저가 입력한 이름을 저장할 객체 상태
  const [nicknames, setNicknames] = useState({});
  const [activeTab, setActiveTab] = useState('CRITTER');

  const { t } = useTranslation('shop');

  const FOOD_ITEMS = [
    { type: 'FISH', price: 3 },
    { type: 'SHRIMP', price: 3 },
    { type: 'MEAT', price: 3 },
    { type: 'FRUIT', price: 3 },
    { type: 'WEED', price: 3 },
    { type: 'BUG', price: 3 },
    { type: 'EGG', price: 3 },
    { type: 'KIBBLE', price: 3 },
    { type: 'BAMBOO', price: 3 },
  ];

  // 1. 서버에서 상점 아이템 목록 가져오기
  useEffect(() => {
    const loadItems = async () => {
      try {
        const data = await fetchShopItems(roomId);
        setCritters(data.critters || []);
      } catch (error) {
        alert(t('alert.load_fail'));
      }
    };
    loadItems();
  }, [roomId]);

  // 2. 입양 요청 처리
  const handleAdopt = async (critter) => {
    const translatedName = t(`item.critter.${critter.type.toLowerCase()}`);
    const name = prompt(t('alert.naming', { name: translatedName }), translatedName);
    if (name === null) return; 
    const finalName = name.trim() === '' ? translatedName : name;

    try {
      const data = await adoptCritterApi(roomId, currentUser.userId, finalName, critter.type);
      setCurrentUser(data.user);
      alert(t('alert.success', { name: finalName }));
      if (onAdoptSuccess) onAdoptSuccess();
      onClose();
    } catch (error) {
      console.error("입양 실패:", error);
      alert(t('error.adopt_fail')); // 에러 처리도 간단해짐!
    }
  };

  // 먹이 구매
  const handleBuyFood = async (food) => {
  try {
    await buyFoodApi(roomId, currentUser.userId, food.type);
    setCurrentUser(prev => ({ ...prev, point: prev.point - food.price }));

    if (onBuyFood) {
        onBuyFood(food.type);
    }

    alert(t('alert.buy_success'));
    onClose();
  } catch (error) {
    alert(t('alert.buy_fail'));
    console.log("구매 실패 이유: " + error);
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
      
      {/* 🟢 탭 버튼 */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
        {isMyRoom && (
          <button 
            onClick={() => setActiveTab('CRITTER')} 
            style={{ 
              padding: '8px 16px', 
              background: activeTab === 'CRITTER' ? '#3498db' : '#ccc', 
              color: 'white', border: 'none', borderRadius: '4px' 
            }}
          >
            {t('shop.critter')}
          </button>
        )}
        <button onClick={() => setActiveTab('FOOD')} style={{ padding: '8px 16px', background: activeTab === 'FOOD' ? '#e67e22' : '#ccc', color: 'white', border: 'none', borderRadius: '4px' }}>
          {t('shop.food')}
        </button>
      </div>
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '10px', maxHeight: '400px', overflowY: 'auto' }}>
        {/* 🐾 크리터 탭 */}
        {activeTab === 'CRITTER' && critters.map((critter) => {
          const isCompatible = critter.theme === currentRoom.roomTheme;
          return (
            <div key={critter.id} style={{ border: '1px solid #ccc', padding: '10px', borderRadius: '8px', background: 'white', textAlign: 'center', fontSize: '12px' }}>
              <h4>{t(`item.${critter.name}`)}</h4>
              <p>{critter.price}P</p>
              <button disabled={!isCompatible || currentUser.point < critter.price} onClick={() => handleAdopt(critter)} style={{ width: '100%' }}>
                {isCompatible ? (currentUser.point >= critter.price ? t('btn_adopt.adopt') : t('btn_adpot.no_point')) : t('btn_adopt.incompatible')}
              </button>
            </div>
          );
        })}

        {/* 🍔 먹이 탭 */}
        {activeTab === 'FOOD' && FOOD_ITEMS.map((food) => (
          <div key={food.type} style={{ border: '1px solid #e67e22', padding: '10px', borderRadius: '8px', background: 'white', textAlign: 'center', fontSize: '12px' }}>
            <h4>{t(`item.food.${food.type.toLowerCase()}`)}</h4>
            <p>{food.price}P</p>
            <button onClick={() => handleBuyFood(food)} style={{ width: '100%', background: '#e67e22', color: 'white', border: 'none' }}>구매</button>
          </div>
        ))}
      </div>
    </div>
  );
}