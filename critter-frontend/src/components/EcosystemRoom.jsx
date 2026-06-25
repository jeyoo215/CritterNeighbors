import React, { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useNavigate } from 'react-router-dom';

const CANVAS_WIDTH = 800;
const CANVAS_HEIGHT = 600;

export default function EcosystemRoom({ roomId, currentUserId, nickname, onLeaveRoom }) {
  const navigate = useNavigate();
  
  const [critters, setCritters] = useState([]);
  const stompClientRef = useRef(null);

  // 🛒 [추가] 방 안에서 즉석으로 펭귄을 상점 분양받을 수 있게 하는 함수
  const adoptPenguinInside = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/ecosystems/${roomId}/creatures`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          creatureName: "핑구",
          creatureType: "PENGUIN" // 우리의 사랑 펭귄 🐧
        }),
      });

      if (response.ok) {
        alert("🐧 펭귄이 상점에서 분양되어 실시간 생태계 필드에 즉시 투입되었습니다!");
      } else {
        alert("분양 실패: 서식지 환경 테마를 확인하세요.");
      }
    } catch (error) {
      console.error("분양 중 에러:", error);
    }
  };

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws-ecosystem');
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        roomId: String(roomId), 
      },
      //debug: (str) => console.log(str),
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      console.log(`🔄 [Room ${roomId}] 웹소켓 연결 성공!`);
      
      // 방 입장 노크 신호 (Join)부터 먼저 보내서 서버가 이 방을 감지하게 함
      // EcosystemRoom.jsx -> onConnect 내부
      client.publish({
        destination: `/app/ecosystem/${roomId}/join`,
        body: JSON.stringify({ 
          userId: currentUserId, 
          nickname: nickname // 컨트롤러가 입장 처리를 함!
        })
      });

      // 🟢 0.5초 뒤에 구독을 시작해서 서버가 입장 처리를 마칠 시간을 벌어줌
      setTimeout(() => {
        client.subscribe(`/topic/ecosystem/${roomId}`, (message) => {
          if (message.body) {
            setCritters(JSON.parse(message.body));
          }
        });
      }, 500);
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        console.log('❌ 방 퇴장, 소켓 연결 해제 (인메모리 자원 청소 완료)');
      }
    };
  }, [roomId, currentUserId, nickname]);

  // 마우스 접근 시 PANIC 상태 변화 인터랙션 송신
  const handleMouseMove = (e) => {
    if (!stompClientRef.current || !stompClientRef.current.connected) return;

    const rect = e.currentTarget.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseY = e.clientY - rect.top;

    critters.forEach((critter) => {
      const distance = Math.hypot(critter.x - mouseX, critter.y - mouseY);
      
      if (distance < 100 && critter.status !== 'PANIC') {
        stompClientRef.current.publish({
          destination: '/app/mouse-move', 
          body: JSON.stringify({
            roomId: roomId,
            critterId: critter.critterId,
            status: 'PANIC' 
          }),
        });
      }
    });
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '10px' }}>
      <div style={{ display: 'flex', gap: '20px', alignItems: 'center', marginBottom: '15px', width: `${CANVAS_WIDTH}px`, justifyContent: 'space-between' }}>
        <h3 style={{ margin: 0 }}>🏞️ 현재 관찰 중: {roomId}번 생태계 서식지</h3>
        
        <div style={{ display: 'flex', gap: '10px' }}>
          {/* 🛒 즉석 펭귄 소환 버튼 결합 */}
          <button onClick={adoptPenguinInside} style={{ cursor: 'pointer', padding: '8px 16px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
            🐧 여기서 바로 펭귄 분양받기
          </button>
          
          <button onClick={onLeaveRoom} style={{ cursor: 'pointer', padding: '8px 16px', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
            🚪 방 나가기 (로비로)
          </button>
        </div>
      </div>

      {/* 🎨 실시간 도트 동물들이 뛰어놀 가상 생태계 필드 */}
      <div
        onMouseMove={handleMouseMove}
        style={{
          width: `${CANVAS_WIDTH}px`,
          height: `${CANVAS_HEIGHT}px`,
          backgroundColor: '#2c3e50', 
          position: 'relative',
          overflow: 'hidden',
          borderRadius: '12px',
          boxShadow: '0 8px 24px rgba(0,0,0,0.3)',
          cursor: 'crosshair',
          backgroundImage: 'radial-gradient(#34495e 1px, transparent 0)', 
          backgroundSize: '40px 40px'
        }}
      >
        {critters.map((critter) => (
          <div
            key={critter.critterId}
            style={{
              position: 'absolute',
              left: `${critter.x}px`,
              top: `${critter.y}px`,
              transform: 'translate(-50%, -50%)',
              transition: 'left 0.033s linear, top 0.033s linear', 
              textAlign: 'center',
              userSelect: 'none'
            }}
          >
            {/* 🟢 [싱크로 교정] 펭귄(PENGUIN) 이모지 매핑 장치 드디어 장착!! */}
            <div style={{ fontSize: '36px', filter: 'drop-shadow(0px 4px 4px rgba(0,0,0,0.2))' }}>
              {critter.creatureType === 'RABBIT' ? '🐇' : 
               critter.creatureType === 'FOX' ? '🦊' : 
               critter.creatureType === 'TURTLE' ? '🐢' : 
               critter.creatureType === 'OCTOPUS' ? '🐙' : 
               critter.creatureType === 'PENGUIN' ? '🐧' : '🐹'}
            </div>
            
            <div style={{
              backgroundColor: 'rgba(0,0,0,0.6)',
              color: 'white',
              fontSize: '11px',
              padding: '2px 6px',
              borderRadius: '4px',
              whiteSpace: 'nowrap',
              marginTop: '4px'
            }}>
              {critter.name} ({critter.status || 'IDLE'})
            </div>
          </div>
        ))}

        {critters.length === 0 && (
          <div style={{ color: '#ccc', textAlign: 'center', marginTop: '260px', fontSize: '15px', fontWeight: 'bold' }}>
            ⏳ 🐧 생태계가 고요합니다. 상점에서 펭귄을 분양해 이 서식지를 가동해 주세요!
          </div>
        )}
      </div>
    </div>
  );
}