// authSlice.js

import { createSlice } from "@reduxjs/toolkit";

const INITIAL_STATE = {
  user: null,
  auth: {},
  jwtToken: null,
  userEmail: null,
  userRoles: [],
  refreshToken: null,
  loading: false,
  languageCode: "en",
};

const authSlice = createSlice({
  name: "auth",
  initialState: INITIAL_STATE,
  reducers: {
    LOGIN: {
      reducer(state, action) {
        state.user = action.payload;
        state.loading = false;
      },
      prepare(
        userEmail,
        name,
        jwtToken,
        jwtTokenRefreshToken,
        refreshToken,
        userRoles,
        accessToken,
        serviceAccAccessToken,
        orgId,
        languageCode
      ) {
        return {
          payload: {
            userEmail,
            name,
            jwtToken,
            jwtTokenRefreshToken,
            refreshToken,
            userRoles,
            accessToken,
            serviceAccAccessToken,
            orgId,
            languageCode,
          },
        };
      },
    },
    LOGOUT: (state, action) => {
      state.user = null;
    },
    SET_LOADING: (state, action) => {
      state.loading = action.payload;
    },
    SET_REFRESHTOKEN: (state, action) => {},
    UPDATE_LANGUAGE: {
      reducer(state, action) {
        const languageCode = action.payload.languageCode;

        state.user = { ...state.user, languageCode };
      },
      prepare(languageCode) {
        return {
          payload: {
            languageCode,
          },
        };
      },
    },
  },
});

export const {
  LOGOUT,
  LOGIN,
  SET_LOADING,
  SET_REFRESHTOKEN,
  UPDATE_TOKEN,
  UPDATE_LANGUAGE,
} = authSlice.actions;
export default authSlice.reducer;
