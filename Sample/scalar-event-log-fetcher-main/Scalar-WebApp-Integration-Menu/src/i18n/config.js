import i18n from "i18next";
import { initReactI18next } from "react-i18next";

import enTranslations from './locales/en/translations.json';
import jaTranslations from './locales/ja/translations.json';

i18n.use(initReactI18next).init({
  fallbackLng: "en",
  lng: "ja",
  resources: {
    // en: {
    //   translations: require("./locales/en/translations.json"),
    // },
    // ja: {
    //   translations: require("./locales/ja/translations.json"),
    // },
    en: {
      translations: enTranslations,
    },
    ja: {
      translations: jaTranslations,
    },
  },
  ns: ["translations"],
  defaultNS: "translations",
});

i18n.languages = ["en", "ja"];

export default i18n;
