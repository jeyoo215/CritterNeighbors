import React, { useState, useEffect } from 'react';

export default function Lobby({ user, setUser, onEnterRoom, onGoToBoard, onLogout }) {
  const [myRooms, setMyRooms] = useState([]);
  const [recommendedRooms, setRecommendedRooms] = useState([]);
  const [roomNameInput, setRoomNameInput] = useState('');
  const [themeInput, setThemeInput] = useState('OCEAN');

  // 🔍 마운트 시 유저의 실제 방 목록 조회
  const fetchMyRooms = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/ecosystems/my?userId=${user.userId}`);
      if (response.ok) {
        const data = await response.json();
        setMyRooms(data);
      }
    } catch (error) {
      console.error("방 목록 로딩 실패:", error);
    }
  };

  const fetchRecommendedRooms = async () => {
    try {
      // 예: 서버에서 랜덤 5개를 주는 엔드포인트가 있다고 가정
      const response = await fetch(`http://localhost:8080/api/ecosystems/random?userId=${user.userId}`);
      if (response.ok) {
        const data = await response.json();
        setRecommendedRooms(data);
      }
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
    if (!roomNameInput.trim()) return alert("방 이름을 입력해 주세요!");

    // 1. 첫 방 무료, 추가 방은 50P 체크 (백엔드 로직에 맞춰서 예외 처리 추가 가능)
    const cost = myRooms.length > 0 ? 50 : 0; 
    if (user.point < cost) {
      return alert(`포인트가 부족합니다! (필요: ${cost}P, 보유: ${user.point}P)`);
    }

    try {
      const response = await fetch("http://localhost:8080/api/ecosystems", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId: user.userId,
          roomName: roomNameInput,
          roomTheme: themeInput
        }),
      });

      if (response.ok) {
        const newRoom = await response.json();
        
        setUser(prev => ({ 
          ...prev, 
          point: prev.point - cost 
        }));

        alert(`🌊 방 개설 완료! (-${cost}P)`);
        setRoomNameInput('');
        fetchMyRooms();
      } else {
        const errorMsg = await response.text();
        alert(errorMsg || "방 생성 실패!");
      }
    } catch (error) {
      console.error("방 생성 에러:", error);
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h2>🏢 생태계 통합 로비 센터</h2>
      <p>👤 <strong>{user.nickname}</strong>
      <strong> </strong>
      <button 
        onClick={onLogout} 
        style={{ padding: '8px 12px', backgroundColor: '#ff4d4f', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
      >
        로그아웃
      </button>
      </p>

      {/* 3. 버튼 로직 수정 */}
      <button 
        className="board-btn" 
        onClick={onGoToBoard} // 👈 navigate 대신 이 함수 호출
        style={{ padding: '10px 20px', marginBottom: '20px', cursor: 'pointer' }}
      >
        질문 게시판 가기 📢
      </button>

      {/* 🛠️ 새 방 만들기 섹션 */}
      <div style={{ background: '#e3f2fd', padding: '20px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>🛠️ 새 생태계 서식지 개설</h3>
  
        <div style={{ marginBottom: '15px', fontSize: '14px', color: '#555' }}>
          <p>보유 포인트: <strong>{user.point}P</strong></p>
        </div>

        <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
          <input 
            type="text" 
            placeholder="생태계 서식지 이름 입력" 
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
            ? "포인트 부족"
            : `➕ 생태계 생성 (${myRooms.length > 0 ? "50P" : "무료"})`
          } 
        </button>
      </div>

      {/* 📋 방 목록 섹션 */}
      <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px' }}>
        <h3>🗺️ 활성화된 생태계 목록</h3>
        {myRooms.length === 0 ? (
          <div style={{ color: '#666', padding: '10px 0' }}>
            <p>아직 개설된 생태계가 존재하지 않습니다.</p>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
            {myRooms.map((myRoom) => (
              <div key={myRoom.roomId} style={{ background: 'white', border: '1px solid #ddd', padding: '15px', borderRadius: '6px', width: '200px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
                <h4>{myRoom.roomName}</h4>
                <p>테마: {myRoom.roomTheme}</p>
                <button 
                  onClick={() => onEnterRoom(myRoom)}
                  style={{ width: '100%', padding: '8px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                  🚪 입장하기
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 🌐 추천 생태계 목록 섹션 */}
      <div style={{ background: '#fff3e0', padding: '20px', borderRadius: '8px', marginTop: '20px', border: '1px solid #ffe0b2' }}>
        <h3>🌐 여행 가기</h3>
        {recommendedRooms.length === 0 ? (
          <div style={{ color: '#666', padding: '10px 0' }}>
            <p>현재 여행할 수 있는 다른 생태계가 없습니다.</p>
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
                <p style={{ margin: '0 0 10px 0', fontSize: '13px' }}>테마: {room.roomTheme}</p>
                <button 
                  onClick={() => onEnterRoom(room)}
                  style={{ 
                    width: '100%', 
                    padding: '8px', 
                    backgroundColor: '#fb8c00', // 추천 생태계는 따뜻한 오렌지색!
                    color: 'white', 
                    border: 'none', 
                    borderRadius: '4px', 
                    cursor: 'pointer', 
                    fontWeight: 'bold' 
                  }}
                >
                  🚀 구경가기
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}