// GIF가 존재하는 동물들만 관리하는 리스트
export const GIF_SUPPORTED_CRITTERS = ['PENGUIN', 'DOG', 'CAT', 'FOX', 'OCTOPUS', 'RABBIT', 'REDPANDA', 'SQUIRREL'];

export const getCritterImagePath = (critterType, status) => {
  const type = critterType.toUpperCase();
  const state = (status || 'IDLE').toUpperCase();
  
  // 만약 이 동물이 GIF 지원 목록에 있다면? .gif를 반환
  if (GIF_SUPPORTED_CRITTERS.includes(type)) {
    return `/sprites/${type}/${state}.gif`;
  }
  
  // 없으면? 그냥 안전하게 .png를 반환
  return `/sprites/${type}/${state}.png`;
};