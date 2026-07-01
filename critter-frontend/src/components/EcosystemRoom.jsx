import React, { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { fetchGuestbooks, postGuestbook } from '../api/guestbookApi';
import { adoptCritterApi } from '../api/shopApi';
import { getCritterImagePath } from '../constants/critterImages';
import Shop from './Shop';

const CANVAS_WIDTH = 800;
const CANVAS_HEIGHT = 600;

export default function EcosystemRoom({ currentRoom, currentUser, setUser, onLeaveRoom }) {
  const [critters, setCritters] = useState([]);
  const [guestbooks, setGuestbooks] = useState([]); // 방명록
  const [newContent, setNewContent] = useState(''); // 입력창
  const [showShop, setShowShop] = useState(false); // 상점
  const stompClientRef = useRef(null);

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

  const roomId = currentRoom.roomId;
  const userId = currentUser.userId;

  // 1. 방 입장 시 방명록 불러오기 (GET)
  const loadGuestbooks = async () => {
    try {
      const data = await fetchGuestbooks(roomId);
      setGuestbooks(data);
    } catch (error) {
      console.error("방명록 로딩 실패:", error);
    }
  };

  // 2. 방명록 작성 (POST)
  const handleSendGuestbook = async () => {
    console.log("전송할 roomId:", roomId); 
    console.log("전송할 writerId:", userId);

    if (!newContent.trim()) return;

    if (!roomId) {
      alert("방 ID가 없습니다!"); // 👈 이 메시지가 뜨면 100% roomId가 안 들어온 거임
      return;
    }

    if (!userId) {
      alert("로그인이 필요한 기능입니다.");
      return;
    } 

    try {
      await postGuestbook(roomId, userId, newContent);
      setNewContent('');
      loadGuestbooks(); // 작성 후 새로고침
    } catch (error) {
      if (error && error.message) {
        alert(error.message);
      } else {
        alert("알 수 없는 오류가 발생했습니다.");
      }
    }
  };

  const handleCritterClick = (critter, e) => {
    if (!stompClientRef.current || !stompClientRef.current.connected) return;
  
    // 마우스 위치 전달해서 서버가 방향을 계산하게 함
    const rect = e.currentTarget.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    stompClientRef.current.publish({
      destination: `/app/ecosystem/${roomId}/interact`, // 서버에 컨트롤러 메서드 매핑 주소
      body: JSON.stringify({ 
        critterId: critter.critterId, 
        action: "CLICK",
        mouseX: mouseX, 
        mouseY: mouseY 
      })
    });
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
      // 입장 알림
      client.publish({
        destination: `/app/ecosystem/${roomId}/join`,
        body: JSON.stringify({ userId: userId, nickname: currentUser.nickname })
      });

      // 데이터 구독
      client.subscribe(`/topic/ecosystem/${roomId}`, (message) => {
        if (message.body) setCritters(JSON.parse(message.body));
      });
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (stompClientRef.current) stompClientRef.current.deactivate();
    };
  }, [roomId, userId, currentUser.nickname]);


  return (
    <div style={{ padding: '20px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <div style={{ width: `${CANVAS_WIDTH}px`, display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
        <h3>🏞️현재 관찰 중: {currentRoom.account.nickname}님의 {currentRoom.roomTheme} 생태계</h3>
        <div style={{ 
          display: 'flex', 
          justifyContent: 'flex-end', 
          gap: '10px'
        }}>
        {currentUser.userId === currentRoom.account.userId && (
          <button onClick={() => setShowShop(true)} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '4px' }}>
            🛒 상점 열기
          </button>
        )}
        <button onClick={onLeaveRoom} style={{ backgroundColor: '#e74c3c', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '4px' }}>
          🚪 방 나가기
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
              const response = await fetch(`http://localhost:8080/api/users/${currentUser.userId}`);
              const updatedUser = await response.json();
              setUser(updatedUser);
              alert(`${critter.name} 입양 성공!`);
            } catch (error) {
              alert("입양 실패: " + error.message);
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

        {/* 방명록 UI 영역 */}
        <div style={{ width: '300px', height: `${CANVAS_HEIGHT}px`, backgroundColor: '#f4f4f4', padding: '15px', borderRadius: '8px', display: 'flex', flexDirection: 'column' }}>
          <h4>💬방명록</h4>
          <div style={{ flex: 1, overflowY: 'auto', marginBottom: '10px', border: '1px solid #ddd', padding: '10px', backgroundColor: 'white' }}>
            {guestbooks.map((gb, i) => <p key={gb.guestbookId || i}><strong>{gb.writer?.nickname || "알수없음"}:</strong> {gb.content}</p>)}
          </div>
          {currentUser.userId !== currentRoom.account.userId && (
            <>
              <textarea value={newContent} onChange={(e) => setNewContent(e.target.value)} style={{ height: '60px', marginBottom: '10px' }} />
              <button onClick={handleSendGuestbook} style={{ padding: '10px', backgroundColor: '#3498db', color: 'white', border: 'none', cursor: 'pointer' }}>작성하기</button>
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
    TURTLE: 1.5,
    PENGUIN: 4.0,
    SQUIRREL: 2.0,
    FOX: 2.0,
    REDPANDA: 2.0,
    RABBIT: 1.0,
    DOG: 1.0,
    CAT: 2.0
  }

  const scale = SCALE_MAP[critter.critterType] || 1.0;

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
        width: '40px',
        height: '40px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}>

      {useEmoji ? (
        <div style={{ fontSize: '30px' }}>{EMOJI_MAP[critter.critterType] || '🐾'}</div>
      ) : (
        <img 
          src={imagePath} 
          alt={critter.critterType} 
          onError={handleError} 
          style={{ 
            width: '100%', 
            height: '100%', 
            transform: `scale(${scale})`,
            objectFit: 'contain',
            display: 'block' 
          }}
        />
      )}
    </div>
  );
}