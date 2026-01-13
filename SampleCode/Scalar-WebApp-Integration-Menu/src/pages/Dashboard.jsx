import { useEffect, useState } from "react";
import SignUp from "../components/SignUp";
import Loader from "../components/Loader";
import { useSearchParams } from "react-router-dom";
import { useDispatch } from "react-redux";
import {
  BASE_URL,
  CLIENT_ID,
  CLIENT_SECRET,
  REDIRECT_URL,
} from "../utils/constant";
import Error from "../components/Error";
import AuditorDashboard from "../components/AuditorDashboard";
import FolderAuditorDashboard from "../features/auditfolder/FolderAuditorDashboard";
import { axiosPrivate } from "../api/axios";
import { UPDATE_REFRESH_TOKEN } from "../features/auth/tokenSlice";

import { useTranslation } from "react-i18next";

const SHOW_DATA = "SHOW_DATA";
const SHOW_LOADING = "SHOW_LOADING";
const SHOW_ERROR = "SHOW_ERROR";
const SHOW_SIGNUP = "SHOW_SIGNUP";
// --port 3000
function Dashboard() {
  const [screen, setScreen] = useState({ screen: SHOW_LOADING, data: {} });
  // const [refresh, setRefresh] = useState([false]);
  const dispatch = useDispatch();
  const [loadUseEffect, setLoadUseEffect] = useState(false);

  const [searchParams, setSearchParams] = useSearchParams();

  const {t,i18n} = useTranslation();

  const state = searchParams.get("state");
  const code = searchParams.get("code");
  let itemId = searchParams.get("file_id");
  let userId = searchParams.get("user_id");
  let redirectUrl = searchParams.get("redirect_url");

  // itemId = 1490256450740; //1465563532639
  // userId = 29472851413; //29490661237;

  // redirectUrl =
  //   "https://app.box.com/index.php?rm=box_openbox_redirect_to_box&service_action_id=29672&item_id=1396009086165&item_type=file&auth_token=";

  // itemId = 249714606126;
  // userId = 29472851413;

  // redirectUrl =
  //   "https://app.box.com/index.php?rm=box_openbox_redirect_to_box&service_action_id=28887&item_id=240062903338&item_type=folder&auth_token=";

  console.log("**************STATE_STATE*********************  ", state);
  console.log("CODE = ", code);
  console.log("FILE_ID = ", itemId);
  console.log("USER_ID = ", userId);
  console.log("REDIRECT_URL = ", redirectUrl);
  console.log(
    "**************STATE_STATE*********************",
    window.location
  );

  const fetchUserData = async (userId, itemId, itemType) => {
    // const url = `${BASE_URL}/box/item/getIntegratedItemDetails`;
    // console.log("*******************URL*********************");
    // console.log(url);
    // console.log(
    //   "*******************URL*********************",
    //   userId,
    //   itemId,
    //   itemType
    // );

    try {
      const data = {
        itemId: itemId,
        itemType: itemType,
        userId: userId,
      };

      const url = `${BASE_URL}/box/item/getIntegratedItemDetail`;
      const response = await axiosPrivate.post(url, data);
      // const response = await fetch(url, {
      //   method: "POST",
      //   headers: {
      //     "Content-Type": "application/json",
      //   },
      //   body: JSON.stringify(data),
      // });
      console.log("RESPONSE :: 11", response);
      //console.log("user lang",response.data.data.userResponse.languageCode);
      i18n.changeLanguage(response.data.data.userResponse.languageCode);
      if (response.status === 200) {
        // const jsonData = await response.json();
        if (response.data.status) {
          const {
            userEmail,
            jwtToken,
            jwtTokenRefreshToken,
            refreshToken,
            userRoles,
            name,
            languageCode,
          } = {
            ...response.data.data.userResponse,
          };
          console.log("jwtTokenRefreshToken :: ", jwtTokenRefreshToken);
          const { itemDetails } = { ...response.data.data };
          dispatch({
            type: "auth/login",
            payload: {
              userEmail,
              jwtToken,
              refreshToken,
              userRoles,
              itemDetails,
              username: name,
              languageCode,
            },
          });

          dispatch(
            UPDATE_REFRESH_TOKEN(jwtToken, jwtTokenRefreshToken, userEmail)
          );
          setScreen({
            screen: SHOW_DATA,
            data: response.data.data.itemDetails,
          });
        } else {
          setScreen({ screen: SHOW_SIGNUP, data: data });
        }
      } else {
        setScreen({ screen: SHOW_ERROR, data: null });
      }
    } catch (error) {
      setScreen({ screen: SHOW_ERROR, data: error });
    }
  };

  const errorHandler = () => {
    setScreen({ screen: SHOW_LOADING, data: null });
    setLoadUseEffect((value) => !value);
  };

  const submitAccessTokenAndRefreshToken = async (
    boxAccessToken,
    boxRefreshToken
  ) => {
    const data = {
      accessToken: boxAccessToken,
      refreshToken: boxRefreshToken,
    };

    try {
      const url = `${BASE_URL}/box/user/submitToken`;
      const response = await axiosPrivate.post(url, data);
      // const response = await fetch(`${BASE_URL}/box/user/submitToken`, {
      //   method: "POST",
      //   headers: {
      //     "Content-Type": "application/json",
      //   },
      //   body: JSON.stringify(data),
      // });

      if (response.status === 200) {
        const {
          userEmail,
          jwtToken,
          jwtTokenRefreshToken,
          refreshToken,
          userRoles,
        } = {
          ...response.data.data.userResponse,
        };
        const { itemDetails } = { ...response.data.data.itemDetails };
        dispatch({
          type: "auth/login",
          payload: {
            userEmail,
            jwtToken,
            jwtTokenRefreshToken,
            refreshToken,
            userRoles,
            boxAccessToken,
            boxRefreshToken,
            itemDetails,
          },
        });

        const params = state.split(",");
        fetchUserData(params[0], params[1], params[2]);
      } else {
        setScreen({ screen: SHOW_ERROR, data: "" });
        dispatch({
          type: "auth/error",
          payload: "Something Went Wrong",
        });
      }
    } catch (error) {
      setScreen({ screen: SHOW_ERROR, data: error });
      dispatch({
        type: "auth/error",
        payload: "Something Went Wrong",
      });
    }
  };

  async function exchangeCodeForToken(code) {
    try {
      const response = await fetch("https://api.box.com/oauth2/token", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `grant_type=authorization_code&code=${code}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&redirect_uri=${REDIRECT_URL}`,
      });

      const data = await response.json();
      if (response.ok) {
        const boxAccessToken = data.access_token;
        const boxRefreshToken = data.refresh_token;

        await submitAccessTokenAndRefreshToken(boxAccessToken, boxRefreshToken);
      } else {
        setScreen({
          screen: SHOW_ERROR,
          data: "Something Went Wrong Please try again",
        });
        dispatch({
          type: "auth/error",
          payload: "Something Went Wrong",
        });
      }
    } catch (error) {
      setScreen({ screen: SHOW_ERROR, data: error });
      dispatch({
        type: "auth/error",
        payload: "Something Went Wrong",
      });
    }
  }

  useEffect(() => {
    console.log("useEffect useEffect useEffect");
    if (code !== null && state !== null) {
      exchangeCodeForToken(code);
    } else if (userId !== null && itemId !== null && redirectUrl !== null) {
      const redirectUrlQueryParams = new URLSearchParams(redirectUrl);
      const itemType = redirectUrlQueryParams.get("item_type");
      fetchUserData(userId, itemId, itemType);
    } else {
      setScreen({ screen: SHOW_ERROR, data: "Something Went Wrong" });
    }
  }, [loadUseEffect]);

  // return <Error />;

  if (screen.screen === SHOW_DATA) {
    if (screen.data.type === "folder") {
      return <FolderAuditorDashboard itemdata={screen.data} />;
    }

    if (screen.data.type === "file") {
      return <AuditorDashboard reduxdata={screen.data} />;
    }

    return <Error onClick={errorHandler} />;
  }

  if (screen.screen === SHOW_LOADING) {
    return <Loader />;
  }
  const redirectUrlQueryParams = new URLSearchParams(redirectUrl);
  const itemType = redirectUrlQueryParams.get("item_type");
  if (screen.screen === SHOW_SIGNUP) {
    return <SignUp userId={userId} itemId={itemId} itemType={itemType} />;
  }

  return <Error onClick={errorHandler} />;
}

export default Dashboard;
