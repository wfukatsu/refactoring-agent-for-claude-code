import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import "./PreviewFile.css";
import dayjs from "dayjs";
import axios from "../../../api/axios";
import { LOGOUT } from "../../../redux/reducerSlice/authSlice";
import {
  CLEAR_REFRESH_TOKEN,
  UPDATE_REFRESH_TOKEN,
} from "../../../redux/reducerSlice/tokenSlice";

const PreviewFile = ({ selectedItem }) => {
  const dispatch = useDispatch();
  const { jwtTokenRefreshToken, userEmail, serviceAccAccessToken } =
    useSelector((state) => state.token.tokenRepo ?? {});
  const { itemId } = selectedItem;
  console.log("USER :: ", serviceAccAccessToken);

  useEffect(() => {
    const refreshTime = localStorage.getItem("refreshAt");
    console.log(
      "REFRESH :: ",
      dayjs(Date.now()).diff(dayjs(refreshTime), "minute")
    );
    if (dayjs(Date.now()).diff(dayjs(refreshTime), "minute") < 42) {
      try {
        if (window.Box && typeof window.Box.Preview === "function") {
          const preview = new window.Box.Preview();
          console.log(preview, "PREVIEW");

          preview.show(itemId, serviceAccAccessToken, {
            container: ".preview-container",
            showDownload: true,
          });
        }
      } catch (error) {
        console.error("Error occurred while initializing Box Preview:", error);
      }
    } else {
      const responsebody = {
        userName: userEmail,
        refreshToken: jwtTokenRefreshToken,
      };

      axios
        .post("/box/user/getNewAccessToken", responsebody)
        .then(async (response) => {
          if (response.status === 200) {
            const { jwtToken, jwtTokenRefreshToken, serviceAccAccessToken } =
              response.data.data;

            dispatch(
              UPDATE_REFRESH_TOKEN(
                jwtToken,
                jwtTokenRefreshToken,
                serviceAccAccessToken,
                userEmail
              )
            );
            localStorage.setItem("refreshAt", new Date());
          }
        })
        .catch((error) => {
          dispatch(LOGOUT());
          dispatch(CLEAR_REFRESH_TOKEN());
          return Promise.reject(error);
        });
    }
  }, [
    serviceAccAccessToken,
    itemId,
    userEmail,
    jwtTokenRefreshToken,
    dispatch,
  ]);

  return (
    <div>
      <div
        className="preview-container"
        style={{ height: "250px", border: "0px solid black" }}
      ></div>
    </div>
  );
};

export default PreviewFile;
