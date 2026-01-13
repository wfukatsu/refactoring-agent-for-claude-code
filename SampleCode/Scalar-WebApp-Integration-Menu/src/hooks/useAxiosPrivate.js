/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect } from "react";
import axios, { axiosPrivate } from "../api/axios";
import { useDispatch, useSelector } from "react-redux";

import { useTranslation } from "react-i18next";
import {
  CLEAR_REFRESH_TOKEN,
  UPDATE_REFRESH_TOKEN,
} from "../features/auth/tokenSlice";
import { logout } from "../features/auth/authSlice";
import { useNavigate } from "react-router-dom";

// const useAxiosPrivate = () => {
//   const dispatch = useDispatch();
//   const { jwtToken, jwtTokenRefreshToken, userEmail } = useSelector(
//     (state) => state.auth.user
//   );
//   const { i18n } = useTranslation();

//   useEffect(() => {
//     const requestIntercept = axiosPrivate.interceptors.request.use(
//       (config) => {
//         console.log(
//           "requestInterceptrequestInterceptrequestIntercept :: ",
//           jwtToken
//         );
//         if (!config.headers["Authorization"]) {
//           config.headers["Authorization"] = `Bearer ${encodeURIComponent(
//             jwtToken
//           )}`;
//           config.headers["Accept-Language"] = i18n.language;
//         }
//         return config;
//       },
//       (error) => {
//         return Promise.reject(error);
//       }
//     );

//     const responseIntercept = axiosPrivate.interceptors.response.use(
//       (response) => {
//         return response;
//       },
//       async (error) => {
//         const prevRequest = error?.config;
//         if (
//           error?.response?.status === 401 &&
//           error.response.headers["message"] === "Token Expired"
//         ) {
//           prevRequest.sent = true;
//           const responsebody = {
//             userName: userEmail,
//             refreshToken: jwtTokenRefreshToken,
//           };

//           return await axios
//             .post("/box/user/getNewAccessToken", responsebody)
//             .then((response) => {
//               if (response.status === 200) {
//                 const {
//                   jwtToken,
//                   jwtTokenRefreshToken,
//                   serviceAccAccessToken,
//                 } = response.data.data;
//                 console.log(`INSIDE 33 ${serviceAccAccessToken}`);
//                 prevRequest.headers[
//                   "Authorization"
//                 ] = `Bearer ${encodeURIComponent(jwtToken)}`;
//                 dispatch(
//                   UPDATE_TOKEN(
//                     jwtToken,
//                     jwtTokenRefreshToken,
//                     serviceAccAccessToken
//                   )
//                 );
//                 return axiosPrivate(prevRequest);
//               }
//             })
//             .catch((error) => {
//               console.log(`INSIDE ERROE ${error.response.data.message}`);
//               dispatch(logout());
//               return Promise.reject(error);
//             });
//         } else {
//           return Promise.reject(error);
//         }
//       }
//     );

//     return () => {
//       axiosPrivate.interceptors.request.eject(requestIntercept);
//       axiosPrivate.interceptors.response.eject(responseIntercept);
//     };
//   }, [jwtToken]);

//   return axiosPrivate;
// };

// export default useAxiosPrivate;
let isRefreshing = false; // Flag to track if a refresh request is in progress
let refreshPromise = null;

const useAxiosPrivate = () => {
  const dispatch = useDispatch();
  const { i18n } = useTranslation();
  const { jwtToken, jwtTokenRefreshToken, userEmail } = useSelector(
    (state) => state.token.tokenRepo ?? {}
  );

  const navigate = useNavigate();

  console.log("useAxiosPrivate :: ", userEmail);

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
        console.log(
          "MESSAGE",
          error.response.headers["message"],
          error.response.headers["Message"]
        );
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
                  const { jwtToken, jwtTokenRefreshToken } = response.data.data;
                  prevRequest.headers[
                    "Authorization"
                  ] = `Bearer ${encodeURIComponent(jwtToken)}`;
                  dispatch(
                    UPDATE_REFRESH_TOKEN(
                      jwtToken,
                      jwtTokenRefreshToken,
                      userEmail
                    )
                  );
                  return await axiosPrivate(prevRequest);
                }
              })
              .catch((error) => {
                console.log(`INSIDE ERROE ${error.response}`, prevRequest);
                prevRequest.sent = false;
                // dispatch(LOGOUT());
                navigate("/logout");
                dispatch(logout());
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
