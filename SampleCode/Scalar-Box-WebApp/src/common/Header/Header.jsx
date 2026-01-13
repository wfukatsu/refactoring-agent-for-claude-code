import React, { useState, useEffect, useRef, useMemo } from "react";
import logoupdated from "../../assets/logoupdated.png";
import { Avatar, Button, Typography } from "@mui/material";
import { ArrowDropDownIcon } from "@mui/x-date-pickers";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { LOGOUT, UPDATE_LANGUAGE } from "../../redux/reducerSlice/authSlice";

import { useTranslation } from "react-i18next";

import axios from "../../api/axios";
import { BASE_URL } from "../../utils/constants";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

const Header = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const navigate = useNavigate();
  const id = "simple-popover";
  const [anchorEl, setAnchorEl] = useState(null);

  const authUser = useSelector((state) => state.auth.user);

  const { t, i18n } = useTranslation();
  const axiosPrivate = useAxiosPrivate();

  function changeLanguage(e) {
    // i18n.changeLanguage(e.target.value);
    updateLanguageForUser(e.target.value);
  }

  async function updateLanguageForUser(languageCode) {
    const url = `${BASE_URL}/box/user/updateLanguageForUser?lang=${languageCode}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        i18n.changeLanguage(languageCode);
        localStorage.setItem("LANGUAGE_CODE", languageCode);
        // dispatch(UPDATE_LANGUAGE(languageCode));
        // console.log("Response:", response.data.message);
      })
      .catch((error) => {
        console.error("Error updating language:", error);
      });
  }

  const [getLanguages, setLanguages] = useState([]);
  // const allSupportedLangues = useMemo(() => {
  //   console.log("allSupportedLangues", getLanguages);
  //   return [...getLanguages];
  // }, [getLanguages]);
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

  useEffect(() => {
    console.log(
      "HEADER HHHHHHHHHHH",
      allSupportedLangues.current.length
      // allSupportedLangues.current.length > 0
    );
    if (authUser === null) {
      return; // Exit early if authUser is null
    }

    if (allSupportedLangues.current.length > 0) {
      console.log("INSIDE");
      return;
    }

    console.log("HEADER 2222222222222222222");
    async function getAllLanguages() {
      const url = `${BASE_URL}/box/user/getAllLanguagesSupported`;

      axiosPrivate
        .get(url)
        .then((response) => {
          allSupportedLangues.current = [...response.data.data];
          setLanguages(response.data.data);
          console.log("DATA ", response.data.data);
        })
        .catch((error) => {
          console.log("I got the below error");
          console.error(error);
          // setLanguages([]);
        });
    }
    getAllLanguages();
    // i18n.changeLanguage(languageCode);
  }, []); // Include dependencies i

  const handleLogout = () => {
    dispatch(LOGOUT());
  };

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  return (
    <div className="py-3 px-6 w-full flex justify-between items-center bg-[#000d2d]">
      <img className="h-[32px] w-[140px]" alt="Image" src={logoupdated} />
      {user && (
        <div className="flex items-center justify-center gap-15">
          <div className="flex flex-1 gap-3 items-center">
            <Avatar alt="Remy Sharp">{user.name && user.name.charAt(0)}</Avatar>
            <div className="flex flex-col">
              <Typography
                variant="h7"
                noWrap
                component="div"
                style={{ color: "white" }}
              >
                {user.name}
              </Typography>
              <p className="text-sm" style={{ color: "white" }}>
                {user.userEmail}
              </p>
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
              {/* <Button
                aria-describedby={id}
                onClick={handleClick}
                sx={{ color: "white" }}
              >
                <ArrowDropDownIcon />
              </Button> */}
            </div>
          </div>

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
      )}
    </div>
  );
};

export default Header;
