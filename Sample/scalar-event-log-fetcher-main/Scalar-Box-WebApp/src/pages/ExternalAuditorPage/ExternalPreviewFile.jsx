import { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import "./ExternalPreviewFile.css";
import { BASE_URL } from "../../utils/constants";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import dayjs from "dayjs";
import axios from "../../api/axios";
import {
  CLEAR_REFRESH_TOKEN,
  UPDATE_REFRESH_TOKEN,
} from "../../redux/reducerSlice/tokenSlice";
import { LOGOUT } from "../../redux/reducerSlice/authSlice";

const ExternalPreviewFile = ({ selectedItem, auditSetId }) => {
  const { userRoles } = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const { jwtTokenRefreshToken, userEmail, serviceAccAccessToken } =
    useSelector((state) => state.token.tokenRepo ?? {});

  const { itemId, itemType } = selectedItem;
  const previewRef = useRef(null);
  const axiosPrivate = useAxiosPrivate();

  const addedFileEvent = async (actionType) => {
    if (userRoles.length === 1 && userRoles[0] === "EXTERNAL_AUDITOR") {
      const url = `${BASE_URL}/box/file/addExtAuditorEventLog`;
      const data = {
        auditSetId: auditSetId,
        itemId: itemId,
        actionType: actionType,
        itemType: itemType,
      };
      await axiosPrivate.post(url, data).then(() => {});
    }
  };

  useEffect(() => {
    console.log("useEffectuseEffectuseEffectuseEffect");

    const refreshTime = localStorage.getItem("refreshAt");
    console.log(
      "REFRESH :: ",
      dayjs(Date.now()).diff(dayjs(refreshTime), "minute")
    );
    if (dayjs(Date.now()).diff(dayjs(refreshTime), "minute") < 42) {
      try {
        if (
          window.Box &&
          typeof window.Box.Preview === "function" &&
          serviceAccAccessToken !== null
        ) {
          const preview = new window.Box.Preview();
          previewRef.current = preview;
          console.log(preview, "PREVIEW");
          preview.addListener("viewer", (viewer) => {
            viewer.addListener("load", (data) => {
              var downloadClassButton =
                document.getElementsByClassName("bp-btn-download");

              if (downloadClassButton) {
                if (downloadClassButton.length > 0) {
                  downloadClassButton[0].addEventListener("click", function () {
                    addedFileEvent("ITEM_DOWNLOAD");
                  });
                }
              }
              var fullscreenIcon = document.getElementsByClassName(
                "bp-enter-fullscreen-icon"
              );

              if (fullscreenIcon) {
                if (fullscreenIcon.length > 0) {
                  fullscreenIcon[0].addEventListener("click", function () {
                    addedFileEvent("ITEM_PREVIEW");
                    console.log(
                      "fullscreenIconfullscreenIconfullscreenIconfullscreenIcon "
                    );
                  });
                }
              }
            });
          });

          preview.show(itemId, serviceAccAccessToken, {
            container: ".preview-container",
            showDownload: true,
          });
          addedFileEvent("ITEM_VIEW");
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
    auditSetId,
    dispatch,
    itemId,
    jwtTokenRefreshToken,
    serviceAccAccessToken,
    userEmail,
  ]);

  return (
    <div>
      <div
        className="preview-container"
        style={{
          height: "250px",
          width: "100%",
          border: "0px solid #eee",
          position: "relative",
        }}
      >
        {/* <div
          style={{
            height: "50px",
            width: "100%",
            border: "10px solid red",
            position: "absolute",
            top: "100px",
          }}
        >
          <FileDownloadIcon
            onClick={() => {
              console.log("ddddddddddddddddddddd");
              if (previewRef !== null) {
                previewRef.current.download();
                addedFileEvent("ITEM_DOWNLOAD");
              }
            }}
          ></FileDownloadIcon>
        </div> */}
      </div>
    </div>
  );
};

export default ExternalPreviewFile;
