import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // 👈 이거 추가!

export default function Lobby({ user, onEnterRoom }) {
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [roomNameInput, setRoomNameInput] = useState('');
  const [themeInput, setThemeInput] = useState('OCEAN');

  // 🔍 마운트 시 유저의 실제 방 목록 조회
  const fetchMyRooms = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/ecosystems/my");
      if (response.ok) {
        const data = await response.json();
        setRooms(data);
      }
    } catch (error) {
      console.error("방 목록 로딩 실패:", error);
    }
  };

  useEffect(() => {
    fetchMyRooms();
  }, []);

  // ➕ 생태계 방 생성 함수
  const createRoom = async () => {
    if (!roomNameInput.trim()) return alert("방 이름을 입력해 주세요!");

    try {
      const response = await fetch("http://localhost:8080/api/ecosystems", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          roomName: roomNameInput,
          roomTheme: themeInput
        }),
      });

      if (response.ok) {
        const newRoom = await response.json();
        alert(`🌊 방 개설 완료! (ID: ${newRoom.roomId})`);
        setRoomNameInput('');
        fetchMyRooms(); // 목록 새로고침
      } else if (response.status === 401) {
        alert("💡 백엔드 세션(401) 제한이 걸려있습니다. 하단 가상 수동 점프 버튼을 이용하세요!");
      }
    } catch (error) {
      console.error("방 생성 에러:", error);
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h2>🏢 생태계 통합 로비 센터</h2>
      <p>👤 <strong>{user.nickname}</strong>님의 개인 관리 대시보드</p>

      <button 
        className="board-btn" 
        onClick={() => navigate('/board')}
      >
        질문 게시판 가기 📢
      </button>

      {/* 🛠️ 새 방 만들기 섹션 */}
      <div style={{ background: '#e3f2fd', padding: '20px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>🛠️ 새 생태계 서식지 개설</h3>
        <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
          <input 
            type="text" 
            placeholder="생태계 서식지 이름 입력" 
            value={roomNameInput}
            onChange={(e) => setRoomNameInput(e.target.value)}
            style={{ padding: '8px', flex: 1 }}
          />
          <select value={themeInput} onChange={(e) => setThemeInput(e.target.value)} style={{ padding: '8px' }}>
            <option value="OCEAN">🌊 OCEAN (펭귄 서식지 필수)</option>
            <option value="FOREST">🌳 FOREST</option>
            <option value="GRASSLAND">🌱 GRASSLAND</option>
          </select>
        </div>
        <button onClick={createRoom} style={{ padding: '10px 20px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          ➕ 생태계 생성하기
        </button>
      </div>

      {/* 📋 방 목록 섹션 */}
      <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px' }}>
        <h3>🗺️ 나의 활성화된 생태계 목록</h3>
        {rooms.length === 0 ? (
          <div style={{ color: '#666', padding: '10px 0' }}>
            <p>아직 개설된 실제 DB 방이 목록에 없습니다. (새로고침 시 인메모리 create 초기화 현상)</p>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
            {rooms.map((room) => (
              <div key={room.roomId} style={{ background: 'white', border: '1px solid #ddd', padding: '15px', borderRadius: '6px', width: '200px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
                <h4>{room.roomName}</h4>
                <p>테마: {room.roomTheme}</p>
                <button 
                  onClick={() => onEnterRoom(room)}
                  style={{ width: '100%', padding: '8px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                  🚪 입장하기
                </button>
              </div>
            ))}
          </div>
        )}

        {/* 🚨 중요: 치트키 수동 점프 장치 */}
        <hr style={{ marginTop: '20px', border: '0', borderTop: '1px dashed #ccc' }} />
        <h4 style={{ color: '#d32f2f' }}>🚀 테스트 수동 점프 장치 (새로고침 구원용)</h4>
        <p style={{ fontSize: '13px', color: '#666' }}>백엔드가 재부팅되었거나 방 목록이 비어있을 때, 방금 성공했던 그 소켓 번호 채널로 직통 워프합니다.</p>
        <button 
          onClick={() => onEnterRoom({ roomId: 6, roomName: "우리가 성공시킨 6번방", roomTheme: "OCEAN" })}
          style={{ padding: '10px 15px', backgroundColor: '#ff9800', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
        >
          🐧 6번 펭귄방 강제 입장하기
        </button>
      </div>
    </div>
  );
}