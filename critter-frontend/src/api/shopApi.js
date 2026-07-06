import api from './axios';

export const adoptCritterApi = async (roomId, userId, critter) => {
  const response = await api.post(`/ecosystems/${roomId}/critters`, {
    userId,
    critterName: critter.name,
    critterType: critter.critterType
  });
  
  return response.data;
};