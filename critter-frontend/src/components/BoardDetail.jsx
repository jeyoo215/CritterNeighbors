import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { 
  fetchBoardDetail, 
  fetchNeighbors,
  getCommentsByBoard, 
  postComment, 
  deleteBoard, 
  deleteComment, 
  updateBoard, 
  updateComment
} from '../api/boardApi';

export default function BoardDetail({ boardId, setBoardId, user, onBackToList, refreshUser }) {
  // 게시글
  const [board, setBoard] = useState(null);
  // 이전글 다음글
  const [neighbors, setNeighbors] = useState({ prev: null, next: null });
  // 댓글
  const [comments, setComments] = useState([]);
  const [comment, setComment] = useState('');
  // 게시글 수정
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ title: '', content: '' });
  // 댓글 수정
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editCommentContent, setEditCommentContent] = useState('');

  const loadAllData = async () => {
    try {
        const boardRes = await fetchBoardDetail(boardId);
        setBoard(boardRes.data);
        const commentRes = await getCommentsByBoard(boardId);
        setComments(commentRes.data);
        
        const neighborRes = await fetchNeighbors(boardId); 
        setNeighbors(neighborRes.data);
    } catch (e) {
      console.error("데이터 불러오기 실패", e);
    }
  };

  useEffect(() => {
    loadAllData();
  }, [boardId]);

  // 핸들러 함수들
  const handleCommentSubmit = async () => {
    if (!comment) return alert("댓글 내용을 입력하세요.");
    await postComment(boardId, { writerId: user.userId, content: comment });
    if (refreshUser) {
        await refreshUser(); 
    }
    setComment('');
    loadAllData();
  };

  const handleDeleteBoard = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    await deleteBoard(boardId);
    onBackToList();
  };

  const handleUpdateBoard = async () => {
    await updateBoard(boardId, editForm);
    setIsEditing(false);
    loadAllData();
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    await deleteComment(commentId);
    loadAllData();
  };

  const startEditComment = (c) => {
    setEditingCommentId(c.commentId);
    setEditCommentContent(c.content);
  };

  const handleUpdateComment = async (commentId) => {
    await updateComment(commentId, { content: editCommentContent });
    setEditingCommentId(null);
    loadAllData();
  };

  if (!board) return <div>데이터 로딩 중...</div>;

  const btnStyle = { padding: '6px 12px', margin: '0 4px', cursor: 'pointer', borderRadius: '4px', border: 'none' };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '20px', background: 'white', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
      <button onClick={onBackToList} style={{ marginBottom: '20px', ...btnStyle, background: '#eee' }}>⬅️ 목록으로</button>
      
      {/* 1. 게시글 영역 */}
      <div style={{ borderBottom: '2px solid #f0f0f0', paddingBottom: '20px' }}>
        {isEditing ? (
          <div>
            <input value={editForm.title} onChange={(e) => setEditForm({...editForm, title: e.target.value})} style={{ width: '100%', padding: '10px', fontSize: '18px', marginBottom: '10px' }} />
            <textarea value={editForm.content} onChange={(e) => setEditForm({...editForm, content: e.target.value})} style={{ width: '100%', height: '150px', padding: '10px' }} />
            <div style={{ marginTop: '10px' }}>
              <button onClick={async () => { await updateBoard(boardId, editForm); setIsEditing(false); loadAllData(); }} style={{ ...btnStyle, background: '#4CAF50', color: 'white' }}>저장</button>
              <button onClick={() => setIsEditing(false)} style={{ ...btnStyle }}>취소</button>
            </div>
          </div>
        ) : (
          <div>
            <h2 style={{ margin: '0 0 10px 0' }}>{board.title}</h2>
            <p style={{ color: '#888', fontSize: '14px' }}>작성자: {board.writer?.nickname}</p>
            <div style={{ padding: '20px', background: '#fcfcfc', borderRadius: '8px', minHeight: '100px', margin: '20px 0' }}>{board.content}</div>
            {user && user.userId === board.writer?.userId && (
              <div style={{ textAlign: 'right' }}>
                <button onClick={() => { setEditForm({title: board.title, content: board.content}); setIsEditing(true); }} style={btnStyle}>수정</button>
                <button onClick={handleDeleteBoard} style={{ ...btnStyle, background: '#ff4d4f', color: 'white' }}>삭제</button>
              </div>
            )}
          </div>
        )}
      </div>

      <div style={{ marginTop: '20px', borderTop: '1px solid #eee', padding: '10px 0' }}>
        {neighbors.prev && (
          <div style={{ marginBottom: '5px' }}>
            <span style={{ fontWeight: 'bold', color: '#2196F3' }}>◀ 이전글: </span>
            <button onClick={() => setBoardId(neighbors.prev.boardId)} style={{ border: 'none', background: 'none', color: '#2196F3', cursor: 'pointer' }}>
              {neighbors.prev.title}
            </button>
          </div>
        )}
        {neighbors.next && (
          <div>
            <span style={{ fontWeight: 'bold', color: '#2196F3' }}>▶ 다음글: </span>
            <button onClick={() => setBoardId(neighbors.next.boardId)} style={{ border: 'none', background: 'none', color: '#2196F3', cursor: 'pointer' }}>
              {neighbors.next.title}
            </button>
          </div>
        )}
      </div>

      {/* 2. 댓글 영역 */}
      <div style={{ marginTop: '40px', borderTop: '2px solid #eee', paddingTop: '20px' }}>
        <h4 style={{ marginBottom: '15px', color: '#333' }}>💬 댓글 {comments.length}개</h4>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '25px' }}>
          <textarea 
            value={comment} 
            onChange={(e) => setComment(e.target.value)} 
            placeholder="따뜻한 댓글을 남겨주세요!" 
            style={{ width: '100%', height: '80px', padding: '12px', borderRadius: '8px', border: '1px solid #ddd', resize: 'none' }} 
          />
          <button onClick={handleCommentSubmit} style={{ alignSelf: 'flex-end', padding: '8px 20px', background: '#2196F3', color: 'white', borderRadius: '20px', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>
            등록하기
          </button>
        </div>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}></div>
        {comments.map((c) => (
          <div key={c.commentId} style={{ background: '#f8f9fa', padding: '15px', borderRadius: '10px', borderLeft: '4px solid #2196F3' }}>
              {editingCommentId === c.commentId ? (
                <div style={{ display: 'flex', gap: '10px' }}>
                  <input value={editCommentContent} onChange={(e) => setEditCommentContent(e.target.value)} style={{ flex: 1, padding: '8px' }} />
                  <button onClick={() => handleUpdateComment(c.commentId)} style={btnStyle}>저장</button>
                </div>
              ) : (
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '5px' }}>
                    <strong style={{ fontSize: '14px', color: '#555' }}>{c.writer?.nickname}</strong>
                    {user && user.userId === c.writer?.userId && (
                      <div style={{ display: 'flex', gap: '5px' }}>
                        <button onClick={() => startEditComment(c)} style={{ border: 'none', background: 'none', color: '#888', fontSize: '12px', cursor: 'pointer' }}>수정</button>
                        <button onClick={() => handleDeleteComment(c.commentId)} style={{ border: 'none', background: 'none', color: '#ff4d4f', fontSize: '12px', cursor: 'pointer' }}>삭제</button>
                      </div>
                    )}
                  </div>
                  <p style={{ margin: 0, fontSize: '15px', color: '#333' }}>{c.content}</p>
                </div>
              )}
            </div>
        ))}
      </div>
    </div>
  );
}