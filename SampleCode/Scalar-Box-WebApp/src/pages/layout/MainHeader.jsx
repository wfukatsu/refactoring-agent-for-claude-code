import { Avatar, Typography } from "@mui/material";
import { useRef } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { LOGOUT, UPDATE_LANGUAGE } from "../../redux/reducerSlice/authSlice";
import { useNavigate } from "react-router-dom";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { BASE_URL } from "../../utils/constants";

const MainHeader = () => {
  const user = useSelector((state) => state.auth.user);

  const { t, i18n } = useTranslation();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const axiosPrivate = useAxiosPrivate();

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
        localStorage.setItem("LANGUAGE_CODE", newLanguageCode);
        // dispatch(UPDATE_LANGUAGE(newLanguageCode));
      })
      .catch((error) => {
        console.error("Error updating language:", error);
      });
  }

  const handleLogout = () => {
    dispatch(LOGOUT());
  };

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

  return (
    <div
      className="flex w-full items-center justify-end gap-15  bg-cyan-900 top-0 left-0 p-2 "
      style={{ backgroundColor: "#000d2d" }}
    >
      {user && (
        <div className="flex gap-3 items-center">
          <Avatar alt="Remy Sharp">{user.name && user.name.charAt(0)}</Avatar>
          <div className="flex flex-col" style={{ color: "white" }}>
            <Typography variant="h7" noWrap component="div">
              {user.name}
            </Typography>
            <p className="text-sm">{user.userEmail}</p>
          </div>
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
          </div>
        </div>
      )}

      <button
        className="bg-[#2834d8] hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full"
        onClick={() => {
          handleLogout();
          navigate("/", { replace: true });
        }}
      >
        {t("logoutText")}
      </button>
    </div>
  );
};

export default MainHeader;
