import api from './axios';

// 로딩
export const fetchShopItems = async (roomId) => {
  const response = await api.get(`/ecosystems/${roomId}/shop/items`); // 위에서 맞춘 주소!
  return response.data;
};

export const adoptCritterApi = async (roomId, userId, critterName, critterType) => {
  const response = await api.post(`/ecosystems/${roomId}/shop/adopt`, {
    userId,
    critterName,
    critterType
  });
  return response.data;
};

export const buyFoodApi = async (roomId, userId, foodType) => {
  const response = await api.post(`/ecosystems/${roomId}/shop/buy-food`, {
    userId,
    roomId,
    foodType
  });
  return response.data;
};