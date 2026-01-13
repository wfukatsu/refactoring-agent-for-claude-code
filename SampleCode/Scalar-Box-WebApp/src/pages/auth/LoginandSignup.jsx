import React, { useEffect, useState } from "react";
import Header from "../../common/Header/Header";
import {
  Button,
  Divider,
  IconButton,
  TextField,
  InputAdornment,
} from "@mui/material";
import logo from "../../assets/logo.png";
import { useDispatch } from "react-redux";
import { LOGIN } from "../../redux/reducerSlice/authSlice";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import "./LoginandSignup.css";
import axios from "axios";
import Loder from "../../common/Loader/Loder";
import { useNavigate } from "react-router-dom";
import {
  BASE_URL,
  CLIENT_ID,
  CLIENT_SECRET,
  REDIRECT_URL,
} from "../../utils/constants";
import { Formik, Field, Form, ErrorMessage } from "formik";
import ForgotPassword from "./ForgotPassword";

import GlobalStyles from "@mui/material/GlobalStyles";
import dayjs from "dayjs";
import { UPDATE_REFRESH_TOKEN } from "../../redux/reducerSlice/tokenSlice";

import { useTranslation } from "react-i18next";

import detectBrowserLanguage from "detect-browser-language";

function LoginandSignup() {
  const [email, setEmail] = useState("");
  const [errorrMessage, setMessage] = useState("");
  const [password, setPassword] = useState("");
  const [activeButton, setActiveButton] = useState("Organization");
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [errorMessage, setErrorMessage] = useState();
  const [validEmail, setValidEmail] = useState(true);
  const [showPassword, setShowPassword] = useState(false);

  const { t } = useTranslation();
  const { i18n } = useTranslation();

  // console.log("location and browser code ",detectBrowserLanguage(),i18n.language);

  useEffect(() => {
    const browserLanguage = detectBrowserLanguage();

    if (browserLanguage === "ja-JP") {
      i18n.changeLanguage("ja");
    } else {
      i18n.changeLanguage("en");
    }
  }, []);

  const validateEmail = (input) => {
    // Regular expression for email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(input);
  };

  const handleEmailChange = (e) => {
    const inputEmail = e.target.value;
    setEmail(inputEmail);
    setValidEmail(validateEmail(inputEmail));
  };

  const initiateOAuth = () => {
    const authorizationUrl = `https://account.box.com/api/oauth2/authorize?response_type=code&client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URL}`;
    window.location.href = authorizationUrl;
  };

  const exchangeCodeForToken = (code) => {
    const fetchBoxAcessToken = async () => {
      const response = await fetch("https://api.box.com/oauth2/token", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: `grant_type=authorization_code&code=${code}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&redirect_uri=${REDIRECT_URL}`,
      });

      if (response.ok) {
        const data = await response.json();
        return data;
      } else {
        throw Error("FAILED TO FETCH");
      }
    };

    fetchBoxAcessToken()
      .then((data) => {
        const { access_token, refresh_token, expires_in } = { ...data };
        const submitToken = async () => {
          const url = `${BASE_URL}/box/user/submitToken`;
          const data = {
            accessToken: `${access_token}`,
            refreshToken: `${refresh_token}`,
            expiresIn: `${expires_in}`,
          };
          console.log("RESPONSE :: ", data);
          const response = await axios.post(url, data);
          console.log("RESPONSE :: ", response);
          console.log("language data", response.data.data.languageCode);
          i18n.changeLanguage(response.data.data.languageCode);
          localStorage.setItem("LANGUAGE_CODE", response.data.data.languageCode);

          if (response.status === 200) {
            const {
              userEmail,
              name,
              jwtToken,
              jwtTokenRefreshToken,
              refreshToken,
              userRoles,
              accessToken,
              serviceAccAccessToken,
              orgId,
              languageCode,
            } = {
              ...response.data.data,
            };

            if (userRoles.length === 1 && userRoles[0] === "GENERAL_USER") {
              navigate("/scalar-box/", { replace: true });
            } else {
              navigate("/scalar-box/", { replace: true });
            }

            dispatch(
              LOGIN(
                userEmail,
                name,
                jwtToken,
                jwtTokenRefreshToken,
                refreshToken,
                userRoles,
                accessToken,
                serviceAccAccessToken,
                orgId,
                languageCode
              )
            );
            localStorage.setItem("refreshAt", new Date());
            dispatch(
              UPDATE_REFRESH_TOKEN(
                jwtToken,
                jwtTokenRefreshToken,
                serviceAccAccessToken,
                userEmail
              )
            );
          } else {
            throw Error("FAILED TO SUBMIT");
          }
        };
        submitToken();
      })
      .catch((error) => {
        console.log("ERROR :: ", error);
      });
  };

  const handleOAuthButtonClick = () => {
    initiateOAuth();
  };

  const handleButtonClick = (button) => {
    setActiveButton(button);
  };

  const signInWithUserNameAndPassword = async (
    email,
    password,
    setSubmitting
  ) => {
    try {
      const response = await axios.post(
        `${BASE_URL}/box/user/login`,
        { userEmail: email, password },
        {
          headers: {
            "Content-Type": "application/json",
            "Accept-Language": i18n.language,
          },
        }
      );
      if (response.status === 200) {
        const {
          userEmail,
          name,
          jwtToken,
          jwtTokenRefreshToken,
          refreshToken,
          userRoles,
          accessToken,
          serviceAccAccessToken,
          languageCode,
        } = response.data.data;
        if (userRoles.length === 1 && userRoles[0] === "EXTERNAL_AUDITOR") {
          navigate("/scalar-box/", { replace: true });
        } else {
          navigate("/scalar-box/", { replace: true });
        }
        console.log("code language", languageCode);
        i18n.changeLanguage(languageCode);
        localStorage.setItem("LANGUAGE_CODE", languageCode);

        dispatch(
          LOGIN(
            userEmail,
            name,
            jwtToken,
            jwtTokenRefreshToken,
            refreshToken,
            userRoles,
            accessToken,
            serviceAccAccessToken,
            "",
            languageCode
          )
        );
        localStorage.setItem("refreshAt", new Date());
        dispatch(
          UPDATE_REFRESH_TOKEN(
            jwtToken,
            jwtTokenRefreshToken,
            serviceAccAccessToken,
            userEmail
          )
        );
      } else {
        throw new Error("FAILED TO SUBMIT");
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        const errorMessage = error.response.data.message;
        console.log("Error message:", errorMessage);
        setErrorMessage(errorMessage);
      } else {
        console.log("ERROR :: signInApi", error);
        setErrorMessage(t("errorOccuredPleaseTryAgainText"));
      }
    } finally {
      setSubmitting(false);
    }
  };

  const signInWithBoxUser = async (email, setFieldError) => {
    try {
      setIsLoading(true);
      const response = await axios.post(
        `${BASE_URL}/box/user/userSignIn/${email}`,
        null,
        {
          headers: {
            "Content-Type": "application/json",
            "Accept-Language": i18n.language,
          },
        }
      );

      if (response.status === 200) {
        console.log("RESPONSE :: ", response.data.data);
        const {
          userEmail,
          name,
          jwtToken,
          jwtTokenRefreshToken,
          refreshToken,
          userRoles,
          accessToken,
          serviceAccAccessToken,
          orgId,
          languageCode,
        } = response.data.data;
        if (userRoles.length === 1 && userRoles[0] === "GENERAL_USER") {
          navigate("/scalar-box/", { replace: true });
        } else {
          navigate("/scalar-box/", { replace: true });
        }
        console.log("code language", languageCode);
        i18n.changeLanguage(languageCode);
        localStorage.setItem("LANGUAGE_CODE", languageCode);
        dispatch(
          LOGIN(
            userEmail,
            name,
            jwtToken,
            jwtTokenRefreshToken,
            refreshToken,
            userRoles,
            accessToken,
            serviceAccAccessToken,
            orgId,
            languageCode
          )
        );
        console.log(
          "11jwtToken :: ",
          jwtToken,
          "jwtTokenRefreshToken :: ",
          jwtTokenRefreshToken,
          "serviceAccAccessToken :: ",
          serviceAccAccessToken
        );

        localStorage.setItem("refreshAt", new Date());
        dispatch(
          UPDATE_REFRESH_TOKEN(
            jwtToken,
            jwtTokenRefreshToken,
            serviceAccAccessToken,
            userEmail
          )
        );
      } else {
        throw Error("FAILED TO SUBMIT");
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        const errorMessage = error.response.data.message;
        if (errorMessage === "Incorrect user email !!") {
          setFieldError("email", "Email is incorrect");
          setMessage(errorMessage);
        } else {
          setFieldError("email", errorMessage);
          setMessage(errorMessage);
        }
      } else if (error.response.status === 401) {
        setMessage(errorMessage);
        // console.log("this is,");
      } else {
        console.log("ERROR :: signInApi", error);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const handleExchangeCodeButtonClick = () => {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get("code");
      if (code) {
        exchangeCodeForToken(code);
      } else {
        setIsLoading(false);
      }
    };
    handleExchangeCodeButtonClick();
  }, []);

  if (isLoading) {
    return <Loder />;
  }

  const handleTogglePasswordVisibility = () => {
    setShowPassword((prevShowPassword) => !prevShowPassword);
  };

  const style = {
    "input::-ms-reveal, input::-ms-clear": {
      display: "none",
    },
  };

  return (
    <div className="main_container">
      <img
        className="absolute top-0 -z-10"
        alt="Rectangle"
        src="https://cdn.animaapp.com/projects/65b9ec8e286a69d30c95000e/releases/65b9ecc24ae9e26d265a26e6/img/rectangle-1.png"
      />
      <Header />
      <div className="left_side_container">
        {/* <div style={{justifyContent:"left",alignItems: "flex-start"}}>
          <img src={logo} alt="log" className="left_inner" />
          <p className="paregraph_left">File Auditing Tool by Scalar Inc.</p>

          <p className="inner_paregrapg">
            File Auditing Tool integrated with cloud storage services - Perform
            <br />
            auditing as per your convenience.
          </p>
        </div> */}
        <div className="first-container">
          <div
            style={{
              flexDirection: "column",
              alignItems: "flex-start",
              marginTop: "50px",
              marginRight: "50px",
            }}
          >
            <img src={logo} alt="log" className="left_inner" />
            <p className="paregraph_left">{t("loginScreenTitleText")}</p>
            <p className="inner_paregrapg">{t("loginScreenInfoText")}</p>
          </div>
        </div>

        <div className="second-container">
          <div className="right_main_container">
            <div className="auth_component_main">
              <button
                className={`p-4 w-full rounded-tl-lg font-bold  ${
                  activeButton === "Organization"
                    ? "bg-[#2834d8] text-white"
                    : "bg-[#dfeafb] text-black"
                }`}
                onClick={() => handleButtonClick("Organization")}
              >
                {t("loginScreenExternalAuditorText")}
              </button>
              <button
                className={`p-4 w-full  rounded-tr-lg font-bold ${
                  activeButton === "ExternalAuditor"
                    ? "bg-[#2834d8] text-white"
                    : "bg-[#dfeafb] text-black"
                }`}
                onClick={() => handleButtonClick("ExternalAuditor")}
              >
                {t("loginScreenOrganizationUserText")}
              </button>
            </div>
            {activeButton === "Organization" && (
              <div className="organisation_sidebar">
                <div className="organisation_inner">
                  {t("loginScreenSignInText")}
                </div>
                <Formik
                  initialValues={{ email: "", password: "" }}
                  validate={(values) => {
                    const errors = {};
                    if (!values.email) {
                      errors.email = t("loginScreenBlankEmailText");
                    } else if (
                      !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(
                        values.email
                      )
                    ) {
                      errors.email = t("loginScreenEmailErrorText");
                    }
                    if (!values.password) {
                      errors.password = t("loginScreenEnterPasswordText");
                      setErrorMessage("");
                    }
                    return errors;
                  }}
                  onSubmit={(values, { setSubmitting }) => {
                    signInWithUserNameAndPassword(
                      values.email,
                      values.password,
                      setSubmitting
                    );
                  }}
                >
                  {({ isSubmitting, errors, touched }) => (
                    <Form>
                      <div className="textfiled_1">
                        <Field
                          type="email"
                          name="email"
                          className={`email_input ${
                            errors.email && touched.email
                              ? "border-red-500"
                              : ""
                          }`}
                          label={t("loginScreenEmailText")}
                          variant="standard"
                          fullWidth
                          as={TextField}
                        />
                        <ErrorMessage
                          name="email"
                          component="div"
                          className="flex justify-end items-end text-red-700"
                        />
                        <div style={{ height: "5px" }}></div>
                        <GlobalStyles styles={style} />
                        <Field
                          type={showPassword ? "text" : "password"}
                          name="password"
                          className={`password_input ${
                            errors.password && touched.password
                              ? "border-red-500"
                              : ""
                          }`}
                          label={t("loginScreenPassWordText")}
                          variant="standard"
                          as={TextField}
                          InputProps={{
                            endAdornment: (
                              <InputAdornment position="end">
                                <IconButton
                                  onClick={handleTogglePasswordVisibility}
                                  edge="end"
                                >
                                  {showPassword ? (
                                    <Visibility />
                                  ) : (
                                    <VisibilityOff />
                                  )}
                                </IconButton>
                              </InputAdornment>
                            ),
                          }}
                        />
                        <ErrorMessage
                          name="password"
                          component="div"
                          className="flex justify-end items-end text-red-700"
                        />
                        <p className="flex justify-end items-end text-red-700">
                          {errorMessage}
                        </p>
                      </div>
                      {/* <p className="forget_password flex justify-end">
                        Forgot Password?
                      </p> */}

                      <div style={{ height: "5px" }}></div>
                      <Button
                        variant="contained"
                        type="submit"
                        disabled={isSubmitting}
                      >
                        {isSubmitting
                          ? t("loginScreenSigningButtonText")
                          : t("loginScreenSignInButtonText")}
                      </Button>
                    </Form>
                  )}
                </Formik>
                <ForgotPassword />
                {/* <p className="support">Support + Privacy Policy</p> */}
              </div>
            )}
            {activeButton === "ExternalAuditor" && (
              <div className="ExternalAuditor">
                <div className="ExternalAuditor_inner">
                  <Formik
                    initialValues={{ email: "" }}
                    validate={(values) => {
                      const errors = {};
                      if (!values.email) {
                        setMessage("");
                        errors.email = t("loginScreenOrgEnterEmailText");
                      } else if (
                        !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(
                          values.email
                        )
                      ) {
                        setMessage("");
                        errors.email = t("loginScreenEmailErrorText");
                      }
                      return errors;
                    }}
                    onSubmit={(values, { setSubmitting, setFieldError }) => {
                      signInWithBoxUser(values.email, setFieldError);
                      setSubmitting(false);
                    }}
                  >
                    {({ isSubmitting, isValid, dirty }) => (
                      <Form>
                        <Field
                          type="email"
                          name="email"
                          className="enteruser_input"
                          label={t("loginScreenOrgEnterEmailLabelText")}
                          variant="standard"
                          style={{ margin: "10px" }}
                          fullWidth
                          as={TextField}
                        />
                        <ErrorMessage
                          name="email"
                          component="div"
                          className="flex justify-end items-end text-red-700"
                        />
                        <div className="flex justify-end items-end text-red-700">
                          {errorrMessage}
                        </div>
                        <Button
                          variant="contained"
                          className="SignInBox_Button w-full"
                          type="submit"
                          disabled={!isValid || !dirty || isSubmitting}
                        >
                          {isSubmitting
                            ? t("loginScreenSigningButtonText")
                            : t("loginScreenOrgSignInButtonText")}
                        </Button>
                      </Form>
                    )}
                  </Formik>
                </div>
                <div style={{ height: "20px" }}></div>
                <Divider />
                <div style={{ height: "20px" }}></div>
                <Button
                  variant="outlined"
                  className="SignUpBox_Button"
                  onClick={handleOAuthButtonClick}
                >
                  {t("loginScreenOrgSignInBoxButtonText")}
                </Button>
                {/* <p className="support">Support + Privacy Policy</p> */}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginandSignup;
