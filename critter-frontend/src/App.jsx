import React, { useState } from 'react';
import AuthForm from './components/AuthForm';
import Lobby from './components/Lobby';
import EcosystemRoom from './components/EcosystemRoom';
import BoardList from './components/BoardList';
import BoardDetail from './components/BoardDetail'; // 🆕 상세 페이지 컴포넌트 임포트

export default function App() {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [currentRoom, setCurrentRoom] = useState(null);
  const [currentView, setCurrentView] = useState("LOBBY"); 
  const [selectedBoardId, setSelectedBoardId] = useState(null); // 🆕 선택한 글 ID 관리

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem("user", JSON.stringify(userData));
  };

  // 3️⃣ [수정됨] 로그아웃 시 localStorage에서도 삭제
  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem("user");
    setCurrentView("LOBBY"); // 로비로 복귀
    window.location.reload(); // 🚨 깔끔하게 새로고침 한 번 때려주는 게 제일 확실해!
  };

  // 1️⃣ 로그인 체크
  if (!user) return <AuthForm onLoginSuccess={handleLoginSuccess} />;

  // 2️⃣ 화면 분기 처리
  
  // 룸 입장 화면
  if (currentView === "ROOM") {
    return (
      <EcosystemRoom 
        currentRoom={currentRoom}
        currentUser={user}
        onLeaveRoom={() => setCurrentView("LOBBY")} 
      />
    );
  }

  // 게시판 상세 페이지
  if (currentView === "BOARD_DETAIL") {
    return (
      <BoardDetail 
        boardId={selectedBoardId} 
        user={user} 
        onBackToList={() => setCurrentView("BOARD_LIST")} 
      />
    );
  }

  // 게시판 목록 화면
  if (currentView === "BOARD_LIST") {
    return (
      <BoardList 
        user={user}
        onLogout={handleLogout}
        onSelectBoard={(id) => { 
          setSelectedBoardId(id); 
          setCurrentView("BOARD_DETAIL");
        }}
        onBackToLobby={() => setCurrentView("LOBBY")} 
      />
    );
  }

  // 3️⃣ 기본 로비 화면
  return (
    <Lobby 
      user={user}
      onLogout={handleLogout}
      onEnterRoom={(data) => { setCurrentRoom(data); setCurrentView("ROOM"); }}
      onGoToBoard={() => setCurrentView("BOARD_LIST")} // 리스트로 이동
    />
  );
}