// src/i18n.js
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

import korAuth from './locales/kor/auth.json';
import jpnAuth from './locales/jpn/auth.json';
import korBoard from './locales/kor/board.json';
import jpnBoard from './locales/jpn/board.json';
import korEcosystem from './locales/kor/ecosystemroom.json';
import jpnEcosystem from './locales/jpn/ecosystemroom.json';
import korLobby from './locales/kor/lobby.json';
import jpnLobby from './locales/jpn/lobby.json';
import korShop from './locales/kor/shop.json';
import jpnShop from './locales/jpn/shop.json';

i18n
  .use(LanguageDetector) // 브라우저 언어 설정 자동 감지
  .use(initReactI18next) // 리액트랑 연결
  .init({
    resources: {
      kor: {
        auth: korAuth,
        board: korBoard,
        ecosystemroom: korEcosystem,
        lobby: korLobby,
        shop: korShop
      },
      jpn: {
        auth: jpnAuth,
        board: jpnBoard,
        ecosystemroom: jpnEcosystem,
        lobby: jpnLobby,
        shop: jpnShop
      }
    },
    fallbackLng: 'kor',
    interpolation: {
      escapeValue: false // 리액트가 알아서 보안 처리해주니까 꺼도 됨
    },
    ns: ['auth'], // 사용할 네임스페이스들
    defaultNS: 'auth'
  });

export default i18n;