import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 백엔드 베이스 주소
  withCredentials: true, // 스프링 부트 세션 쿠키(JSESSIONID)를 프론트와 공유하기 위한 핵심 옵션!
});

export default api;