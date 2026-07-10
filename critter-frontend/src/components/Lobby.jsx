import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useTranslation } from 'react-i18next';

export default function Lobby({ user, setUser, onEnterRoom, onGoToBoard, onLogout }) {
  const [myRooms, setMyRooms] = useState([]);
  const [recommendedRooms, setRecommendedRooms] = useState([]);
  const [roomNameInput, setRoomNameInput] = useState('');
  const [themeInput, setThemeInput] = useState('OCEAN');

  const { t } = useTranslation('lobby');

  // 유저의 실제 방 목록 조회
  const fetchMyRooms = async () => {
    try {
      const response = await api.get(`/ecosystems/my`);
      setMyRooms(response.data);
    } catch (error) {
      console.error("방 목록 로딩 실패:", error);
    }
  };

  const fetchRecommendedRooms = async () => {
    try {
      // 서버에서 추천 목록 받아옴
      const response = await api.get(`/ecosystems/random`);
      setRecommendedRooms(response.data);
    } catch (error) {
      console.error("추천 방 로딩 실패:", error);
    }
  };

  useEffect(() => {
    fetchMyRooms();
    fetchRecommendedRooms();
  }, []);

  // 생태계 생성
  const createRoom = async () => {
    if (!roomNameInput.trim()) return alert(t('error.empty'));

    // 첫 방 무료, 추가 방은 50P 체크
    const cost = myRooms.length > 0 ? 50 : 0; 
    if (user.point < cost) {
      return alert(t('error.points', {cost: cost, points: user.point}));
    }

    try {
      const response = await api.post(`/ecosystems`, {
        userId: user.userId,
        roomName: roomNameInput,
        roomTheme: themeInput
      });

      const newRoom = await response.data;
        
      setUser(prev => ({ 
        ...prev, 
        point: prev.point - cost 
      }));

      alert(t('alert.create_room', {cost: cost}));
      setRoomNameInput('');
      fetchMyRooms();
    } catch (error) {
      console.error("방 생성 에러:", error);

      let errorMsg = t('error.cant_create');
      if (error.response && error.response.data) {
      errorMsg = typeof error.response.data === 'string' 
        ? error.response.data 
        : (error.response.data.message || errorMsg);
      }
      alert(errorMsg);
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h2>{t('title')}</h2>
      <p>👤<strong>{user.nickname} </strong>
      <button 
        onClick={onLogout} 
        style={{ padding: '8px 12px', backgroundColor: '#ff4d4f', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
      >
        {t('logout')}
      </button>
      </p>

      <button 
        className="board-btn" 
        onClick={onGoToBoard}
        style={{ padding: '10px 20px', marginBottom: '20px', cursor: 'pointer' }}
      >
        {t('goto_board')}
      </button>

      {/* 새 방 만들기 섹션 */}
      <div style={{ background: '#e3f2fd', padding: '20px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>{t('create_room.section_title')}</h3>
  
        <div style={{ marginBottom: '15px', fontSize: '14px', color: '#555' }}>
          <p>{t('create_room.point')}<strong>{user.point}P</strong></p>
        </div>

        <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
          <input 
            type="text" 
            placeholder={t('create_room.placeholder')}
            value={roomNameInput}
            onChange={(e) => setRoomNameInput(e.target.value)}
            style={{ padding: '8px', flex: 1 }}
          />
          <select value={themeInput} onChange={(e) => setThemeInput(e.target.value)} style={{ padding: '8px' }}>
            <option value="OCEAN">🌊OCEAN🌊</option>
            <option value="FOREST">🌳FOREST🌳</option>
            <option value="GRASSLAND">🌱GRASSLAND🌱</option>
          </select>
        </div>

        <button 
          onClick={createRoom} 
          disabled={user.point < (myRooms.length > 0 ? 50 : 0)}
          style={{ 
            padding: '10px 20px', 
            backgroundColor: user.point < (myRooms.length > 0 ? 50 : 0) ? '#ccc' : '#2196F3', 
            color: 'white', 
            border: 'none', 
            borderRadius: '4px', 
            cursor: user.point < (myRooms.length > 0 ? 50 : 0) ? 'not-allowed' : 'pointer'
          }}
          >
          {user.point < (myRooms.length > 0 ? 50 : 0) 
          ? t('error.points', { cost: 50, points: user.point })
          : t('create_room.button', { 
            price: myRooms.length > 0 ? "50P" : t('create_room.button_free') 
            })
          }
        </button>
      </div>

      {/* 방 목록 섹션 */}
      <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px' }}>
        <h3>{t('my_rooms.title')}</h3>
        {myRooms.length === 0 ? (
          <div style={{ color: '#666', padding: '10px 0' }}>
            <p>{t('my_rooms.empty')}</p>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
            {myRooms.map((myRoom) => (
              <div key={myRoom.roomId} style={{ background: 'white', border: '1px solid #ddd', padding: '15px', borderRadius: '6px', width: '200px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
                <h4>{myRoom.roomName}</h4>
                <p>{t('theme')} {myRoom.roomTheme}</p>
                <button 
                  onClick={() => onEnterRoom(myRoom)}
                  style={{ width: '100%', padding: '8px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                  {t('my_rooms.enter')}
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 추천 생태계 목록 섹션 */}
      <div style={{ background: '#fff3e0', padding: '20px', borderRadius: '8px', marginTop: '20px', border: '1px solid #ffe0b2' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '30px', marginBottom: '15px' }}>
          <h3 style={{ margin: 0 }}>
            {t('recommended_rooms.title')}
          </h3>
          <button 
            onClick={fetchRecommendedRooms}
            style={{
              padding: '6px 12px',
              backgroundColor: '#fff',
              color: '#fb8c00',
              border: '1px solid #fb8c00',
              borderRadius: '4px',
              cursor: 'pointer',
              fontWeight: 'bold',
              fontSize: '13px',
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              transition: 'all 0.2s'
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.backgroundColor = '#fb8c00';
              e.currentTarget.style.color = '#white';
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.backgroundColor = '#fff';
              e.currentTarget.style.color = '#fb8c00';
            }}
          >
            🔄 {t('recommended_rooms.refresh')} 
          </button>
        </div>
        {recommendedRooms.length === 0 ? (
          <div style={{ color: '#666', padding: '10px 0' }}>
            <p>{t('recommended_rooms.empty')}</p>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
            {recommendedRooms.map((room) => (
              <div key={room.roomId} style={{ 
                background: 'white', 
                border: '1px solid #ffe0b2', 
                padding: '15px', 
                borderRadius: '6px', 
                width: '200px', 
                boxShadow: '0 2px 4px rgba(0,0,0,0.05)' 
              }}>
                <h4 style={{ margin: '0 0 5px 0' }}>{room.roomName}</h4>
                <p style={{ margin: '0 0 10px 0', fontSize: '13px' }}>
                  {t('theme')} {room.roomTheme}
                </p>
                <button 
                  onClick={() => onEnterRoom(room)}
                  style={{ 
                    width: '100%', 
                    padding: '8px', 
                    backgroundColor: '#fb8c00',
                    color: 'white', 
                    border: 'none', 
                    borderRadius: '4px', 
                    cursor: 'pointer', 
                    fontWeight: 'bold' 
                  }}
                >
                  {t('recommended_rooms.enter')}
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}