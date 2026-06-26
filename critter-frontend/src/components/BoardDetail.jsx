import React, { useEffect, useState } from 'react';
import { 
  fetchBoardDetail, 
  getCommentsByBoard, 
  postComment, 
  deleteBoard, 
  deleteComment, 
  updateBoard, 
  updateComment 
} from '../api/boardApi';

export default function BoardDetail({ boardId, user, onBackToList }) {
  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]);
  const [comment, setComment] = useState('');

  // 📝 게시글 수정 관련 상태
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ title: '', content: '' });

  // 💬 댓글 수정 관련 상태
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editCommentContent, setEditCommentContent] = useState('');

  const loadAllData = async () => {
    try {
      const boardRes = await fetchBoardDetail(boardId);
      setBoard(boardRes.data);
      const commentRes = await getCommentsByBoard(boardId);
      setComments(commentRes.data);
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

  return (
    <div style={{ padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
      <button onClick={onBackToList}>⬅️ 목록으로 돌아가기</button>
      
      {/* 1. 게시글 출력/수정 영역 */}
      <div style={{ marginTop: '20px' }}>
        {isEditing ? (
          <div>
            <input value={editForm.title} onChange={(e) => setEditForm({...editForm, title: e.target.value})} style={{ width: '100%' }} />
            <textarea value={editForm.content} onChange={(e) => setEditForm({...editForm, content: e.target.value})} style={{ width: '100%', height: '100px' }} />
            <button onClick={handleUpdateBoard}>저장</button>
            <button onClick={() => setIsEditing(false)}>취소</button>
          </div>
        ) : (
          <div>
            <h2>{board.title}</h2>
            <p><small>작성자: {board.writer?.nickname}</small></p>
            {user && user.userId === board.writer?.userId && (
              <div>
                <button onClick={() => { setEditForm({title: board.title, content: board.content}); setIsEditing(true); }}>수정</button>
                <button onClick={handleDeleteBoard} style={{ color: 'red' }}>삭제</button>
              </div>
            )}
            <div style={{ padding: '15px', background: '#f9f9f9' }}>{board.content}</div>
          </div>
        )}
      </div>

      {/* 2. 댓글 영역 */}
      <div style={{ marginTop: '30px' }}>
        <h4>💬 댓글</h4>
        <textarea value={comment} onChange={(e) => setComment(e.target.value)} placeholder="댓글을 입력하세요" style={{ width: '100%' }} />
        <button onClick={handleCommentSubmit}>댓글 달기</button>
        
        <div style={{ marginTop: '10px' }}>
          {comments.map((c) => (
            <div key={c.commentId} style={{ borderBottom: '1px solid #eee', padding: '5px 0' }}>
              {editingCommentId === c.commentId ? (
                <div>
                  <input value={editCommentContent} onChange={(e) => setEditCommentContent(e.target.value)} />
                  <button onClick={() => handleUpdateComment(c.commentId)}>저장</button>
                </div>
              ) : (
                <div>
                  <strong>{c.writer?.nickname}:</strong> {c.content}
                  {user && user.userId === c.writer?.userId && (
                    <div style={{ float: 'right' }}>
                      <button onClick={() => startEditComment(c)}>수정</button>
                      <button onClick={() => handleDeleteComment(c.commentId)}>삭제</button>
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}