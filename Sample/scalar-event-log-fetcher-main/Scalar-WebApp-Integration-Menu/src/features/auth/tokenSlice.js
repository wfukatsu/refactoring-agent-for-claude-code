import { createSlice } from "@reduxjs/toolkit";

const INITIAL_STATE = {
  tokenRepo: {
    jwtToken: "",
    jwtTokenRefreshToken: "",
    userEmail: "",
  },
};

const tokenSlice = createSlice({
  name: "token",
  initialState: INITIAL_STATE,
  reducers: {
    CLEAR_REFRESH_TOKEN: {
      reducer(state, action) {
        state.tokenRepo = action.payload;
      },
      prepare() {
        return {
          payload: {
            jwtToken: "",
            jwtTokenRefreshToken: "",
            userEmail: "",
          },
        };
      },
    },

    UPDATE_REFRESH_TOKEN: {
      reducer(state, action) {
        const {
          jwtToken,
          jwtTokenRefreshToken,
          userEmail,
        } = action.payload;

        state.tokenRepo = {
          jwtToken,
          jwtTokenRefreshToken,
          userEmail,
        };
      },
      prepare(
        jwtToken,
        jwtTokenRefreshToken,
        userEmail
      ) {
        return {
          payload: {
            jwtToken,
            jwtTokenRefreshToken,
            userEmail,
          },
        };
      },
    },
  },
});

export const { UPDATE_REFRESH_TOKEN, CLEAR_REFRESH_TOKEN } = tokenSlice.actions;
export default tokenSlice.reducer;
