import React, { useState } from 'react';
import AuthForm from './components/AuthForm';
import Lobby from './components/Lobby';
import EcosystemRoom from './components/EcosystemRoom';
import BoardList from './components/BoardList';
import BoardDetail from './components/BoardDetail';
import BoardCreate from './components/BoardCreate';

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

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem("user");
    setCurrentView("LOBBY");
    window.location.reload();
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
        setUser={setUser}
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

  // 게시판 목록
  if (currentView === "BOARD_LIST") {
    return (
      <BoardList 
        user={user}
        onLogout={handleLogout}
        onSelectBoard={(id) => { 
          setSelectedBoardId(id); 
          setCurrentView("BOARD_DETAIL");
        }}
        onGoToCreate={() => setCurrentView("BOARD_CREATE")}
        onBackToLobby={() => setCurrentView("LOBBY")} 
      />
    );
  }

  if (currentView === "BOARD_CREATE") {
    return (
      <BoardCreate 
        user={user} 
        onBackToList={() => setCurrentView("BOARD_LIST")} 
      />
    );
  }

  // 로비
  return (
    <Lobby 
      user={user}
      setUser={setUser}
      onLogout={handleLogout}
      onEnterRoom={(data) => { setCurrentRoom(data); setCurrentView("ROOM"); }}
      onGoToBoard={() => setCurrentView("BOARD_LIST")} // 리스트로 이동
    />
  );
}