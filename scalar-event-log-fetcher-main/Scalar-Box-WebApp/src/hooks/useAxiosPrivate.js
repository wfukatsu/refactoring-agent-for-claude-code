/* eslint-disable react-hooks/exhaustive-deps */
// import { useDispatch, useSelector } from "react-redux";
// import axios, { axiosPrivate } from "../api/axios";
// import { LOGOUT } from "../redux/reducerSlice/authSlice";
// import {
//   CLEAR_REFRESH_TOKEN,
//   UPDATE_REFRESH_TOKEN,
// } from "../redux/reducerSlice/tokenSlice";

// import { useTranslation } from "react-i18next";

// let isRefreshing = false; // Flag to track if a refresh request is in progress
// let refreshPromise = null;

// const useAuthData = () => {
//   const dispatch = useDispatch();
//   const { jwtToken, jwtTokenRefreshToken, userEmail } = useSelector(
//     (state) => state.token.tokenRepo ?? {}
//   );
//   const { i18n } = useTranslation();
//   console.log(
//     "jwtTokenRefreshToken body",
//     jwtTokenRefreshToken,
//     "userEmail",
//     userEmail
//   );
//   return { dispatch, jwtToken, jwtTokenRefreshToken, userEmail, i18n };
// };

// const setupAxiosInterceptors = ({
//   dispatch,
//   jwtToken,
//   jwtTokenRefreshToken,
//   userEmail,
//   i18n,
// }) => {
//   let requestIntercept;
//   let responseIntercept;

//   // console.log("requestIntercept  useAxiosPrivate");

//   // const { i18n } = useTranslation();

//   requestIntercept = axiosPrivate.interceptors.request.use((config) => {
//     console.log("requestIntercept 111");
//     if (!config.headers["Authorization"]) {
//       config.headers["Authorization"] = `Bearer ${encodeURIComponent(
//         jwtToken
//       )}`;
//       config.headers["Accept-Language"] = i18n.language;
//     }
//     return config;
//   });

//   responseIntercept = axiosPrivate.interceptors.response.use(
//     (response) => {
//       return response;
//     },
//     async (error) => {
//       const prevRequest = error?.config;

//       if (
//         error?.response?.status === 401 &&
//         error.response.headers["message"] === "Token Expired"
//       ) {
//         prevRequest.sent = true;
//         console.log(
//           "jwtTokenRefreshToken :: 11",
//           jwtTokenRefreshToken,
//           "userEmail",
//           userEmail
//         );
//         const responsebody = {
//           userName: userEmail,
//           refreshToken: jwtTokenRefreshToken,
//         };

//         console.log("jwtTokenRefreshToken  body", responsebody);
//         if (!isRefreshing) {
//           isRefreshing = true;
//           return await axios
//             .post("/box/user/getNewAccessToken", responsebody)
//             .then(async (response) => {
//               if (response.status === 200) {
//                 const {
//                   jwtToken,
//                   jwtTokenRefreshToken,
//                   serviceAccAccessToken,
//                 } = response.data.data;
//                 prevRequest.headers[
//                   "Authorization"
//                 ] = `Bearer ${encodeURIComponent(jwtToken)}`;
//                 dispatch(
//                   UPDATE_REFRESH_TOKEN(
//                     jwtToken,
//                     jwtTokenRefreshToken,
//                     serviceAccAccessToken,
//                     userEmail
//                   )
//                 );
//                 return await axiosPrivate(prevRequest);
//               }
//             })
//             .catch((error) => {
//               console.log(`INSIDE ERROE ${error.response}`, prevRequest);
//               prevRequest.sent = false;
//               dispatch(LOGOUT());
//               dispatch(CLEAR_REFRESH_TOKEN());
//               return Promise.reject(error);
//             })
//             .finally(() => {
//               isRefreshing = false;
//             });
//         } else {
//           return new Promise((resolve, reject) => {
//             const intervalId = setInterval(() => {
//               if (!isRefreshing && refreshPromise === null) {
//                 clearInterval(intervalId);
//                 resolve(axiosPrivate(prevRequest));
//               }
//             }, 100);
//           });
//         }
//       } else {
//         return Promise.reject(error);
//       }
//     }
//   );

//   return () => {
//     axiosPrivate.interceptors.request.eject(requestIntercept);
//     axiosPrivate.interceptors.response.eject(responseIntercept);
//   };
// };

// const useAxiosPrivate = () => {
//   const { dispatch, jwtToken, jwtTokenRefreshToken, userEmail, i18n } =
//     useAuthData();
//   console.log("jwtTokenRefreshToken :: ", jwtTokenRefreshToken);
//   setupAxiosInterceptors({
//     dispatch,
//     jwtToken,
//     jwtTokenRefreshToken,
//     userEmail,
//     i18n,
//   });

