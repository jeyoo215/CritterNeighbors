const AVAILABLE_STATES = ['IDLE', 'SHELTER', 'PANIC'];

export const GIF_SUPPORTED_CRITTERS = ['PENGUIN', 'DOG', 'CAT', 'FOX', 'OCTOPUS', 'RABBIT', 'REDPANDA', 'SQUIRREL', "TURTLE"];

export const getCritterImagePath = (critterType, status) => {
  const type = critterType.toUpperCase();

  const state = AVAILABLE_STATES.includes((status || '').toUpperCase()) 
    ? status.toUpperCase() 
    : 'IDLE';
  
  const extension = GIF_SUPPORTED_CRITTERS.includes(type) ? 'gif' : 'png';

  return `/sprites/${type}/${state}.${extension}`;
};