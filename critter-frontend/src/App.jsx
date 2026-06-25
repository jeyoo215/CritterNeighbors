import React, { useState } from 'react';
import AuthForm from './components/AuthForm';
import Lobby from './components/Lobby';
import EcosystemRoom from './components/EcosystemRoom';

export default function App() {
  // 1️⃣ 로그인한 유저 정보를 담을 전역 상태
  const [user, setUser] = useState(null);

  // 2️⃣ 현재 유저가 입장한 실제 방 정보를 가리키는 상태 (null이면 로비에 있는 상태)
  const [currentRoom, setCurrentUserId] = useState(null);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
  };

  // 🚪 로비에서 [입장하기] 또는 치트키 워프 버튼을 눌렀을 때 실행될 함수
  const handleEnterRoom = (roomData) => {
    setCurrentUserId(roomData);
  };

  // ↩️ 방 안에서 [로비로 나가기] 버튼을 눌렀을 때 다시 로비 껍데기로 복원하는 함수
  const handleLeaveRoom = () => {
    setCurrentUserId(null);
  };

  // 🔐 1차 관문: 로그인이 안 되어 있다면 무조건 로그인 폼 출력
  if (!user) {
    return (
      <div style={{ padding: '20px' }}>
        <AuthForm onLoginSuccess={handleLoginSuccess} />
      </div>
    );
  }

  // 🌌 2차 관문: 로그인은 됐는데 클릭해서 들어간 방(currentRoom)이 없다면? -> 로비 센터 출력!
  if (!currentRoom) {
    return (
      <Lobby 
        user={user} 
        onEnterRoom={handleEnterRoom} 
      />
    );
  }

  // 🐧 3차 관문: 로그인도 됐고, 입장한 방 정보도 존재한다면? -> 대망의 2D 생태계 관찰 룸 시동!
  return (
    <EcosystemRoom 
      roomId={currentRoom.roomId}
      currentUserId={user.userId} // 혹은 백엔드 스펙에 따라 user.id
      nickname={user.nickname}
      onLeaveRoom={handleLeaveRoom}
    />
  );
}