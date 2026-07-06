import React, { useState, useEffect } from 'react';
import api from './api/axios';
import AuthForm from './components/AuthForm';
import Lobby from './components/Lobby';
import EcosystemRoom from './components/EcosystemRoom';
import BoardList from './components/BoardList';
import BoardDetail from './components/BoardDetail';
import BoardCreate from './components/BoardCreate';

export default function App() {
  const [user, setUser] = useState(null);
  const [currentRoom, setCurrentRoom] = useState(null);
  const [currentView, setCurrentView] = useState("LOBBY"); 
  const [selectedBoardId, setSelectedBoardId] = useState(null);

  const refreshUser = async () => {
    try {
      const res = await api.get('/users/me'); 
      setUser(res.data);
    } catch (err) {
      setUser(null);
    }
  };

  useEffect(() => {
    refreshUser();
  }, []);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem("user", JSON.stringify(userData));
  };

  const handleLogout = async() => {
    try {
      await api.post('/users/logout');
    } catch (err) {
      console.error("로그아웃 실패:", err);
    } finally {
      localStorage.removeItem("user");
      setUser(null);
      setCurrentRoom(null);
      window.location.href = "/";
    }
  };

  if (!user) {
    return <AuthForm onLoginSuccess={handleLoginSuccess} />;
  }

  switch (currentView) {
    case "ROOM":
      return (
        <EcosystemRoom 
          currentRoom={currentRoom}
          currentUser={user}
          refreshUser={refreshUser}
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
          refreshUser={refreshUser}
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
          refreshUser={refreshUser}
        />
      );
    default: // LOBBY
      return (
        <Lobby 
          user={user}
          setUser={setUser}
          onLogout={handleLogout}
          refreshUser={refreshUser}
          onEnterRoom={(data) => { setCurrentRoom(data); setCurrentView("ROOM"); }}
          onGoToBoard={() => setCurrentView("BOARD_LIST")}
        />
      );
  }
}