import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 백엔드 베이스 주소
  withCredentials: true
});

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.config.url.includes('/users/me')) {
      return Promise.reject(error); 
    }
    
    if (error.response && error.response.status === 401) {
      alert("서버와의 연결이 끊어졌습니다. 다시 로그인해주세요!");
      localStorage.removeItem('user');
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

export default api;