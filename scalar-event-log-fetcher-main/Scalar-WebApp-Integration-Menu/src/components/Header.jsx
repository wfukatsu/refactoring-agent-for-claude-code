import DownArrowIcon from "../Icons/DownArrowIcon";
import NotificationIcon from "../Icons/NotificationIcon";
import SettingsIcon from "../Icons/SettingIcon";
import ProfileIcon from "../Icons/ProfileIcon";

// import Profile from "../images/Ellipse 3.png";
//import ScalarLogo from "../icons/ic_scalar.svg";
import imageSrc from "../Icons/ic_scalar.png";
import { useDispatch, useSelector } from "react-redux";
import { Store } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { logout } from "../features/auth/authSlice";
import { useRef } from "react";

import { useTranslation } from "react-i18next";
import useAxiosPrivate from "../hooks/useAxiosPrivate";
import { BASE_URL } from "../utils/constant";

import { Avatar, Button } from "@mui/material";

function Header() {
  const { user } = useSelector((Store) => Store.auth);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const axiosPrivate = useAxiosPrivate();

  const {t,i18n} = useTranslation();

  function setting() {
    alert("Setting");
  }
  function Notification() {
    alert("Notification");
  }


  const allSupportedLangues = useRef([
    {
      language: "English",
      code: "en",
    },
    {
      language: "日本語",
      code: "ja",
    },
  ]);

  function changeLanguage(e) {
    // i18n.changeLanguage(e.target.value);
    updateLanguageForUser(e.target.value);
  }

  async function updateLanguageForUser(newLanguageCode) {
    const url = `${BASE_URL}/box/user/updateLanguageForUser?lang=${newLanguageCode}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        i18n.changeLanguage(newLanguageCode);
        // console.log("Response:", response.data.message);
      })
      .catch((error) => {
        console.error("Error updating language:", error);
      });
  }

  return (
    <div
      style={{
        width: "100%",
        backgroundColor: "#00132B",
        top: "0",
        height: "100%",
        boxSizing: "border-box",
        alignItems: "center",
        justifyContent: "center",
        justifyItems: "center",
        paddingBottom: "7px",
      }}
    >
      <div
        style={{
          width: "100%",
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          alignItems: "center",
          paddingTop: "10px",
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "center",
            paddingLeft: "20px",
            marginBottom: "12px",
          }}
        >
          <img
            src={imageSrc}
            style={{ width: "40px", height: "40px", marginRight: "10px" }}
            alt="Description of the image"
          />
          <div
            className="scalarheading"
            style={{
              backgroundColor: "#00132B",
              color: "white",
              fontSize: "21px",
              fontWeight: 900,
            }}
          >
            Scalar
          </div>
          <div style={{ width: "60px" }}></div>
        </div>

        {/* section2   */}
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            gap: "40px",
            justifyContent: "center",
          }}
        >
          {/* <SettingsIcon
            style={{ width: "16px", height: "16px" }}
            onMouseEnter={(e) => {
              e.target.style.width = "18px";
              e.target.style.height = "18px";
            }}
            onMouseLeave={(e) => {
              e.target.style.width = "16px";
              e.target.style.height = "16px";
            }}
            onClick={setting}
          />
          <NotificationIcon
            style={{ width: "16px", height: "16px" }}
            onMouseEnter={(e) => {
              e.target.style.width = "18px";
              e.target.style.height = "18px";
            }}
            onMouseLeave={(e) => {
              e.target.style.width = "16px";
              e.target.style.height = "16px";
            }}
            onClick={Notification}
          /> */}
          <div
            style={{
              display: "flex",
              flexDirection: "row",
              alignItems: "center",
              gap: "6px",
            }}
          >
            {/* <ProfileIcon /> */}
            <Avatar alt="Remy Sharp">
                  {user.username && user.username.charAt(0)}
            </Avatar>
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                color: "white",
                marginLeft: "10px",
              }}
            >
              <div
                style={{ display: "flex", gap: "10px", alignItems: "center" }}
              >
                <div>{user.username}</div>
                {/* <DownArrowIcon
                  style={{ width: "8px", height: "8px" }}
                ></DownArrowIcon> */}
              </div>

              <div style={{ opacity: "0.6" }}>{user.userEmail}</div>
            </div>
            <div style={{ width: "5px" }}></div>
            <div className="flex items-center justify-center mr-4">
                  <select
                    className="p-1" // You can adjust this class to include text color if necessary
                    value={i18n.language}
                    onChange={changeLanguage}
                    style={{ color: "black" }} // Set text color explicitly
                  >
                    {allSupportedLangues.current.map((lang) => (
                      <option key={lang.code} value={lang.code}>
                        {lang.language}
                      </option>
                    ))}
                  </select>

                  {/* <Button
                aria-describedby={id}
                onClick={handleClick}
                sx={{ color: "white" }}
              >
                <ArrowDropDownIcon />
              </Button> */}
                </div>

            <div style={{ width: "5px" }}></div>
            <button
              style={{ margins: "20px" }}
              className="bg-[#2834d8] hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full"
              onClick={() => {
                dispatch(logout());
                navigate("/logout", { replace: true });
              }}
            >
              {t("logoutText")}
            </button>
            <div style={{ width: "5px" }}></div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Header;
