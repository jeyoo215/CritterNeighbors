export const adoptCritterApi = async (roomId, userId, critter) => {
  const response = await fetch(`http://localhost:8080/api/ecosystems/${roomId}/critters`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, critterName: critter.name, critterType: critter.critterType })
  });
  
  if (!response.ok) {
    const errorText = await response.text(); 
    throw new Error(errorText || "포인트가 부족하거나 입양에 실패했습니다.");
  }
  
  return response.json();
};