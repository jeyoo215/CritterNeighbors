import React, { useEffect, useState } from 'react';
import { fetchBoards } from '../api/boardApi';

export default function BoardList() {
    const [boards, setBoards] = useState([]);

    useEffect(() => {
        fetchBoards().then(res => setBoards(res.data));
    }, []);

    return (
        <div style={{ padding: '20px' }}>
            <h2>📢 전체 커뮤니티 게시판</h2>
            {boards.map(board => (
                <div key={board.boardId} style={{ borderBottom: '1px solid #ccc', padding: '10px' }}>
                    <h3>{board.title}</h3>
                    <p>작성자: {board.writer.nickname}</p>
                </div>
            ))}
        </div>
    );
}