//   return axiosPrivate;
// };

// export default useAxiosPrivate;

import { useDispatch, useSelector } from "react-redux";
import axios, { axiosPrivate } from "../api/axios";
import { LOGOUT } from "../redux/reducerSlice/authSlice";
import {
  CLEAR_REFRESH_TOKEN,
  UPDATE_REFRESH_TOKEN,
} from "../redux/reducerSlice/tokenSlice";

import { useTranslation } from "react-i18next";
import { useEffect } from "react";

let isRefreshing = false; // Flag to track if a refresh request is in progress
let refreshPromise = null;

const useAuthData = () => {
  const dispatch = useDispatch();
  const { jwtToken, jwtTokenRefreshToken, userEmail } = useSelector(
    (state) => state.token.tokenRepo ?? {}
  );
  const { i18n } = useTranslation();
  console.log(
    "jwtTokenRefreshToken body",
    jwtTokenRefreshToken,
    "userEmail",
    userEmail
  );
  return { dispatch, jwtToken, jwtTokenRefreshToken, userEmail, i18n };
};

const useAxiosPrivate = () => {
  const dispatch = useDispatch();
  const { i18n } = useTranslation();
  const { jwtToken, jwtTokenRefreshToken, userEmail } = useSelector(
    (state) => state.token.tokenRepo ?? {}
  );

  let requestIntercept;
  let responseIntercept;

  // console.log("requestIntercept  useAxiosPrivate");

  // const { i18n } = useTranslation();

  useEffect(() => {
    requestIntercept = axiosPrivate.interceptors.request.use((config) => {
      console.log("requestIntercept 111");
      if (!config.headers["Authorization"]) {
        config.headers["Authorization"] = `Bearer ${encodeURIComponent(
          jwtToken
        )}`;
        config.headers["Accept-Language"] = i18n.language;
      }
      return config;
    });

    responseIntercept = axiosPrivate.interceptors.response.use(
      (response) => {
        return response;
      },
      async (error) => {
        const prevRequest = error?.config;

        if (
          error?.response?.status === 401 &&
          error.response.headers["message"] === "Token Expired"
        ) {
          prevRequest.sent = true;
          console.log(
            "jwtTokenRefreshToken :: 11",
            jwtTokenRefreshToken,
            "userEmail",
            userEmail
          );
          const responsebody = {
            userName: userEmail,
            refreshToken: jwtTokenRefreshToken,
          };

          console.log("jwtTokenRefreshToken  body", responsebody);
          if (!isRefreshing) {
            isRefreshing = true;
            return await axios
              .post("/box/user/getNewAccessToken", responsebody)
              .then(async (response) => {
                if (response.status === 200) {
                  const {
                    jwtToken,
                    jwtTokenRefreshToken,
                    serviceAccAccessToken,
                  } = response.data.data;
                  prevRequest.headers[
                    "Authorization"
                  ] = `Bearer ${encodeURIComponent(jwtToken)}`;
                  dispatch(
                    UPDATE_REFRESH_TOKEN(
                      jwtToken,
                      jwtTokenRefreshToken,
                      serviceAccAccessToken,
                      userEmail
                    )
                  );
                  localStorage.setItem("refreshAt", new Date());
                  return await axiosPrivate(prevRequest);
                }
              })
              .catch((error) => {
                console.log(`INSIDE ERROE ${error.response}`, prevRequest);
                prevRequest.sent = false;
                dispatch(LOGOUT());
                dispatch(CLEAR_REFRESH_TOKEN());
                return Promise.reject(error);
              })
              .finally(() => {
                isRefreshing = false;
              });
          } else {
            return new Promise((resolve, reject) => {
              const intervalId = setInterval(() => {
                if (!isRefreshing && refreshPromise === null) {
                  clearInterval(intervalId);
                  resolve(axiosPrivate(prevRequest));
                }
              }, 100);
            });
          }
        } else {
          return Promise.reject(error);
        }
      }
    );

    return () => {
      axiosPrivate.interceptors.request.eject(requestIntercept);
      axiosPrivate.interceptors.response.eject(responseIntercept);
    };
  }, [jwtToken, jwtTokenRefreshToken]);

  return axiosPrivate;
};

// const useAxiosPrivate = () => {
//   const { dispatch, i18n } =
//     useAuthData();
//   console.log("jwtTokenRefreshToken :: ", jwtTokenRefreshToken);
//   setupAxiosInterceptors({
//     dispatch,
//     i18n,
//   });

//   return axiosPrivate;
// };

export default useAxiosPrivate;
