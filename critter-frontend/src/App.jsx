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
  const [selectedBoardId, setSelectedBoardId] = useState(null);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem("user", JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem("user");
    setCurrentView("LOBBY");
    window.location.reload(); // 로그아웃 시 깔끔하게 초기화
  };

  // 1️⃣ 로그인 여부 확인
  if (!user) {
    return <AuthForm onLoginSuccess={handleLoginSuccess} />;
  }

  // 2️⃣ 화면 분기 처리 (로그인 상태일 때)
  switch (currentView) {
    case "ROOM":
      return (
        <EcosystemRoom 
          currentRoom={currentRoom}
          currentUser={user}
          setUser={setUser}
          onLeaveRoom={() => setCurrentView("LOBBY")} 
        />
      );
    case "BOARD_DETAIL":
      return (
        <BoardDetail 
          boardId={selectedBoardId}
          setBoardId={setSelectedBoardId}
          user={user} 
          onBackToList={() => setCurrentView("BOARD_LIST")} 
        />
      );
    case "BOARD_LIST":
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
    case "BOARD_CREATE":
      return (
        <BoardCreate 
          user={user} 
          onBackToList={() => setCurrentView("BOARD_LIST")} 
        />
      );
    default: // LOBBY
      return (
        <Lobby 
          user={user}
          setUser={setUser}
          onLogout={handleLogout}
          onEnterRoom={(data) => { setCurrentRoom(data); setCurrentView("ROOM"); }}
          onGoToBoard={() => setCurrentView("BOARD_LIST")}
        />
      );
  }
}