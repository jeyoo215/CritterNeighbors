import React, { useEffect, useState, useRef } from 'react';
import api from '../api/axios';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { fetchGuestbooks, postGuestbook } from '../api/guestbookApi';
import { adoptCritterApi } from '../api/shopApi';
import { getCritterImagePath } from '../constants/critterImages';
import Shop from './Shop';
import { useTranslation } from 'react-i18next';

const CANVAS_WIDTH = 800;
const CANVAS_HEIGHT = 600;

export default function EcosystemRoom({ currentRoom, currentUser, setUser, refreshUser, onLeaveRoom }) {
  const [critters, setCritters] = useState([]);
  const [guestbooks, setGuestbooks] = useState([]); // 방명록
  const [newContent, setNewContent] = useState(''); // 입력창
  const [showShop, setShowShop] = useState(false); // 상점
  const [chatMessages, setChatMessages] = useState([]); // 채팅
  const [newChatMessage, setNewChatMessage] = useState('');
  const [activeTab, setActiveTab] = useState('GUESTBOOK'); // 접속 시 방명록으로 고정

  const { t } = useTranslation('ecosystemroom');

  // 배경 설정
  const theme = currentRoom?.roomTheme || 'DEFAULT';
  const backgroundStyle = {
    backgroundImage: `url(/${theme}.png)`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    width: `${CANVAS_WIDTH}px`,
    height: `${CANVAS_HEIGHT}px`,
    position: 'relative',
    borderRadius: '12px',
    overflow: 'hidden'
  };

  const stompClientRef = useRef(null);
  const roomId = currentRoom.roomId;
  const userId = currentUser.userId;

  // 방명록 불러오기 (GET)
  const loadGuestbooks = async () => {
    try {
      const data = await fetchGuestbooks(roomId);
      setGuestbooks(data);
    } catch (error) {
      console.error("방명록 로딩 실패:", error);
    }
  };

  // 방명록 작성 (POST)
  const handleSendGuestbook = async () => {
    
    if (!newContent.trim()) return;

    if (!roomId) {
      alert(t('alert.no_room_id'));
      return;
    }

    if (!userId) {
      alert(t('alert.login_required'));
      return;
    } 

    try {
      await postGuestbook(roomId, userId, newContent);
      if (refreshUser) {
        await refreshUser(); 
      }
      setNewContent('');
      loadGuestbooks();
    } catch (error) {
      if (error && error.message) {
        alert(error.message);
      } else {
        alert(t('alert.error'));
      }
    }
  };

  // 크리터 클릭
  const handleCritterClick = (critter, e) => {
    if (!stompClientRef.current || !stompClientRef.current.connected) return;
  
    // 마우스 위치 전달해서 서버가 방향을 계산하게 함
    const rect = e.currentTarget.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    stompClientRef.current.publish({
      destination: `/app/ecosystem/${roomId}/interact`,
      body: JSON.stringify({ 
        critterId: critter.critterId, 
        mouseX: mouseX, 
        mouseY: mouseY 
      })
    });
  };

  // 채팅 전송
  const handleSendChatMessage = () => {
    if (!newChatMessage.trim()) return;
    stompClientRef.current.publish({
      destination: `/app/chat/${roomId}/send`,
      body: JSON.stringify({
        sender: currentUser.nickname,
        content: newChatMessage
      })
    });
    setNewChatMessage('');
  };

  const visit = async () => {
    try {
        await api.post(`/users/visit/${roomId}`);
        if (refreshUser) refreshUser();
    } catch (err) {
        console.error("방문 포인트 적립 실패:", err);
    }
  };

  useEffect(() => {
    loadGuestbooks();
    const socket = new SockJS('http://localhost:8080/ws-ecosystem');
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: { roomId: String(roomId) },
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      visit();

      // 입장 알림
      client.publish({
        destination: `/app/ecosystem/${roomId}/join`,
        body: JSON.stringify({ userId: userId, nickname: currentUser.nickname })
      });

      // 데이터 구독
      client.subscribe(`/topic/ecosystem/${roomId}`, (message) => {
        if (message.body) setCritters(JSON.parse(message.body));
      });

      // 채팅 구독
      client.subscribe(`/topic/chat/${roomId}`, (msg) => {
        const chat = JSON.parse(msg.body);
        setChatMessages((prev) => [...prev, chat]);
      });
    };

    client.activate();
    stompClientRef.current = client;

  }, [roomId, userId, currentUser.nickname]);


  return (
    <div style={{ padding: '20px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <div style={{ width: `${CANVAS_WIDTH}px`, display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
        <h3>
          {t('room.title', {nickname : currentRoom.account.nickname, theme: currentRoom.roomTheme})}
        </h3>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'flex-end', 
          gap: '10px'
        }}>
        {currentUser.userId === currentRoom.account.userId && (
          <button onClick={() => setShowShop(true)} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '4px' }}>
            {t('room.btn_shop')}
          </button>
        )}
        <button onClick={onLeaveRoom} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '4px' }}>
          {t('room.btn_leave')}
        </button>
        </div>
      </div>

      {showShop && (
      <div style={{ position: 'fixed', top: '10%', left: '20%', width: '60%', zIndex: 1000 }}>
        <Shop 
          roomId={roomId}
          currentUser={currentUser}
          setCurrentUser={setUser}
          currentRoom={currentRoom}
          onClose={() => setShowShop(false)}
          onAdopt={async (critter) => {
            try {
              await adoptCritterApi(roomId, currentUser.userId, critter);
              const response = await api.get(`/users/${currentUser.userId}`);
              setUser(response.data);
              alert(t('alert.adopt_success', {name: critter.name}));
            } catch (error) {
              alert(t('alert.adopt_fail', {message: error.message}));
            }
          }}
        />
      </div>
      )}

      <div style={{ display: 'flex', gap: '20px' }}>
        {/* 생태계 필드 */}
        <div style={backgroundStyle}>
          {critters.map((critter) => (
            <CritterRendering 
              key={critter.critterId} 
              critter={critter} 
              onClick={(e) => handleCritterClick(critter, e)}
            />
          ))}
        </div>

        {/* 🚨 [통합 소셜 사이드바] 방명록과 채팅 스위칭 영역 */}
        <div style={{ 
          width: '300px', 
          height: `${CANVAS_HEIGHT}px`, 
          backgroundColor: '#f4f4f4', 
          padding: '15px', 
          borderRadius: '8px', 
          display: 'flex', 
          flexDirection: 'column' 
        }}>
  
          {/* 1. 상단 탭 헤더 */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <h4 style={{ margin: 0 }}>{activeTab === 'GUESTBOOK' ? t('sidebar.guestbook_tab') : t('sidebar.chat_tab')}</h4>
            <button 
              onClick={() => setActiveTab(activeTab === 'GUESTBOOK' ? 'CHAT' : 'GUESTBOOK')}
              style={{ fontSize: '12px', cursor: 'pointer' }}
            >
              {activeTab === 'GUESTBOOK' ? t('sidebar.chat_tab') : t('sidebar.guestbook_tab')}
            </button>
          </div>

          {/* 2. 탭에 따른 본문 출력 */}
          {activeTab === 'GUESTBOOK' ? (
          /* 기존에 쓰던 방명록 스타일 유지 */
          <>
            <div style={{ flex: 1, overflowY: 'auto', marginBottom: '10px', border: '1px solid #ddd', padding: '10px', backgroundColor: 'white' }}>
              {guestbooks.map((gb, i) => (
                <p key={gb.guestbookId || i}><strong>{gb.writer?.nickname || t('guestbook.no_name')}:</strong> {gb.content}</p>
              ))}
            </div>
            {currentUser.userId !== currentRoom.account.userId && (
              <>
                <textarea value={newContent} onChange={(e) => setNewContent(e.target.value)} style={{ height: '60px', marginBottom: '10px' }} />
                <button onClick={handleSendGuestbook} style={{ padding: '10px', backgroundColor: '#3498db', color: 'white', border: 'none', cursor: 'pointer' }}>
                  {t('guestbook.btn_write')}
                </button>
              </>
            )}
          </>
          ) : (
            <>
              <div style={{ flex: 1, overflowY: 'auto', marginBottom: '10px', border: '1px solid #ddd', padding: '10px', backgroundColor: 'white' }}>
                {chatMessages.map((msg, i) => (
                  <p key={i}><strong>{msg.sender}:</strong> {msg.content}</p>
                ))}
              </div>
              <textarea 
                value={newChatMessage} 
                onChange={(e) => setNewChatMessage(e.target.value)} 
                onKeyDown={(e) => e.key === 'Enter' && handleSendChatMessage()}
                style={{ height: '60px', marginBottom: '10px' }} 
                placeholder={t('chat.placeholder')}
              />
              <button onClick={handleSendChatMessage} style={{ padding: '10px', backgroundColor: '#2ecc71', color: 'white', border: 'none', cursor: 'pointer' }}>
                {t('chat.btn_send')}
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}


// 이미지
// EcosystemRoom.jsx 파일 맨 아래
function CritterRendering({ critter, onClick }) {
  const imagePath = getCritterImagePath(critter.critterType, critter.status);
  const [useEmoji, setUseEmoji] = useState(false);

  const handleError = () => setUseEmoji(true);

  const SCALE_MAP = {
    OCTOPUS: 1.7,
    TURTLE: 2.0,
    PENGUIN: 2.5,
    SQUIRREL: 2.0,
    FOX: 3.0,
    REDPANDA: 2.5,
    RABBIT: 0.7,
    DOG: 0.7,
    CAT: 2.5
  }
  const scale = SCALE_MAP[critter.critterType] || 1.0;

  const MARGIN_MAP = {
    OCTOPUS: '0px',
    TURTLE: '20px',
    PENGUIN: '-30px',
    SQUIRREL: '20px',
    FOX: '35px',
    REDPANDA : '30px',
    RABBIT: '0px',
    DOG: '0px',
    CAT: '30px'
  };
  const nameMargin = MARGIN_MAP[critter.critterType] || '5px';

  const EMOJI_MAP = {
    CAT: '🐈',
    DOG: '🐕',
    FOX: '🦊',
    OCTOPUS: '🐙',
    PENGUIN: '🐧',
    RABBIT: '🐇',
    REDPANDA: '🐼',
    SQUIRREL: '🐿',
    TURTLE: '🐢'
  };

  return (
    <div
      onClick={onClick}
      style={{ 
        position: 'absolute', 
        left: `${critter.x}px`, 
        top: `${critter.y}px`, 
        transform: 'translate(-50%, -50%)',
        transition: 'left 0.033s linear, top 0.033s linear',
        width: '60px', // 이름을 담으려고 조금 넓혔어
        height: '60px',
        display: 'flex',
        flexDirection: 'column', // 위아래로 쌓기
        alignItems: 'center',
        justifyContent: 'center'
      }}>

      <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        {useEmoji ? (
          <div style={{ fontSize: '30px' }}>{EMOJI_MAP[critter.critterType] || '🐾'}</div>
        ) : (
          <img 
            src={imagePath} 
            alt={critter.critterType} 
            onError={handleError} 
            style={{ width: '100%', height: '100%', transform: `scale(${scale})`, objectFit: 'contain' }}
          />
        )}
      </div>
      <div style={{
        marginTop: nameMargin,
        padding: '2px 6px',
        backgroundColor: 'rgba(0, 0, 0, 0.5)', // 반투명 검은 판
        color: 'white',
        borderRadius: '4px',
        fontSize: '10px',
        whiteSpace: 'nowrap', // 이름 길어도 한 줄 유지
        pointerEvents: 'none' // 이름표 클릭 시 동물 클릭 안 되게 방지
      }}>
        {critter.name || "이름없음"}
      </div>
    </div>
  );
}