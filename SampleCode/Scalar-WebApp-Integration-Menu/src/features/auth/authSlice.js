import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  user: {
    jwtToken: "",
    username: "",
  },
  loading: false,
  screen: "loading",
  errorMessage: null,
  boxAccessToken: null,
  boxRefreshToken: null,
  code: null,
  state: null,
  userId: null,
  itemId: null,
  redirectUrl: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    updateScreen(state, action) {
      state.screen = action.payload;
    },
    loading(state, action) {
      state.loading = true;
      console.log(state, action);
    },
    error(state, action) {
      state.loading = false;
      state.errorMessage = action.payload;
    },
    login(state, action) {
      state.user = action.payload;
      state.loading = false;
    },
    logout(state, action) {
      state.user = null;
      console.log("LOGOUT :: " ,state, action);
    },
    UPDATE_TOKEN: {
      reducer(state, action) {
        const { jwtToken, jwtTokenRefreshToken, serviceAccAccessToken } =
          action.payload;
        state.user = {
          ...state.user,
          jwtToken,
          jwtTokenRefreshToken,
          serviceAccAccessToken,
        };
        state.loading = false;
      },
      prepare(jwtToken, jwtTokenRefreshToken, serviceAccAccessToken) {
        return {
          payload: {
            jwtToken,
            jwtTokenRefreshToken,
            serviceAccAccessToken,
          },
        };
      },
    },
  },
});

export const {
  loading,
  screen,
  externalAuditorLogin,
  boxLoginWithEmail,
  boxLoginWithRedirectUrl,
  logout,
  UPDATE_TOKEN,
} = authSlice.actions;

export const selectCurrentToken = (state) => state.auth.user.jwtToken;
// const submitAccessTokenAndRefreshToken = async (
//   boxAccessToken,
//   boxRefreshToken,
//   dispatch
// ) => {
//   const data = {
//     accessToken: boxAccessToken,
//     refreshToken: boxRefreshToken,
//   };

//   try {
//     const response = await fetch(`${baseUrl}/user/submitToken`, {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       body: JSON.stringify(data),
//     });

//     console.log("RESPONSE");
//     console.log(response);
//     if (response.ok) {
//       const data = await response.json();
//       const { userEmail, jwtToken, refreshToken, userRoles } = {
//         ...data.data,
//       };
//       dispatch({
//         type: "auth/login",
//         payload: {
//           userEmail,
//           jwtToken,
//           refreshToken,
//           userRoles,
//           boxAccessToken,
//           boxRefreshToken,
//         },
//       });
//     } else {
//       dispatch({
//         type: "auth/error",
//         payload: "Something Went Wrong",
//       });
//     }
//   } catch (error) {
//     dispatch({
//       type: "auth/error",
//       payload: "Something Went Wrong",
//     });
//   }
// };

// export function exchangeCodeForToken(code) {
//   return async function (dispatch) {
//     dispatch(loading());

//     try {
//       const response = await fetch("https://api.box.com/oauth2/token", {
//         method: "POST",
//         headers: {
//           "Content-Type": "application/x-www-form-urlencoded",
//         },
//         body: `grant_type=authorization_code&code=${code}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&redirect_uri=${REDIRECT_URL}`,
//       });

//       const data = await response.json();
//       console.log("REREREREREREREEEEEEEEEEE");
//       console.log(response);
//       if (response.ok) {
//         const boxAccessToken = data.access_token;
//         const boxRefreshToken = data.refresh_token;

//         await submitAccessTokenAndRefreshToken(
//           boxAccessToken,
//           boxRefreshToken,
//           dispatch
//         );
//       } else {
//         dispatch({
//           type: "auth/error",
//           payload: "Something Went Wrong",
//         });
//       }
//     } catch (error) {
//       dispatch({
//         type: "auth/error",
//         payload: "Something Went Wrong",
//       });
//     }
//   };
// }

export default authSlice.reducer;
